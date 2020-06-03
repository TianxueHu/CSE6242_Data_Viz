// Databricks notebook source
// STARTER CODE - DO NOT EDIT THIS CELL
import org.apache.spark.sql.functions.desc
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import spark.implicits._

// COMMAND ----------

// STARTER CODE - DO NOT EDIT THIS CELL
val customSchema = StructType(Array(StructField("lpep_pickup_datetime", StringType, true), StructField("lpep_dropoff_datetime", StringType, true), StructField("PULocationID", IntegerType, true), StructField("DOLocationID", IntegerType, true), StructField("passenger_count", IntegerType, true), StructField("trip_distance", FloatType, true), StructField("fare_amount", FloatType, true), StructField("payment_type", IntegerType, true)))

// COMMAND ----------

// STARTER CODE - YOU CAN LOAD ANY FILE WITH A SIMILAR SYNTAX. Edit the filepath on line 7 (.load(...)) to point to your uploaded file
val df = spark.read
   .format("com.databricks.spark.csv")
   .option("header", "true") // Use first line of all files as header
   .option("nullValue", "null")
   .schema(customSchema)
   .load("/FileStore/tables/nyc_tripdata-069c2.csv") // UPDATE this line with your filepath. Refer Databricks Setup Guide Step 3.
   .withColumn("pickup_datetime", from_unixtime(unix_timestamp(col("lpep_pickup_datetime"), "MM/dd/yyyy HH:mm")))
   .withColumn("dropoff_datetime", from_unixtime(unix_timestamp(col("lpep_dropoff_datetime"), "MM/dd/yyyy HH:mm")))
   .drop($"lpep_pickup_datetime")
   .drop($"lpep_dropoff_datetime")

// COMMAND ----------

// STARTER CODE - DO NOT EDIT THIS CELL
// Some commands that you can use to see your dataframes and results of the operations. You can alternatively uncomment the display() and show() functions to see the data differently. These two functions will be useful in reporting the results.

//display(df) //display in a tabular format for easy download

df.show(5) // view the first 5 rows of the dataframe

// COMMAND ----------

// BEFORE YOU BEGIN: Replace gburdell3 with your GT username.
val gt_username = "thu82"
println(gt_username)

// COMMAND ----------

// PART 1: Filter the data to only keep the rows where "PULocationID" and the "DOLocationID" are different and the "trip_distance" is strictly greater than 2.0 (>2.0).
// Hint: Checkout the filter() function.
val data = df.filter("PULocationID != DOLocationID").filter($"trip_distance" > 2.0)

//data.show(10)
// VERY VERY IMPORTANT: ALL THE SUBSEQUENT OPERATIONS MUST BE PERFORMED ON THIS FILTERED DATA

// ENTER THE CODE BELOW

// COMMAND ----------

// PART 2: The top-5 most popular drop locations - "DOLocationID", sorted in descending order - if there is a tie, then one with lower "DOLocationID" gets listed first
// Hint: Checkout the groupBy(), orderBy() and count() functions.

val p2 = data.orderBy("DOLocationID").groupBy("DOLocationID").count().orderBy(desc("count")).withColumnRenamed("count","number_of_dropoffs")

p2.show(5)


// COMMAND ----------

// PART 3: The top-5 most popular pickup locations - "PULocationID", sorted in descending order - if there is a tie, then one with lower "PULocationID" gets listed first 

// ENTER THE CODE BELOW
val p3 = data.orderBy("PULocationID").groupBy("PULocationID").count().orderBy(desc("count")).withColumnRenamed("count","number_of_pickups")

p3.show(5)

// COMMAND ----------

// PART 4: The top-5 most popular pickup-dropoff pairs - sorted in descending order - if there is a tie, then one with lower "PULocationID" gets listed first.
val p4 = data.orderBy("PULocationID").groupBy("PULocationID","DOLocationID").count().orderBy(desc("count")).withColumnRenamed("count","count of pickup-dropoff")


p4.show(5)
// ENTER THE CODE BELOW

// COMMAND ----------

// PART 5: Number of dropoffs over the period from January 1, 2019 (inclusive of January 1) to January 5, 2019 (inclusive of January 5). List the entries by day from January 1 to January 5.

// Reference: https://www.obstkel.com/blog/spark-sql-date-functions
// Read in the data and extract the month and year from the date column.
// Hint 1: Observe how we extracted the date from the timestamp in the thrid cell.
// Hint 2: Filter by month as well since there are a few dates for the month of February present in the dataset.

// ENTER THE CODE BELOW
val p5 = data.filter($"dropoff_datetime" >=lit("2019-01-01")).filter($"dropoff_datetime" <=lit("2019-01-06")).groupBy(dayofmonth($"dropoff_datetime".alias("dayofmonth"))).count().withColumnRenamed("dayofmonth(dropoff_datetime AS `dayofmonth`)","DayofJan.").withColumnRenamed("count","total_activity")

//df.select(F.date_format('timestamp','yyyy-MM-dd').alias('day')).groupby('day').count().show()
p5.show()

// COMMAND ----------

// PART 6: List the top-3 locations with the maximum overall activity, i.e. sum of all pickups and all dropoffs at that LocationID. In case of a tie, the lower LocationID gets listed first.
// Hint: Checkout join() and na.drop() functions. You will need to perform a join operation between two dataframes which you created in earlier parts to get the result.

// ENTER THE CODE BELOW

val PU = data.groupBy("PULocationID").count().withColumnRenamed("count", "P")
val DO = data.groupBy("DOLocationID").count().withColumnRenamed("count", "D")

val join=DO.join(PU, PU("PULocationID")===DO("DOLocationID"), "outer" ).withColumn("userID", coalesce(PU("PULocationID"),DO("DOLocationID"))).drop("DOLocationID").drop("PULocationID")

val total = join.withColumn("total", join("P") + join("D")).drop("P").drop("D").orderBy(desc("total")).withColumnRenamed("total","total_activity").withColumnRenamed("userID","locationID")
total.show(3)



