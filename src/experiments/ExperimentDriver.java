package experiments;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import gridWorldAmdp.AmdpDriver;
import teach.gridLearnedAMDP.L_AmdpDriver;

public class ExperimentDriver {
	
	public static void main(String[] args) {
		int iterations = 10000; //set value
		int step_size = 100; //Math.max(iterations/10,1); //prints out reassuring experiment message every step_size;
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
		
			//COMMENT OUT UNINTERESTED METHODS 
			//(note commenting out will cause error for outputting statistics but not visualization).
			Data d_VI = ValueIterationL0.executeValueIteration(experiment, start, goal);
			collectedDataVI.add(d_VI);
			
			Data d_AMDP =  AmdpDriver.executeAmdp(experiment, start, goal);
			collectedDataAMDP.add(d_AMDP);
			
			Data d_LAMDP = L_AmdpDriver.executeAmdp(experiment, start, goal);	
			collectedDataLAMDP.add(d_LAMDP);
		}
		int corpus_size = collectedDataLAMDP.size();
		
		//Data Results 
		double as_VI = 0;
		double t_VI = 0;
		double as_AMDP = 0;
		double t_AMDP = 0;
		double as_LAMDP = 0;
		double t_LAMDP = 0;
		//Mean
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
		double mean_t_LAMDP= t_LAMDP/corpus_size;
		
		//Standard Deviation
		double var_as_VI = 0;
		double var_t_VI = 0;
		double var_as_AMDP = 0;
		double var_t_AMDP = 0;
		double var_as_LAMDP = 0;
		double var_t_LAMDP = 0;
		int correctedSamples = Math.max(1, corpus_size - 1);
		for(int i = 0; i < corpus_size; i++){
			Data d_vi = collectedDataVI.get(i);
			var_as_VI += Math.pow(d_vi.action-mean_as_VI,2)/correctedSamples;
			var_t_VI += Math.pow(d_vi.timing-mean_t_VI,2)/correctedSamples;
			
			Data d_amdp = collectedDataAMDP.get(i);
			var_as_AMDP += Math.pow(d_amdp.action-mean_as_AMDP,2)/correctedSamples;
			var_t_AMDP += Math.pow(d_amdp.timing-mean_t_AMDP,2)/correctedSamples;
			
			Data d_lamdp = collectedDataLAMDP.get(i);
			var_as_LAMDP += Math.pow(d_lamdp.action-mean_as_LAMDP,2)/correctedSamples;
			var_t_LAMDP += Math.pow(d_lamdp.timing-mean_t_LAMDP,2)/correctedSamples;
		}
		double sd_as_VI = Math.sqrt(var_as_VI);
		double sd_t_VI = Math.sqrt(var_t_VI);
		double sd_as_AMDP = Math.sqrt(var_as_AMDP);
		double sd_t_AMDP = Math.sqrt(var_t_AMDP);
		double sd_as_LAMDP = Math.sqrt(var_as_LAMDP);
		double sd_t_LAMDP = Math.sqrt(var_t_LAMDP);
		
		double sqrtCorpusSize = Math.sqrt(corpus_size);
		double se_as_VI = roundDigits(sd_as_VI/sqrtCorpusSize, 2);
		double se_t_VI = roundDigits(sd_t_VI/sqrtCorpusSize, 2);
		double se_as_AMDP = roundDigits(sd_as_AMDP/sqrtCorpusSize,2);
		double se_t_AMDP = roundDigits(sd_t_AMDP/sqrtCorpusSize, 2);
		double se_as_LAMDP = roundDigits(sd_as_LAMDP/sqrtCorpusSize,2);
		double se_t_LAMDP = roundDigits(sd_t_LAMDP/sqrtCorpusSize,2);
		
		System.out.println("\nTotal number of iterations: " + iterations);
		System.out.println("====Value Iteration====");
		System.out.println("Mean Action Sequence Size: " + String.valueOf(mean_as_VI) + " +- " + String.valueOf(se_as_VI));
		//System.out.println("Standard Deviation Action Sequence Size: " + String.valueOf(sd_as_VI));
		System.out.println("Mean Time: " + String.valueOf(mean_t_VI) + " +- " + String.valueOf(se_t_VI));
		//System.out.println("Standard Deviation Time: " + String.valueOf(sd_t_VI));
		System.out.println("====AMDP====");
		System.out.println("Mean Action Sequence Size: " + String.valueOf(mean_as_AMDP) + " +- " + String.valueOf(se_as_AMDP));
		//System.out.println("Standard Deviation Action Sequence Size: " + String.valueOf(sd_as_AMDP));
		System.out.println("Mean Time: " + String.valueOf(mean_t_AMDP) + " +- " + String.valueOf(se_t_AMDP));
		//System.out.println("Standard Deviation Time: " + String.valueOf(sd_t_AMDP));
		System.out.println("====LAMDP====");
		System.out.println("Mean Action Sequence Size: " + String.valueOf(mean_as_LAMDP) + " +- " + String.valueOf(se_as_LAMDP));
		//System.out.println("Standard Deviation Action Sequence Size: " + String.valueOf(sd_as_LAMDP));
		System.out.println("Mean Time: " + String.valueOf(mean_t_LAMDP) + " +- " + String.valueOf(se_t_LAMDP));
		//System.out.println("Standard Deviation Time: " + String.valueOf(sd_t_LAMDP));

//		System.out.println(actionSequences);
//		System.out.println(Times);
	}
	
	private static double roundDigits(double value, int numDigits) {
		return (double)Math.round(value * Math.pow(10, numDigits)) / Math.pow(10, numDigits);
	}
}
