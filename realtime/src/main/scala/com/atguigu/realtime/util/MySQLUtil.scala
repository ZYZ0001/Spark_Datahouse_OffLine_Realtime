package com.atguigu.realtime.util

import java.sql.{Connection, PreparedStatement, ResultSet}

class MySQLUtil {

  private var rs: ResultSet = _
  private var psmt: PreparedStatement = _

  /**
    * 执行更新语句, 获取更新条数
    *
    * @param conn    : 连接
    * @param sql     : sql语句
    * @param params : 更新数据参数
    * @return count: 更新条数
    */
  def executeUpdate(conn: Connection, sql: String, params: Array[Any]) = {
    var count = 0
    psmt = conn.prepareStatement(sql)
    if (params != null && params.size != 0) {
      for (i <- 0 until params.size)
        psmt.setObject(i + 1, params(i))
    }
    count = psmt.executeUpdate()
    count
  }

  /**
    * 执行查询语句, 获取结果集
    *
    * @param conn
    * @param sql
    * @param params
    * @return
    */
  def selectQuery(conn: Connection, sql: String, params: Array[Any]) = {
    rs = null
    psmt = conn.prepareStatement(sql)
    if (params != null && params.size != 0) {
      for (i <- 0 until params.size)
        psmt.setObject(i + 1, params(i))
    }
    rs = psmt.executeQuery()
    rs
  }

  /**
    * 关闭资源
    *
    * @param conn
    */
  def shutdown(conn: Connection) = DruidUtil.closeResource(rs, psmt, conn)
}
