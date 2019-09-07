package com.atguigu.server

import java.text.SimpleDateFormat

import com.atguigu.bean.MemberZipperTable
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}

object DWDToDWS {
  /**
    * 导入用户信息宽表
    *
    * @param spark
    */
  def insertWideTale(spark: SparkSession, time: String): Unit = {

    val sql =
      s"""
         |select
         |    t1.uid uid,
         |    first(t1.ad_id) ad_id,
         |    first(t1.fullname) fullname,
         |    first(t1.iconurl) iconurl,
         |    first(t1.lastlogin) lastlogin,
         |    first(t1.mailaddr) mailaddr,
         |    first(t1.memberlevel) memberlevel,
         |    first(t1.password) password,
         |    sum(cast(t5.paymoney as decimal(10, 4))) paymoney,
         |    first(t1.phone) phone,
         |    first(t1.qq) qq,
         |    first(t1.register) register,
         |    first(t1.regupdatetime) regupdatetime,
         |    first(t1.unitname) unitname,
         |    first(t1.userip) userip,
         |    first(t1.zipcode) zipcode,
         |    first(t2.appkey) appkey,
         |    first(t2.appregurl) appregurl,
         |    first(t2.bdp_uuid) bdp_uuid,
         |    first(t2.createtime) reg_createtime,
         |    first(t2.domain) domain,
         |    first(t2.isranreg) isranreg,
         |    first(t2.regsource) regsource,
         |    first(t2.regsourcename) regsourcename,
         |    first(t3.adname) adname,
         |    first(t4.siteid) siteid,
         |    first(t4.sitename) sitename,
         |    first(t4.siteurl) siteurl,
         |    first(t4.delete) site_delete,
         |    first(t4.createtime) site_createtime,
         |    first(t4.creator) site_creator,
         |    max(t6.vip_id) vip_id,
         |    case max(t6.vip_id) when 0 then '普通会员' when 1 then '白金' when 2 then '银卡' when 3 then '金卡' when 4 then '钻石' end vip_level,
         |    min(t6.start_time) vip_start_time,
         |    max(t6.end_time) vip_end_time,
         |    max(t6.last_modify_time) vip_last_modify_time,
         |    first(t6.max_free) vip_max_free,
         |    first(t6.min_free) vip_min_free,
         |    max(t6.next_level) vip_next_level,
         |    first(t6.operator) vip_operator,
         |    t1.dt dt,
         |    t1.dn dn
         |from dwd.dwd_member t1
         |left join dwd.dwd_member_regtype t2 on t1.uid = t2.uid and t1.dn = t2.dn
         |left join dwd.dwd_base_ad t3 on t1.ad_id = t3.adid and t1.dn = t3.dn
         |left join dwd.dwd_base_website t4 on t2.websiteid = t4.siteid and t2.dn = t4.dn
         |left join dwd.dwd_pcentermempaymoney t5 on t1.uid = t5.uid and t1.dn = t5.dn
         |left join dwd.dwd_vip_level t6 on t5.vip_id = t6.vip_id and t5.dn = t6.dn
         |where t1.dt = ${time}
         |group by t1.uid, t1.dt, t1.dn
      """.stripMargin

    spark.sql(sql).coalesce(2)
      .write.mode(SaveMode.Overwrite).insertInto("dws.dws_member")
  }

  /**
    * 导入用户信息拉链表
    *
    * @param spark
    */
  def insertZipperTale(spark: SparkSession, time: String): Unit = {
    import spark.implicits._
    //获取每日增量
    val sql =
      s"""
         |select
         |    uid,
         |    sum(cast(paymoney as decimal(10, 4))) paymoney,
         |    case max(vip_id) when 0 then '普通会员' when 1 then '白金' when 2 then '银卡' when 3 then '金卡' when 4 then '钻石' end vip_level,
         |    from_unixtime(unix_timestamp('${time}', 'yyyyMMdd'), 'yyyy-MM-dd') start_time,
         |    '9999-01-01' end_time,
         |    dn
         |from dwd.dwd_pcentermempaymoney
         |where dt = ${time} group by uid, dn
      """.stripMargin
    val newMemberZipper = spark.sql(sql).as[MemberZipperTable]
    //获取拉链表原始数据
    val oldMemberZipper = spark.sql("select * from dws.dws_member_zipper").as[MemberZipperTable]
    //union两表, 准备更新数据
    oldMemberZipper.union(newMemberZipper)
      .groupByKey(item => item.uid + "_" + item.dn)
      .mapGroups((_, memberZipperItr) => {
        val memberZipperList = memberZipperItr.toList.sortBy(_.start_time)
        if (memberZipperList.size >= 2 && "9999-01-01".equals(memberZipperList(memberZipperList.size - 2).end_time)) {
          // 如果存在历史数据, 修改历史数据, 并计算最新的paymoney
          val oldLastMember = memberZipperList(memberZipperList.size - 2)
          val newMember = memberZipperList(memberZipperList.size - 1)
          oldLastMember.end_time = newMember.start_time
          newMember.paymoney = (BigDecimal.apply(oldLastMember.paymoney) + BigDecimal.apply(newMember.paymoney)).toString()
        }
        memberZipperList
      })
      .flatMap(item => item)
      .coalesce(4)
      .write.mode(SaveMode.Overwrite).insertInto("dws.dws_member_zipper")
  }
}
