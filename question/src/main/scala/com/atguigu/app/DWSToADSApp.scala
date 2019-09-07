package com.atguigu.app

import com.atguigu.server.{DWSToADSApi, DWSToADSSql}
import com.atguigu.util.MySparkUtil
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object DWSToADSApp {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("dws_wide_table").setMaster("local[*]")
    val spark: SparkSession = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()

    MySparkUtil.openDynamicPartition(spark)

//    DWSToADSSql.insertADS(spark, args(0))
    DWSToADSApi.insertADS(spark, "20190722")
    spark.close()
  }
}
