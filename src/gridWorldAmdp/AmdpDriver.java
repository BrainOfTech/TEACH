package gridWorldAmdp;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.valuefunction.ConstantValueFunction;
import burlap.behavior.valuefunction.QProvider;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.debugtools.DPrint;
import burlap.mdp.auxiliary.StateMapping;
import burlap.mdp.auxiliary.common.GoalConditionTF;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.oo.propositional.GroundedProp;
import burlap.visualizer.Visualizer;
import experiments.Data;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import gridAmdpFramework.AMDPAgent;
import gridAmdpFramework.AMDPPolicyGenerator;
import gridAmdpFramework.GroundedPropSC;
import gridAmdpFramework.GroundedTask;
import gridAmdpFramework.NonPrimitiveTaskNode;
import gridAmdpFramework.RootTaskNode;
import gridAmdpFramework.TaskNode;
import gridAmdpTestingTools.BoundedRTDPForTests;
import gridAmdpTestingTools.GreedyReplan;
import gridAmdpTestingTools.MutableGlobalInteger;
import gridWorldL0.AmdpL0Agent;
import gridWorldL0.AmdpL0Domain;
import gridWorldL0.AmdpL0Room;
import gridWorldL0.AmdpL0State;
import gridWorldL1.AmdpL1Domain;
import gridWorldL1.AmdpL1Room;
import gridWorldAmdp.AmdpStateMapper;

import static gridWorldL0.AmdpL0Domain.*;
import static gridWorldL1.AmdpL1Domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amdp.cleanup.PullCostGoalRF;

public class AmdpDriver {
    static int maxTrajectoryLength = 1000;
	static String agent_name = "agent";
   
    public AmdpDriver(){}

	//public static void main(String[] args) {
    public static Data executeAmdp(Boolean experiment, int[] sl, String gl){
    	if (!experiment){
    		DPrint.toggleCode(3214986, true);
    	}
    	else {
    		DPrint.toggleUniversal(false);
    	}
	
		//Create Definable Parameters
		int[] start_location = sl; //{0,10};
		String goal_location = gl; //"room1";
	
		//Create Termination/Reward Functions via the propositional function
		//L0
		PropositionalFunction pfL0 = new PF_InCoordinateSpace(PF_AGENT_IN_COORDINATE_SPACE, new String[]{CLASS_COORDINATE_SPACE});
		GroundedProp gpL0 =  new GroundedProp(pfL0,new String[]{goal_location}); //Ground generic proposition to goal
		
	    GroundedPropSC L0sc = new GroundedPropSC(gpL0);
	    GoalBasedRF L0rf = new GoalBasedRF(L0sc, 1, -1);
	    GoalConditionTF L0tf = new GoalConditionTF(L0sc);
	    
	    //L1
	    PropositionalFunction pfL1 = new PF_InRoom(PF_AGENT_IN_ROOM, new String[]{CLASS_ROOM});
	    GroundedProp gpL1 =  new GroundedProp(pfL1,new String[]{goal_location});
	    
	    GroundedPropSC L1sc = new GroundedPropSC(gpL1);
	    GoalBasedRF L1rf = new GoalBasedRF(L1sc, 1, -1);
	    GoalConditionTF L1tf = new GoalConditionTF(L1sc);
	   
	    
		//Create Domains
		//L0
		AmdpL0Domain gw = new AmdpL0Domain(L0rf, L0tf); 
		gw.setMapToFourRooms(); // four rooms layout, 11x11 grid world (essential)
		OOSADomain domainL0 = gw.generateDomain(); // generate the grid world domain
		
		//L1
		AmdpL1Domain rw = new AmdpL1Domain(L1rf, L1tf);
		OOSADomain domainL1 = rw.generateDomain();

		//Create States
		//L0: room object (room assignment numbered top-left proceeding counterclockwise) 
		ObjectInstance r1L0 = new AmdpL0Room("room1", 10, 6, 5, 10, 5, 8);
		ObjectInstance r2L0 = new AmdpL0Room("room2", 10, 0, 6, 4, 1, 5);
		ObjectInstance r3L0 = new AmdpL0Room("room3", 4, 0, 0, 4, 5, 1);
		ObjectInstance r4L0 = new AmdpL0Room("room4", 3, 6, 0, 10, 8, 4);
		List<ObjectInstance> L0_rooms = new ArrayList<ObjectInstance>(Arrays.asList(r1L0, r2L0, r3L0, r4L0));
		
		//L0 State-->Starting location(GridAgent), Rooms(AmdpL0Room), Ending Location(GridLocation)
		AmdpL0State L0_state = new AmdpL0State(new AmdpL0Agent(start_location[0],start_location[1], agent_name), L0_rooms);
		
		//L1 State-->is dynamically set by AmdpL1StateMapper
		State L1_state = new AmdpStateMapper().mapState(L0_state);
			
		//Create Action Types
        ActionType north = domainL0.getAction(AmdpL0Domain.ACTION_NORTH);
        ActionType east = domainL0.getAction(AmdpL0Domain.ACTION_EAST);
        ActionType west = domainL0.getAction(AmdpL0Domain.ACTION_WEST);
        ActionType south = domainL0.getAction(AmdpL0Domain.ACTION_SOUTH);

        ActionType aget_to_room = domainL1.getAction(AmdpL1Domain.ACTION_AGENT_TO_ROOM);
        
        //Create TaskNodes (DAG-directed acyclic graph)
        TaskNode nt = new L0TaskNode(north);
        TaskNode et = new L0TaskNode(east);
        TaskNode st = new L0TaskNode(south);
        TaskNode wt = new L0TaskNode(west);
        TaskNode[] L1Subtasks = new TaskNode[]{nt, et, st, wt};

        TaskNode a2rt = new L1TaskNode(aget_to_room, gw.generateDomain(), L1Subtasks);
        TaskNode L1Root = new RootTaskNode("root",new TaskNode[]{a2rt},domainL1, L1tf,L1rf);

        List<AMDPPolicyGenerator> pgList = new ArrayList<AMDPPolicyGenerator>();
        pgList.add(0,new l0PolicyGenerator(domainL0));
        pgList.add(1,new l1PolicyGenerator(domainL1));
		
        AMDPAgent agent = new AMDPAgent(L1Root.getApplicableGroundedTasks(L1_state).get(0),pgList);
        SimulatedEnvironment env = new SimulatedEnvironment(gw.generateDomain(), L0_state);
        
        long startTime = System.currentTimeMillis();
        Episode e = agent.actUntilTermination(env, maxTrajectoryLength);
        long endTime = System.currentTimeMillis();
        
        if (!experiment){
            //Timing and performance data
            System.out.println("Start Location of Agent : " + 
            		String.valueOf(start_location[0]) + "," +
            		String.valueOf(start_location[1]));
            System.out.println("Goal Location: " + goal_location);
            System.out.println("/////////////////////////////");
        	
	        System.out.println("==========================================================\n"
	        		+ "==========================OUTPUT==========================\n"
	        		+ "==========================================================");
	        System.out.println("Action Sequence Size: \n" + e.actionSequence.size());
	        System.out.println("Action Sequence: \n" + e.actionSequence);
	        System.out.println("Reward Sequence Size: \n" + e.rewardSequence.size());
	        System.out.println("Reward Sequence: \n" + e.rewardSequence);
	        System.out.println("Total Discounted Reward: \n" + e.discountedReturn(1.));
	//        System.out.println("State Sequence Size: \n" + e.stateSequence.size());
	//        System.out.println("State Sequence: \n" + e.stateSequence);
	
	        //Visualization
			Visualizer v = GridWorldVisualizer.getVisualizer(gw.getMap(), goal_location, L0_state);
			new EpisodeSequenceVisualizer(v, domainL0, Arrays.asList(e));
			
	        HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
	    	Planner planner = new ValueIteration(domainL0, 0.99, hashingFactory, 0.001, 100);
	    	Policy p = planner.planFromState(L0_state);	
	  		List<State> allStates = StateReachability.getReachableStates(L0_state, domainL0, hashingFactory);
			ValueFunctionVisualizerGUI gui = GridWorldVisualizer.getGridWorldValueFunctionVisualization(
				allStates, 11, 11, (ValueFunction)planner, p);
			gui.initGUI();
			
//	    	Planner planner2 = new ValueIteration(domainL1, 0.99, hashingFactory, 0.001, 100);
//	    	Policy p2 = planner.planFromState(L1_state);
//	    	List<State> allStates2 = StateReachability.getReachableStates(L1_state, domainL1, hashingFactory);
//	    	ValueFunctionVisualizerGUI gui2 = GridWorldVisualizer.getGridWorldValueFunctionVisualization(
//	    			allStates2, 2, 2, (ValueFunction)planner2, p2);
//	    	gui2.initGUI();
	    	
			
			
	        System.out.println("Duration of Time: \n" + String.valueOf(endTime - startTime));
	        System.out.println("Program End!");
        }
        
        Data d = new Data(e, endTime-startTime);
        return d;
	}
	
    
    public static class l0PolicyGenerator implements AMDPPolicyGenerator{
        private OOSADomain l0;
        protected double discount = 0.99;
        public l0PolicyGenerator(OOSADomain l0In){
            l0 = l0In;
        }

        @Override
        public Policy generatePolicy(State s, GroundedTask gt) {
            l0 = ((NonPrimitiveTaskNode)gt.getT()).domain();
            l0.setModel(new FactoredModel(((FactoredModel)l0.getModel()).getStateModel(),gt.rewardFunction(), gt.terminalFunction()));
            
	        HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
	      	Planner planner = new ValueIteration(l0, 0.99, hashingFactory, 0.001, 100);
	      	Policy p = planner.planFromState(s);
	      	
            return p;    
        }
        public State generateAbstractState(State s) {
        	return null;
        }
        @Override
        public QProvider getQProvider(State s, GroundedTask gt) {
        	throw new RuntimeException("No QProvider");
        }
    }
    
    
    public static class l1PolicyGenerator implements AMDPPolicyGenerator{
        private OOSADomain l1;
        protected double discount = 0.99;
        protected StateMapping sm ;

        public l1PolicyGenerator(OOSADomain l1In){
            l1 = l1In;
            sm = new AmdpStateMapper();
        }
        @Override
        public Policy generatePolicy(State s, GroundedTask gt) {
            l1 = ((NonPrimitiveTaskNode)gt.getT()).domain();
            l1.setModel(new FactoredModel(((FactoredModel)l1.getModel()).getStateModel(),gt.rewardFunction(), gt.terminalFunction()));
            
	        HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
	      	Planner planner = new ValueIteration(l1, 0.99, hashingFactory, 0.001, 100);
	      	Policy p = planner.planFromState(s);

            return p;
        }
        @Override
        public State generateAbstractState(State s) {
            return sm.mapState(s);
        }
        @Override
        public QProvider getQProvider(State s, GroundedTask gt) {
        	throw new RuntimeException("No QProvider");
        }
    }
}
