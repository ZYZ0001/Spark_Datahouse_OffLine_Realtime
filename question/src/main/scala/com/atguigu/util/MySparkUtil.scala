package com.atguigu.util

import com.alibaba.fastjson.JSON
import org.apache.spark.sql.SparkSession

object MySparkUtil {
  /**
    * 调优小文件
    * @param spark
    */
  def hiveConfSetSmallFile(spark: SparkSession): Unit = {
    spark.sql("set hive.merge.mapfiles=true")
    spark.sql("set hive.merge.mapredfiles=true")
    spark.sql("set hive.merge.size.per.task=256000000")
    spark.sql("set hive.merge.smallfiles.avgsize=256000000")
    spark.sql("set mapred.max.split.size=256000000")
    spark.sql("set mapred.min.split.size.per.node=128000000")
    spark.sql("set mapred.min.split.size.per.rack=128000000")
    spark.sql("set hive.hadoop.supports.splittable.combineinputformat=true")
    spark.sql("set hive.input.format=org.apache.hadoop.hive.ql.io.CombineHiveInputFormat")
    spark.sql("set hive.exec.compress.output=true")
    spark.sql("set mapred.output.compression.codec=org.apache.hadoop.io.compress.SnappyCodec")
    spark.sql("set mapred.output.compression.type=BLOCK")
    spark.sql("set hive.exec.compress.intermediate=true")
    spark.sql("set hive.intermediate.compression.codec=org.apache.hadoop.io.compress.SnappyCodec")
    spark.sql("set hive.intermediate.compression.type=BLOCK")
    spark.sql("set hive.groupby.skewindata=true")

    spark.sql("set hive.exec.dynamic.partition.mode=nonstrict") //动态分区

    // 关闭类型自动推断
    spark.sparkContext.getConf.set("spark.sql.sources.partitionColumnTypeInference.enabled", "false")
  }

  /**
    * 开启动态分区
    * @param spark
    */
  def openDynamicPartition(spark: SparkSession): Unit = {
    // 动态分区
    spark.sql("set hive.exec.dynamic.partition.mode=nonstrict")

    // 关闭类型自动推断
    spark.sparkContext.getConf.set("spark.sql.sources.partitionColumnTypeInference.enabled", "false")
  }

  /**
    * 支持压缩
    * @param spark
    */
  def hiveConfSetCompress(spark: SparkSession): Unit = {
    spark.sql("set hive.exec.compress.output=true")
    spark.sql("set mapred.output.compression.codec=org.apache.hadoop.io.compress.SnappyCodec")
    spark.sql("set mapred.output.compression.type=BLOCK")
    spark.sql("set hive.exec.compress.intermediate=true")
    spark.sql("set hive.intermediate.compression.codec=org.apache.hadoop.io.compress.SnappyCodec")
    spark.sql("set hive.intermediate.compression.type=BLOCK")
    spark.sql("set hive.groupby.skewindata=true")

  }

  def isJson(string: String): Boolean = {
    try {
      JSON.parseObject(string)
      return true
    } catch {
      case e: Exception => return false
    }
  }
}
