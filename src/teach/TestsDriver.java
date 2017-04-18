package teach;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import burlap.behavior.singleagent.Episode;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SimpleLogistic;
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

		Trajectory[] Traj_array = generateTrajectories(file_directory);
		System.out.println("Execution complete. Num traj's: " + Traj_array.length);

		Classifier class1 = trainClassifierFromLabelsOnly(Traj_array, 1);// 1 is the upper right room

		// TODO
	}

	// TODO: Train based on existing classifiers + labels
	public static Classifier trainClassifierFromLabelsOnly(Trajectory[] trajectories, int labelOfInterest) {
		// Train a classifier for inA/notInA

		// State first = trajectories[0].getStateSequence().get(0);
		// System.out.println(first.variableKeys());
		// for(int i = 0; i < first.variableKeys().size(); i++){
		// System.out.println(first.variableKeys().get(i));
		// System.out.println(first.get(first.variableKeys().get(i)));
		// }

		Attribute agentX = new Attribute("agent:x");
		Attribute agentY = new Attribute("agent:y");
		ArrayList<String> classLabel = new ArrayList<String>(2);
		classLabel.add("positive");
		classLabel.add("negative");
		Attribute classAttribute = new Attribute("class", classLabel);
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(agentX);
		attributes.add(agentY);
		attributes.add(classAttribute);

		// Create an empty training set
		Instances isTrainingSet = new Instances("Rel", attributes, trajectories.length * 2);
		// Set class index
		isTrainingSet.setClassIndex(attributes.size() - 1);// The final attribute

		// Pull out agent:x, agent:y locations for states labeled
		// labelOfInterest
		for (int i = 0; i < trajectories.length; i++) {
			// Create the start instance
			State inst = trajectories[i].getStateSequence().get(0);
			Instance iExample = new DenseInstance(attributes.size());
			iExample.setValue((Attribute) attributes.get(0), (Integer)inst.get("agent:x"));
			iExample.setValue((Attribute) attributes.get(1), (Integer)inst.get("agent:y"));
			if (trajectories[i].start == labelOfInterest) {
				iExample.setValue((Attribute) attributes.get(2), "positive");
			} else {
				iExample.setValue((Attribute) attributes.get(2), "negative");
			}
			// add the instance
			isTrainingSet.add(iExample);
			
			// Create the other instance
			int stateSequenceSize = trajectories[i].getStateSequence().size();
			inst = trajectories[i].getStateSequence().get(stateSequenceSize - 1);
			iExample = new DenseInstance(attributes.size());
			iExample.setValue((Attribute) attributes.get(0), (Integer) inst.get("agent:x"));
			iExample.setValue((Attribute) attributes.get(1), (Integer) inst.get("agent:y"));
			if (trajectories[i].end == labelOfInterest) {
				iExample.setValue((Attribute) attributes.get(2), "positive");
			} else {
				iExample.setValue((Attribute) attributes.get(2), "negative");
			}
			// add the instance
			isTrainingSet.add(iExample);
		}
		
		System.out.println(isTrainingSet.numInstances());
		
		// TODO: Train Classifier
		Classifier stateIdentifier = (Classifier) new SimpleLogistic();//TODO: Gaussian Process.
		try {
			stateIdentifier.buildClassifier(isTrainingSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Trained Classifier.");
		
		//Test:
		Instance testInference = new DenseInstance(attributes.size());
		testInference.setValue((Attribute) attributes.get(0), 6);
		testInference.setValue((Attribute) attributes.get(1), 6);
		testInference.setDataset(isTrainingSet);//Re-use data-set format.
		// Get the likelihood of each classes
		// fDistribution[0] is the probability of being “positive”
		// fDistribution[1] is the probability of being “negative”
		double[] fDistribution = {};
		try {
			fDistribution = stateIdentifier.distributionForInstance(testInference);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Test Inference:");
		for(int i = 0; i< fDistribution.length; i++){
			System.out.println(fDistribution[i]);
		}
		
		return stateIdentifier;
	}

	public static boolean testTrajectories() {
		return false;// TODO: Replace with actual tests
	}

	// This method doesn't serve any purpose other than documentation
	public static String getActionName(Action a) {
		return a.actionName();
	}

	// Function may not be robust to different state formulations
	public static int[] getStateXY(State s) {
		List<Object> o = s.variableKeys();
		int x = (Integer) s.get(o.get(0));
		int y = (Integer) s.get(o.get(1));

		int[] a = { x, y };
		return a;
	}

	// Takes in directory of episode saved under annotated file names
	// and returns an array of Trajectories.
	public static Trajectory[] generateTrajectories(String directoryName) {
		File directory = new File(directoryName);
		List<Episode> episodes = Episode.readEpisodes(directoryName);

		File[] fList = directory.listFiles();
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
