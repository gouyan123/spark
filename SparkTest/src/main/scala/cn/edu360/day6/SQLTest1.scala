package cn.edu360.day6

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object SQLTest1 {

  def main(args: Array[String]): Unit = {
    //spark2.x SQL的编程API(SparkSession)
    //sparkSession 是 spark2.x SQL执行的入口，通过 sparkSession创建 DataFrame
    val session = SparkSession.builder()
      .appName("SQLTest1")
      /**含义 ：将代码交给本地执行，分配*个线程；.master("spark:spark集群主节点ip:port")表示将代码交给spark集群执行*/
      .master("local[*]")
      /**.config()可以传属性名和属性值*/
//      .config()
      /**含义：有则使用以前的 sparkSession，没有则创建*/
      .getOrCreate()

    //创建 DataFrame之前要有RDD，RDD通过 sprakContext读取数据创建
    val lines: RDD[String] = session.sparkContext.textFile("hdfs://192.168.245.128:9000/people")

    //将数据进行整理，将非结构化数据转为结构化数据
    val rowRDD: RDD[Row] = lines.map(line => {
      val fields = line.split(",")
      val id = fields(0).toLong
      val name = fields(1)
      val age = fields(2).toInt
      val fv = fields(3).toDouble
      Row(id, name, age, fv)
    })

    //结果类型，其实就是表头，用于描述DataFrame
    val schema: StructType = StructType(List(
      StructField("id", LongType, true),
      StructField("name", StringType, true),
      StructField("age", IntegerType, true),
      StructField("fv", DoubleType, true)
    ))

    //创建DataFrame
    val df: DataFrame = session.createDataFrame(rowRDD, schema)
    import session.implicits._
//    val df2: Dataset[Row] = df.where($"fv" > 98).orderBy($"fv" desc, $"age" asc)
    df.show()
    /**将数据保存到 mysql数据库*/
//    df.write.jdbc();
    /**将数据保存为 json*/
//    df.write.json();
    // 关闭连接
    session.stop()
  }
}
