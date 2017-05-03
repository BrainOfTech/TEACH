package teach;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import burlap.behavior.functionapproximation.dense.DenseStateFeatures;
import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.learnfromdemo.RewardValueProjection;
import burlap.behavior.singleagent.learnfromdemo.mlirl.MLIRL;
import burlap.behavior.singleagent.learnfromdemo.mlirl.MLIRLRequest;
import burlap.behavior.singleagent.learnfromdemo.mlirl.commonrfs.LinearStateDifferentiableRF;
import burlap.behavior.singleagent.learnfromdemo.mlirl.differentiableplanners.DifferentiableSparseSampling;
import burlap.behavior.valuefunction.QProvider;
import burlap.debugtools.RandomFactory;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.OODomain;
import burlap.mdp.core.oo.propositional.GroundedProp;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.meta.LogitBoost;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

//A driver for experiments with abstraction on the 4-rooms domain.
public class TestsDriver {
	// The primary purpose of this class is to hold the main method and run
	// the helper tests in the following methods.
	public static void main(String[] args) {
		final String file_directory = System.getProperty("user.dir") + "/trajectory";

		Trajectory[] traj_array = generateTrajectories(file_directory);
		//System.out.println("Execution complete. Num traj's: " + traj_array.length);
		
		LearningFunctions lsm = LearningFunctions.buildMapping(traj_array);
		ArrayList<Attribute> attributes = lsm.getAttributes();
		
		//Defining Training Sets and Classifiers
		Instances training1 = Trajectory.trainingForLabel(traj_array, 1, attributes);
		Classifier class1 = LearningFunctions.trainClassifierFromLabelsOnly(training1, attributes);// 1 is the upper right room
		Instances training2 = Trajectory.trainingForLabel(traj_array, 2, attributes);
		Classifier class2 = LearningFunctions.trainClassifierFromLabelsOnly(training2, attributes);
		Instances training3 = Trajectory.trainingForLabel(traj_array, 3, attributes);
		Classifier class3 = LearningFunctions.trainClassifierFromLabelsOnly(training3, attributes);
		Instances training4 = Trajectory.trainingForLabel(traj_array, 4, attributes);
		Classifier class4 = LearningFunctions.trainClassifierFromLabelsOnly(training4, attributes);
		
		// Test:
		for(int y = 10; y>=0; y--){
			for(int x = 0; x <= 10; x++){
				Instance testInference = new DenseInstance(attributes.size());
				testInference.setValue((Attribute) attributes.get(0), x);
				testInference.setValue((Attribute) attributes.get(1), y);
				testInference.setDataset(training1);// Re-use data-set format.
				// Get the likelihood of each classes
				// fDistribution[0] is the probability of being �positive�
				// fDistribution[1] is the probability of being �negative�
				double[] fDistribution = {0,0,0,0};
				try {
					fDistribution[0] = class1.distributionForInstance(testInference)[0];
					fDistribution[1] = class2.distributionForInstance(testInference)[0];
					fDistribution[2] = class3.distributionForInstance(testInference)[0];
					fDistribution[3] = class4.distributionForInstance(testInference)[0];
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.print(argmax(fDistribution) + 1 + " ");
//				if(fDistribution[0]>.5){
//					System.out.print(1 + " ");
//				} else {
//					System.out.print(0 + " ");
//				}
				
//				for(int i = 0; i < fDistribution.length; i++){
//					System.out.print(fDistribution[i] + " ");
//				}
//				System.out.println("");
			}
			System.out.println("");
		}
		
		//TODO: RHIRL
		Episode[] episodes12 = filterEpisodes(traj_array, 1, 2);
		//1-2; 2-1
		runIRL(episodes12);
		//2-3; 3-2
		//3-4; 4-3
		//4-1; 1-4
		
	}//End Main
	
	public static Episode[] filterEpisodes(Trajectory[] trajs, int label1, int label2){
		LinkedList<Episode> episodes = new LinkedList<Episode>();
		for(int i = 0; i < trajs.length; i++){
			if (trajs[i].start == label1 && trajs[i].end == label2){
				episodes.add(trajs[i].e);
			}
		}
		return episodes.toArray(new Episode[0]);
	}
	
	public static void runIRL(Episode[] episodes) {
		
		System.out.println("TODO: Fill in actual method");
	}
	
	public static int argmax(double[] elems) {
		int bestIdx = -1;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < elems.length; i++) {
			double elem = elems[i];
			if (elem > max) {
				max = elem;
				bestIdx = i;
			}
		}
		return bestIdx;
	}

	// This method doesn't serve any purpose other than documentation
	public static String getActionName(Action a) {
		return a.actionName();
	}

	// Takes in directory of episode saved under annotated file names
	// and returns an array of Trajectories.
	public static Trajectory[] generateTrajectories(String directoryName) {
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();
		if (fList[0].getName().equals(".DS_Store")){
			File[] newlist = Arrays.copyOfRange(fList, 1, fList.length);
			fList = newlist;
		}
		List<Episode> episodes = Episode.readEpisodes(directoryName);
		Trajectory[] T = new Trajectory[episodes.size()];
		for (int i = 0; i < episodes.size(); i++) {
			String[] split = fList[i].getName().split("_");
			String start_symbol = split[1], end_symbol = split[3];

			// Convert symbol e.g. 'a' into number 1 via ascii
			int start_code = (int) start_symbol.charAt(0) - 96;
			int end_code = (int) end_symbol.charAt(0) - 96;

			Trajectory t = new Trajectory();
			t.e = episodes.get(i);
			t.start = start_code;
			t.end = end_code;
			T[i] = t; // add to trajectories
		}

		return T;
	}
}
