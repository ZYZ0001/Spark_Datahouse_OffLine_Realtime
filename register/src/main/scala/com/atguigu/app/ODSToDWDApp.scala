package com.atguigu.app

import com.atguigu.server.ODSToDWD
import com.atguigu.util.MySparkUtil
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object ODSToDWDApp {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("ods_dwd").setMaster("yarn-cluster")
    val spark: SparkSession = SparkSession.builder().enableHiveSupport().config(conf).getOrCreate()

    // 调优小文件, 支持压缩
    MySparkUtil.hiveConfSetSmallFile(spark)
    MySparkUtil.hiveConfSetCompress(spark)
    MySparkUtil.openDynamicPartition(spark)

    ODSToDWD.insetToDWD(spark)

    spark.close()
  }
}
