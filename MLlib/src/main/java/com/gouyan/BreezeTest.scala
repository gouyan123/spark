package com.gouyan

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.{DenseMatrix, DenseVector, Vectors}

object breeze_test01 {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("breeze_test01")
    val sc = new SparkContext(conf)
    Logger.getRootLogger.setLevel(Level.WARN)

    // 3.1.1 Breeze 创建函数
    val m1 = DenseMatrix.zeros[Double](2, 3)
    val v1 = DenseVector.zeros[Double](3)
    val v2 = DenseVector.ones[Double](3)
    val v3 = DenseVector.fill(3) { 5.0 }
    /*取1-10之间的元素，产生向量，步长为2*/
    val v4 = DenseVector.range(1, 10, 2)
    val m2 = DenseMatrix.eye[Double](3)
    /*diag()创建对角矩阵；对角矩阵：除了对角线元素，其他元素都是0*/
    val v6 = diag(DenseVector(1.0, 2.0, 3.0))
    /*按行创建 矩阵*/
    val m3 = DenseMatrix((1.0, 2.0), (3.0, 4.0))
    /*创建 向量*/
    val v8 = DenseVector(1, 2, 3, 4)
    /*t 表示转置*/
    val v9 = DenseVector(1, 2, 3, 4).t
    /*从函数创建 3维向量，i=0,1,2*/
    val v10 = DenseVector.tabulate(3) { i => 2 * i }
    /*从函数创建 3 * 2矩阵；i=0,1,2；j=0,1*/
    val m4 = DenseMatrix.tabulate(3, 2) { case (i, j) => i + j }
    /*使用普通数组创建 向量*/
    val v11 = new DenseVector(Array(1, 2, 3, 4))
    /*使用普通数组创建 矩阵*/
    val m5 = new DenseMatrix(2, 3, Array(11, 12, 13, 21, 22, 23))
    /*创建长度为4的随机数组，数值 [0,1]*/
    val v12 = DenseVector.rand(4)
    /*创建 2*3的随机矩阵，数值 [0,1]*/
    val m6 = DenseMatrix.rand(2, 3)

    // 3.1.2 Breeze 元素访问及操作函数
    // 元素访问
    val a = DenseVector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    a(0)
    a(1 to 4)
    a(5 to 0 by -1)
    /*-1代表最后一个*/
    a(1 to -1)
    a(-1)
    val m = DenseMatrix((1.0, 2.0, 3.0), (3.0, 4.0, 5.0))
    /*取0行1列*/
    m(0, 1)
    /*取第1列*/
    m(::, 1)

    // 元素操作
    val m_1 = DenseMatrix((1.0, 2.0, 3.0), (3.0, 4.0, 5.0))
    /*将m_1矩阵转置为 3*2矩阵*/
    m_1.reshape(3, 2)
    /*将m_1矩阵转为向量*/
    m_1.toDenseVector

    val m_3 = DenseMatrix((1.0, 2.0, 3.0), (4.0, 5.0, 6.0), (7.0, 8.0, 9.0))
    /*取m_3的下三角，上三角都为0*/
    lowerTriangular(m_3)
    /*取m_3的上三角，下三角都为0*/
    upperTriangular(m_3)
    /*拷贝 m_3*/
    m_3.copy
    /*取m_3矩阵的主对角线*/
    diag(m_3)
    /*将m_3矩阵的第2列全赋值为5*/
    m_3(::, 2) := 5.0
    m_3
    /*将m_3矩阵的第1,2行，第1,2列都赋值为5*/
    m_3(1 to 2, 1 to 2) := 5.0
    m_3

    val a_1 = DenseVector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    /*将a_1向量的第1-4个元素赋值为5*/
    a_1(1 to 4) := 5
    /*将a_1向量的第1-4个元素替换为数组*/
    a_1(1 to 4) := DenseVector(1, 2, 3, 4)
    a_1
    val a1 = DenseMatrix((1.0, 2.0, 3.0), (4.0, 5.0, 6.0))
    val a2 = DenseMatrix((1.0, 1.0, 1.0), (2.0, 2.0, 2.0))
    /*垂直连接 a1，a2两个矩阵*/
    DenseMatrix.vertcat(a1, a2)
    /*水平连接a1，a2两个矩阵*/
    DenseMatrix.horzcat(a1, a2)
    val b1 = DenseVector(1, 2, 3, 4)
    val b2 = DenseVector(1, 1, 1, 1)
    DenseVector.vertcat(b1, b2)

    // 3.1.3 Breeze 数值计算函数
    val a_3 = DenseMatrix((1.0, 2.0, 3.0), (4.0, 5.0, 6.0))
    val b_3 = DenseMatrix((1.0, 1.0, 1.0), (2.0, 2.0, 2.0))
    /*矩阵对应位置元素相加*/
    a_3 + b_3
    /*矩阵对应位置元素相乘*/
    a_3 :* b_3
    /*矩阵对应位置元素相除*/
    a_3 :/ b_3
    /*矩阵对应位置元素比较，返回true，false矩阵*/
    a_3 :< b_3
    a_3 :== b_3
    /*a_3矩阵的每个元素加1*/
    a_3 :+= 1.0
    a_3 :*= 2.0
    /*取出a_3矩阵中最大值*/
    max(a_3)
    /*返回最大元素所在位置*/
    argmax(a_3)
    DenseVector(1, 2, 3, 4) dot DenseVector(1, 1, 1, 1)

    // 3.1.4 Breeze 求和函数
    val a_4 = DenseMatrix((1.0, 2.0, 3.0), (4.0, 5.0, 6.0), (7.0, 8.0, 9.0))
    sum(a_4)
    /*矩阵按列求和*/
    sum(a_4, Axis._0)
    /*矩阵按行求和*/
    sum(a_4, Axis._1)
    /*对矩阵对角线求和*/
    trace(a_4)
    /*对向量累计求和，每个元素都等于前面所有元素的和*/
    accumulate(DenseVector(1, 2, 3, 4))

    // 3.1.5 Breeze 布尔函数
    val a_5 = DenseVector(true, false, true)
    val b_5 = DenseVector(false, true, true)
    a_5 :& b_5
    a_5 :| b_5
    !a_5
    val a_5_2 = DenseVector(1.0, 0.0, -2.0)
    /*a_5_2里面任意一个元素为0，返回true*/
    any(a_5_2)
    /*a_5_2里面所有元素为0，返回true*/
    all(a_5_2)

    // 3.1.6 Breeze 线性代数函数
    val a_6 = DenseMatrix((1.0, 2.0, 3.0), (4.0, 5.0, 6.0), (7.0, 8.0, 9.0))
    val b_6 = DenseMatrix((1.0, 1.0, 1.0), (1.0, 1.0, 1.0), (1.0, 1.0, 1.0))
    /*a_6除以b_6，注意是矩阵的除法，不是对应位置元素的除法*/
    a_6 \ b_6
    a_6.t
    /*求特征值*/
    det(a_6)
    /*求逆矩阵*/
    inv(a_6)
    /*奇异值分解*/
    val svd.SVD(u, s, v) = svd(a_6)
    /*矩阵行数*/
    a_6.rows
    /*矩阵列数*/
    a_6.cols
    /*求矩阵的秩*/
    rank(a_6)
    // 3.1.7 Breeze 取整函数
    val a_7 = DenseVector(1.2, 0.6, -2.3)
    /*四舍五入*/
    round(a_7)
    /*向右取整*/
    ceil(a_7)
    /*向左取整*/
    floor(a_7)
    signum(a_7)
    abs(a_7)
  }
}
