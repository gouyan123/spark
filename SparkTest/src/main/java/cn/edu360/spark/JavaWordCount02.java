package cn.edu360.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;

public class JavaWordCount02 {
    public static void main(String[] args) {
        /*创建 SparkConf对象，并设置程序名称，方便在spark集群中查看*/
        SparkConf conf = new SparkConf().setAppName("JavaWordCount02");
        /**创建JavaSparkContext，作为Java操作Spark的入口*/
        JavaSparkContext jsc = new JavaSparkContext(conf);
        /**读取数据，并转换为 RDD(分布式数据集)；数据集指同类元素的集合，这里指 行的集合；分布式指RDD发到不同worker执行 最后汇总到driver*/
        JavaRDD<String> lines = jsc.textFile(args[0]);
        /**切分压平；java不支持函数式编程，但是支持lamda表达式，lamda表达式可以看做函数式编程；怎么实现函数式编程？
         * 传一个接口的实现类，接口定义了规范即传递的参数和返回的结果；可以 new 接口的实现类，把业务逻辑通过实现类传进去，通常要写一个匿名回调
         * 跟flatMap()，发现这个方法要传 FlatMapFunction[T, R]接口作为flatMap()的参数，[T, R]：T传入值类型，R表示返回值类型*/
        JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String line) throws Exception {
                /*line.split(" ")返回数组，先转为 List，再转为 Iterator*/
                return Arrays.asList(line.split(" ")).iterator();
            }
        });
        /**将 单词 和 1 组成元组；java里面只能调 mapToPair()方法，因为java里面没有元组概念；map返回元组*/
        JavaPairRDD<String,Integer> wordAndOne = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String word) throws Exception {
                return new Tuple2<>(word,1);
            }
        });
    }
}
