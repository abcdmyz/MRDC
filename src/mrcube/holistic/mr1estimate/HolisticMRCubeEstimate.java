package mrcube.holistic.mr1estimate;


import mrcube.configuration.MRCubeParameter;
import mrcube.holistic.common.StringPair;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.*;
import org.apache.hadoop.util.GenericOptionsParser;

public class HolisticMRCubeEstimate 
{
	public void run(Configuration conf) throws Exception 
	{
		Job job = new Job(conf, "mrcube_mr1");
		job.setJarByClass(HolisticMRCubeEstimate.class);
		
		job.setMapperClass(HolisticMRCubeEstimateMapper.class);
		job.setReducerClass(HolisticMRCubeEstimateReducer.class);

		job.setMapOutputKeyClass(StringPair.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setPartitionerClass(StringPairPartitioner.class);
		job.setSortComparatorClass(StringPairKeyComparator.class);
		job.setGroupingComparatorClass(StringPairGroupComparator.class);
    
		job.setInputFormatClass(TextInputFormat.class);
		job.setNumReduceTasks(1);

		String inputPath = conf.get("hdfs.root.path") + conf.get("dataset") + conf.get("dataset.input.path") + conf.get("total.tuple.size");
		String outputPath = conf.get("hdfs.root.path") +  conf.get("dataset") + conf.get("mrcube.mr1.output.path");  
		
		System.out.println("mr1 input: " + inputPath);
		System.out.println("mr1 output: " + outputPath);
		
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		job.waitForCompletion(true);
	}

}
