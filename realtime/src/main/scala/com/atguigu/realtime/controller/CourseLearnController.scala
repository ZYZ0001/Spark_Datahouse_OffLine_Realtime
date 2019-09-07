package com.atguigu.realtime.controller

import com.atguigu.realtime.server.CourseLearnServer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object CourseLearnController {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("RegisterStats").setMaster("local[*]")
    conf.set("spark.streaming.kafka.maxRatePerPartition", "100") //定义消费速度
    conf.set("spark.streaming.backpressure.enabled", "true") //内部反压
    conf.set("spark.streaming.stopGracefullyOnShutdown", "true") //优雅关闭
    val ssc = new StreamingContext(conf, Seconds(3))
    val pageStatsServer = new CourseLearnServer()
    pageStatsServer.courserLearnStats(ssc)
    ssc.start()
    ssc.awaitTermination()
  }
}
