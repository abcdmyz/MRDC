package groupby.terasort.algebraic.mr2materialize.stringpair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

import datacube.common.datastructure.CubeLattice;
import datacube.common.datastructure.StringPair;
import datacube.common.datastructure.StringTriple;
import datacube.common.datastructure.Tuple;
import datacube.configuration.DataCubeParameter;

public class TeraSortGroupByMaterializeStringPairMapper extends Mapper<Object, Text, StringPair, IntWritable> 
{
	private CubeLattice cubeLattice;
	ArrayList<Tuple<Integer>> regionTupleBag = new ArrayList<Tuple<Integer>>();
	private IntWritable one = new IntWritable(1);
	private ArrayList<String> boundary;
	private Configuration conf;
	
	@Override
	public void setup(Context context) throws IOException
	{
		conf = context.getConfiguration();
		cubeLattice = new CubeLattice(DataCubeParameter.getTestDataInfor(conf.get("dataset")).getAttributeSize(), DataCubeParameter.getTestDataInfor(conf.get("dataset")).getGroupAttributeSize());
		cubeLattice.calculateAllRegion(DataCubeParameter.getTestDataInfor(conf.get("dataset")).getAttributeCubeRollUp());		
		
		boundary = new ArrayList<String>(Integer.valueOf(conf.get("total.interval.number")));

		String latticePath = conf.get("hdfs.root.path") +  conf.get("dataset") + conf.get("tscube.mr1.output.path") + conf.get("tscube.boundary.file.path");

		Path path = new Path(latticePath);
		FileSystem fs = FileSystem.get(context.getConfiguration());
		BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(path)));
		
		try 
		{
			String line;
			line=br.readLine();
			while (line != null)
			{
				String[] regionSplit = line.split("\t"); 
				boundary.add(regionSplit[0]);
				
				line = br.readLine();
			}
		} 	
		finally 
		{
			br.close();
		}
	}
     
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException 
	{
		String tupleSplit[] = value.toString().split("\t");
		String regionNum = new String();
		String measureString = new String();
		int partitionerID = 0;
		
		int i = Integer.valueOf(conf.get("groupby.region.id"));
		
		regionNum = String.valueOf(i);
		String groupKey = new String();
			
		for (int k = 0; k < cubeLattice.getRegionBag().get(i).getSize(); k++)
		{
			if (cubeLattice.getRegionBag().get(i).getField(k) != null)
			{
				if (groupKey.length() > 0)
				{
					groupKey += " " + tupleSplit[k];
				}
				else
				{
					groupKey += tupleSplit[k];
				}
			}
		}
				
		String boundaryCMPString = groupKey; 
		partitionerID = binarySearchPartitionerBoundary(boundaryCMPString);
			
		StringPair outputKey = new StringPair();
		outputKey.setFirstString(groupKey);
		outputKey.setSecondString(String.valueOf(partitionerID));

		context.write(outputKey, one);
	}
	
	private int binarySearchPartitionerBoundary(String target)
	{
		int head = 0;
		int tail = boundary.size();
		
		if (target.compareTo(boundary.get(0)) <= 0)
		{
			return 0;
		}
		else if (target.compareTo(boundary.get(boundary.size()-1)) > 0)
		{
			return boundary.size();
		}
		
		while (head < tail - 1)
		{
			int mid = (head +  tail) / 2;
			
			if (target.equals(boundary.get(mid)))
			{
				return mid;
			}
			
			if (target.compareTo(boundary.get(mid)) > 0)
			{
				head = mid;
			}
			else
			{
				tail = mid;
			}
		}
		
		return tail;
	}
}