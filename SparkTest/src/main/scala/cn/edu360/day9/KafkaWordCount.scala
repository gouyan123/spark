package cn.edu360.day9

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaWordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("KafkaWordCount").setMaster("local[*]")
    /*创建StreamingContext；5s处理一次*/
    val ssc = new StreamingContext(conf, Seconds(5))
    val zkQuorum = "172.17.1.247:2181"                       //zookeeper地址
    val groupId = "g1"                                        //consumer分组
    val topic = Map[String, Int]("gy" -> 1)   //从该topic里面读取数据；1表示一个topic一个线程

    //创建DStream，需要KafkaDStream；createStream()是高级api，写法简单但是效率低；createDirectStream()是低级api，写法复杂但效率高，一般用这个；
    val data: ReceiverInputDStream[(String, String)] = KafkaUtils.createStream(ssc, zkQuorum, groupId, topic)
    //对数据进行处理
    //Kafak的ReceiverInputDStream[(String, String)]里面装的是一个元组（key是写入的key，value是实际写入的内容）
    val lines: DStream[String] = data.map(_._2)/*data.map(_._2)获取 value，*/
    //对DSteam进行操作，你操作这个抽象（代理，描述），就像操作一个本地的集合一样
    //切分压平
    val words: DStream[String] = lines.flatMap(_.split(" "))
    //单词和一组合在一起
    val wordAndOne: DStream[(String, Int)] = words.map((_, 1))
    //聚合
    val reduced: DStream[(String, Int)] = wordAndOne.reduceByKey(_+_)
    //打印结果(Action)
    reduced.print()
    //启动sparksteaming程序
    ssc.start()
    //等待优雅的退出
    ssc.awaitTermination()
  }
}
