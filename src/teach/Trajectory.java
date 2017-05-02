package teach;

import java.util.ArrayList;
import java.util.List;

import burlap.behavior.singleagent.Episode;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import gridWorldL0.AmdpL0Agent;
import gridWorldL0.AmdpL0State;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class Trajectory {
	public Episode e;
	public int start;
	public int end;

	public List<Action> getActionSequence() {
		return e.actionSequence;
	}

	public List<State> getStateSequence() {
		return e.stateSequence;
	}
	
	/**
	 * Produce Weka Training Instances from a list of trajectories.
	 * @param trajectories
	 * @param labelOfInterest The room number to use.
	 * @param attributes The training attributes to use.
	 * @return A set of labeled training instances using the attributes specified.
	 */
	//Create Training Set for given label from trajectories
	public static Instances trainingForLabel(Trajectory[] trajectories, int labelOfInterest, ArrayList<Attribute> attributes) {
		// Create an empty training set
		Instances isTrainingSet = new Instances("Rel", attributes, trajectories.length * 2);
		// Set class index
		isTrainingSet.setClassIndex(attributes.size() - 1);// The final
															// attribute

		// Pull out agent:x, agent:y locations for states labeled
		// labelOfInterest
		for (int i = 0; i < trajectories.length; i++) {
			// Create the start instance
			State inst = trajectories[i].getStateSequence().get(0);
			if (trajectories[i].start == labelOfInterest) {
				isTrainingSet.add(instanceFromState(inst, attributes, true));
			} else {
				isTrainingSet.add(instanceFromState(inst, attributes, false));
			}

			// Create the other instance
			int stateSequenceSize = trajectories[i].getStateSequence().size();
			inst = trajectories[i].getStateSequence().get(stateSequenceSize - 1);
			if (trajectories[i].end == labelOfInterest) {
				isTrainingSet.add(instanceFromState(inst, attributes, true));
			} else {
				isTrainingSet.add(instanceFromState(inst, attributes, false));
			}
		}

		//System.out.println(isTrainingSet.numInstances());
		
		return isTrainingSet;
	}
	
	/**
	 * Generate a training instance by pulling xy out of state and using attributes.
	 * @param s The input state.
	 * @param attributes
	 * @return
	 */
	public static Instance instanceFromState(State s, ArrayList<Attribute> attributes, boolean... isPositiveExample){
		int[] xy = getStateXY(s);
		Instance iExample = new DenseInstance(attributes.size());
		iExample.setValue((Attribute) attributes.get(0), xy[0]);
		iExample.setValue((Attribute) attributes.get(1), xy[1]);
		if (isPositiveExample.length > 0) {
			if (isPositiveExample[0]) {
				iExample.setValue((Attribute) attributes.get(2), "positive");
			} else {
				iExample.setValue((Attribute) attributes.get(2), "negative");
			}
		}
		return iExample; //TODO: generalize to different attributes.
	}
	
	/**
	 * A helper function to extract XY from state
	 * @param s
	 * @return
	 */
	// Function may not be robust to different state formulations
	public static int[] getStateXY(State s) {
		List<Object> o = s.variableKeys();
		int x = (Integer) s.get(o.get(0));
		int y = (Integer) s.get(o.get(1));

		int[] a = { x, y };
		return a;
	}
	
	/**
	 * Shadows (I think) the more general State method.
	 * @param s
	 * @return
	 */
	public static int[] getStateXY(AmdpL0State s) {
		AmdpL0Agent agent = s.agent;
		int x = agent.x;
		int y = agent.y;
		int[] a = {x, y};
		return a;
	}
}
