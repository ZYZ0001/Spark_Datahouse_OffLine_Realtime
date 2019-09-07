package com.atguigu.realtime.bean

/**
  * 注册日志数据
  *
  * @param userId     : 用户id
  * @param platformId : 平台id  1:PC  2:APP   3:Other
  * @param createTime : 创建时间
  */
case class Register(
                     userId: String,
                     platformId: String,
                     createTime: String
                   )

/**
  * 做题日志数据
  *
  * @param userId     : 用户id
  * @param courseId   : 课程id
  * @param pointId    : 知识点id
  * @param questionId : 题目id
  * @param isTrue     : 是否正确, 0 错误  1 正确
  * @param createTime : 创建时间
  */
case class QzLog(
                  userId: String,
                  courseId: Int,
                  pointId: Int,
                  questionId: Int,
                  isTrue: Boolean,
                  createTime: String
                )

/**
  * 页面访问日志
  *
  * @param uid          : 用户id
  * @param app_id       : 平台id
  * @param device_id    : 设备id
  * @param distinct_id  : 唯一标识
  * @param ip           : 用户ip地址
  * @param event_name
  * @param last_event_name
  * @param last_page_id : 上一页面id
  * @param next_event_name
  * @param next_page_id : 下一页面id
  * @param page_id      : 当前页面id  0:首页   1:商品课程页  2:订单页面  3:支付页面
  * @param server_time
  */
case class PageLog(
                    uid: String,
                    app_id: Int,
                    device_id: Int,
                    distinct_id: String,
                    ip: String,
                    event_name: String,
                    last_event_name: String,
                    last_page_id: Int,
                    next_event_name: String,
                    next_page_id: Int,
                    page_id: Int,
                    server_time: String
                  )

/**
  * 学员视频播放日志
  *
  * @param biz: 唯一标识
  * @param chapterid: 章节id
  * @param cwareid: 课件id
  * @param edutypeid: 辅导id
  * @param pe: 视频播放结束区间
  * @param ps: 视频播放时间区间
  * @param sourceType: 播放平台
  * @param speed: 播放倍速
  * @param subjectid:科目id
  * @param te: 视频播放结束时间(时间戳)
  * @param ts: 视频播放开始时间（时间戳）
  * @param uid
  * @param videoid: 视频id
  */
case class CourseLearn(
                        biz: String,
                        chapterid: Int,
                        cwareid: Int,
                        edutypeid: Int,
                        pe: Int,
                        ps: Int,
                        sourceType: String,
                        speed: Double,
                        subjectid: Int,
                        te: Long,
                        ts: Long,
                        uid: String,
                        videoid: Int
                      )