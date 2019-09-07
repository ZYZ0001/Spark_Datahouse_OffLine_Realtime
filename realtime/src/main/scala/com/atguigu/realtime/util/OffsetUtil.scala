package com.atguigu.realtime.util

import java.sql.ResultSet

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}

class OffsetUtil(groupId: String) {

  /**
    * 获取历史offset数据
    *
    * @return
    */
  def getOffsetMap(): collection.mutable.Map[TopicPartition, Long] = {
    val sqlUtil = new MySQLUtil()
    val conn = DruidUtil.getConnection()
    val offsetMap = collection.mutable.Map[TopicPartition, Long]()
    // 获取历史offset
    try {
      val sql = "select * from topic_manager where group_id = ?"
      val rs: ResultSet = sqlUtil.selectQuery(conn, sql, Array(groupId))
      while (rs.next()) {
        offsetMap.put(new TopicPartition(rs.getString(2), rs.getInt(3)), rs.getLong(4))
      }
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      sqlUtil.shutdown(conn)
    }
    offsetMap
  }

  /**
    * 更新Mysql上的offset数据
    *
    * @param kafkaInputDStream
    */
  def updateOffset(kafkaInputDStream: InputDStream[ConsumerRecord[String, String]]): Unit = {
    kafkaInputDStream.foreachRDD(rdd => {
      val sqlUtil = new MySQLUtil()
      val conn = DruidUtil.getConnection()
      try {
        val sql = "replace into topic_manager(group_id, topic_name, partition_id, partition_offset) values(?, ?, ?, ?)"
        val offsetRanges: Array[OffsetRange] = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        offsetRanges.foreach(or => {
          sqlUtil.executeUpdate(conn, sql, Array(groupId, or.topic, or.partition, or.untilOffset))
        })
      } catch {
        case e: Exception => e.printStackTrace()
      } finally {
        sqlUtil.shutdown(conn)
      }
    })
  }
}
