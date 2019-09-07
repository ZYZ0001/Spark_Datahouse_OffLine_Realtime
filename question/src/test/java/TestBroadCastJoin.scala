import com.atguigu.bean.DWSUserPaperDetail
import com.atguigu.util.MySparkUtil
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession}
import org.apache.spark.storage.StorageLevel

object TestBroadCastJoin {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("TestBroadCastJoin").setMaster("local[*]")
//    conf.registerKryoClasses(Array(classOf[DWSUserPaperDetail]))
//      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val spark: SparkSession = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()
    import spark.implicits._
    MySparkUtil.openDynamicPartition(spark)

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
         |where t1.dt = '20190722'
      """.stripMargin
    val result = spark.sql(UserPaperDetailSql).as[DWSUserPaperDetail].persist(StorageLevel.MEMORY_ONLY_SER)
//      .cache()
//    val resultRDD = result.rdd
//    resultRDD.persist(StorageLevel.MEMORY_ONLY_SER)
//    resultRDD.map((_, 1)).foreach(println(_))
    result.map((_, 1)).foreach(println(_))
  }
}
