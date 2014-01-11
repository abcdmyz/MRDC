package mrcube.main;

import java.util.ArrayList;

import test.CubeLatticeTest;
import mrcube.holistic.CubeLattice;
import mrcube.holistic.Tuple;
import mrcube.holistic.mr1estimate.HolisticMRCubeEstimate;
import mrcube.holistic.mr2materialize.HolisticMRCubeMaterialize;
import mrcube.naive.NaiveMRCube;
import mrcube.naive.text.NaiveMRCubeText;
import mrcube.reducerlearning.ReducerLearningMRCube;

public class MRCubeMain 
{
	public static void main(String[] args) throws Exception 
	{
		//NaiveMRCube mrCube = new NaiveMRCube();
		//mrCube.run(args);
		
		//NaiveMRCubeText mrCube = new NaiveMRCubeText();
		//mrCube.run(args);
		
		//ReducerLearningMRCube mrCube = new ReducerLearningMRCube();
		//mrCube.run(args);
		
		//LatticeTest.exec();
		//CubeLatticeTest.exec();
		
		//HolisticMRCubeEstimate mrCube = new HolisticMRCubeEstimate();
		HolisticMRCubeMaterialize mrCube = new HolisticMRCubeMaterialize();
		mrCube.run(args);
	}
}
;