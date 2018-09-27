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
      r.getAs[Int]("age") <= 13
    })
    filtered.show()

    /*lambda表达式，推荐；集合中 每行代表一个集合元素*/
    val r = logs.filter($"age" <= 13)
    /*如果想使用 sql，注册试图即可*/
    r.show();
    val reslut: DataFrame = r.select($"sms_id", $"phone", $"age" * 10 as "age")
    reslut.show();

    //val props = new Properties()
    //props.put("user","root")
    //props.put("password","123568")
    //reslut.write.mode("ignore").jdbc("jdbc:mysql://localhost:3306/bigdata", "logs1", props)

    //DataFrame保存成text时出错(只能保存一列)
    //reslut.write.text("/Users/zx/Desktop/text")

    //reslut.write.json("/Users/zx/Desktop/json")

    //reslut.write.csv("/Users/zx/Desktop/csv")

    //reslut.write.parquet("hdfs://node-4:9000/parquet")

    //reslut.show()
    spark.close()
  }
}
