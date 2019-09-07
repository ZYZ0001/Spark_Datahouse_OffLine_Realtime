package com.atguigu.bean

/**
  * 用户数据宽表
  *
  * @param uid                  : 用户id
  * @param ad_id                : 广告id
  * @param fullname             : 用户姓名
  * @param iconurl              : 图标地址
  * @param lastlogin            : 上次登录时间
  * @param mailaddr             : 邮箱地址
  * @param memberlevel          : 用户级别
  * @param password             : 密码
  * @param paymoney             : 支付金额
  * @param phone                : 电话号码
  * @param qq                   : qq号码
  * @param register             : 注册时间
  * @param regupdatetime        : 更新时间
  * @param unitname             : 单位名称
  * @param userip               : 用户ip
  * @param zipcode              : 邮编
  * @param appkey               : app识别码
  * @param appregurl            : 注册时跳转地址
  * @param bdp_uuid
  * @param reg_createtime       : 注册时间(与dwd表的字段不一样)
  * @param domain
  * @param isranreg
  * @param regsource            : 所属平台 1.PC  2.MOBILE  3.APP   4.WECHAT
  * @param regsourcename        : 平台名称
  * @param adname               : 广告详情名称
  * @param siteid               : 网站id
  * @param sitename             : 网站名称
  * @param siteurl              : 网站地址
  * @param site_delete
  * @param site_createtime      : 网站创建时间
  * @param site_creator         : 网站创建者
  * @param vip_id               : vipid
  * @param vip_level            : vip级别名称
  * @param vip_start_time       : vip开始时间
  * @param vip_end_time         : vip结束时间
  * @param vip_last_modify_time : 上次修改时间
  * @param vip_max_free         : vip最大权限
  * @param vip_min_free         : vip最小权限
  * @param vip_next_level       : 以一个等级
  * @param vip_operator         : vip的操作区域
  * @param dt                   : 日期分区
  * @param dn                   : 网站分区
  */
case class MemberWideTable(
                            uid: Int,
                            ad_id: Int,
                            fullname: String,
                            iconurl: String,
                            lastlogin: String,
                            mailaddr: String,
                            memberlevel: String,
                            password: String,
                            paymoney: String,
                            phone: String,
                            qq: String,
                            register: String,
                            regupdatetime: String,
                            unitname: String,
                            userip: String,
                            zipcode: String,
                            appkey: String,
                            appregurl: String,
                            bdp_uuid: String,
                            reg_createtime: String,
                            domain: String,
                            isranreg: String,
                            regsource: String,
                            regsourcename: String,
                            adname: String,
                            siteid: Integer,
                            sitename: String,
                            siteurl: String,
                            site_delete: String,
                            site_createtime: String,
                            site_creator: String,
                            vip_id: Integer,
                            vip_level: String,
                            vip_start_time: String,
                            vip_end_time: String,
                            vip_last_modify_time: String,
                            vip_max_free: String,
                            vip_min_free: String,
                            vip_next_level: String,
                            vip_operator: String,
                            dt: String,
                            dn: String
                          )

/**
  * 针对dws层宽表的支付金额（paymoney）和vip等级(vip_level)这两个会变动的字段生成一张拉链表
  *
  * @param uid        : 用户id
  * @param paymoney   : 支付金额
  * @param vip_level  : vip的级别
  * @param start_time : 开始时间
  * @param end_time   : 结束时间
  * @param dn         : 网站分区
  */
case class MemberZipperTable(
                              uid: Int,
                              var paymoney: String,
                              vip_level: String,
                              start_time: String,
                              var end_time: String,
                              dn: String
                            )