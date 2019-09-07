package com.atguigu.realtime.server

import java.sql.ResultSet
import java.text.NumberFormat

import com.alibaba.fastjson.JSON
import com.atguigu.realtime.bean.PageLog
import com.atguigu.realtime.constant.RealtimeConstant
import com.atguigu.realtime.util._
import org.apache.spark.SparkFiles
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.lionsoul.ip2region.{DbConfig, DbSearcher}

import scala.collection.mutable.ListBuffer

class PageStatsServer {

  /**
    * 实时统计商品页到订单页，订单页到支付页转换率
    * 需求1：计算首页总浏览数、订单页总浏览数、支付页面总浏览数
    * 需求2：计算商品课程页面到订单页的跳转转换率、订单页面到支付页面的跳转转换率
    * 需求3：根据ip得出相应省份，展示出top3省份的点击数，需要根据历史数据累加
    *
    * @param ssc
    */
  def pageStats(ssc: StreamingContext): Unit = {
    val groupId = "page_consumer_group0"
    // 获取历史offset
    val offsetUtil = new OffsetUtil(groupId)
    val offsetMap = offsetUtil.getOffsetMap()
    offsetMap.foreach(println)

    val kafkaInputDStream =
      if (offsetMap.isEmpty) MyKafkaUtil.getKafkaDStream(ssc, List(RealtimeConstant.KAFKA_PAGE_LOG_TOPIC), groupId)
      else MyKafkaUtil.getKafkaDStream(ssc, List(RealtimeConstant.KAFKA_PAGE_LOG_TOPIC), groupId, offsetMap)
    // 过滤 转换结构
    val pageDStream: DStream[PageLog] = kafkaInputDStream.filter(line => JsonUtil.isJson(line.value()))
      .map(line => JSON.parseObject(line.value(), classOf[PageLog]))
      .filter(pageLog => pageLog.last_page_id != null && pageLog.page_id != null && pageLog.next_page_id != null)
    pageDStream.cache()

    // TODO 统计每个页面的跳转数据 last_page_id-page_id-next_page_id:count
    pageDStream.map(item => (item.last_page_id + "-" + item.page_id + "-" + item.next_page_id, 1L))
      .reduceByKey(_ + _) //聚合
      .foreachRDD(rdd => {
      rdd.foreachPartition(partition => {
        val sqlUtil = new MySQLUtil()
        val conn = DruidUtil.getConnection()
        try {
          partition.foreach(item => {
            val keys = item._1.split("-")
            val last_page_id = keys(0).toInt
            val page_id = keys(1).toInt
            val next_page_id = keys(2).toInt
            var count = item._2
            //            println(s"本批次次数: ${keys.mkString("->")} : $count")
            // 查询历史数据, 并更新数据库数据
            val selectSql = "select num from page_jump_rate where page_id = ? and last_page_id = ? and next_page_id = ?"
            val updateSql = "insert into page_jump_rate(last_page_id, page_id, next_page_id, num, jump_rate) values(?, ?, ?, ?, ?)" +
              "on duplicate key update num = ?"
            // 查询历史页面访问次数
            val rs: ResultSet = sqlUtil.selectQuery(conn, selectSql, Array(page_id, last_page_id, next_page_id))
            while (rs.next()) {
              count += rs.getLong(1)
            }
            rs.close()
            // 更新页面访问次数
            sqlUtil.executeUpdate(conn, updateSql, Array(last_page_id, page_id, next_page_id, count, "100%", count))
            //            println(s"总次数: ${keys.mkString("->")} : $count")
          })
        } catch {
          case e: Exception => e.printStackTrace()
        }
        finally {
          sqlUtil.shutdown(conn)
        }
      })
    })

    // TODO 计算页面转化率, 每批数据处理完成后直接再driver端处理即可
    pageDStream.foreachRDD(_ => {
      val sqlUil = new MySQLUtil()
      val conn = DruidUtil.getConnection()
      // 查询商品课程页面(1) 订单页面(2) 支付页面(3)的访问次数
      var page1Count = 0L
      var page2Count = 0L
      var page3Count = 0L
      try {
        val selectSql = "select num from page_jump_rate where page_id = ?"
        val page1RS: ResultSet = sqlUil.selectQuery(conn, selectSql, Array(1))
        while (page1RS.next()) {
          page1Count += page1RS.getLong(1)
        }
        val page2RS: ResultSet = sqlUil.selectQuery(conn, selectSql, Array(2))
        while (page2RS.next()) {
          page2Count += page2RS.getLong(1)
        }
        val page3RS: ResultSet = sqlUil.selectQuery(conn, selectSql, Array(3))
        while (page3RS.next()) {
          page3Count += page3RS.getLong(1)
        }
        println(s"1:$page1Count  2:$page2Count  3:$page3Count")
        // 计算转化率
        val nf = NumberFormat.getPercentInstance() //数字格式化
        nf.setMaximumFractionDigits(2)
        val page1ToPage2Rate = if (page1Count == 0) "0%" else nf.format(page2Count.toDouble / page1Count)
        val page2ToPage3Rate = if (page2Count == 0) "0%" else nf.format(page3Count.toDouble / page2Count)

        // 更新数据库
        val updateSql = "update page_jump_rate set jump_rate = ? where page_id = ?"
        sqlUil.executeUpdate(conn, updateSql, Array(page1ToPage2Rate, 2))
        sqlUil.executeUpdate(conn, updateSql, Array(page2ToPage3Rate, 3))
      } catch {
        case e: Exception => e.printStackTrace()
      } finally {
        sqlUil.shutdown(conn)
      }
    })

    // TODO 统计各省份的点击总数
    ssc.sparkContext.addFile(Thread.currentThread().getContextClassLoader.getResource("ip2region.db").getPath) //广播文件
    pageDStream.mapPartitions(partition => {
      // 分区内获取文件, 创建解析ip的类
      val dbFile = SparkFiles.get("ip2region.db")
      val dbSearcher = new DbSearcher(new DbConfig(), dbFile)
      partition.map(item => {
        //获取省份 getRegion: 中国|0|广西|南宁市|联通
        val province: String = dbSearcher.memorySearch(item.ip).getRegion.split("\\|")(2)
        (province, 1L)
      })
    })
      .reduceByKey(_ + _)
      .foreachRDD(_.foreachPartition(partition => {
        val sqlUtil = new MySQLUtil()
        val conn = DruidUtil.getConnection()
        // 获取历史每个省份数据, 更新mysql数据
        try {
          partition.foreach(provinceAndNum => {
            val province = provinceAndNum._1
            var number = provinceAndNum._2
            val selectSql = "select num from tmp_city_num_detail where province = ?"
            // 获取历史数据
            val rs = sqlUtil.selectQuery(conn, selectSql, Array(province))
            while (rs.next()) {
              number += rs.getLong(1)
            }
            rs.close()
            // 更新数据
            val updateSql = "insert into tmp_city_num_detail(province, num) values(?, ?)" +
              "on duplicate key update num = ?"
            sqlUtil.executeUpdate(conn, updateSql, Array(province, number, number))
//            println(province + "->" + number)
          }
          )
        } catch {
          case e: Exception => e.printStackTrace()
        } finally {
          sqlUtil.shutdown(conn)
        }
      }))

    // TODO 统计点击数Top3的省份
    pageDStream.foreachRDD(_ => {
      // 查询tmp_city_num_detail表中的Top3, 存入top_city_num表中
      val sqlUtil = new MySQLUtil()
      val conn = DruidUtil.getConnection()
      val top3List = ListBuffer[(String, Long)]()
      try {
        val selectSql = "select province, num from tmp_city_num_detail order by num desc limit ?"
        val rs: ResultSet = sqlUtil.selectQuery(conn, selectSql, Array(3))
        while (rs.next()) {
          // 查询top3
          top3List.append((rs.getString(1), rs.getLong(2)))
        }
        rs.close()
        // 清除旧的top3
        if (top3List.nonEmpty) {
          println(top3List)
          sqlUtil.executeUpdate(conn, "truncate table top_city_num", null)
          // 更新top3
          val updateSql = "insert into top_city_num(province, num) values(?, ?)"
          top3List.foreach {
            case (province, number) => sqlUtil.executeUpdate(conn, updateSql, Array(province, number))
          }
        }
      } catch {
        case e: Exception => e.printStackTrace()
      } finally {
        sqlUtil.shutdown(conn)
      }
    })

    // 维护offset
    offsetUtil.updateOffset(kafkaInputDStream)
  }

}
