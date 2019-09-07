package com.atguigu.realtime.util

import com.alibaba.fastjson.JSON

object JsonUtil {

  /**
    * 判断是否为Json字符串
    * @return
    */
  def isJson(line: String): Boolean = {
    try {
      JSON.parseObject(line)
      true
    } catch {
      case _ => false
    }
  }

  /**
    * 判断Json字符串是否能够转换为指定类
    * @return
    */
  def isJsonClass(line: String, clazz: Class[_]): Boolean = {
    try {
      JSON.parseObject(line, clazz)
      true
    } catch {
      case _ => false
    }
  }
}
