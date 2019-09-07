package com.atguigu.app

import com.atguigu.server.ODSToDWD
import com.atguigu.util.MySparkUtil
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object ODSToDWDApp {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("osd_dwd").setMaster("local[*]")
    val spark: SparkSession = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()

    MySparkUtil.hiveConfSetSmallFile(spark)
    MySparkUtil.hiveConfSetCompress(spark)
    MySparkUtil.openDynamicPartition(spark)

    ODSToDWD.insertToDWD(spark)

    spark.close()
  }
}
