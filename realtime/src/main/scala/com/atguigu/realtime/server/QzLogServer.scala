package com.atguigu.realtime.server

import java.sql.{Connection, ResultSet}

import com.atguigu.realtime.bean.QzLog
import com.atguigu.realtime.constant.RealtimeConstant
import com.atguigu.realtime.util.{DruidUtil, MyKafkaUtil, MySQLUtil, OffsetUtil}
import org.apache.spark.streaming.StreamingContext

import scala.collection.mutable.ListBuffer

class QzLogServer {

  /**
    * 分析学员做题情况
    * 需求1：要求Spark Streaming 保证数据不丢失，每秒100条处理速度，需要手动维护偏移量
    * 需求2：同一个用户做在同一门课程同一知识点下做题需要去重，并且需要记录去重后的做题id与个数。
    * 需求3：计算知识点正确率 正确率计算公式：做题正确总个数/做题总数 保留两位小数
    * 需求4：计算知识点掌握度 去重后的做题个数/当前知识点总题数（已知30题）*当前知识点的正确率
    *
    * @param ssc
    */
  def qzLogStats(ssc: StreamingContext): Unit = {
    // 获取历史offset
    val groupId = "qz_log_consumer_group0"
    val offsetUtil = new OffsetUtil(groupId)
    val offsetMap = offsetUtil.getOffsetMap()
    offsetMap.foreach(println)

    // 判断是否有历史偏移量, 如果有 从历史偏移量出开始消费, 如果没有, 从头消费
    val kafkaInputDStream =
      if (offsetMap.isEmpty) MyKafkaUtil.getKafkaDStream(ssc, List(RealtimeConstant.KAFKA_QZ_LOG_TOPIC), groupId)
      else MyKafkaUtil.getKafkaDStream(ssc, List(RealtimeConstant.KAFKA_QZ_LOG_TOPIC), groupId, offsetMap)
    //    kafkaInputDStream.cache()

    // 过滤 转换
    val QzLogDStream = kafkaInputDStream.filter(_.value().split("\\t").size == 6)
      .map(item => {
        val dataArr = item.value().split("\\t")
        val isTrue = dataArr(4).trim match {
          case "1" => true
          case _ => false
        }
        QzLog(dataArr(0).trim, dataArr(1).trim.toInt, dataArr(2).trim.toInt, dataArr(3).trim.toInt, isTrue, dataArr(5).trim)
      })

    // TODO 完成需求统计
    QzLogDStream.foreachRDD(rdd => {
      // 按照userId,courseId,pointId分组
      rdd.groupBy(qz => qz.userId + "_" + qz.courseId + "_" + qz.pointId).foreachPartition(partition => {
        val conn: Connection = DruidUtil.getConnection()
        val sqlUtil = new MySQLUtil()
        try {
          partition.foreach {
            case (key, userDatas) => {
              //              println(key + userDatas.toList)
              // 统计本批次的一个知识点下的数据
              val keys = key.split("_")
              val userId = keys(0) //用户id
              val courseId = key(1).toInt //课程id
              val pointId = keys(2).toInt //知识点id
              val questionIds = ListBuffer[Int]() //做过的题目id
              var rightCount = 0 //正确个数
              val batchList = userDatas.toList //本批次数据
              val updateTime = batchList.tail.foldLeft(batchList.head.createTime)((time, qz) => if (time < qz.createTime) time else qz.createTime)
              batchList.foreach(qz => {
                questionIds.append(qz.questionId)
                if (qz.isTrue) rightCount += 1
              })

              // 获取Mysql中的数据
              val userQzHistoryList = ListBuffer[(String, Int)]()
              val selectSql = "select question_ids, right_count from user_qz_detail where user_id = ? and course_id = ? and point_id = ?"
              val rs: ResultSet = sqlUtil.selectQuery(conn, selectSql, Array(userId, courseId, pointId))
              while (rs.next()) {
                userQzHistoryList.append((
                  rs.getString(1), //做过的题目ids
                  rs.getInt(2) //答题正确个数
                ))
              }
              rs.close()
              // 整合本批数据和Mysql中的历史数据
              if (!userQzHistoryList.isEmpty) {
                val historyQuestionIds = userQzHistoryList.head._1.split(",").map(_.toInt)
                val historyRightCount = userQzHistoryList.head._2
                rightCount += historyRightCount
                questionIds.appendAll(historyQuestionIds)
              }
              val distinctQuestionIds = questionIds.distinct //对做过的题目去重
              // 正确率: 做题正确总个数/做题总数 保留两位小数
              val rightRate = BigDecimal(rightCount.toDouble / questionIds.size).setScale(2, BigDecimal.RoundingMode.HALF_UP)
              // 掌握度: 去重后的做题个数/当前知识点总题数（已知30题）*当前知识点的正确率
              val graspRate = BigDecimal(rightRate.toDouble * distinctQuestionIds.size / 30.0).setScale(2, BigDecimal.RoundingMode.HALF_UP)

              // 更新user_qz_set表, 存储去重后的题目id
              val sql = "insert into user_qz_set(user_id, course_id, point_id, question_id_set, question_count, create_time, update_time) " +
                "value(?, ?, ?, ?, ?, ?, ?) on duplicate key update question_id_set = ?, question_count = ?, update_time = ?"
              val question_id_set = distinctQuestionIds.mkString(",")
              val question_count = distinctQuestionIds.size
              val count1 = sqlUtil.executeUpdate(conn, sql,
                Array(userId, courseId, pointId, question_id_set, question_count, updateTime, updateTime, question_id_set, question_count, updateTime))
              println(s"user_qz_set更新了 $count1 条数据")

              // 更新user_qz表, 存储详细数据
              val userQzSql = "replace into user_qz_detail(user_id, course_id, point_id, question_ids, right_count, right_rate, grasp_rate, update_time) " +
                "value(?, ?, ?, ?, ?, ?, ?, ?)"
              val question_ids = questionIds.mkString(",")
              val count2 = sqlUtil.executeUpdate(conn, userQzSql, Array(userId, courseId, pointId, question_ids, rightCount, rightRate.toString(), graspRate.toString(), updateTime))
              println(s"user_qz_detail更新了 $count2 条数据")
            }
          }
        } catch {
          case e: Exception => e.printStackTrace()
        } finally {
          sqlUtil.shutdown(conn)
        }
      })
    })

    // 手动维护偏移量
    offsetUtil.updateOffset(kafkaInputDStream)
  }
}
