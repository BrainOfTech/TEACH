package teach.gridLearnedAMDP;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import experiments.Data;
import experiments.ValueIterationL0;
import gridWorldAmdp.AmdpDriver;

public class LearnedGridExperimentDriver {
	
	public static void main(String[] args) {
		int iterations = 1; //set value
		int step_size = 1; //prints out reassuring experiment message every step_size;
		Boolean experiment = true; //true will suppress all graph visualizations 

		List<Data> collectedDataVI = new ArrayList<Data>();
		List<Data> collectedDataAMDP = new ArrayList<Data>();
		List<Data> collectedDataLAMDP = new ArrayList<Data>();
		
		//Goal Locations
		HashMap<Integer,String> gl = new HashMap<Integer,String>();
		gl.put(1, "room1");
		gl.put(2, "room2");
		gl.put(3, "room3");
		gl.put(4, "room4");
	
		//Start Locations (via corners) 
		int[]c1 = {10,10}; //corners 
		int[]c2 = {0,10};
		int[]c3 = {0,0};
		int[]c4 = {10,0};
		
		HashMap<Integer, int[]> sl = new HashMap<Integer,int[]>();
		sl.put(1, c1);
		sl.put(2, c2);
		sl.put(3, c3);
		sl.put(4, c4);
	   
		//Random Generation
		int max = 4;
		int min = 1;
		Random rn = new Random();
		
		//Execute Experiments
		for (int i = 0; i < iterations; i++){
			int rnStart = 0;
			int rnGoal = 0;
			while (true){ //exclude "in the same room as goal" conditions
				rnStart = rn.nextInt((max - min) + 1) + min;
				rnGoal = rn.nextInt((max - min) + 1) + min;
				if (rnStart != rnGoal){
					break;
				}
			}

			int[] start = sl.get(rnStart); //{10,10};
			String goal = gl.get(rnGoal); //"room2";
			
			if (i%step_size == 0){
				System.out.println("====Experiment " + String.valueOf(i)+ "====");
				System.out.println("Start Location: " + String.valueOf(start[0]) + "," + String.valueOf(start[1]) 
				+ "\nGoal Location: " + goal);
			}
		
			Data d_VI = ValueIterationL0.executeValueIteration(experiment, start, goal);
			System.out.println("---------------");
			Data d_AMDP =  AmdpDriver.executeAmdp(experiment, start, goal);
			System.out.println("---------------");
			Data d_LAMDP = LearnedAMDPDriverDebug.executeAmdp(experiment, start, goal);
			System.out.println("---------------");
			collectedDataVI.add(d_VI);
			collectedDataAMDP.add(d_AMDP);
			collectedDataLAMDP.add(d_LAMDP);
		}
		int corpus_size = collectedDataVI.size();
		
		//Data Results 
		double as_VI = 0;
		double t_VI = 0;
		
		double as_AMDP = 0;
		double t_AMDP = 0;
		
		double as_LAMDP = 0;
		double t_LAMDP = 0;
		
		for(int i = 0; i < corpus_size; i++){
			Data d_vi = collectedDataVI.get(i);
			as_VI += d_vi.action;
			t_VI += d_vi.timing;
			
			Data d_amdp = collectedDataAMDP.get(i);
			as_AMDP += d_amdp.action;
			t_AMDP += d_amdp.timing;
			
			Data d_lamdp = collectedDataLAMDP.get(i);
			as_LAMDP += d_lamdp.action;
			t_LAMDP += d_lamdp.timing;
		}
		double mean_as_VI = as_VI/corpus_size;
		double mean_t_VI = t_VI/corpus_size;
		
		double mean_as_AMDP = as_AMDP/corpus_size;
		double mean_t_AMDP= t_AMDP/corpus_size;
		
		double mean_as_LAMDP = as_LAMDP/corpus_size;
		double mean_t_LAMDP = t_LAMDP/corpus_size;//TODO: refactor to eliminate all this redundancy.
		//TODO: Compute standard deviations as well.
		
		System.out.println("\nTotal number of iterations: " + iterations);
		System.out.println("====Value Iteration====");
		System.out.println("Mean Action Sequence Size: " + String.valueOf(mean_as_VI));
		System.out.println("Mean Time : " + String.valueOf(mean_t_VI));
		System.out.println("====AMDP====");
		System.out.println("Mean Action Sequence Size: " + String.valueOf(mean_as_AMDP));
		System.out.println("Mean Time : " + String.valueOf(mean_t_AMDP));
		System.out.println("====Learned AMDP====");
		System.out.println("Mean Action Sequence Size: " + String.valueOf(mean_as_LAMDP));
		System.out.println("Mean Time : " + String.valueOf(mean_t_LAMDP));
//		System.out.println(actionSequences);
//		System.out.println(Times);
	}
}
