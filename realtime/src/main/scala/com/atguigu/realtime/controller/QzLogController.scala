package com.atguigu.realtime.controller

import com.atguigu.realtime.server.QzLogServer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object QzLogController {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("RegisterStats").setMaster("local[*]")
    conf.set("spark.streaming.kafka.maxRatePerPartition", "100") //定义消费速度
    conf.set("spark.streaming.backpressure.enabled", "true") //内部反压
    conf.set("spark.streaming.stopGracefullyOnShutdown", "true") //优雅关闭
//    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val ssc = new StreamingContext(conf, Seconds(3))
    val qzLogServer = new QzLogServer()
    qzLogServer.qzLogStats(ssc)
    ssc.start()
    ssc.awaitTermination()
  }
}
