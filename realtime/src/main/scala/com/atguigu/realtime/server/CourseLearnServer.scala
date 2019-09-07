package com.atguigu.realtime.server

import java.sql.Connection

import com.alibaba.fastjson.JSON
import com.atguigu.realtime.bean.CourseLearn
import com.atguigu.realtime.constant.RealtimeConstant
import com.atguigu.realtime.util._
import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class CourseLearnServer {
  /**
    * 实时统计学员播放视频各时长
    *
    * 需求1：计算各章节下的播放总时长(按chapterid聚合统计播放总时长)
    * 需求2：计算各课件下的播放总时长(按cwareid聚合统计播放总时长)
    * 需求3：计算各辅导下的播放总时长(按edutypeid聚合统计播放总时长)
    * 需求4：计算各播放平台下的播放总时长(按sourcetype聚合统计播放总时长)
    * 需求5：计算各科目下的播放总时长(按subjectid聚合统计播放总时长)
    * 需求6：计算用户学习视频的播放总时长、有效时长、完成时长，需求记录视频播历史区间，
    * 对于用户多次学习的播放区间不累计有效时长和完成时长。
    *
    * 播放总时长计算：(te-ts)/1000  向下取整  单位：秒
    * 完成时长计算： 根据pe-ps 计算 需要对历史数据进行去重处理
    * 有效时长计算：根据te-ts 除以pe-ts 先计算出播放每一区间需要的实际时长 * 完成时长
    *
    * @param ssc
    */
  def courserLearnStats(ssc: StreamingContext): Unit = {
    val groupId = "course_learn_consumer_group0"
    val offsetUtil = new OffsetUtil(groupId)
    // 获取offset
    val offsetMap: mutable.Map[TopicPartition, Long] = offsetUtil.getOffsetMap()
    val kafkaInputDStream =
      if (offsetMap.isEmpty) MyKafkaUtil.getKafkaDStream(ssc, List(RealtimeConstant.KAFKA_COURSE_LEARN_LOG_TOPIC), groupId)
      else MyKafkaUtil.getKafkaDStream(ssc, List(RealtimeConstant.KAFKA_COURSE_LEARN_LOG_TOPIC), groupId, offsetMap)

    val courseLearnDStream: DStream[CourseLearn] = kafkaInputDStream.filter(line => JsonUtil.isJson(line.value()))
      .map(line => JSON.parseObject(line.value(), classOf[CourseLearn]))
      .cache()

    // TODO 需求1：计算各章节下的播放总时长(按chapterid聚合统计播放总时长)  存入 chapter_learn_detail
    courseLearnDStream.map(cl => (cl.chapterid, (cl.te - cl.ts) / 1000))
      .reduceByKey(_ + _)
      .foreachRDD(_.foreachPartition(partition => {
        val sqlUtil = new MySQLUtil()
        val conn = DruidUtil.getConnection()
        try {
          partition.foreach(data => {
            val chapterId = data._1
            var totalTime = data._2
            // 查询历史总时长
            val selectSql = "select totaltime from chapter_learn_detail where chapterid = ?"
            val rs = sqlUtil.selectQuery(conn, selectSql, Array(chapterId))
            while (rs.next()) totalTime += rs.getLong(1)
            rs.close()
            // 更新总时长
            val updateSql = "replace into chapter_learn_detail(chapterid, totaltime) values(?, ?)"
            sqlUtil.executeUpdate(conn, updateSql, Array(chapterId, totalTime))
          })
        } catch {
          case e: Exception => e.printStackTrace()
        } finally {
          sqlUtil.shutdown(conn)
        }
      }))

    // TODO 需求2：计算各课件下的播放总时长(按cwareid聚合统计播放总时长)  cwareid_learn_detail
    courseLearnDStream.map(cl => (cl.cwareid, (cl.te - cl.ts) / 1000))
      .reduceByKey(_ + _)
      .foreachRDD(_.foreachPartition(partition => {
        val sqlUtil = new MySQLUtil()
        val conn = DruidUtil.getConnection()
        try {
          partition.foreach(data => {
            val cwareId = data._1
            var totalTime = data._2
            // 查询历史总时长
            val selectSql = "select totaltime from cwareid_learn_detail where cwareid = ?"
            val rs = sqlUtil.selectQuery(conn, selectSql, Array(cwareId))
            while (rs.next()) totalTime += rs.getLong(1)
            rs.close()
            // 更新总时长
            val updateSql = "replace into cwareid_learn_detail(cwareid, totaltime) values(?, ?)"
            sqlUtil.executeUpdate(conn, updateSql, Array(cwareId, totalTime))
          })
        } catch {
          case e: Exception => e.printStackTrace()
        } finally {
          sqlUtil.shutdown(conn)
        }
      }))

    // TODO 需求3：计算各辅导下的播放总时长(按edutypeid聚合统计播放总时长)  edutype_learn_detail
    courseLearnDStream.map(cl => (cl.edutypeid, (cl.te - cl.ts) / 1000))
      .reduceByKey(_ + _)
      .foreachRDD(_.foreachPartition(partition => {
        val sqlUtil = new MySQLUtil()
        val conn = DruidUtil.getConnection()
        try {
          partition.foreach(data => {
            val eduTypeId = data._1
            var totalTime = data._2
            // 查询历史总时长
            val selectSql = "select totaltime from edutype_learn_detail where edutypeid = ?"
            val rs = sqlUtil.selectQuery(conn, selectSql, Array(eduTypeId))
            while (rs.next()) totalTime += rs.getLong(1)
            rs.close()
            // 更新总时长
            val updateSql = "replace into edutype_learn_detail(edutypeid, totaltime) values(?, ?)"
            sqlUtil.executeUpdate(conn, updateSql, Array(eduTypeId, totalTime))
          })
        } catch {
          case e: Exception => e.printStackTrace()
        } finally {
          sqlUtil.shutdown(conn)
        }
      }))

    // TODO 需求4：计算各播放平台下的播放总时长(按sourcetype聚合统计播放总时长)  sourcetype_learn_detail
    courseLearnDStream.map(cl => (cl.sourceType, (cl.te - cl.ts) / 1000))
      .reduceByKey(_ + _)
      .foreachRDD(_.foreachPartition(partition => {
        val sqlUtil = new MySQLUtil()
        val conn = DruidUtil.getConnection()
        try {
          partition.foreach(data => {
            val sourceType = data._1
            var totalTime = data._2
            // 查询历史总时长
            val selectSql = "select totaltime from sourcetype_learn_detail where sourceType = ?"
            val rs = sqlUtil.selectQuery(conn, selectSql, Array(sourceType))
            while (rs.next()) totalTime += rs.getLong(1)
            rs.close()
            // 更新总时长
            val updateSql = "replace into sourcetype_learn_detail(sourceType, totaltime) values(?, ?)"
            sqlUtil.executeUpdate(conn, updateSql, Array(sourceType, totalTime))
          })
        } catch {
          case e: Exception => e.printStackTrace()
        } finally {
          sqlUtil.shutdown(conn)
        }
      }))

    // TODO 需求5：计算各科目下的播放总时长(按subjectid聚合统计播放总时长)  subject_learn_detail
    courseLearnDStream.map(cl => (cl.subjectid, (cl.te - cl.ts) / 1000))
      .reduceByKey(_ + _)
      .foreachRDD(_.foreachPartition(partition => {
        val sqlUtil = new MySQLUtil()
        val conn = DruidUtil.getConnection()
        try {
          partition.foreach(data => {
            val subjectId = data._1
            var totalTime = data._2
            // 查询历史总时长
            val selectSql = "select totaltime from subject_learn_detail where subjectid = ?"
            val rs = sqlUtil.selectQuery(conn, selectSql, Array(subjectId))
            while (rs.next()) totalTime += rs.getLong(1)
            rs.close()
            // 更新总时长
            val updateSql = "replace into subject_learn_detail(subjectid, totaltime) values(?, ?)"
            sqlUtil.executeUpdate(conn, updateSql, Array(subjectId, totalTime))
          })
        } catch {
          case e: Exception => e.printStackTrace()
        } finally {
          sqlUtil.shutdown(conn)
        }
      }))

    // TODO 需求6：计算用户学习视频的播放总时长、有效时长、完成时长，需要记录视频播放历史区间
    courseLearnDStream.foreachRDD(rdd => {
      rdd.cache()
      //统计播放视频 有效时长 完成时长 总时长
      rdd.groupBy(item => item.uid + "_" + item.cwareid + "_" + item.videoid).foreachPartition(partitoins => {
        val sqlProxy = new MySQLUtil()
        val client = DruidUtil.getConnection
        try {
          partitoins.foreach { case (key, iters) =>
            calcVideoTime(key, iters, sqlProxy, client) //计算视频时长
          }
        } catch {
          case e: Exception => e.printStackTrace()
        } finally {
          sqlProxy.shutdown(client)
        }
      })
    })

    // 维护offset
    offsetUtil.updateOffset(kafkaInputDStream)
  }

  /**
    * 计算视频 有效时长  完成时长 总时长
    *
    * @param key
    * @param iters
    * @param sqlProxy
    * @param client
    */
  def calcVideoTime(key: String, iters: Iterable[CourseLearn], sqlProxy: MySQLUtil, client: Connection) = {
    val keys = key.split("_")
    val userId = keys(0).toInt
    val cwareId = keys(1).toInt
    val videoId = keys(2).toInt
    //查询历史数据
    var interval_history = ""
    val rs = sqlProxy.selectQuery(client, "select play_interval from video_interval where userid=? and cwareid=? and videoid=?",
      Array(userId, cwareId, videoId))
    while (rs.next()) {
      interval_history = rs.getString(1)
    }
    rs.close()
    var effective_duration_sum = 0l //有效总时长
    var complete_duration_sum = 0l //完成总时长
    var cumulative_duration_sum = 0l //播放总时长
    val learnList = iters.toList.sortBy(item => item.ps) //转成list 并根据开始区间升序排序
    learnList.foreach(item => {
      if ("".equals(interval_history)) {
        //没有历史区间
        val play_interval = item.ps + "-" + item.pe //有效区间
        val effective_duration = Math.ceil((item.te - item.ts) / 1000) //有效时长
        val complete_duration = item.pe - item.ps //完成时长
        effective_duration_sum += effective_duration.toLong
        cumulative_duration_sum += effective_duration.toLong
        complete_duration_sum += complete_duration
        interval_history = play_interval
      } else {
        //有历史区间进行对比
        val interval_arry = interval_history.split(",").sortBy(a => (a.split("-")(0).toInt, a.split("-")(1).toInt))
        val tuple = getEffectiveInterval(interval_arry, item.ps, item.pe)
        val complete_duration = tuple._1 //获取实际有效完成时长
        val effective_duration = Math.ceil((item.te - item.ts) / 1000) / (item.pe - item.ps) * complete_duration //计算有效时长
        val cumulative_duration = Math.ceil((item.te - item.ts) / 1000) //累计时长
        interval_history = tuple._2
        effective_duration_sum += effective_duration.toLong
        complete_duration_sum += complete_duration
        cumulative_duration_sum += cumulative_duration.toLong
      }
      sqlProxy.executeUpdate(client, "insert into video_interval(userid,cwareid,videoid,play_interval) values(?,?,?,?) " +
        "on duplicate key update play_interval=?", Array(userId, cwareId, videoId, interval_history, interval_history))
      sqlProxy.executeUpdate(client, "insert into video_learn_detail(userid,cwareid,videoid,totaltime,effecttime,completetime) " +
        "values(?,?,?,?,?,?) on duplicate key update totaltime=totaltime+?,effecttime=effecttime+?,completetime=completetime+?",
        Array(userId, cwareId, videoId, cumulative_duration_sum, effective_duration_sum, complete_duration_sum, cumulative_duration_sum,
          effective_duration_sum, complete_duration_sum))
    })
  }

  /**
    * 计算有效区间
    *
    * @param array
    * @param start
    * @param end
    * @return
    */
  def getEffectiveInterval(array: Array[String], start: Int, end: Int) = {
    var effective_duration = end - start
    var bl = false //是否对有效时间进行修改
    import scala.util.control.Breaks._
    breakable {
      for (i <- 0 until array.length) {
        //循环各区间段
        var historyStart = 0 //获取其中一段的开始播放区间
        var historyEnd = 0 //获取其中一段结束播放区间
        val item = array(i)
        try {
          historyStart = item.split("-")(0).toInt
          historyEnd = item.split("-")(1).toInt
        } catch {
          case e: Exception => throw new Exception("error array:" + array.mkString(","))
        }
        if (start >= historyStart && historyEnd >= end) {
          //已有数据占用全部播放时长 此次播放无效
          effective_duration = 0
          bl = true
          break()
        } else if (start <= historyStart && end > historyStart && end < historyEnd) {
          //和已有数据左侧存在交集 扣除部分有效时间（以老数据为主进行对照）
          effective_duration -= end - historyStart
          array(i) = start + "-" + historyEnd
          bl = true
        } else if (start > historyStart && start < historyEnd && end >= historyEnd) {
          //和已有数据右侧存在交集 扣除部分有效时间
          effective_duration -= historyEnd - start
          array(i) = historyStart + "-" + end
          bl = true
        } else if (start < historyStart && end > historyEnd) {
          //现数据 大于旧数据 扣除旧数据所有有效时间
          effective_duration -= historyEnd - historyStart
          array(i) = start + "-" + end
          bl = true
        }
      }
    }
    val result = bl match {
      case false => {
        //没有修改原array 没有交集 进行新增
        val distinctArray2 = ArrayBuffer[String]()
        distinctArray2.appendAll(array)
        distinctArray2.append(start + "-" + end)
        val distinctArray = distinctArray2.distinct.sortBy(a => (a.split("-")(0).toInt, a.split("-")(1).toInt))
        val tmpArray = ArrayBuffer[String]()
        tmpArray.append(distinctArray(0))
        for (i <- 1 until distinctArray.length) {
          val item = distinctArray(i).split("-")
          val tmpItem = tmpArray(tmpArray.length - 1).split("-")
          val itemStart = item(0)
          val itemEnd = item(1)
          val tmpItemStart = tmpItem(0)
          val tmpItemEnd = tmpItem(1)
          if (tmpItemStart.toInt < itemStart.toInt && tmpItemEnd.toInt < itemStart.toInt) {
            //没有交集
            tmpArray.append(itemStart + "-" + itemEnd)
          } else {
            //有交集
            val resultStart = tmpItemStart
            val resultEnd = if (tmpItemEnd.toInt > itemEnd.toInt) tmpItemEnd else itemEnd
            tmpArray(tmpArray.length - 1) = resultStart + "-" + resultEnd
          }
        }
        val play_interval = tmpArray.sortBy(a => (a.split("-")(0).toInt, a.split("-")(1).toInt)).mkString(",")
        play_interval
      }
      case true => {
        //修改了原array 进行区间重组
        val distinctArray = array.distinct.sortBy(a => (a.split("-")(0).toInt, a.split("-")(1).toInt))
        val tmpArray = ArrayBuffer[String]()
        tmpArray.append(distinctArray(0))
        for (i <- 1 until distinctArray.length) {
          val item = distinctArray(i).split("-")
          val tmpItem = tmpArray(tmpArray.length - 1).split("-")
          val itemStart = item(0)
          val itemEnd = item(1)
          val tmpItemStart = tmpItem(0)
          val tmpItemEnd = tmpItem(1)
          if (tmpItemStart.toInt < itemStart.toInt && tmpItemEnd.toInt < itemStart.toInt) {
            //没有交集
            tmpArray.append(itemStart + "-" + itemEnd)
          } else {
            //有交集
            val resultStart = tmpItemStart
            val resultEnd = if (tmpItemEnd.toInt > itemEnd.toInt) tmpItemEnd else itemEnd
            tmpArray(tmpArray.length - 1) = resultStart + "-" + resultEnd
          }
        }
        val play_interval = tmpArray.sortBy(a => (a.split("-")(0).toInt, a.split("-")(1).toInt)).mkString(",")
        play_interval
      }
    }
    (effective_duration, result)
  }
}
