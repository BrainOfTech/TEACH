package experiments;


import static gridWorldL0.AmdpL0Domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.SarsaLam;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.debugtools.DPrint;
import burlap.mdp.auxiliary.common.ConstantStateGenerator;
import burlap.mdp.auxiliary.common.GoalConditionTF;
import burlap.mdp.core.oo.propositional.GroundedProp;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.vardomain.VariableDomain;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import gridAmdpFramework.GroundedPropSC;
import gridWorldAmdp.GridWorldVisualizer;
import gridWorldL0.AmdpL0Agent;
import gridWorldL0.AmdpL0Domain;
import gridWorldL0.AmdpL0Room;
import gridWorldL0.AmdpL0State;
import gridWorldL0.AmdpL0Domain.PF_InCoordinateSpace;

public class ValueIterationL0 {
	public static Data executeValueIteration(Boolean experiment, int[] sl, String gl){
    	if (!experiment){
    		DPrint.toggleCode(3214986, true);
    	}
    	else {
    		DPrint.toggleUniversal(false);
    	}
		
		//Create Definable Parameters
		String agent_name = "agent";
		int[] start_location = sl;
		String goal_location = gl;
		
		PropositionalFunction pfL0 = new PF_InCoordinateSpace(PF_AGENT_IN_COORDINATE_SPACE, new String[]{CLASS_COORDINATE_SPACE});
		GroundedProp gpL0 =  new GroundedProp(pfL0,new String[]{goal_location}); //Ground generic proposition to goal
		
	    GroundedPropSC L0sc = new GroundedPropSC(gpL0);
	    final GoalBasedRF L0rf = new GoalBasedRF(L0sc, 1, -1);
	    final GoalConditionTF L0tf = new GoalConditionTF(L0sc);
	    
	    //Create Domains
  		//L0
  		AmdpL0Domain gw = new AmdpL0Domain(L0rf, L0tf); 
  		gw.setMapToFourRooms(); // four rooms layout, 11x11 grid world (essential)
  		final OOSADomain domainL0 = gw.generateDomain(); // generate the grid world domain
	
		//L0: room object (room assignment numbered top-left proceeding counterclockwise) 
		ObjectInstance r1L0 = new AmdpL0Room("room1", 10, 6, 5, 10, 5, 8);
		ObjectInstance r2L0 = new AmdpL0Room("room2", 10, 0, 6, 4, 1, 5);
		ObjectInstance r3L0 = new AmdpL0Room("room3", 4, 0, 0, 4, 5, 1);
		ObjectInstance r4L0 = new AmdpL0Room("room4", 3, 6, 0, 10, 8, 4);
		List<ObjectInstance> L0_rooms = new ArrayList<ObjectInstance>(Arrays.asList(r1L0, r2L0, r3L0, r4L0));
		
		//L0 State-->Starting location(GridAgent), Rooms(AmdpL0Room), Ending Location(GridLocation)
		AmdpL0State L0_state = new AmdpL0State(new AmdpL0Agent(start_location[0],start_location[1], agent_name), L0_rooms);
		
	    HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
	  	Planner planner = new ValueIteration(domainL0, 0.99, hashingFactory, 0.001, 100);
	  	
	  	long startTime = System.currentTimeMillis();
	  	Policy p = planner.planFromState(L0_state);
	  	long endTime = System.currentTimeMillis();
	  	
	  	Episode e = PolicyUtils.rollout(p, L0_state, domainL0.getModel());
	  	
	  	if (!experiment){
			Visualizer v = GridWorldVisualizer.getVisualizer(gw.getMap(), goal_location, L0_state);
			new EpisodeSequenceVisualizer(v, domainL0, Arrays.asList(e));
	  		
			List<State> allStates = StateReachability.getReachableStates(
			L0_state, domainL0, hashingFactory);
			ValueFunctionVisualizerGUI gui = GridWorldVisualizer.getGridWorldValueFunctionVisualization(
			allStates, 11, 11, (ValueFunction)planner, p);
			gui.initGUI();
	  	}
	  	
	  	Data d = new Data(e, endTime-startTime);
	  	return d;
	}
}
