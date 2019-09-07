package com.atguigu.realtime.util

import java.sql.{Connection, PreparedStatement, ResultSet}
import java.util.Properties

import com.alibaba.druid.pool.DruidDataSourceFactory
import javax.sql.DataSource

object DruidUtil {

  private val properties: Properties = MyPropertiesUtil.getProperties("druid.properties")
  private val dataSource: DataSource = DruidDataSourceFactory.createDataSource(properties)

  /**
    * 获取mysql连接
    *
    * @return
    */
  def getConnection(): Connection = {
    dataSource.getConnection
  }

  /**
    * 关闭资源
    * @param resultSet
    * @param preparedStatement
    * @param connection
    */
  def closeResource(resultSet: ResultSet, preparedStatement: PreparedStatement, connection: Connection): Unit = {
    closeResultSet(resultSet)
    closePreparedStatement(preparedStatement)
    closeConnect(connection)
  }

  // 关闭连接
  def closeConnect(connection: Connection) = {
    if (connection != null) connection.close()
  }

  // 关闭结果集
  def closeResultSet(resultSet: ResultSet): Unit = {
    if (resultSet != null) resultSet.close()
  }

  // 关闭执行器
  def closePreparedStatement(preparedStatement: PreparedStatement): Unit = {
    if (preparedStatement != null) preparedStatement.close()
  }
}
