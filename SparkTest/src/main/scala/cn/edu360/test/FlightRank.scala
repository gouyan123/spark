package cn.edu360.test

import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object FlightRank {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("FlightRank").master("local[*]").getOrCreate()
    val flitht :DataFrame = spark.read.format("jdbc").options(
      Map("url" -> "jdbc:mysql://172.17.1.242:3306/userbehaviorrecorddb",
        "driver" -> "com.mysql.jdbc.Driver",
        "dbtable" -> "flightqueryrecord201704",
        "user" -> "write",
        "password" -> "write123",
        "useSSL" -> "false")
    ).load()

    /*打印 mysql的schema*/
//    flitht.printSchema()
    flitht.createTempView("v_wc")
    val company : DataFrame = spark.sql("SELECT CompanyKeyID, COUNT(*) counts FROM v_wc GROUP BY CompanyKeyID ORDER BY counts DESC")
    company.show()
//    val line : DataFrame = spark.sql("SELECT concat(FromCity,'-',ArrCity) as line ,COUNT(*) counts FROM v_wc  GROUP BY concat(FromCity,'-',ArrCity)  ORDER BY counts DESC")
    val line : DataFrame = spark.sql("SELECT CompanyKeyID,concat(FromCity,'-',ArrCity) as line ,COUNT(*) counts FROM v_wc  where CompanyKeyID = '1703011132444700c0400231345' GROUP BY CompanyKeyID,concat(FromCity,'-',ArrCity)  ORDER BY counts DESC")
    line.show()
    val time : DataFrame = spark.sql("SELECT distinct AddTime,TakeOffDate ,datediff(TakeOffDate,AddTime) as diff FROM v_wc ORDER BY diff DESC")
    time.show()
    val rule : DataFrame = spark.sql("SELECT CompanyKeyID as id,DAY(TakeOffDate) as day ,COUNT(*) count FROM v_wc where CompanyKeyID = '1703011132444700c0400231345' GROUP BY CompanyKeyID,DAY(TakeOffDate) ORDER BY count DESC")
    rule.show()
    spark.stop()
  }
}
