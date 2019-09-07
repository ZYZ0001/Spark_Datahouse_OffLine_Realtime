package com.atguigu.realtime.server

import java.sql.ResultSet

import com.atguigu.realtime.constant.RealtimeConstant
import com.atguigu.realtime.util.{DruidUtil, MyKafkaUtil, MySQLUtil}
import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}

class RegisterStatsServer {

  /**
    * 需求1：实时统计注册人数，批次为3秒一批，使用updateStateBykey算子计算历史数据和当前批次的数据总数，
    * 仅此需求使用updateStateBykey，后续需求不使用updateStateBykey。
    *
    * updateStateByKey: 必须使用checkpoint, 会产生大量的小文件
    *
    * @param ssc
    */
  def registerCountWithHistory(ssc: StreamingContext): Unit = {
    ssc.sparkContext.setCheckpointDir("checkpoint")
    val groupId = "register_consumer_group1"
    val kafkaInputDStream = MyKafkaUtil.getKafkaDStream(ssc, List(RealtimeConstant.KAFKA_REGISTER_LOG_TOPIC), "groupId")

    val registerCountByPlatformDStream = kafkaInputDStream
      .filter(_.value().split("\\t").size == 3)
      .map(item => {
        val dataArray: Array[String] = item.value().split("\\t")
        dataArray(1) match {
          case "1" => ("PC", 1L)
          case "2" => ("APP", 1L)
          case _ => ("Other", 1L)
        }
      })
      .reduceByKey(_ + _)

    registerCountByPlatformDStream.foreachRDD(rdd => {
      println("本批次各平台注册用户数:")
      rdd.foreach(println)
    })

    val allRegisterDStream = registerCountByPlatformDStream.updateStateByKey[Long]((seq: Seq[Long], buffer: Option[Long]) => {
      val sum = seq.sum + buffer.getOrElse(0L)
      Option(sum)
    })

    allRegisterDStream.foreachRDD(rdd => {
      println("注册用户总数:")
      rdd.foreach(println)
    })

    ssc.start()
    ssc.awaitTermination()
  }

  /**
    * 需求2：每6秒统统计一次1分钟内的注册数据，不需要历史数据 提示:reduceByKeyAndWindow算子
    *
    * @param ssc
    */
  def registerCount(ssc: StreamingContext): Unit = {
    val groupId = "register_consumer_group1"
    val kafkaInputDStream = MyKafkaUtil.getKafkaDStream(ssc, List(RealtimeConstant.KAFKA_REGISTER_LOG_TOPIC), groupId)

    kafkaInputDStream.filter(_.value().split("\\t").size == 3)
      .repartition(6)
      .map(item => {
        val dataArray: Array[String] = item.value().split("\\t")
        dataArray(1) match {
          case "1" => ("PC", 1L)
          case "2" => ("APP", 1L)
          case _ => ("Other", 1L)
        }
      })
      .reduceByKeyAndWindow((x: Long, y: Long) => x + y, Seconds(60), Seconds(6))
      .foreachRDD(rdd => {
        println("本批次各平台注册人数:")
        rdd.collect().foreach(println)
      })
  }

  /**
    * 手动维护offset
    *
    * @param ssc
    */
  def registerCountWithOffset(ssc: StreamingContext): Unit = {
    val groupId = "register_consumer_group2"
    val topic = RealtimeConstant.KAFKA_REGISTER_LOG_TOPIC
    val offsetMap = scala.collection.mutable.Map[TopicPartition, Long]()
    val sqlUtil = new MySQLUtil()
    val conn = DruidUtil.getConnection()
    // 查询mysql中历史offset
    try {
      val offsetSql = s"select * from topic_manager where group_id = ? and topic_name = ?"
      val rs: ResultSet = sqlUtil.selectQuery(conn, offsetSql, Array(groupId, topic))
      while (rs.next()) {
        val model = new TopicPartition(rs.getString(2), rs.getInt(3))
        val offset = rs.getLong(4)
        offsetMap.put(model, offset)
      }
      rs.close()
    } catch {
      case e: Exception => e.printStackTrace()
        sqlUtil.shutdown(conn)
    }
    offsetMap.foreach(println)

    // 判断是否有历史的offset, 获取Kafka数据
    val kafkaInputDStream =
      if (offsetMap.isEmpty) MyKafkaUtil.getKafkaDStream(ssc, List(topic), groupId)
      else MyKafkaUtil.getKafkaDStream(ssc, List(topic), groupId, offsetMap)

    // 统计本批次的数据
    val resultDStream: DStream[(String, Long)] = kafkaInputDStream
      .filter(_.value().split("\\t").size == 3)
      .map(item => {
        val dataArray: Array[String] = item.value().split("\\t")
        dataArray(1) match {
          case "1" => ("PC", 1L)
          case "2" => ("APP", 1L)
          case _ => ("Other", 1L)
        }
      })
      .reduceByKey(_ + _)
    resultDStream.cache()
    resultDStream.foreachRDD(rdd => {
      println("本批次各平台注册用户数:" + rdd.collect().toMap)
    })

    // 将本批次数据累加到历史数据中 更新Mysql上的数据
    resultDStream.foreachRDD(_.foreachPartition(partition => {
      val sqlUtil = new MySQLUtil
      val conn = DruidUtil.getConnection()
      val countMap = scala.collection.mutable.Map[String, Long]()

      try {
        // 查询mysql中的历史统计数据
        val sql = "select * from register_count where group_id = ?"
        val rs: ResultSet = sqlUtil.selectQuery(conn, sql, Array(groupId))
        while (rs.next()) {
          countMap.put(rs.getString(2), rs.getLong(3))
        }
        rs.close()
        // 更新Mysql上的数据
        partition.foreach(item => {
          countMap.update(item._1, countMap.getOrElse(item._1, 0L) + item._2)

          val sql = "insert into register_count(group_id, platform, `count`) values(?, ?, ?)" +
            " on duplicate key update `count` = ?"
          countMap.foreach {
            case (platform, count) =>
              sqlUtil.executeUpdate(conn, sql, Array(groupId, platform, count, count))
          }
        })
      } catch {
        case e: Exception => e.printStackTrace()
      } finally {
        sqlUtil.shutdown(conn)
      }
      println("各平台注册用户总数:" + countMap)
    }))

    // 维护offset  kafka的输入流
    kafkaInputDStream.foreachRDD(rdd => {
      val sqlUtil = new MySQLUtil
      val conn = DruidUtil.getConnection()
      val sql = "insert into topic_manager(group_id, topic_name, partition_id, partition_offset) values(?, ?, ?, ?)" +
        " on duplicate key update partition_offset = ?"
      try {
        // rdd转为OffsetRange类获取offset数据
        val offsetRanges: Array[OffsetRange] = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        offsetRanges.foreach(or => {
          sqlUtil.executeUpdate(conn, sql, Array(groupId, or.topic, or.partition, or.untilOffset, or.untilOffset))
        })
      } catch {
        case e: Exception => e.printStackTrace()
      } finally {
        sqlUtil.shutdown(conn)
      }
    })
  }

}
