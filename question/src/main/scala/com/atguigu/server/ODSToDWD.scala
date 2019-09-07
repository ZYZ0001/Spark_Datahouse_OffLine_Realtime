package com.atguigu.server

import java.math.BigDecimal

import com.alibaba.fastjson.JSON
import com.atguigu.bean._
import com.atguigu.util.MySparkUtil
import org.apache.spark.sql.{SaveMode, SparkSession}

object ODSToDWD {

  /**
    * 将数据存入到对应的hive表中,要求对所有score 分数字段进行保留1位小数并且四舍五入。
    *
    * @param spark
    */
  def insertToDWD(spark: SparkSession): Unit = {
    import spark.implicits._

    // dwd_qz_chapter  章节数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzChapter.log")
      .filter(MySparkUtil.isJson(_))
      .map(JSON.parseObject(_, classOf[QzChapter]))
      .toDF().coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_qz_chapter")

    // dwd_qz_chapter_list  章节列表数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzChapterList.log")
      .filter(MySparkUtil.isJson(_))
      .map(JSON.parseObject(_, classOf[QzChapterList]))
      .toDF().coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_qz_chapter_list")

    // dwd_qz_point  知识点数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzPoint.log")
      .filter(MySparkUtil.isJson(_))
      .map(item => {
        val point: QzPoint = JSON.parseObject(item, classOf[QzPoint])
        point.score = new BigDecimal(point.score).setScale(1, BigDecimal.ROUND_HALF_UP).toString()
        point
      })
      .toDF().coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_qz_point")

    // dwd_qz_point_question  做题知识点关联数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzPointQuestion.log")
      .filter(MySparkUtil.isJson(_))
      .map(JSON.parseObject(_, classOf[QzPointQuestion]))
      .toDF().coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_qz_point_question")

    // dwd_qz_site_course  网站课程日志数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzSiteCourse.log")
      .filter(MySparkUtil.isJson(_))
      .map(JSON.parseObject(_, classOf[QzSiteCourse]))
      .toDF().coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_qz_site_course")

    // dwd_qz_course  题库课程数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzCourse.log")
      .filter(MySparkUtil.isJson(_))
      .map(JSON.parseObject(_, classOf[QzCourse]))
      .toDF().coalesce(1)
      .write.mode(SaveMode.Append).insertInto("dwd.dwd_qz_course")

    // dwd_qz_course_edusubject  课程辅导数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzCourseEduSubject.log")
      .filter(MySparkUtil.isJson(_))
      .map(JSON.parseObject(_, classOf[QzCourseEduSubject]))
      .toDF().coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_qz_course_edusubject")

    // dwd_qz_website  做题网站日志数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzWebsite.log")
      .filter(MySparkUtil.isJson(_))
      .map(JSON.parseObject(_, classOf[QzWebsite]))
      .toDF().coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_qz_website")

    // dwd_qz_major  主修数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzMajor.log")
      .filter(MySparkUtil.isJson(_))
      .map(JSON.parseObject(_, classOf[QzMajor]))
      .toDF().coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_qz_major")

    // dwd_qz_business  所属行业数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzBusiness.log")
      .filter(MySparkUtil.isJson(_))
      .map(JSON.parseObject(_, classOf[QzBusiness]))
      .toDF().coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_qz_business")

    // dwd_qz_paper_view  试卷视图数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzPaperView.log")
      .filter(MySparkUtil.isJson(_))
      .map(JSON.parseObject(_, classOf[QzPaperView]))
      .toDF().coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_qz_paper_view")

    // dwd_qz_center_paper  试卷主题关联数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzCenterPaper.log")
      .filter(MySparkUtil.isJson(_))
      .map(JSON.parseObject(_, classOf[QzCenterPaper]))
      .toDF().coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_qz_center_paper")

    // dwd_qz_paper  做题试卷日志数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzPaper.log")
      .filter(MySparkUtil.isJson(_))
      .map(item => {
        val paper: QzPaper = JSON.parseObject(item, classOf[QzPaper])
        paper.totalscore = new BigDecimal(paper.totalscore).setScale(1, BigDecimal.ROUND_HALF_UP).toString
        paper
      })
      .toDF().coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_qz_paper")

    // dwd_qz_center  主题数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzCenter.log")
      .filter(MySparkUtil.isJson(_))
      .map(JSON.parseObject(_, classOf[QzCenter]))
      .toDF().coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_qz_center")

    // dwd_qz_question  做题日志数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzQuestion.log")
      .filter(MySparkUtil.isJson(_))
      .map(item => {
        val question: QzQuestion = JSON.parseObject(item, classOf[QzQuestion])
        question.score = new BigDecimal(question.score).setScale(1, BigDecimal.ROUND_HALF_UP).toString
        question.splitscore = new BigDecimal(question.splitscore).setScale(1, BigDecimal.ROUND_HALF_UP).toString
        question
      })
      .toDF().coalesce(1)
      .write.mode(SaveMode.Append).insertInto("dwd.dwd_qz_question")

    // dwd_qz_question_type  题目类型数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzQuestionType.log")
      .filter(MySparkUtil.isJson(_))
      .map(JSON.parseObject(_, classOf[QzQuestionType]))
      .toDF().coalesce(1)
      .write.mode(SaveMode.Overwrite).insertInto("dwd.dwd_qz_question_type")

    // dwd_qz_member_paper_question  学员做题详情数据表
    spark.sparkContext.textFile("hdfs://hadoop102:9000/user/atguigu/ods/QzMemberPaperQuestion.log")
      .filter(MySparkUtil.isJson(_))
      .map(JSON.parseObject(_, classOf[QzMemberPaperQuestion]))
      .toDF().coalesce(1)
      .write.mode(SaveMode.Append).insertInto("dwd.dwd_qz_member_paper_question")
  }
}
