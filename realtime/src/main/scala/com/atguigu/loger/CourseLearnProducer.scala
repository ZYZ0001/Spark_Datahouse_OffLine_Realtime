package com.atguigu.loger

import java.util.Properties

import com.atguigu.realtime.constant.RealtimeConstant
import com.atguigu.realtime.util.MyPropertiesUtil
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.{SparkConf, SparkContext}

object CourseLearnProducer {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("CourseLearnProducer").setMaster("local[*]")
    val ssc = new SparkContext(conf)
    ssc.textFile("E:\\BigDate\\39.在线教育项目\\2.资料\\日志数据\\course_learn.log", 10)
      .foreachPartition(par => {
        val properties: Properties = MyPropertiesUtil.getProperties("kafka_producer.properties")
        val producer = new KafkaProducer[String, String](properties)
        par.foreach(item => {
          val request = new ProducerRecord[String, String](RealtimeConstant.KAFKA_COURSE_LEARN_LOG_TOPIC, item)
          producer.send(request)
        })
        producer.flush()
        producer.close()
      })
  }
}
