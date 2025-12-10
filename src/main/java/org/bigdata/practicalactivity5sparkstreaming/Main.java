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
import org.apache.spark.sql.functions.*;

public class Main {
    public static void main(String[] args) throws Exception {
        SparkSession spark = SparkSession.builder()
                .appName("Structured Streaming with Update Mode")
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

        // Read from HDFS (micro-batch only)
        Dataset<Row> rawStream = spark
                .readStream()
                .schema(schema)
                .option("header", "true")
                .csv("hdfs://namenode:8020/data");

        // Example: Aggregation (required for Update mode to make sense)
        Dataset<Row> aggregated = rawStream
                .groupBy("product")
                .sum("total")
                .withColumnRenamed("sum(total)", "total_revenue");

        // Write with Update mode
        StreamingQuery query = aggregated
                .writeStream()
                .outputMode(OutputMode.Update()) // Update mode
                .format("console")
                .option("truncate", false)
                .trigger(Trigger.ProcessingTime("10 seconds"))
                .start();

        query.awaitTermination();
    }
}