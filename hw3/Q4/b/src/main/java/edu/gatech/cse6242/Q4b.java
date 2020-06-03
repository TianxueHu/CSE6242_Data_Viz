package edu.gatech.cse6242;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.IOException;

public class Q4b {

  public static class TripMapper extends Mapper<Object, Text, Text, Text>{
  
    private Text totalFare = new Text();
    private Text psCount = new Text();
    public void map(Object key, Text value, Context context
) throws IOException, InterruptedException{
      String data[] = value.toString().split("\t");
      psCount.set(data[2]);
      totalFare.set(data[3]);
      
      context.write(psCount, totalFare);
      
    }
  }
  public static class SumReducer extends Reducer<Text, Text, Integer, Text>{
    
    public void reduce(Text key, Iterable<Text> values, Context context
)throws IOException, InterruptedException{
      int tripSUM = 0;
      float fareSUM = 0;
      for (Text val: values){
        tripSUM += 1;
        fareSUM += Float.parseFloat(val.toString());
      }
      String avgFare = String.format("%.2f", fareSUM/tripSUM);
      context.write(new Integer(key.toString()), new Text(avgFare));
    }
  }

  public static void main(String[] args) throws Exception {
    /* TODO: Update variable below with your gtid */
    final String gtid = "thu82";

    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Q4b");

    job.setJarByClass(Q4b.class);
    job.setMapperClass(TripMapper.class);
    //job.setCombinerClass(SumReducer.class);
    job.setReducerClass(SumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    /* TODO: Needs to be implemented */

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
