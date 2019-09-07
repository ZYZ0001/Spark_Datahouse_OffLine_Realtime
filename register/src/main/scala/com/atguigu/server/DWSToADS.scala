package com.atguigu.server

import com.atguigu.bean.MemberWideTable
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}

object DWSToADS {

  def insertADS(spark: SparkSession, time: String): Unit = {
    import spark.implicits._
    // 查询用户宽表数据
    val resultDS: Dataset[MemberWideTable] = spark.sql(s"select * from dws.dws_member where dt = ${time}").as[MemberWideTable].cache()

    // 统计通过各注册跳转地址(appregurl)进行注册的用户数
    resultDS.map(item => (item.appregurl + "_" + item.dt + "_" + item.dn, 1))
      .groupByKey(_._1)
      .mapValues(item => item._2)
      .reduceGroups(_ + _)
      .map(item => {
        val dataArr = item._1.split("_")
        (dataArr(0), item._2, dataArr(1), dataArr(2))
      }).toDF().coalesce(1)
          .write.mode(SaveMode.Append).insertInto("ads.ads_register_appregurlnum")

    // 统计各所属网站（sitename）的用户数
    resultDS.map(item => (item.sitename + "_" + item.dt + "_" + item.dn, 1))
      .groupByKey(_._1)
      .mapValues(_._2)
      .reduceGroups(_ + _)
      .map { case (key: String, count: Int) => {
        val dataArr = key.split("_")
        (dataArr(0), count, dataArr(1), dataArr(2))
      }
      }
      .toDF().coalesce(1)
      .write.mode(SaveMode.Append).insertInto("ads.ads_register_sitenamenum")

    // 统计各所属平台的（regsourcename）用户数
    resultDS.map(item => (item.regsourcename + "_" + item.dt + "_" + item.dn, 1))
      .groupByKey(_._1)
      .mapValues(_._2)
      .reduceGroups(_ + _)
      .map { case (key: String, count: Int) => {
        val dataArr = key.split("_")
        (dataArr(0), count, dataArr(1), dataArr(2))
      }
      }.toDF().coalesce(1)
      .write.mode(SaveMode.Append).insertInto("ads.ads_register_regsourcenamenum")

    // 统计通过各广告跳转（adname）的用户数
    resultDS.map(item => (item.adname + "_" + item.dt + "_" + item.dn, 1))
      .groupByKey(_._1)
      .mapValues(_._2)
      .reduceGroups(_ + _)
      .map { case (key: String, count: Int) => {
        val dataArr = key.split("_")
        (dataArr(0), count, dataArr(1), dataArr(2))
      }
      }.toDF().coalesce(1)
          .write.mode(SaveMode.Append).insertInto("ads.ads_register_adnamenum")

    // 统计各用户级别（memberlevel）的用户数
    resultDS.map(item => (item.memberlevel + "_" + item.dt + "_" + item.dn, 1))
      .groupByKey(_._1)
      .mapValues(_._2)
      .reduceGroups(_ + _)
      .map { case (key: String, count: Int) => {
        val dataArr = key.split("_")
        (dataArr(0), count, dataArr(1), dataArr(2))
      }
      }.toDF().coalesce(1)
          .write.mode(SaveMode.Append).insertInto("ads.ads_register_memberlevelnum")

    // 统计各vip等级人数
    resultDS.map(item => (item.vip_level + "_" + item.dt + "_" + item.dn, 1))
      .groupByKey(_._1)
      .mapValues(_._2)
      .reduceGroups(_ + _)
      .map { case (key: String, count: Int) => {
        val dataArr = key.split("_")
        (dataArr(0), count, dataArr(1), dataArr(2))
      }
      }.toDF().coalesce(1)
          .write.mode(SaveMode.Append).insertInto("ads.ads_register_viplevelnum")

    // 统计各分区网站、用户级别下(dn、memberlevel)的top3用户
    import org.apache.spark.sql.functions._  // 导入function包
    //resultDS.withColumn("rownum", lit("1111")) //固定添加添加一列
    resultDS.withColumn("rownum", row_number() over(Window.partitionBy("dn", "memberlevel").orderBy(desc("paymoney"))))
      .where("rownum < 4")
      .orderBy("memberlevel", "rownum")
      .select("uid", "memberlevel", "register", "appregurl", "regsourcename", "adname"
        , "sitename", "vip_level", "paymoney", "rownum", "dt", "dn")
      .coalesce(1)
          .write.mode(SaveMode.Append).insertInto("ads.ads_register_top3memberpay")
  }
}
