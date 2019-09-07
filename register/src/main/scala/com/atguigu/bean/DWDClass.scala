package com.atguigu.bean

/**
  * 用户基本信息表
  *
  * @param uid           : 用户id
  * @param ad_id         : 广告id
  * @param birthday      : 生日
  * @param email         : 邮箱
  * @param fullname      : 姓名
  * @param iconurl
  * @param lastlogin     : 上一次登录时间
  * @param mailaddr
  * @param memberlevel   : 用户级别
  * @param password      : 密码
  * @param paymoney
  * @param phone         : 手机号
  * @param qq
  * @param register      : 注册时间
  * @param regupdatetime : 更新时间
  * @param unitname
  * @param userip        : ip地址
  * @param zipcode
  * @param dt            : 日期分区
  * @param dn            : 网站分区
  */
case class Member(
                   uid: Int,
                   ad_id: Int,
                   birthday: String,
                   email: String,
                   var fullname: String,
                   iconurl: String,
                   lastlogin: String,
                   mailaddr: String,
                   memberlevel: String,
                   var password: String,
                   paymoney: String,
                   var phone: String,
                   qq: String,
                   register: String,
                   regupdatetime: String,
                   unitname: String,
                   userip: String,
                   zipcode: String,
                   dt: String,
                   dn: String
                 )

/**
  * 用户跳转地址注册表
  *
  * @param uid       : 用户id
  * @param appkey
  * @param appregurl : 注册时跳转地址
  * @param bdp_uuid
  * @param createtime
  * @param domain
  * @param isranreg
  * @param regsource : 所属平台 1.PC  2.MOBILE  3.APP   4.WECHAT
  * @param regsourcename
  * @param websiteid
  * @param dt        : 日期分区
  * @param dn        : 网站分区
  */
case class MemberRegtype(
                          uid: Int,
                          appkey: String,
                          appregurl: String,
                          bdp_uuid: String,
                          createtime: String,
                          domain: String,
                          isranreg: String,
                          regsource: String,
                          var regsourcename: String,
                          websiteid: Int,
                          dt: String,
                          dn: String
                        )

/**
  * 广告基础表
  *
  * @param adid   : 基础广告表广告id
  * @param adname : 广告详情名称
  * @param dn     : 网站分区
  */
case class BaseAd(
                   adid: Int,
                   adname: String,
                   dn: String
                 )

/**
  * 网站基础表
  *
  * @param siteid     : 网站id
  * @param sitename   : 网站名称
  * @param siteurl    : 网站地址
  * @param delete
  * @param createtime : 创建时间
  * @param creator
  * @param dn         : 网站分区
  */
case class BaseWebsite(
                        siteid: Int,
                        sitename: String,
                        siteurl: String,
                        delete: Int,
                        createtime: String,
                        creator: String,
                        dn: String
                      )

/**
  * 用户支付金额表
  *
  * @param uid      : 用户id
  * @param paymoney : 支付金额
  * @param siteid   : 网站id对应 对应BaseWebsite 下的siteid网站
  * @param vip_id   : 对应VipLevel表的vip_id
  * @param dt       : 日期分区
  * @param dn       : 网站分区
  */
case class PCEnterMemPayMoney(
                               uid: Int,
                               paymoney: String,
                               siteid: Int,
                               vip_id: Int,
                               dt: String,
                               dn: String
                             )

/**
  * 用户vip等级基础表
  * @param vip_id
  * @param vip_level: vip级别名称
  * @param start_time: vip开始时间
  * @param end_time: vip结束时间
  * @param last_modify_time: 上次修改时间
  * @param max_free
  * @param min_free
  * @param next_level
  * @param operator
  * @param dn: 网站分区
  */
case class VipLevel(
                     vip_id: Int,
                     vip_level: String,
                     start_time: String,
                     end_time: String,
                     last_modify_time: String,
                     max_free: String,
                     min_free: String,
                     next_level: String,
                     operator: String,
                     dn: String
                   )