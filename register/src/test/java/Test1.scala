import java.math.BigDecimal

import com.atguigu.server.DWDToDWS
import com.atguigu.util.MySparkUtil
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object Test1 {
  def main(args: Array[String]): Unit = {
//    val conf: SparkConf = new SparkConf().setAppName("test").setMaster("local[*]")
//    val spark: SparkSession = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()
//
//    MySparkUtil.hiveConfSetSmallFile(spark)
//    MySparkUtil.hiveConfSetCompress(spark)
//
//    DWDToDWS.insertZipperTale(spark, "20190722")
//    spark.close()
    println(new BigDecimal("3.1455926").setScale(2, BigDecimal.ROUND_HALF_UP).toString())
  }
}
