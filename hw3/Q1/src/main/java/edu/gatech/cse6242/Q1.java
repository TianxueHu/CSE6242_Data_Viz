package edu.gatech.cse6242;

import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class Q1 {
/* TODO: Update variable below with your gtid */
  final String gtid = "thu82";

  public static class TokenizerMapper extends Mapper<Object, Text, Text, Text>{
    float distance = 0;
    float totalFare = 0;
    private Text PUID = new Text();
    public void map(Object key, Text value, Context context
) throws IOException, InterruptedException{
      String data[] = value.toString().split(",");
      PUID.set(data[0]);
      distance=Float.parseFloat(data[2]);
      totalFare=Float.parseFloat(data[3]);
      
      if (data.length == 4 && totalFare > 0 && distance != 0){
        context.write(PUID, new Text(data[3]));
      }
    }
  }
  public static class IntSumReducer extends Reducer<Text, Text, Text, Text>{
    
    public void reduce(Text key, Iterable<Text> values, Context context
)throws IOException, InterruptedException{
      int tripSUM = 0;
      float fareSUM = 0;
      for (Text val: values){
        tripSUM += 1;
        fareSUM += Float.parseFloat(val.toString());
      }
      String output = String.format("%,d,%,.2f", tripSUM, fareSUM);
      context.write(new Text(key), new Text(output));
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Q1");

    /* TODO: Needs to be implemented */
    job.setJarByClass(Q1.class);
    job.setMapperClass(TokenizerMapper.class);
    // job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);


  }
}
