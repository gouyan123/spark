package cn.edu360.day7

import java.util.Properties
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object JdbcDataSource {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("JdbcDataSource")
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._
    /**load方法不会读取真正mysql的数据，因为load()是transformation算子；而是连接数据库，获取数据库表头信息；*/
    val logs: DataFrame = spark.read.format("jdbc").options(
      Map("url" -> "jdbc:mysql://localhost:3306/test",
        "driver" -> "com.mysql.jdbc.Driver",
        "dbtable" -> "sms",
        "user" -> "root",
        "password" -> "123456",
        "useSSL" -> "false")
    ).load()
    /*打印sms表的 schema*/
    logs.printSchema()
    /**show()是action算子，会真正读取mysql数据，并返回给Driver*/
    logs.show()
    /*过滤：r 代表每一行，但是 filter()里面写函数，有点复杂，不推荐*/
    val filtered: Dataset[Row] = logs.filter(r => {
      r.getAs[Int]("age") <= 130
    })
    filtered.show()

    /*lambda表达式，推荐；集合中 每行代表一个集合元素*/
    val r = logs.filter($"age" <= 13)
    /*如果想使用 sql，注册试图即可*/
    r.show();
    val reslut: DataFrame = r.select($"sms_id",$"content",$"phone", $"age" * 10 as "age")
    reslut.show();

    /**---*/
    /**将数据通过jdbc 写入mysql*/
    val props = new Properties()
    props.put("user","root")
    props.put("password","123456")
    /*ignore表示数据库中已有则忽略，有多种模式*/
    reslut.write.mode("ignore").jdbc("jdbc:mysql://localhost:3306/test", "sms", props)
    /**将数据保存到 text文件里*/
    /*DataFrame保存成文本时，只能保存一列*/
//    reslut.write.text("D:/text")
    /**将数据保存到 json里面*/
    reslut.write.json("D:/json")
    /**将数据保存到 csv里面，但是没有表头*/
    reslut.write.csv("D:/csv")
    /**将数据保存到 parquet里面，parquet是一种特殊的文件格式，不仅用parquet存储，还用snappy压缩了*/
//    reslut.write.parquet("hdfs://192.168.245.128:9000/parquet")
    reslut.show()
    /*关闭连接*/
    spark.close()
  }
}
