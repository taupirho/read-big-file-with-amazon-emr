package spark-scala

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.sql._
import org.apache.log4j._
import java.util.Calendar

object test {  
  /** Our main function where the action happens */
  def main(args: Array[String]) {
    
    
    println(Calendar.getInstance.getTime)
    
    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)

    println("Setting up Spark session")
    
    // Use new SparkSession interface in Spark 2.0
    // we comment out some elements not required when running
    // on a cluster
    val spark = SparkSession
      .builder
      .appName("SparkSQL")
//      .master("spark://127.0.0.1:7077")
//      .master("local[*]")
//      .config("spark.sql.warehouse.dir", "file:///C:/temp") // Necessary to work around a Windows bug in Spark 2.0.0; omit if you're not on Windows.
      .getOrCreate()
    
    println("Reading in input file")
    
    // S3 bucket named obfuscated for privacy reasons
    val df = spark.read.format("com.databricks.spark.csv").option("header", "false").option("inferSchema", "true").option("delimiter", "|").load("s3n://XXXXXXXXXX/iholding/issueholding.txt")
    
    println("Adding column to DF")
    
    val newdf = df.withColumn("period", df("_c1"))
    
    println("Writing out data to files")
    
    // S3 bucket named obfuscated for privacy reasons
    newdf.write.partitionBy("period").format("com.databricks.spark.csv").option("header", "false").option("delimiter", "|").mode("overwrite").save("s3n://XXXXXXXXXX/iholding/myfiles")

    println(Calendar.getInstance.getTime)
    
    spark.stop()
  }
}
