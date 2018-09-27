package cn.edu360.day6

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object DataSetWordCount {

  def main(args: Array[String]): Unit = {

    //创建SparkSession
    val spark = SparkSession.builder()
      .appName("DataSetWordCount")
      .master("local[*]")
      .getOrCreate()

    //(指定以后从哪里)读数据，是lazy

    //Dataset分布式数据集，是对RDD的进一步封装，是更加智能的RDD；dataset只有一列，默认这列叫value
    /*spark.read 可以读多种类型数据*/
    val lines: Dataset[String] = spark.read.textFile("hdfs://192.168.245.128:9000/people")
    //整理数据(切分压平)
    //导入隐式转换
    import spark.implicits._
    val words: Dataset[String] = lines.flatMap(_.split(","))

    //使用DataSet的API，groupBy()分组后，对每个分组执行count()；第一个count()计算每个分组里面有多少个元素，返回DataFrame，第二个count()计算有多少个分组，返回非DataFrame；
    val r = words.groupBy($"value" as "word").count().count()

    //分组后，在每个分组里面聚合
    //import org.apache.spark.sql.functions._
    //val counts = words.groupBy($"value".as("word")).agg(count("*") as "counts").orderBy($"counts" desc)

    //counts.show()
    println(r)
    //r.show()
    spark.stop()
  }
}
