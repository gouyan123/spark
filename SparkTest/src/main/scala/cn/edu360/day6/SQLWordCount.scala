package cn.edu360.day6

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object SQLWordCount {

  def main(args: Array[String]): Unit = {

    //创建SparkSession，sparksession是操作sql的入口
    val spark = SparkSession.builder()
      .appName("SQLWordCount")
      /**代码交给本地执行，分配*个线程；实际中 代码交给 spark集群执行，设置方式：.master("spark:主节点ip:port")*/
      .master("local[*]")
      .getOrCreate()

    //(指定以后从哪里)读数据，都是lazy；读数据方式：①使用sparksession里面持有的sparkContext来读；②sparksession里面的 read来读；

    //Dataset分布式数据集，是对RDD的进一步封装，是更加智能的RDD；dataset只有一列，默认这列叫value；Dataset执行前要制定执行计划；
//    val lines: Dataset[String] = spark.read.textFile("hdfs://192.168.245.128:9000/people")
    val lines: Dataset[String] = spark.read.textFile("D:/123.txt")
        lines.show();

    //整理数据，将Dataset中的这一列value转换为一个 Table
    //导入隐式转换
    import spark.implicits._
    //切分压平，Dataset也是一个DataFrame，只不过只有一列；注意，把行切分为多个单词后，这多个单词在 Dataset中依然只是一列；
    val words: Dataset[String] = lines.flatMap(_.split(" "))

    //注册视图，即虚拟表
    words.createTempView("v_wc")

    //执行SQL（sql()是Transformation算子，lazy），对value中的数据进行计数
    val result: DataFrame = spark.sql("SELECT value word, COUNT(*) counts FROM v_wc GROUP BY word ORDER BY counts DESC")

    /**执行Action；show()方法本质，将计算任务下发到集群，集群各节点计算完成后，将结果收集回来*/
    result.show()
//    关闭会话
    spark.stop()
  }
}
