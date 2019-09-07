package com.atguigu.bean

/**
  * 章节维度表
  *
  * @param chapterid          : 章节id
  * @param chapterlistid      : 所属章节列表id
  * @param chaptername        : 章节名称
  * @param sequence
  * @param showstatus
  * @param status
  * @param chapter_creator    : 创建者
  * @param chapter_createtime : 创建时间
  * @param chapter_courseid   : 课程id
  * @param chapternum         : 章节个数
  * @param chapterallnum      : 章节总个数
  * @param outchapterid
  * @param chapterlistname    : 章节列表名称
  * @param pointid            : 知识点id
  * @param questionid         : 题id
  * @param questype
  * @param pointname          : 知识点名称
  * @param pointyear          : 知识点所属年份
  * @param chapter            : 所属章节
  * @param excisenum
  * @param pointlistid        : 知识点列表id
  * @param pointdescribe
  * @param pointlevel         : 知识点级别
  * @param typelist
  * @param point_score        : 知识点分数
  * @param thought
  * @param remid
  * @param pointnamelist
  * @param typelistids
  * @param pointlist
  * @param dt                 : 日期分区
  * @param dn                 : 网站分区
  */
case class DWSQzChapter(
                      chapterid: Int,
                      chapterlistid: Int,
                      chaptername: String,
                      sequence: String,
                      showstatus: String,
                      status: String,
                      chapter_creator: String,
                      chapter_createtime: String,
                      chapter_courseid: Int,
                      chapternum: Int,
                      chapterallnum: Int,
                      outchapterid: Int,
                      chapterlistname: String,
                      pointid: Int,
                      questionid: Int,
                      questype: Int,
                      pointname: String,
                      pointyear: String,
                      chapter: String,
                      excisenum: Int,
                      pointlistid: Int,
                      pointdescribe: String,
                      pointlevel: String,
                      typelist: String,
                      point_score: Double,
                      thought: String,
                      remid: String,
                      pointnamelist: String,
                      typelistids: String,
                      pointlist: String,
                      dt: String,
                      dn: String
                    )

/**
  * 课程维度表
  *
  * @param sitecourseid          : 网站课程id
  * @param siteid                : 网站id
  * @param courseid              : 课程id
  * @param sitecoursename        : 网站课程名称
  * @param coursechapter
  * @param sequence
  * @param status
  * @param sitecourse_creator    : 创建者
  * @param sitecourse_createtime : 创建时间
  * @param helppaperstatus
  * @param servertype
  * @param boardid               : 课程模板id
  * @param showstatus
  * @param majorid               : 主修id
  * @param coursename            : 课程名称
  * @param isadvc
  * @param chapterlistid         : 章节列表id
  * @param pointlistid           : 知识点列表id
  * @param courseeduid           : 课程辅导id
  * @param edusubjectid          : 辅导科目id
  * @param dt                    : 日期分区
  * @param dn                    : 网站分区
  */
case class DWSQzCourse(
                     sitecourseid: Int,
                     siteid: Int,
                     courseid: Int,
                     sitecoursename: String,
                     coursechapter: String,
                     sequence: String,
                     status: String,
                     sitecourse_creator: String,
                     sitecourse_createtime: String,
                     helppaperstatus: String,
                     servertype: String,
                     boardid: Int,
                     showstatus: String,
                     majorid: Int,
                     coursename: String,
                     isadvc: String,
                     chapterlistid: Int,
                     pointlistid: Int,
                     courseeduid: Int,
                     edusubjectid: Int,
                     dt: String,
                     dn: String
                   )

/**
  * 主修维度表
  *
  * @param majorid      : 主修id
  * @param businessid   : 主修行业id
  * @param siteid       : 网站id
  * @param majorname    : 主修名称
  * @param shortname
  * @param status
  * @param sequence
  * @param major_creator
  * @param major_createtime
  * @param businessname : 行业名称
  * @param sitename     : 网站名称
  * @param domain
  * @param multicastserver
  * @param templateserver
  * @param multicastgateway
  * @param multicastport
  * @param dt
  * @param dn
  */
case class DWSQzMajor(
                    majorid: Int,
                    businessid: Int,
                    siteid: Int,
                    majorname: String,
                    shortname: String,
                    status: String,
                    sequence: String,
                    major_creator: String,
                    major_createtime: String,
                    businessname: String,
                    sitename: String,
                    domain: String,
                    multicastserver: String,
                    templateserver: String,
                    multicastgateway: String,
                    multicastport: String,
                    dt: String,
                    dn: String
                  )

/**
  * 试卷维度表
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
  * @param paper_view_creator
  * @param paper_view_createtime
  * @param paperviewcatid
  * @param modifystatus
  * @param description
  * @param paperuse
  * @param paperdifficult
  * @param testreport
  * @param paperuseshow
  * @param centerid      : 主题id
  * @param sequence
  * @param centername    : 主题名称
  * @param centeryear    : 主题年份
  * @param centertype    : 主题类型
  * @param provideuser
  * @param centerviewtype
  * @param stage
  * @param papercatid    : 试卷类别
  * @param courseid      : 课程id
  * @param paperyear     : 试卷所属年份
  * @param suitnum
  * @param papername     : 试卷名称
  * @param totalscore    : 试卷总分
  * @param chapterid     : 章节id
  * @param chapterlistid : 所属章节列表id
  * @param dt
  * @param dn
  */
case class DWSQzPaper(
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
                    paper_view_creator: String,
                    paper_view_createtime: String,
                    paperviewcatid: Int,
                    modifystatus: String,
                    description: String,
                    paperuse: String,
                    paperdifficult: String,
                    testreport: String,
                    paperuseshow: String,
                    centerid: Int,
                    sequence: String,
                    centername: String,
                    centeryear: String,
                    centertype: String,
                    provideuser: String,
                    centerviewtype: String,
                    stage: String,
                    papercatid: Int,
                    courseid: Int,
                    paperyear: String,
                    suitnum: String,
                    papername: String,
                    totalscore: Double,
                    chapterid: Int,
                    chapterlistid: Int,
                    dt: String,
                    dn: String
                  )

/**
  * 题目维度表
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
  * @param viewtypename
  * @param description
  * @param papertypename
  * @param splitscoretype
  * @param dt
  * @param dn
  */
case class DWSQzQuestion(
                       questionid: Int,
                       parentid: Int,
                       questypeid: Int,
                       quesviewtype: Int,
                       content: String,
                       answer: String,
                       analysis: String,
                       limitminute: String,
                       score: Double,
                       splitscore: Double,
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
                       viewtypename: String,
                       description: String,
                       papertypename: String,
                       splitscoretype: String,
                       dt: String,
                       dn: String
                     )

/**
  * 用户试卷详情表
  *
  * @param userid: 用户id
  * @param courseid
  * @param questionid
  * @param useranswer
  * @param istrue
  * @param lasttime
  * @param opertype
  * @param paperid
  * @param spendtime
  * @param chapterid
  * @param chaptername
  * @param chapternum
  * @param chapterallnum
  * @param outchapterid
  * @param chapterlistname
  * @param pointid
  * @param questype
  * @param pointyear
  * @param chapter
  * @param pointname
  * @param excisenum
  * @param pointdescribe
  * @param pointlevel
  * @param typelist
  * @param point_score
  * @param thought
  * @param remid
  * @param pointnamelist
  * @param typelistids
  * @param pointlist
  * @param sitecourseid
  * @param siteid
  * @param sitecoursename
  * @param coursechapter
  * @param course_sequence
  * @param course_stauts
  * @param course_creator
  * @param course_createtime
  * @param servertype
  * @param helppaperstatus
  * @param boardid
  * @param showstatus
  * @param majorid
  * @param coursename
  * @param isadvc
  * @param chapterlistid
  * @param pointlistid
  * @param courseeduid
  * @param edusubjectid
  * @param businessid
  * @param majorname
  * @param shortname
  * @param major_status
  * @param major_sequence
  * @param major_creator
  * @param major_createtime
  * @param businessname
  * @param sitename
  * @param domain
  * @param multicastserver
  * @param templateserver
  * @param multicastgatway
  * @param multicastport
  * @param paperviewid
  * @param paperviewname
  * @param paperparam
  * @param openstatus
  * @param explainurl
  * @param iscontest
  * @param contesttime
  * @param conteststarttime
  * @param contestendtime
  * @param contesttimelimit
  * @param dayiid
  * @param paper_status
  * @param paper_view_creator
  * @param paper_view_createtime
  * @param paperviewcatid
  * @param modifystatus
  * @param description
  * @param paperuse
  * @param testreport
  * @param centerid
  * @param paper_sequence
  * @param centername
  * @param centeryear
  * @param centertype
  * @param provideuser
  * @param centerviewtype
  * @param paper_stage
  * @param papercatid
  * @param paperyear
  * @param suitnum
  * @param papername
  * @param totalscore
  * @param question_parentid
  * @param questypeid
  * @param quesviewtype
  * @param question_content
  * @param question_answer: 做题结果（0错误 1正确）
  * @param question_analysis
  * @param question_limitminute
  * @param score
  * @param splitscore
  * @param lecture
  * @param question_creator
  * @param question_createtime
  * @param question_modifystatus
  * @param question_attanswer
  * @param question_questag
  * @param question_vanalysisaddr
  * @param question_difficulty
  * @param quesskill
  * @param vdeoaddr
  * @param question_description
  * @param question_splitscoretype
  * @param user_question_answer
  * @param dt
  * @param dn
  */
case class DWSUserPaperDetail(
                            userid: Int,
                            courseid: Int,
                            questionid: Int,
                            useranswer: String,
                            istrue: String,
                            lasttime: String,
                            opertype: String,
                            paperid: Int,
                            spendtime: Int,
                            chapterid: Int,
                            chaptername: String,
                            chapternum: Int,
                            chapterallnum: Int,
                            outchapterid: Int,
                            chapterlistname: String,
                            pointid: Int,
                            questype: Int,
                            pointyear: String,
                            chapter: String,
                            pointname: String,
                            excisenum: Int,
                            pointdescribe: String,
                            pointlevel: String,
                            typelist: String,
                            point_score: String,
                            thought: String,
                            remid: String,
                            pointnamelist: String,
                            typelistids: String,
                            pointlist: String,
                            sitecourseid: Int,
                            siteid: Int,
                            sitecoursename: String,
                            coursechapter: String,
                            course_sequence: String,
                            course_stauts: String,
                            course_creator: String,
                            course_createtime: String,
                            servertype: String,
                            helppaperstatus: String,
                            boardid: Int,
                            showstatus: String,
                            majorid: Int,
                            coursename: String,
                            isadvc: String,
                            chapterlistid: Int,
                            pointlistid: Int,
                            courseeduid: Int,
                            edusubjectid: Int,
                            businessid: Int,
                            majorname: String,
                            shortname: String,
                            major_status: String,
                            major_sequence: String,
                            major_creator: String,
                            major_createtime: String,
                            businessname: String,
                            sitename: String,
                            domain: String,
                            multicastserver: String,
                            templateserver: String,
                            multicastgatway: String,
                            multicastport: String,
                            paperviewid: Int,
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
                            paper_status: String,
                            paper_view_creator: String,
                            paper_view_createtime: String,
                            paperviewcatid: Int,
                            modifystatus: String,
                            description: String,
                            paperuse: String,
                            testreport: String,
                            centerid: Int,
                            paper_sequence: String,
                            centername: String,
                            centeryear: String,
                            centertype: String,
                            provideuser: String,
                            centerviewtype: String,
                            paper_stage: String,
                            papercatid: Int,
                            paperyear: String,
                            suitnum: String,
                            papername: String,
                            totalscore: String,
                            question_parentid: Int,
                            questypeid: Int,
                            quesviewtype: Int,
                            question_content: String,
                            question_answer: String,
                            question_analysis: String,
                            question_limitminute: String,
                            score: String,
                            splitscore: String,
                            lecture: String,
                            question_creator: String,
                            question_createtime: String,
                            question_modifystatus: String,
                            question_attanswer: String,
                            question_questag: String,
                            question_vanalysisaddr: String,
                            question_difficulty: String,
                            quesskill: String,
                            vdeoaddr: String,
                            question_description: String,
                            question_splitscoretype: String,
                            user_question_answer: Int,
                            dt: String,
                            dn: String
                          )