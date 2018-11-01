package cn.edu360.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;
import java.util.Arrays;

public class JavaLambdaWordCount02 {
    public static void main(String[] args) {
        /*创建 SparkConf对象，并设置程序名称，方便在spark集群中查看*/
        /**2种执行方式：
         * 1 提交到spark集群执行：打jar包然后 ./spark-submit --master spark://master128:7077 --class 主类  *.jar  参数1  参数2
         * 2 本地执行：conf.setMaster("Local(4)")，4表示开启 4个线程*/
        SparkConf conf = new SparkConf().setAppName("JavaWordCount02").setMaster("local(*)");
        /**创建JavaSparkContext，作为Java操作Spark的入口*/
        JavaSparkContext jsc = new JavaSparkContext(conf);
        /**读取数据，并转换为 RDD(分布式数据集)；数据集指同类元素的集合，这里指 行的集合；分布式指RDD发到不同worker执行 最后汇总到driver*/
        JavaRDD<String> lines = jsc.textFile(args[0]);
        /**flatMap切分压平；line.split(" ")返回数组，先转为List，再转为 Iterator*/
        JavaRDD<String> words = lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator());
        /**将单词和 1组成 元组；Tuple表示元组，JavaPairRDD表示java中元组RDD*/
        JavaPairRDD<String,Integer> wordAndOne = words.mapToPair(word -> new Tuple2<>(word,1));
        /**对key聚合，将相同key的value进行叠加*/
        JavaPairRDD<String,Integer> reduced = wordAndOne.reduceByKey((x,y) -> x + y);
        /*排序，java里面只有sortByKey()，因此要将 元组reduced中的key和value对调，例如("hello",5)转为(5,"hello")*/
        JavaPairRDD<Integer,String> swaped = reduced.mapToPair(tp -> tp.swap());
        /*根据key进行排序*/
        JavaPairRDD<Integer,String> sorted = swaped.sortByKey(false);
        /*swap()转换 元组tuple里面的key和value*/
        JavaPairRDD<String,Integer> result = sorted.mapToPair(tp -> tp.swap());
        /*将结果保存到 hdfs；result保存到hdfs是分区保存的；*/
//        result.saveAsTextFile(args[1]);
//        result.foreach(new VoidFunction<Tuple2<String, Integer>>() {
//            @Override
//            public void call(Tuple2<String, Integer> tuple) throws Exception {
//                System.out.println(tuple);
//            }
//        });
        /*释放资源*/
        jsc.stop();
    }
}
