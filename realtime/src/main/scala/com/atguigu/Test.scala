package com.atguigu

import java.text.NumberFormat

import org.lionsoul.ip2region.{DbConfig, DbSearcher}

object Test1 {
  def main(args: Array[String]): Unit = {
    val nf = NumberFormat.getPercentInstance()
    nf.setMaximumFractionDigits(2)
    println(nf.format(3.33))
    println(nf.format(0.33))
    println(nf.format(0.033))
    println(nf.format(0.33555333))
    println(nf.format(333.333333))
  }
}

object Test2 {
  def main(args: Array[String]): Unit = {
    val ipSearcher = new DbSearcher(new DbConfig(), Thread.currentThread().getContextClassLoader.getResource("ip2region.db").getPath)
    println(ipSearcher.memorySearch("192.168.1.1"))
    println(ipSearcher.memorySearch("182.90.210.119").getRegion)
    println(ipSearcher.memorySearch("36.59.146.50"))

  }
}