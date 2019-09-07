package com.atguigu.realtime.util

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010._

object MyKafkaUtil {

  private val kafkaConsumerParam = Map(
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "hadoop102:9092,hadoop103:9092,hadoop104:9092",
    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
    //    ConsumerConfig.GROUP_ID_CONFIG -> "register",
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest", //起始位置读取数据
    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (false: java.lang.Boolean) //关闭自动提交
  )

  /**
    * 获取kafkaDStream从起始位置消费kafka数据
    *
    * @param topics: 可以同时消费多个topic
    * @param ssc
    * @return
    */
  def getKafkaDStream(ssc: StreamingContext, topics: List[String], groupId: String) = {
    val map = kafkaConsumerParam + (ConsumerConfig.GROUP_ID_CONFIG -> groupId)
    KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, map)
    )
  }

  /**
    * 获取kafkaDStream从指定offset位置消费kafka数据
    *
    * @param topic
    * @param ssc
    * @return
    */
  def getKafkaDStream(ssc: StreamingContext, topic: List[String], groupId: String, offsets: scala.collection.mutable.Map[TopicPartition, Long]) = {
    val map = kafkaConsumerParam + (ConsumerConfig.GROUP_ID_CONFIG -> groupId)
    KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topic, map, offsets)
    )
  }

}
