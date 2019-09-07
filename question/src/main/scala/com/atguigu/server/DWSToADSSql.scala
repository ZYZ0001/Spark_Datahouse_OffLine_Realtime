package com.atguigu.server

import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * 统计报表, 先使用Spark Sql 完成指标统计，再使用Spark DataFrame Api
  */
object DWSToADSSql {

  def insertADS(spark: SparkSession, time: String): Unit = {

    // TODO 统计各试卷平均耗时、平均分 ads_paper_avgtimeandscore
    println("insert into ads_paper_avgtimeandscore")
    val PaperAvgTimeAndScoreSql =
      s"""
         |select
         |  paperviewid,
         |  paperviewname,
         |  cast(avg(score) as decimal(4, 1)) avgscore,
         |  cast(avg(spendtime) as decimal(10, 2)) avgspendtime,
         |  dt, dn
         |from dws.dws_user_paper_detail
         |where dt = ${time}
         |group by paperviewid, paperviewname, dt, dn
      """.stripMargin
    spark.sql(PaperAvgTimeAndScoreSql)
      .coalesce(1).write.mode(SaveMode.Append).insertInto("ads.ads_paper_avgtimeandscore")

    // TODO 统计各试卷最高分、最低分 ads_paper_maxdetail
    println("insert into ads_paper_maxdetail")
    spark.sql(
      s"""
         |select
         |  paperviewid,
         |  paperviewname,
         |  max(score) maxscore,
         |  min(score) minscore,
         |  dt, dn
         |from dws.dws_user_paper_detail
         |where dt = ${time}
         |group by paperviewid, paperviewname, dt, dn
       """.stripMargin)
      .coalesce(1).write.mode(SaveMode.Append).insertInto("ads.ads_paper_maxdetail")

    // TODO 按试卷分组统计每份试卷的前三用户详情 ads_top3_userdetail
    println("insert into ads_top3_userdetail")
    spark.sql(
      s"""
         |select * from
         |(
         |  select
         |    userid,
         |    paperviewid,
         |    paperviewname,
         |    chaptername,
         |    pointname,
         |    sitecoursename,
         |    coursename,
         |    majorname,
         |    shortname,
         |    papername,
         |    score,
         |    dense_rank() over(partition by paperviewid order by score desc) rk,
         |    dt, dn
         |  from dws.dws_user_paper_detail
         |  where dt = ${time}
         |) t1
         |where rk <= 3
       """.stripMargin)
      .coalesce(1).write.mode(SaveMode.Append).insertInto("ads.ads_top3_userdetail")

    // TODO 按试卷分组统计每份试卷的倒数前三的用户详情 ads_low3_userdetail
    println("insert into ads_low3_userdetail")
    spark.sql(
      s"""
         |select * from
         |(
         |  select
         |    userid,
         |    paperviewid,
         |    paperviewname,
         |    chaptername,
         |    pointname,
         |    sitecoursename,
         |    coursename,
         |    majorname,
         |    shortname,
         |    papername,
         |    score,
         |    dense_rank() over(partition by paperviewid order by score) rk,
         |    dt, dn
         |  from dws.dws_user_paper_detail
         |  where dt = ${time}
         |) t1
         |where rk <= 3
       """.stripMargin)
      .coalesce(1).write.mode(SaveMode.Append).insertInto("ads.ads_low3_userdetail")

    // TODO 统计各试卷各分段的用户id，分段有0-20,20-40,40-60，60-80,80-100 ads_paper_scoresegment_user
    println("insert into ads_paper_scoresegment_user")
    spark.sql(
      s"""
         |select paperviewid, paperviewname, score_segment, concat_ws(',',collect_set(cast(userid as string))) as userids, dt, dn
         |from
         |(
         |  select
         |    paperviewid,
         |    paperviewname,
         |    case when score >=0 and score < 20 then '0-20'
         |         when score >=20 and score < 40 then '20-40'
         |         when score >=40 and score < 60 then '40-60'
         |         when score >=60 and score < 80 then '60-80'
         |         else '80-100' end as score_segment,
         |    userid,
         |    dt, dn
         |  from dws.dws_user_paper_detail
         |  where dt = ${time}
         |) t1
         |group by paperviewid, paperviewname, score_segment, dt, dn
         |order by paperviewid, score_segment
       """.stripMargin)
      .coalesce(1).write.mode(SaveMode.Append).insertInto("ads.ads_paper_scoresegment_user")

    // TODO 统计试卷未及格的人数，及格的人数，试卷的及格率 及格分数60 ads_user_paper_detail
    println("insert into ads_user_paper_detail")
    spark.sql(
      s"""
         |select paperviewid, paperviewname, unpasscount, passcount, cast(passcount/(passcount + unpasscount) as decimal(4,2)) rate, dt, dn
         |from (
         |  select
         |    paperviewid,
         |    paperviewname,
         |    sum(if(score < 60, 1, 0)) unpasscount,
         |    sum(if(score >= 60, 1, 0)) passcount,
         |    dt, dn
         |  from dws.dws_user_paper_detail
         |  where dt = ${time}
         |  group by paperviewid, paperviewname, dt, dn) t1
      """.stripMargin)
      .coalesce(1).write.mode(SaveMode.Append).insertInto("ads.ads_user_paper_detail")

    // TODO 统计各题的错误数，正确数，错题率 ads_user_question_detail
    println("insert into ads_user_question_detail")
    spark.sql(
      s"""
         |select questionid, errcount, rightcount, cast(rightcount/(rightcount + errcount) as decimal(4,2)) rate, dt, dn
         |from
         |(
         |  select
         |    questionid,
         |    sum(if(user_question_answer = 0, 1, 0)) errcount,
         |    sum(if(user_question_answer = 1, 1, 0)) rightcount,
         |    dt, dn
         |  from dws.dws_user_paper_detail
         |  where dt = ${time}
         |  group by questionid, dt, dn
         |) t1
       """.stripMargin)
      .coalesce(1).write.mode(SaveMode.Append).insertInto("ads.ads_user_question_detail")
  }
}
