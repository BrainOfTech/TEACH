package teach;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import burlap.mdp.auxiliary.StateMapping;
import burlap.mdp.core.oo.state.MutableOOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import gridWorldAmdp.AmdpStateMapper;
import gridWorldL0.AmdpL0Agent;
import gridWorldL0.AmdpL0Domain;
import gridWorldL0.AmdpL0Room;
import gridWorldL0.AmdpL0State;
import gridWorldL1.AmdpL1Agent;
import gridWorldL1.AmdpL1Room;
import gridWorldL1.AmdpL1State;
import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.meta.LogitBoost;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Learned StateMapping is constructed with training trajectories.
 * It applies abstract labels to the state based on the learned classifications.
 * The questionably named "ObjectInstance" "agent" stores the location information.
 * 
 * @author (S)am
 *
 */
//TODO: Add relearning based on additional trajectories.
//TODO: Add relearning based on unsupervised exploration.
public class LearningFunctions{
	
	ArrayList<Attribute> attributes;
	Trajectory[] inputData;
	int[] labels;
	HashMap<Integer, Classifier> classifiers;
	
	/**
	 * Don't call this constructor directly, but instead use the factory method
	 * {@link LearningFunctions#buildMapping(Trajectory[])}
	 * @param labeledTrajectories an array of input trajectories.
	 */
	private LearningFunctions(Trajectory[] labeledTrajectories){
		//Create Attributes Vector for Training.
		//TODO: Create from data.
		Attribute agentX = new Attribute("agent:x");
		Attribute agentY = new Attribute("agent:y");
		ArrayList<String> classLabel = new ArrayList<String>(2);
		classLabel.add("positive");
		classLabel.add("negative");
		Attribute classAttribute = new Attribute("class", classLabel);
		attributes = new ArrayList<Attribute>();
		attributes.add(agentX);
		attributes.add(agentY);
		attributes.add(classAttribute);
		
		//load input data
		inputData = labeledTrajectories;
	}
	
	/**
	 * Generates and Trains a LearnedStateMapping based on input trajectories.
	 * @param labeledTrajectories an array of input trajectories.
	 * @return a new LearnedStateMapping, properly trained.
	 */
	public static LearningFunctions buildMapping(Trajectory[] labeledTrajectories){
		LearningFunctions lsm = new LearningFunctions(labeledTrajectories);
		lsm.train();
		return lsm;//TODO: train.
	}
	
	/**
	 * Sets the internals for performing a state mapping based on a learned classifier.
	 */
	public void train(){
		//Set the set of labels
		Set<Integer> sourceLabels = new HashSet<Integer>();
		for(int i = 0; i < inputData.length; i++){
			sourceLabels.add(inputData[i].start);
			sourceLabels.add(inputData[i].end);
		}
		Integer[] empty = {};//Used to infer type
		Integer[] newLabels = sourceLabels.toArray(empty);
		labels = new int[newLabels.length];
		for(int i = 0; i < newLabels.length; i++){
			labels[i] = newLabels[i];
		}
		
		//TODO: Train a classifier for each label.
		classifiers = new HashMap<Integer, Classifier>();
		for (int i = 0; i < labels.length; i++){
			Instances trainData = Trajectory.trainingForLabel(inputData, labels[i], attributes);
			Classifier classifier = trainClassifierFromLabelsOnly(trainData, attributes);
			classifiers.put(labels[i], classifier);
		}
	}
	
	/**
	 * 
	 * @param trainingSet 
	 * @param attributes
	 * @return
	 */
	// TODO: Train based on existing classifiers + labels
	public static Classifier trainClassifierFromLabelsOnly(Instances trainingSet, List<Attribute> attributes) {
		
		//Train Classifiers
		Classifier stateIdentifier = (Classifier) new SimpleLogistic();//new LogitBoost();//TODO: Gaussian Process.
		try {
			stateIdentifier.buildClassifier(trainingSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println("Trained Classifier.");
		
		return stateIdentifier;
	}
	
	/**
	 * @return the list of attributes used for training.
	 * This may be used by other classes to reverse engineer classification.
	 */
	public ArrayList<Attribute> getAttributes(){
		return attributes;
	}
	
	/**
	 * A helper method to give the index of the most likely classification.
	 * @param elems
	 * @return
	 */
	private static int argmax(double[] elems) {
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
	
	public int[][] makeMap(State s, int width, int height){
		AmdpL0State a0s = (AmdpL0State) s;
		int[][] map = new int[width][height];

		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				AmdpL0State L0_state = new AmdpL0State(new AmdpL0Agent(x,y, "agent"), a0s.rooms);
				Instance test = Trajectory.instanceFromState(L0_state, attributes);
				double[] fDistribution = {0,0,0,0};
				try {
					fDistribution[0] = classifiers.get(1).distributionForInstance(test)[0];
					fDistribution[1] = classifiers.get(2).distributionForInstance(test)[0];
					fDistribution[2] = classifiers.get(3).distributionForInstance(test)[0];
					fDistribution[3] = classifiers.get(4).distributionForInstance(test)[0];
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int roomNumber = argmax(fDistribution);
				map[x][y] = roomNumber+1;
			}
		}
		
//		for (int[] x : map){
//		   for (int y : x){
//		        System.out.print(y + " ");
//		   }
//		   System.out.println();
//		}
	return map;
}
}
