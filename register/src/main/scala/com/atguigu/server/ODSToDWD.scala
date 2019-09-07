package com.atguigu.server

import com.alibaba.fastjson.JSON
import com.atguigu.bean._
import com.atguigu.util.MySparkUtil
import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * ods层数据导入dwd层
  */
object ODSToDWD {
  def insetToDWD(spark: SparkSession): Unit = {
    import spark.implicits._

    // 导入dwd_member表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/member.log")
      .filter(line => MySparkUtil.isJson(line))
      .map(line => {
        val member: Member = JSON.parseObject(line, classOf[Member])
        member.fullname = member.fullname.substring(0, 1) + "XX"
        member.phone = member.phone.substring(0, 3) + "*****" + member.phone.substring(8)
        member.password = "********"
        member
      })
      .toDF()
      .coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_member")

    // 导入dwd_member_regtype表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/memberRegtype.log")
      .filter(line => MySparkUtil.isJson(line))
      .map(line => {
        val memberRegtype: MemberRegtype = JSON.parseObject(line, classOf[MemberRegtype])
        memberRegtype.regsource match {
          case "1" => memberRegtype.regsourcename = "PC"
          case "2" => memberRegtype.regsourcename = "MOBILE"
          case "3" => memberRegtype.regsourcename = "APP"
          case "4" => memberRegtype.regsourcename = "WECHAT"
          case _ => memberRegtype.regsourcename = "-"
        }
        memberRegtype
      })
      .toDF()
      .coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_member_regtype")

    // 导入dwd_base_ad表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/baseadlog.log")
      .filter(line => MySparkUtil.isJson(line))
      .map(line => JSON.parseObject(line, classOf[BaseAd]))
      .toDF()
      .coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_base_ad")

    // 导入dwd_base_website表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/baswewebsite.log")
      .filter(line => MySparkUtil.isJson(line))
      .map(line => JSON.parseObject(line, classOf[BaseWebsite]))
      .toDF()
      .coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_base_website")

    // 导入dwd_pcentermempaymoney表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/pcentermempaymoney.log")
      .filter(line => MySparkUtil.isJson(line))
      .map(line => JSON.parseObject(line, classOf[PCEnterMemPayMoney]))
      .toDF()
      .coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_pcentermempaymoney")

    // 导入dwd_vip_level表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/pcenterMemViplevel.log")
      .filter(line => MySparkUtil.isJson(line))
      .map(line => JSON.parseObject(line, classOf[VipLevel]))
      .toDF()
      .coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_vip_level")

    spark.close()
  }
}
