package com.atguigu.server

import org.apache.spark.sql.{SaveMode, SparkSession}

object DWDToDWS {
  def insertToDWS(spark: SparkSession, time: String): Unit = {

    // TODO dws_qz_chapter(章节维度表)
    println("insert into dws_qz_chapter")
    val chapterSql =
      s"""
         |select
         |  t1.chapterid,
         |  t1.chapterlistid,
         |  t1.chaptername,
         |  t1.sequence,
         |  t1.showstatus,
         |  t2.status,
         |  t1.creator chapter_creator,
         |  t1.createtime chapter_createtime,
         |  t1.courseid chapter_courseid,
         |  t1.chapternum,
         |  t2.chapterallnum,
         |  t1.outchapterid,
         |  t2.chapterlistname,
         |  t3.pointid,
         |  t4.questionid,
         |  t4.questype,
         |  t3.pointname,
         |  t3.pointyear,
         |  t3.chapter,
         |  t3.excisenum,
         |  t3.pointlistid,
         |  t3.pointdescribe,
         |  t3.pointlevel,
         |  t3.typelist,
         |  t3.score point_score,
         |  t3.thought,
         |  t3.remid,
         |  t3.pointnamelist,
         |  t3.typelistids,
         |  t3.pointlist,
         |  t1.dt,
         |  t1.dn
         |from dwd.dwd_qz_chapter t1
         |join dwd.dwd_qz_chapter_list t2 on t1.chapterlistid = t2.chapterlistid and t1.dn = t2.dn
         |join dwd.dwd_qz_point t3 on t1.chapterid = t3.chapterid and t1.dn = t3.dn
         |join dwd.dwd_qz_point_question t4 on t3.pointid = t4.pointid and t3.dn = t4.dn
         |where t1.dt = ${time}
      """.stripMargin
    spark.sql(chapterSql).coalesce(2)
      .write.mode(SaveMode.Overwrite).insertInto("dws.dws_qz_chapter")


    // TODO dws_qz_course（课程维度表）
    println("insert into dws_qz_course")
    val courseSql =
      s"""
         |select
         |    t1.sitecourseid,
         |    t1.siteid,
         |    t1.courseid,
         |    t1.sitecoursename,
         |    t1.coursechapter,
         |    t1.sequence,
         |    t1.status,
         |    t1.creator sitecourse_creator,
         |    t1.createtime sitecourse_createtime,
         |    t1.helpparperstatus helppaperstatus,
         |    t1.servertype,
         |    t1.boardid,
         |    t1.showstatus,
         |    t2.majorid,
         |    t2.coursename,
         |    t2.isadvc,
         |    t2.chapterlistid,
         |    t2.pointlistid,
         |    t3.courseeduid,
         |    t3.edusubjectid,
         |    t1.dt,
         |    t1.dn
         |from dwd.dwd_qz_site_course t1
         |join dwd.dwd_qz_course t2 on t1.courseid = t2.courseid and t1.dn = t2.dn
         |join dwd.dwd_qz_course_edusubject t3 on t1.courseid = t3.courseid and t1.dn = t3.dn
         |where t1.dt = ${time}
      """.stripMargin
    spark.sql(courseSql).coalesce(2)
      .write.mode(SaveMode.Overwrite).insertInto("dws.dws_qz_course")

    // TODO dws_qz_major(主修维度表)
    println("insert into dws_qz_major")
    val majorSql =
      s"""
         |select
         |    t1.majorid,
         |    t1.businessid,
         |    t1.siteid,
         |    t1.majorname,
         |    t1.shortname,
         |    t1.status,
         |    t1.sequence,
         |    t1.creator major_creator,
         |    t1.createtime major_createtime,
         |    t3.businessname,
         |    t2.sitename,
         |    t2.domain,
         |    t2.multicastserver,
         |    t2.templateserver,
         |    t2.multicastgateway,
         |    t2.multicastport,
         |    t1.dt,
         |    t1.dn
         |from dwd.dwd_qz_major t1
         |join dwd.dwd_qz_website t2 on t1.siteid = t2.siteid and t1.dn = t2.dn
         |join dwd.dwd_qz_business t3 on t1.businessid = t3.businessid and t1.dn = t3.dn
         |where t1.dt = ${time}
      """.stripMargin
    spark.sql(majorSql).coalesce(2)
//      .write.mode(SaveMode.Overwrite).insertInto("dws.dws_qz_major")

    // TODO dws_qz_paper(试卷维度表)
    println("insert into dws_qz_paper")
    val paperSql =
      s"""
         |select
         |    t1.paperviewid,
         |    t1.paperid,
         |    t1.paperviewname,
         |    t1.paperparam,
         |    t1.openstatus,
         |    t1.explainurl,
         |    t1.iscontest,
         |    t1.contesttime,
         |    t1.conteststarttime,
         |    t1.contestendtime,
         |    t1.contesttimelimit,
         |    t1.dayiid,
         |    t1.status,
         |    t1.creator paper_view_creator,
         |    t1.createtime paper_view_createtime,
         |    t1.paperviewcatid,
         |    t1.modifystatus,
         |    t1.description,
         |    t1.paperuse,
         |    t1.paperdifficult,
         |    t1.testreport,
         |    t1.paperuseshow,
         |    t2.centerid,
         |    t2.sequence,
         |    t3.centername,
         |    t3.centeryear,
         |    t3.centertype,
         |    t3.provideuser,
         |    t3.centerviewtype,
         |    t3.stage,
         |    t4.papercatid,
         |    t4.courseid,
         |    t4.paperyear,
         |    t4.suitnum,
         |    t4.papername,
         |    t4.totalscore,
         |    t4.chapterid,
         |    t4.chapterlistid,
         |    t1.dt,
         |    t1.dn
         |from dwd.dwd_qz_paper_view t1
         |left join dwd.dwd_qz_center_paper t2 on t1.paperviewid = t2.paperviewid and t1.dn = t2.dn
         |left join dwd.dwd_qz_center t3 on t2.centerid = t3.centerid and t2.dn = t3.dn
         |inner join dwd.dwd_qz_paper t4 on t1.paperid = t4.paperid and t1.dn = t4.dn
         |where t1.dt = ${time}
      """.stripMargin
    spark.sql(paperSql).repartition(8)
      .write.mode(SaveMode.Overwrite).insertInto("dws.dws_qz_paper")

    // TODO dws_qz_question(题目维度表)
    println("insert into dws_qz_question")
    val questionSql =
      s"""
         |select
         |    t1.questionid,
         |    t1.parentid,
         |    t1.questypeid,
         |    t1.quesviewtype,
         |    t1.content,
         |    t1.answer,
         |    t1.analysis,
         |    t1.limitminute,
         |    t1.score,
         |    t1.splitscore,
         |    t1.status,
         |    t1.optnum,
         |    t1.lecture,
         |    t1.creator,
         |    t1.createtime,
         |    t1.modifystatus,
         |    t1.attanswer,
         |    t1.questag,
         |    t1.vanalysisaddr,
         |    t1.difficulty,
         |    t1.quesskill,
         |    t1.vdeoaddr,
         |    t2.viewtypename,
         |    t2.description,
         |    t2.papertypename,
         |    t2.splitscoretype,
         |    t1.dt,
         |    t1.dn
         |from dwd.dwd_qz_question t1
         |join dwd.dwd_qz_question_type t2 on t1.questypeid = t2.questypeid and t1.dn = t2.dn
         |where t1.dt = ${time}
      """.stripMargin
    spark.sql(questionSql).coalesce(2)
      .write.mode(SaveMode.Overwrite).insertInto("dws.dws_qz_question")

    // TODO dws_user_paper_detail 用户试卷详情宽表
    println("insert into dws_user_paper_detail")
    val UserPaperDetailSql =
      s"""
         |select
         |    t1.userid,
         |    t3.courseid,
         |    t1.questionid,
         |    t1.useranswer,
         |    t1.istrue,
         |    t1.lasttime,
         |    t1.opertype,
         |    t1.paperid,
         |    t1.spendtime,
         |    t1.chapterid,
         |    t2.chaptername,
         |    t2.chapternum,
         |    t2.chapterallnum,
         |    t2.outchapterid,
         |    t2.chapterlistname,
         |    t2.pointid,
         |    t2.questype,
         |    t2.pointyear,
         |    t2.chapter,
         |    t2.pointname,
         |    t2.excisenum,
         |    t2.pointdescribe,
         |    t2.pointlevel,
         |    t2.typelist,
         |    t2.point_score,
         |    t2.thought,
         |    t2.remid,
         |    t2.pointnamelist,
         |    t2.typelistids,
         |    t2.pointlist,
         |    t3.sitecourseid,
         |    t3.siteid,
         |    t3.sitecoursename,
         |    t3.coursechapter,
         |    t3.sequence course_sequence,
         |    t3.status course_stauts,
         |    t3.sitecourse_creator course_creator,
         |    t3.sitecourse_createtime course_createtime,
         |    t3.servertype,
         |    t3.helppaperstatus,
         |    t3.boardid,
         |    t3.showstatus,
         |    t3.majorid,
         |    t3.coursename,
         |    t3.isadvc,
         |    t3.chapterlistid,
         |    t3.pointlistid,
         |    t3.courseeduid,
         |    t3.edusubjectid,
         |    t4.businessid,
         |    t4.majorname,
         |    t4.shortname,
         |    t4.status major_status,
         |    t4.sequence major_sequence,
         |    t4.major_creator,
         |    t4.major_createtime,
         |    t4.businessname,
         |    t4.sitename,
         |    t4.domain,
         |    t4.multicastserver,
         |    t4.templateserver,
         |    t4.multicastgateway multicastgatway,
         |    t4.multicastport,
         |    t5.paperviewid,
         |    t5.paperviewname,
         |    t5.paperparam,
         |    t5.openstatus,
         |    t5.explainurl,
         |    t5.iscontest,
         |    t5.contesttime,
         |    t5.conteststarttime,
         |    t5.contestendtime,
         |    t5.contesttimelimit,
         |    t5.dayiid,
         |    t5.status paper_status,
         |    t5.paper_view_creator,
         |    t5.paper_view_createtime,
         |    t5.paperviewcatid,
         |    t5.modifystatus,
         |    t5.description,
         |    t5.paperuse,
         |    t5.testreport,
         |    t5.centerid,
         |    t5.sequence paper_sequence,
         |    t5.centername,
         |    t5.centeryear,
         |    t5.centertype,
         |    t5.provideuser,
         |    t5.centerviewtype,
         |    t5.stage paper_stage,
         |    t5.papercatid,
         |    t5.paperyear,
         |    t5.suitnum,
         |    t5.papername,
         |    t5.totalscore,
         |    t6.parentid question_parentid,
         |    t6.questypeid,
         |    t6.quesviewtype,
         |    t6.content question_content,
         |    t6.answer question_answer,
         |    t6.analysis question_analysis,
         |    t6.limitminute question_limitminute,
         |    t6.score,
         |    t6.splitscore,
         |    t6.lecture,
         |    t6.creator question_creator,
         |    t6.createtime question_createtime,
         |    t6.modifystatus question_modifystatus,
         |    t6.attanswer question_attanswer,
         |    t6.questag question_questag,
         |    t6.vanalysisaddr question_vanalysisaddr,
         |    t6.difficulty question_difficulty,
         |    t6.quesskill,
         |    t6.vdeoaddr,
         |    t6.description question_description,
         |    t6.splitscoretype question_splitscoretype,
         |    t1.question_answer user_question_answer,
         |    t1.dt,
         |    t1.dn
         |from dwd.dwd_qz_member_paper_question t1
         |join dws.dws_qz_chapter t2 on t1.chapterid = t2.chapterid and t1.dn = t2.dn
         |join dws.dws_qz_course t3 on t1.sitecourseid = t3.sitecourseid and t1.dn = t3.dn
         |join dws.dws_qz_major t4 on t1.majorid = t4.majorid and t1.dn = t4.dn
         |join dws.dws_qz_paper t5 on t1.paperviewid = t5.paperviewid and t1.dn = t5.dn
         |join dws.dws_qz_question t6 on t1.questionid = t6.questionid and t1.dn = t6.dn
         |where t1.dt = ${time}
      """.stripMargin
    spark.sql(UserPaperDetailSql).repartition(8)
      .write.mode(SaveMode.Overwrite).insertInto("dws.dws_user_paper_detail")
  }
}
