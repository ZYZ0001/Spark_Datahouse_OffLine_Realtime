package com.atguigu.server

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window

object DWSToADSApi {

  def insertADS(spark: SparkSession, dt: String): Unit = {
    // 导入function包
    import org.apache.spark.sql.functions._

    // TODO 统计各试卷平均耗时、平均分 ads_paper_avgtimeandscore
    spark.sql("select paperviewid, paperviewname, score, spendtime, dt, dn from dws.dws_user_paper_detail")
      .where(s"dt=${dt}")
      .groupBy("paperviewid", "paperviewname", "dt", "dn")
      .agg(avg("score").cast("decimal(4,1)").as("avgscore"),
        avg("spendtime").cast("decimal(10,1)").as("avgspendtime"))
      .select("paperviewid", "paperviewname", "avgscore", "avgspendtime", "dt", "dn")
    //      .coalesce(1).write.mode(SaveMode.Append).insertInto("ads.ads_paper_avgtimeandscore")

    // TODO 统计各试卷最高分、最低分 ads_paper_maxdetail
    spark.sql("select paperviewid, paperviewname, score, dt, dn from dws.dws_user_paper_detail")
      .where(s"dt=${dt}")
      .groupBy("paperviewid", "paperviewname", "dt", "dn")
      .agg(max("score").as("maxscore"),
        min("score").as("minscore"))
      .select("paperviewid", "paperviewname", "maxscore", "minscore", "dt", "dn")

    // TODO 按试卷分组统计每份试卷的前三用户详情 ads_top3_userdetail
    spark.sql("select userid, paperviewid, paperviewname, chaptername, pointname, sitecoursename, majorname, shortname, papername, score, dt, dn" +
      " from dws.dws_user_paper_detail")
      .where(s"dt=$dt")
      .withColumn("rk", dense_rank() over (Window.partitionBy("paperviewid").orderBy(desc("score"))))
      .where("rk<=3")
      .select("userid", "paperviewid", "paperviewname", "chaptername", "pointname",
        "sitecoursename", "majorname", "shortname", "papername", "score", "rk", "dt", "dn")
      .orderBy("paperviewid")

    // TODO 按试卷分组统计每份试卷的倒数前三的用户详情 ads_low3_userdetail
    spark.sql("select userid, paperviewid, paperviewname, chaptername, pointname, sitecoursename, majorname, shortname, papername, score, dt, dn" +
      " from dws.dws_user_paper_detail")
      .where(s"dt=$dt")
      .withColumn("rk", dense_rank() over (Window.partitionBy("paperviewid").orderBy("score")))
      .where("rk<=3")
      .select("userid", "paperviewid", "paperviewname", "chaptername", "pointname",
        "sitecoursename", "majorname", "shortname", "papername", "score", "rk", "dt", "dn")
      .orderBy("paperviewid")

    // TODO 统计各试卷各分段的用户id，分段有0-20,20-40,40-60，60-80,80-100 ads_paper_scoresegment_user
    spark.sql("select paperviewid, paperviewname, score, userid, dt, dn from dws.dws_user_paper_detail")
      .where(s"dt=$dt")
      .withColumn("score_segment", when(col("score").between(0, 20), "0-20")
        .when(col("score") > 20 && col("score") <= 40, "20-40")
        .when(col("score") > 40 && col("score") <= 60, "40-60")
        .when(col("score") > 60 && col("score") <= 80, "60-80")
        .when(col("score") > 80 && col("score") <= 100, "80-100"))
      .drop("score")  //删除指定字段节省空间
      .groupBy("paperviewid", "paperviewname", "score_segment", "dt", "dn")
      .agg(concat_ws(",", collect_set(col("userid").cast("string"))).as("userids"))
      .select("paperviewid", "paperviewname", "score_segment", "userids", "dt", "dn")
      .orderBy("paperviewid", "score_segment")

    // TODO 统计试卷未及格的人数，及格的人数，试卷的及格率 及格分数60 ads_user_paper_detail
    spark.sql("select paperviewid, paperviewname, score, dt, dn from dws.dws_user_paper_detail")
      .where(s"dt=$dt")
      .withColumn("unpass", when(col("score") < 60, 1))
      .withColumn("pass", when(col("score") >= 60, 1))
      .drop("score")  //删除指定字段节省空间
      .groupBy("paperviewid", "paperviewname", "dt", "dn")
      .agg(sum("unpass").as("unpasscount"),
        sum("pass").as("passcount"))
      .withColumn("rate", (col("passcount") / (col("passcount") + col("unpasscount"))).cast("decimal(4, 2)"))
      .select("paperviewid", "paperviewname", "unpasscount", "passcount", "rate", "dt", "dn")
      .orderBy("paperviewid")

    // TODO 统计各题的错误数，正确数，错题率 ads_user_question_detail
    spark.sql("select questionid, user_question_answer, dt, dn from dws.dws_user_paper_detail")
      .where(s"dt=$dt")
      .withColumn("err", when(col("user_question_answer") === 0, 1))
      .withColumn("right", when(col("user_question_answer") === 1, 1))
      .drop("user_question_answer")  //删除指定字段节省空间
      .groupBy("questionid", "dt", "dn")
      .agg(sum("err").as("errcount"), sum("right").as("rightcount"))
      .withColumn("rate", (col("rightcount") / (col("rightcount") + col("errcount"))).cast("decimal(4, 2)"))
      .select("questionid", "errcount", "rightcount", "rate", "dt", "dn")
      .orderBy("questionid").show()
  }
}
