package edu.gatech.cse6242;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.IOException;
import java.util.StringTokenizer;


public class Q4a {

  public static class DegMapper extends Mapper<Object, Text, Text, Text>{
    private Text from = new Text();
    private Text to = new Text();
    private Text in_deg = new Text("1");
    private Text out_deg = new Text("-1");

    public void map(Object key, Text values, Context context
) throws IOException, InterruptedException{
       	String data[] = values.toString().split("\t");
        from.set(data[0]);
    	to.set(data[1]);
     	context.write(from, in_deg);
       	context.write(to, out_deg);
     }
  }


  public static class DegReducer extends Reducer<Text, Text, Text, Text> {
    public void reduce(Text key, Iterable<Text> values, Context context
) throws IOException, InterruptedException {
      int diff = 0;

      for (Text val : values){
        diff += Integer.parseInt(val.toString());
      }  
      context.write(new Text(Integer.toString(diff)), new Text("1"));	  
    }
  }

  public static class DiffMapper extends Mapper<Object, Text, Text, Text> {
    private Text diff1 = new Text();
    private Text diff2 = new Text();

    public void map (Object key, Text values, Context context
) throws IOException, InterruptedException {
      String data[] = values.toString().split("\t");
      diff1.set(data[0]);
      diff2.set(data[1]);
      context.write(diff1, diff2);	
    }
  }


  
  public static class DiffReducer extends Reducer<Text, Text, Text, Text> {
    public void reduce(Text key, Iterable<Text> values, Context context
)throws IOException, InterruptedException {
      int count = 0;
      for (Text val : values){
	count += 1;
      }
     
      context.write(key,new Text(Integer.toString(count)));
    }
  }
  

  public static void main(String[] args) throws Exception {

    /* TODO: Update variable below with your gtid */
    final String gtid = "thu82";
    Configuration conf = new Configuration();
    Job j1 = Job.getInstance(conf, "Q4a");

    j1.setJarByClass(Q4a.class);
    j1.setMapperClass(DegMapper.class);
    //j1.setCombinerClass(DegReducer.class);
    j1.setReducerClass(DegReducer.class);
    j1.setOutputKeyClass(Text.class);
    j1.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(j1, new Path(args[0]));
    FileOutputFormat.setOutputPath(j1, new Path("tmp"));
    j1.waitForCompletion(true);

    Job j2 = Job.getInstance(conf, "Q4a");
    j2.setJarByClass(Q4a.class);
    j2.setMapperClass(DiffMapper.class);
    j2.setReducerClass(DiffReducer.class);
    //j2.setCombinerClass(DiffReducer.class);
    j2.setOutputKeyClass(Text.class);
    j2.setOutputValueClass(Text.class);
    
    

    /* TODO: Needs to be implemented */
  

    FileInputFormat.addInputPath(j2, new Path("tmp"));
    FileOutputFormat.setOutputPath(j2, new Path(args[1]));

    System.exit(j2.waitForCompletion(true) ? 0 : 1);
  }
}
