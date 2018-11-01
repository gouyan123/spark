package cn.edu360

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object ScalaWordCount02 {
  def main(args: Array[String]): Unit = {
    /**创建 spark的配置conf；设置appName作用：当提交到spark集群时，可视化页面可以看到；*/
      /**2种执行方式：
        * 1 提交到spark集群执行：打jar包然后 ./spark-submit --master spark://master128:7077 --class 主类  *.jar  参数1  参数2
        * 2 本地执行：conf.setMaster("Local(4)")，4表示开启 4个线程*/
    val conf = new SparkConf().setAppName("ScalaWordCount02");
    /**创建 sparkContext，sparkContext为spark的入口*/
    val sc = new SparkContext(conf);
    /**将读取的内容 转换 为 分布式集RDD，RDD就是一个同类元素集合，这里指同类元素指 行；textFile()需要指定读取路径和 rdd分区数*/
    val lines : RDD[String] = sc.textFile(args(0));
    /**切分压平，_表示集合中的每个元素*/
    val words : RDD[String] = lines.flatMap(_.split(" "))
    /**将单词 和 1 组合为 元组，方便对单词个数进行计数*/
    val wordAndOne : RDD[(String,Int)] = words.map((_,1))
    /**通过reduceByKey 对 key 进行聚合*/
    val reduced : RDD[(String,Int)] = wordAndOne.reduceByKey(_+_);
    /**对reduced排序；_表示reduced这个RDD里面的元素("hello",5)，_.2表示取出(,)中第二个元素，然后按照从高到低排序*/
    val sorted : RDD[(String,Int)] = reduced.sortBy(_._2,false)
    /**将结果保存到 hdfs*/
//    sorted.saveAsTextFile(args(1))
    /*释放资源*/
    sc.stop()
  }
}
