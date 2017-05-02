package teach.gridLearnedAMDP;

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
import gridWorldL0.AmdpL0Room;
import gridWorldL0.AmdpL0State;
import gridWorldL1.AmdpL1Agent;
import gridWorldL1.AmdpL1Room;
import gridWorldL1.AmdpL1State;
import teach.TestsDriver;
import teach.Trajectory;
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
public class LearnedStateMapping implements StateMapping{
	
	ArrayList<Attribute> attributes;
	Trajectory[] inputData;
	int[] labels;
	HashMap<Integer, Classifier> classifiers;
	
	/**
	 * Don't call this constructor directly, but instead use the factory method
	 * {@link LearnedStateMapping#buildMapping(Trajectory[])}
	 * @param labeledTrajectories an array of input trajectories.
	 */
	private LearnedStateMapping(Trajectory[] labeledTrajectories){
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
	public static LearnedStateMapping buildMapping(Trajectory[] labeledTrajectories){
		LearnedStateMapping lsm = new LearnedStateMapping(labeledTrajectories);
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

	/**
	 * This is an intermediate method that takes an AmdpL0State and returns an AmdpL1State
	 * 
	 */
	@Override
	public State mapState(State s) {
//		//TODO: To Switch Back
//		return (new AmdpStateMapper()).mapState(s);
		//Typecast input
		AmdpL0State a0s = (AmdpL0State) s;
		
		//Identify Room
		Instance testInference = Trajectory.instanceFromState(a0s, attributes);
		// Get the likelihood of each classes
		// fDistribution[0] is the probability of being “positive”
		// fDistribution[1] is the probability of being “negative”
		double[] fDistribution = {0,0,0,0};
		try {
			fDistribution[0] = classifiers.get(1).distributionForInstance(testInference)[0];
			fDistribution[1] = classifiers.get(2).distributionForInstance(testInference)[0];
			fDistribution[2] = classifiers.get(3).distributionForInstance(testInference)[0];
			fDistribution[3] = classifiers.get(4).distributionForInstance(testInference)[0];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int roomNumber = argmax(fDistribution) + 1;
		String roomName = "room" + roomNumber;//TODO: This is the issue.
		//System.out.println("Debugging: Ground to room " + roomName + ", X: " + a0s.agent.x + " Y: " + a0s.agent.y);
		
		//Construct L1 State
		//TODO: Construct connections from data or remove.
		List<String> r1L1_connectedRooms = Arrays.asList("room1", "room2", "room4");
		List<String> r2L1_connectedRooms = Arrays.asList("room2", "room1", "room3");
		List<String> r3L1_connectedRooms = Arrays.asList("room3", "room2", "room4");
		List<String> r4L1_connectedRooms = Arrays.asList("room4", "room3", "room1");
		AmdpL1Room r1L1 = new AmdpL1Room("room1", r1L1_connectedRooms);
		AmdpL1Room r2L1 = new AmdpL1Room("room2", r2L1_connectedRooms);
		AmdpL1Room r3L1 = new AmdpL1Room("room3", r3L1_connectedRooms);
		AmdpL1Room r4L1 = new AmdpL1Room("room4", r4L1_connectedRooms);
		List<AmdpL1Room> L2_rooms = new ArrayList<AmdpL1Room>(Arrays.asList(r1L1, r2L1, r3L1, r4L1));

		// Construct L1 agent object
		//AmdpL1Agent L1_agent = new AmdpL1Agent(a0s.agent.name(), roomName);
		String roomTest = "room" + (int)(Math.random()*4 + 1);
		AmdpL1Agent L1_agent = new AmdpL1Agent(a0s.agent.name(), roomTest);
		
		// Construct L1 state
		return new AmdpL1State(L1_agent, L2_rooms);
	}
	
//	/**
//	 * mapState assumes that State s is a MutableOOState.
//	 * It returns a state with all abstract labels set.
//	 * This assumes that there is an Agent StateObject with location info.
//	 * This method applies hierarchical LearnedLabel StateObjects as applicable.
//	 * 
//	 */
//	//TODO: Accommodate other state member objects.
//	@Override
//	public State mapState(State s) {
//		MutableOOState moos = (MutableOOState)s;
//		ObjectInstance agent = moos.object("agent");//TODO: alternative to hardcoding - pull "agent" from appropriate class.
//		
//		MutableOOState abstractState = new MutableOOState();
//		
//		// TODO Auto-generated method stub
//		return null;
//	}

	/**
	 * Shows state mapping on the 4 rooms domain. This can be used to select a sensible classifier.
	 * @param args
	 */
	public static void main(String[] args){
		final String file_directory = System.getProperty("user.dir") + "/trajectory";
		Trajectory[] traj_array = TestsDriver.generateTrajectories(file_directory);//TODO: Refactor Loading
		System.out.println("Parsing complete. Num traj's: " + traj_array.length);
		LearnedStateMapping lsm = LearnedStateMapping.buildMapping(traj_array);
		
		//Testing classification:
		for(int y = 10; y >= 0; y--){
			for(int x = 0; x <= 10; x++){
				//Create States
				//L0: room object (room assignment numbered top-left proceeding counterclockwise) 
				AmdpL0Room r1L0 = new AmdpL0Room("room1", 10, 6, 5, 10, 5, 8);
				AmdpL0Room r2L0 = new AmdpL0Room("room2", 10, 0, 6, 4, 1, 5);
				AmdpL0Room r3L0 = new AmdpL0Room("room3", 4, 0, 0, 4, 5, 1);
				AmdpL0Room r4L0 = new AmdpL0Room("room4", 3, 6, 0, 10, 8, 4);
				List<AmdpL0Room> L0_rooms = new ArrayList<AmdpL0Room>(Arrays.asList(r1L0, r2L0, r3L0, r4L0));
				
				//L0 State-->Starting location(GridAgent), Rooms(AmdpL0Room), Ending Location(GridLocation)
				AmdpL0State L0_state = new AmdpL0State(new AmdpL0Agent(x,y, "agent"), L0_rooms);
				
				AmdpL1State L1_State;
				try {
					L1_State = (AmdpL1State)lsm.mapState(L0_state);
					System.out.print(L1_State.agent.inRoom.charAt(4) + " ");
				} catch (Exception e) {
					System.out.print("  ");
				}
			}
			System.out.println("");
		}
	}
}
