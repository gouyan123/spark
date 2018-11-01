package cn.edu360.day10

import cn.edu360.day4.MyUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD

/**数据如下：
  * A 202.106.196.115 手机 iPhone8 8000
  * B 202.106.0.20 服装 布莱奥尼西服 199
  * C 202.102.152.3 家具 婴儿床 2000
  * D 202.96.96.68 家电 电饭锅 1000
  * F 202.98.0.68 化妆品 迪奥香水 200
  * H 202.96.75.68 食品 奶粉 600
  * J 202.97.229.133 图书 Hadoop编程指南 90
  * A 202.106.196.115 手机 手机壳 200
  * B 202.106.0.20 手机 iPhone8 8000
  * C 202.102.152.3 家具 婴儿车 2000
  * D 202.96.96.68 家具 婴儿车 1000
  * F 202.98.0.68 化妆品 迪奥香水 200
  * H 202.96.75.68 食品 婴儿床 600
  * J 202.97.229.133 图书 spark实战 80*/
object CalculateUtil {

  def calculateIncome(fields: RDD[Array[String]]) = {
    //将数据计算后写入到Reids
    val priceRDD: RDD[Double] = fields.map(arr => {
      val price = arr(4).toDouble   //arr(4)取数组中第4个元素；
      price   //返回price
    })
    //reduce是一个Action，会把多个Executor的计算结果返回到Driver端，相当于求出了当前批次的总金额；reduce(_+_表示聚合，将分组里面所有元素进行相加)
    //将当前批次的总金额返回了
    val sum: Double = priceRDD.reduce(_+_)
    //获取一个jedis连接
    val conn = JedisConnectionPool.getConnection()
    //将历史值和当前的值进行累加
    //conn.set(Constant.TOTAL_INCOME, sum.toString)   //该方法会覆盖历史值，因此不用set()
    conn.incrByFloat(Constant.TOTAL_INCOME, sum)
    //释放连接
    conn.close()
  }

  /**
    * 计算分类的成交金额，对产品类型进行聚合；
    * @param fields
    */
  def calculateItem(fields: RDD[Array[String]]) = {
    //对field的map方法是在哪一端调用的呢？Driver；map(arr => {...}) ...在触发执行时，在Executor中执行
    val itemAndPrice: RDD[(String, Double)] = fields.map(arr => {
      //分类
      val item = arr(2)
      //金额
      val parice = arr(4).toDouble
      (item, parice)
    })
    //按照商品分类进行聚合
    val reduced: RDD[(String, Double)] = itemAndPrice.reduceByKey(_+_)  //等价于reduceByKey((a,b) => (a+b))
    //将当前批次的数据累加到Redis中
    //foreachPartition是一个Action；判断action方法：①提交job则是action方法；②不返回RDD则是 action放；
    //现在这种方式，jeids的连接是在Driver端创建的
    //在Driver端拿Jedis连接不好；conn要随conn.incrByFloat(t._1, t._2)一起序列化提交给Executor执行；
    //val conn = JedisConnectionPool.getConnection()

    reduced.foreachPartition(part => {//foreachPartition表示遍历每个分区；part表示其中一个分区；
      //获取一个Jedis连接，这个连接其实是在Executor中的获取的
      //JedisConnectionPool在一个Executor进程中有一个实例，是单例的；一个Executor里面有很多task
      val conn = JedisConnectionPool.getConnection()
      part.foreach(t => {
        //一个连接更新多条数据
        conn.incrByFloat(t._1, t._2)
      })
      //将当前分区中的数据跟新完在关闭连接
      conn.close()
    })
  }

  //根据Ip计算归属地
  def calculateZone(fields: RDD[Array[String]], broadcastRef: Broadcast[Array[(Long, Long, String)]]) = {

    val provinceAndPrice: RDD[(String, Double)] = fields.map(arr => {
      val ip = arr(1)
      val price = arr(4).toDouble
      val ipNum = MyUtils.ip2Long(ip)
      //在Executor中获取到广播的全部规则
      val allRules: Array[(Long, Long, String)] = broadcastRef.value
      //二分法查找
      val index = MyUtils.binarySearch(allRules, ipNum)
      var province = "未知"
      if (index != -1) {
        province = allRules(index)._3
      }
      //省份，订单金额
      (province, price)
    })
    //按省份进行聚合
    val reduced: RDD[(String, Double)] = provinceAndPrice.reduceByKey(_+_)
    //将数据跟新到Redis
    reduced.foreachPartition(part => {
      val conn = JedisConnectionPool.getConnection()
      part.foreach(t => {
        conn.incrByFloat(t._1, t._2)
      })
      conn.close()
    })
  }
}
