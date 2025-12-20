package org.bigdata.practicalactivity5sparkstreaming;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class Main {
    public static void main(String[] args) throws Exception {
        SparkSession spark = SparkSession.builder()
                .appName("Structured Streaming with Update Mode")
                .master("spark://spark-master:7077")
                .config("spark.hadoop.fs.defaultFS", "hdfs://namenode:8020")
                .getOrCreate();

        StructType schema = new StructType(new StructField[]{
                new StructField("order_id", DataTypes.LongType, false, Metadata.empty()),
                new StructField("client_id", DataTypes.LongType, false, Metadata.empty()),
                new StructField("client_name", DataTypes.StringType, false, Metadata.empty()),
                new StructField("product", DataTypes.StringType, false, Metadata.empty()),
                new StructField("quantity", DataTypes.IntegerType, false, Metadata.empty()),
                new StructField("price", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("order_date", DataTypes.StringType, false, Metadata.empty()),
                new StructField("status", DataTypes.StringType, false, Metadata.empty()),
                new StructField("total", DataTypes.DoubleType, false, Metadata.empty())
        });

        // Read from HDFS
        Dataset<Row> rawStream = spark
                .readStream()
                .schema(schema)
                .option("header", "true")
                .option("maxFilesPerTrigger", 1) // Traiter 1 fichier à la fois
                .csv("hdfs://namenode:8020/data/stream");

        // Aggregation par produit
        Dataset<Row> aggregated = rawStream
                .groupBy("product")
                .agg(
                        org.apache.spark.sql.functions.sum("total").alias("total_revenue"),
                        org.apache.spark.sql.functions.count("order_id").alias("order_count")
                );

        // Write avec Update mode
        StreamingQuery query = aggregated
                .writeStream()
                .outputMode(OutputMode.Update())
                .format("console")
                .option("truncate", false)
                .trigger(Trigger.ProcessingTime("10 seconds"))
                .start();

        query.awaitTermination();
    }
}