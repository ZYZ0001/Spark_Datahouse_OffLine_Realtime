package com.atguigu.app

import com.atguigu.server.DWDToDWS
import com.atguigu.util.MySparkUtil
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object DWDToDWSApp {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("dwd_dws").setMaster("local[*]")
    val spark: SparkSession = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()

    MySparkUtil.hiveConfSetSmallFile(spark)
    MySparkUtil.hiveConfSetCompress(spark)
    MySparkUtil.openDynamicPartition(spark)

    DWDToDWS.insertToDWS(spark, "20190722")

    spark.close()
  }
}
