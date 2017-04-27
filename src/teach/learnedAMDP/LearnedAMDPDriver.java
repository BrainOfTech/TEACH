package teach.learnedAMDP;

import static gridWorldL0.AmdpL0Domain.CLASS_COORDINATE_SPACE;
import static gridWorldL0.AmdpL0Domain.PF_AGENT_IN_COORDINATE_SPACE;

import burlap.debugtools.DPrint;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import gridWorldL0.AmdpL0Domain.PF_InCoordinateSpace;

public class LearnedAMDPDriver {
	//Other classes with primary functionality:
	//	Prop Functions in AMDP model
	//	PolicyGenerator
	//  Defining L1 Actions should be just like the hand-made version?
	//  Learning: Prop Functions for In-Room, Expected Reward (predictive function?) for Transition, abstract action policies...
	
	public static void main(String[] args){
		DPrint.toggleCode(3214986, true);//Sets Burlap output for something...
		
		//TODO: Prop Functions (implemented as a class in the AMDP classes)
		//		Used here for termination conditions
		//PropositionalFunction pfL0 = new PF_InCoordinateSpace(PF_AGENT_IN_COORDINATE_SPACE, new String[]{CLASS_COORDINATE_SPACE});
		// args: "name of prop function", "class names for each parameter"
		// Derived from PropositionalFunction in BURLAP
		//Termination conditions defined in terms of prop function
		//Note: Prop Functions defined at each level of the AMDP...
		
		//Note: State is object-oriented, so Prop Functions can pull out objects from state
		
		//TODO: Instantiate Domains 
		
		//TODO: Set trained rooms
		
		//TODO: Set initial state and Derive L1 state from AMDPStateMapping (class defined elsewhere)
		
		//TODO: Task Nodes? - Probably not applicable in learned case
		
		//TODO: Policy Generators - One for each level. (This is where the planning gets swapped.)
		
		//TODO: Instantiate Agent that solves task using PolicyGenerator s
		
		//TODO: Instantiate Environment the agent can run on.
		
		//TODO: Timing and Performance Data
		
	}
}
