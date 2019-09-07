package com.atguigu.realtime.util

import java.io.InputStreamReader
import java.util.Properties

object MyPropertiesUtil {

  /**
    * 获取Properties读取配置文件
    * @param file
    * @return
    */
  def getProperties(file: String): Properties = {
    val properties = new Properties()
    properties.load(new InputStreamReader(Thread.currentThread().getContextClassLoader.getResourceAsStream(file)))
    properties
  }
}
