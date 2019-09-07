package com.atguigu.bean

/**
  * 章节数据
  *
  * @param chapterid     : 章节id
  * @param chapterlistid : 所属章节列表id
  * @param chaptername   : 章节名称
  * @param sequence
  * @param showstatus
  * @param creator       : 创建者
  * @param createtime    : 创建时间
  * @param courseid      : 课程id
  * @param chapternum    : 章节个数
  * @param outchapterid
  * @param dt            : 日期分区
  * @param dn            : 网站分区
  */
case class QzChapter(
                      chapterid: Int,
                      chapterlistid: Int,
                      chaptername: String,
                      sequence: String,
                      showstatus: String,
                      creator: String,
                      createtime: String,
                      courseid: Int,
                      chapternum: Int,
                      outchapterid: Int,
                      dt: String,
                      dn: String
                    )

/**
  * 章节列表数据
  *
  * @param chapterlistid   : 章节列表id
  * @param chapterlistname : 章节列表名称
  * @param courseid        : 课程id
  * @param chapterallnum   : 章节总个数
  * @param sequence
  * @param status
  * @param creator         : 创建者
  * @param createtime      : 创建时间
  * @param dt              : 日期分区
  * @param dn              : 网站分区
  */
case class QzChapterList(
                          chapterlistid: Int,
                          chapterlistname: String,
                          courseid: Int,
                          chapterallnum: Int,
                          sequence: String,
                          status: String,
                          creator: String,
                          createtime: String,
                          dt: String,
                          dn: String
                        )

/**
  * 知识点数据
  *
  * @param pointid     : 知识点id
  * @param courseid    : 课程id
  * @param pointname   : 知识点名称
  * @param pointyear   : 知识点所属年份
  * @param chapter     : 所属章节
  * @param creator     : 创建者
  * @param createtime  : 创建时间
  * @param status
  * @param modifystatus
  * @param excisenum
  * @param pointlistid : 知识点列表id
  * @param chapterid   : 章节id
  * @param sequence
  * @param pointdescribe
  * @param pointlevel  : 知识点级别
  * @param typelist
  * @param score       : 知识点分数
  * @param thought
  * @param remid
  * @param pointnamelist
  * @param typelistids
  * @param pointlist
  * @param dt          : 日期分区
  * @param dn          : 网站分区
  */
case class QzPoint(
                    pointid: Int,
                    courseid: Int,
                    pointname: String,
                    pointyear: String,
                    chapter: String,
                    creator: String,
                    createtime: String,
                    status: String,
                    modifystatus: String,
                    excisenum: Int,
                    pointlistid: Int,
                    chapterid: Int,
                    sequence: String,
                    pointdescribe: String,
                    pointlevel: String,
                    typelist: String,
                    var score: String,
                    thought: String,
                    remid: String,
                    pointnamelist: String,
                    typelistids: String,
                    pointlist: String,
                    dt: String,
                    dn: String
                  )

/**
  * 做题知识点关联数据
  *
  * @param pointid    : 知识点id
  * @param questionid : 题id
  * @param questype
  * @param creator    : 创建者
  * @param createtime : 创建时间
  * @param dt
  * @param dn
  */
case class QzPointQuestion(
                            pointid: Int,
                            questionid: Int,
                            questype: Int,
                            creator: String,
                            createtime: String,
                            dt: String,
                            dn: String
                          )

/**
  * 网站课程日志数据
  *
  * @param sitecourseid   : 网站课程id
  * @param siteid         : 网站id
  * @param courseid       : 课程id
  * @param sitecoursename : 网站课程名称
  * @param coursechapter
  * @param sequence
  * @param status
  * @param creator        : 创建者
  * @param createtime     : 创建时间
  * @param helpparperstatus
  * @param servertype
  * @param boardid        : 课程模板id
  * @param showstatus
  * @param dt
  * @param dn
  */
case class QzSiteCourse(
                         sitecourseid: Int,
                         siteid: Int,
                         courseid: Int,
                         sitecoursename: String,
                         coursechapter: String,
                         sequence: String,
                         status: String,
                         creator: String,
                         createtime: String,
                         helpparperstatus: String,
                         servertype: String,
                         boardid: Int,
                         showstatus: String,
                         dt: String,
                         dn: String
                       )

/**
  * 题库课程数据
  *
  * @param courseid      : 课程id
  * @param majorid       : 主修id
  * @param coursename    : 课程名称
  * @param coursechapter
  * @param sequence
  * @param isadvc
  * @param creator
  * @param createtime    : 创建时间
  * @param status
  * @param chapterlistid : 章节列表id
  * @param pointlistid   : 知识点列表id
  * @param dt
  * @param dn
  */
case class QzCourse(
                     courseid: Int,
                     majorid: Int,
                     coursename: String,
                     coursechapter: String,
                     sequence: String,
                     isadvc: String,
                     creator: String,
                     createtime: String,
                     status: String,
                     chapterlistid: Int,
                     pointlistid: Int,
                     dt: String,
                     dn: String
                   )

/**
  * 课程辅导数据
  *
  * @param courseeduid  : 课程辅导id
  * @param edusubjectid : 辅导科目id
  * @param courseid     : 课程id
  * @param creator
  * @param createtime
  * @param majorid      : 主修id
  * @param dt
  * @param dn
  */
case class QzCourseEduSubject(
                               courseeduid: Int,
                               edusubjectid: Int,
                               courseid: Int,
                               creator: String,
                               createtime: String,
                               majorid: Int,
                               dt: String,
                               dn: String
                             )

/**
  * 做题网站日志数据
  *
  * @param siteid   : 网站id
  * @param sitename : 网站名称
  * @param domain
  * @param sequence
  * @param multicastserver
  * @param templateserver
  * @param status
  * @param creator
  * @param createtime
  * @param multicastgateway
  * @param multicastport
  * @param dt
  * @param dn
  */
case class QzWebsite(
                      siteid: Int,
                      sitename: String,
                      domain: String,
                      sequence: String,
                      multicastserver: String,
                      templateserver: String,
                      status: String,
                      creator: String,
                      createtime: String,
                      multicastgateway: String,
                      multicastport: String,
                      dt: String,
                      dn: String
                    )

/**
  * 主修数据
  *
  * @param majorid    : 主修id
  * @param businessid : 主修行业id
  * @param siteid     : 网站id
  * @param majorname  : 主修名称
  * @param shortname
  * @param status
  * @param sequence
  * @param creator
  * @param createtime
  * @param column_sitetype
  * @param dt
  * @param dn
  */
case class QzMajor(
                    majorid: Int,
                    businessid: Int,
                    siteid: Int,
                    majorname: String,
                    shortname: String,
                    status: String,
                    sequence: String,
                    creator: String,
                    createtime: String,
                    column_sitetype: String,
                    dt: String,
                    dn: String
                  )

/**
  * 所属行业数据
  *
  * @param businessid   : 行业id
  * @param businessname : 行业名称
  * @param sequence
  * @param status
  * @param creator
  * @param createtime
  * @param siteid       : 所属网站id
  * @param dt
  * @param dn
  */
case class QzBusiness(
                       businessid: Int,
                       businessname: String,
                       sequence: String,
                       status: String,
                       creator: String,
                       createtime: String,
                       siteid: Int,
                       dt: String,
                       dn: String
                     )

/**
  * 试卷视图数据
  *
  * @param paperviewid   : 试卷视图id
  * @param paperid       : 试卷id
  * @param paperviewname : 试卷视图名称
  * @param paperparam
  * @param openstatus
  * @param explainurl
  * @param iscontest
  * @param contesttime
  * @param conteststarttime
  * @param contestendtime
  * @param contesttimelimit
  * @param dayiid
  * @param status
  * @param creator
  * @param createtime
  * @param paperviewcatid
  * @param modifystatus
  * @param description
  * @param papertype
  * @param downurl
  * @param paperuse
  * @param paperdifficult
  * @param testreport
  * @param paperuseshow
  * @param dt
  * @param dn
  */
case class QzPaperView(
                        paperviewid: Int,
                        paperid: Int,
                        paperviewname: String,
                        paperparam: String,
                        openstatus: String,
                        explainurl: String,
                        iscontest: String,
                        contesttime: String,
                        conteststarttime: String,
                        contestendtime: String,
                        contesttimelimit: String,
                        dayiid: Int,
                        status: String,
                        creator: String,
                        createtime: String,
                        paperviewcatid: Int,
                        modifystatus: String,
                        description: String,
                        papertype: String,
                        downurl: String,
                        paperuse: String,
                        paperdifficult: String,
                        testreport: String,
                        paperuseshow: String,
                        dt: String,
                        dn: String
                      )

/**
  * 试卷主题关联数据
  *
  * @param paperviewid : 视图id
  * @param centerid    : 主题id
  * @param openstatus
  * @param sequence
  * @param creator
  * @param createtime
  * @param dt
  * @param dn
  */
case class QzCenterPaper(
                          paperviewid: Int,
                          centerid: Int,
                          openstatus: String,
                          sequence: String,
                          creator: String,
                          createtime: String,
                          dt: String,
                          dn: String
                        )

/**
  * 做题试卷日志数据
  *
  * @param paperid       : 试卷id
  * @param papercatid
  * @param courseid      : 课程id
  * @param paperyear     : 试卷所属年份
  * @param chapter       : 章节
  * @param suitnum
  * @param papername     : 试卷名称
  * @param status
  * @param creator
  * @param createtime
  * @param totalscore    : 试卷总分
  * @param chapterid     : 章节id
  * @param chapterlistid : 所属章节列表id
  * @param dt
  * @param dn
  */
case class QzPaper(
                    paperid: Int,
                    papercatid: Int,
                    courseid: Int,
                    paperyear: String,
                    chapter: String,
                    suitnum: String,
                    papername: String,
                    status: String,
                    creator: String,
                    createtime: String,
                    var totalscore: String,
                    chapterid: Int,
                    chapterlistid: Int,
                    dt: String,
                    dn: String
                  )

/**
  * 主题数据
  *
  * @param centerid   : 主题id
  * @param centername : 主题名称
  * @param centeryear : 主题年份
  * @param centertype : 主题类型
  * @param openstatus
  * @param centerparam
  * @param description
  * @param creator
  * @param createtime
  * @param sequence
  * @param provideuser
  * @param centerviewtype
  * @param stage
  * @param dt
  * @param dn
  */
case class QzCenter(
                     centerid: Int,
                     centername: String,
                     centeryear: String,
                     centertype: String,
                     openstatus: String,
                     centerparam: String,
                     description: String,
                     creator: String,
                     createtime: String,
                     sequence: String,
                     provideuser: String,
                     centerviewtype: String,
                     stage: String,
                     dt: String,
                     dn: String
                   )

/**
  * 做题日志数据
  *
  * @param questionid : 题id
  * @param parentid
  * @param questypeid : 题目类型id
  * @param quesviewtype
  * @param content
  * @param answer
  * @param analysis
  * @param limitminute
  * @param score      : 题的分数
  * @param splitscore
  * @param status
  * @param optnum
  * @param lecture
  * @param creator
  * @param createtime
  * @param modifystatus
  * @param attanswer
  * @param questag
  * @param vanalysisaddr
  * @param difficulty
  * @param quesskill
  * @param vdeoaddr
  * @param dt
  * @param dn
  */
case class QzQuestion(
                       questionid: Int,
                       parentid: Int,
                       questypeid: Int,
                       quesviewtype: Int,
                       content: String,
                       answer: String,
                       analysis: String,
                       limitminute: String,
                       var score: String,
                       var splitscore: String,
                       status: String,
                       optnum: Int,
                       lecture: String,
                       creator: String,
                       createtime: String,
                       modifystatus: String,
                       attanswer: String,
                       questag: String,
                       vanalysisaddr: String,
                       difficulty: String,
                       quesskill: String,
                       vdeoaddr: String,
                       dt: String,
                       dn: String
                     )

/**
  * 题目类型数据
  *
  * @param quesviewtype
  * @param viewtypename
  * @param questypeid : 做题类型id
  * @param description
  * @param status
  * @param creator
  * @param createtime
  * @param papertypename
  * @param sequence
  * @param remark
  * @param splitscoretype
  * @param dt
  * @param dn
  */
case class QzQuestionType(
                           quesviewtype: Int,
                           viewtypename: String,
                           questypeid: Int,
                           description: String,
                           status: String,
                           creator: String,
                           createtime: String,
                           papertypename: String,
                           sequence: String,
                           remark: String,
                           splitscoretype: String,
                           dt: String,
                           dn: String
                         )

/**
  * 学员做题详情数据
  *
  * @param userid: 用户id
  * @param paperviewid: 试卷视图id
  * @param chapterid: 章节id
  * @param sitecourseid: 网站课程id
  * @param questionid: 题id
  * @param majorid: 主修id
  * @param useranswer
  * @param istrue
  * @param lasttime
  * @param opertype
  * @param paperid: 试卷id
  * @param spendtime: 所用时间单位(秒)
  * @param score: 学员成绩分数
  * @param question_answer: 做题结果（0错误 1正确）
  * @param dt
  * @param dn
  */
case class QzMemberPaperQuestion(
                                  userid: Int,
                                  paperviewid: Int,
                                  chapterid: Int,
                                  sitecourseid: Int,
                                  questionid: Int,
                                  majorid: Int,
                                  useranswer: String,
                                  istrue: String,
                                  lasttime: String,
                                  opertype: String,
                                  paperid: Int,
                                  spendtime: Int,
                                  var score: String,
                                  question_answer: Int,
                                  dt: String,
                                  dn: String
                                )