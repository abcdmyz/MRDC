package WordCountStringPair;

import java.io.IOException;


import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

import datacube.common.StringPair;

public class WordCountStringPairReducer extends Reducer<StringPair, IntWritable, Text, IntWritable> 
{
	@Override
	public void reduce(StringPair key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
	{
		calculationWordCount(key, values, context);
		
		//justPrintKeyValue(key, values, context);
	}
	
	private void justPrintKeyValue(StringPair key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
	{
		Text outputKey = new Text();
	
		for (IntWritable val : values) 
	    {
			outputKey.set(key.getFirstString().toString());
			
			context.write(outputKey, val);
	    }
	}

	private void calculationWordCount(StringPair key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
	{
		String last = null;
		int count = 0;

		for (IntWritable val : values) 
	    {
			count++;

			last = key.getSecondString();
	    }	
		
		Text outputKey = new Text();
		outputKey.set(key.getFirstString());
		IntWritable outputValue = new IntWritable(count);
		context.write(outputKey, outputValue);
	}
}