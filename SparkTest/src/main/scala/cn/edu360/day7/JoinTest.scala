package cn.edu360.day7

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

/**
  * Created by zx on 2017/10/14.
  */
object JoinTest {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("JoinTest")
      /**.master("local[*]")表示将代码交给本地执行；当代码交给集群执行时，可以不指定.master()*/
      .master("local[*]")
      /**创建 sparkSession*/
      .getOrCreate()
    /*导入spark的隐士转换，因为下面使用了RDD的map()算子*/
    import spark.implicits._
    /*sparkSession可以创建DataFrame也可以创建DataSet，DataSet只有一列，使用更方便*/
    val lines: Dataset[String] = spark.createDataset(List("1,laozhoa,china", "2,laoduan,usa", "3,laoyang,jp"))
    //对数据进行整理
    /*map()算子，将 一行字符串 转换为 元祖(数据1,数据2,数据3)*/
    val tpDs: Dataset[(Long, String, String)] = lines.map(line => {
      val fields = line.split(",")
      val id = fields(0).toLong
      val name = fields(1)
      val nationCode = fields(2)
      (id, name, nationCode)
    })
    /*DataSet转换为DataFrame，并添加表头 即字段名称*/
    val df1 = tpDs.toDF("id", "name", "nation")
    df1.show()
    val nations: Dataset[String] = spark.createDataset(List("china,中国", "usa,美国"))
    //对数据进行整理
    val ndataset: Dataset[(String, String)] = nations.map(l => {
      val fields = l.split(",")
      val ename = fields(0)
      val cname = fields(1)
      (ename, cname)
    })
    /*将DataSet转换为DataFrame，并指定schema，包括表头*/
    val df2 = ndataset.toDF("ename","cname")
    df2.count()
    //第一种，创建视图
    //df1.createTempView("v_users")
    //df2.createTempView("v_nations")
    //val r: DataFrame = spark.sql("SELECT name, cname FROM v_users JOIN v_nations ON nation = ename")

    //第二种，使用 DataFrame的API
    //import org.apache.spark.sql.functions._
    val r = df1.join(df2, $"nation" === $"ename", "left_outer")
    r.show()
    spark.stop()
  }
}
