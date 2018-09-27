package cn.edu360.day7

import org.apache.spark.sql.{DataFrame, SparkSession}

object JsonDataSource {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("JdbcDataSource")
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._
    /*指定以后读取json类型的数据(有表头)，如果修改了json文件，要删除该json文件的校验文件；*/
    val jsons: DataFrame = spark.read.json("D:/json")
    val filtered: DataFrame = jsons.filter($"age" <=500)
    filtered.printSchema()
    filtered.show()
    spark.stop()
  }
}
