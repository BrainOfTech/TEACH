package gridWorldL1;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.auxiliary.StateEnumerator;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.auxiliary.common.GoalConditionTF;
import burlap.mdp.auxiliary.common.NullTermination;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.oo.ObjectParameterizedAction;
import burlap.mdp.core.oo.propositional.GroundedProp;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.common.UniformCostRF;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.mdp.singleagent.oo.ObjectParameterizedActionType;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import gridAmdpFramework.GroundedPropSC;
import gridWorldAmdp.AmdpStateMapper;
import gridWorldAmdp.GridWorldVisualizer;
import gridWorldL0.AmdpL0Agent;
import gridWorldL0.AmdpL0Domain;
import gridWorldL0.AmdpL0Room;
import gridWorldL0.AmdpL0State;

import static gridWorldL0.AmdpL0Domain.*;
import static gridWorldL1.AmdpL1Domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AmdpL1Domain implements DomainGenerator{
    public static final String VAR_CONNECTED = "connectedObjects";
    public static final String VAR_IN_ROOM = "inRoom";
    
    public static final String CLASS_ROOM = "room";
    public static final String CLASS_AGENT = "agent";
    
    public static final String ACTION_AGENT_TO_ROOM = "agentToRoom";
    
    public static final String PF_AGENT_IN_ROOM = "in_room";
    
    protected RewardFunction rf;
    protected TerminalFunction tf;

    public AmdpL1Domain(RewardFunction rf, TerminalFunction tf){
        this.rf = rf;
        this.tf= tf;
    }

    public OOSADomain generateDomain() {
        OOSADomain domain = new OOSADomain();
        
        domain.addStateClass(AmdpL1Domain.CLASS_AGENT, AmdpL1Agent.class);
        domain.addStateClass(AmdpL1Domain.CLASS_ROOM, AmdpL1Room.class);
        
        domain.addActionTypes(new GoToRoomActionType(ACTION_AGENT_TO_ROOM, new String[]{AmdpL1Domain.CLASS_ROOM}));
        domain.addPropFunction(new PF_InRoom(PF_AGENT_IN_ROOM, new String[]{CLASS_ROOM}));
        
        AmdpL1Model smodel = new AmdpL1Model();
		RewardFunction rf = this.rf;
		TerminalFunction tf = this.tf;
       
        if (rf == null) {
            rf = new UniformCostRF();
        }
        if (tf == null) {
            tf = new NullTermination();
        }
        
        FactoredModel model = new FactoredModel(smodel, rf, tf);
        domain.setModel(model);
        return domain;
    }
    
    public static class PF_InRoom extends PropositionalFunction {
        public PF_InRoom(String name, String [] params){
            super(name, params);
        }
        @Override
        public boolean isTrue(OOState s, String... params) {
        	AmdpL1State working_state = (AmdpL1State)s;
        	if (working_state.agent.inRoom == params[0]) {
        		return true;
        	}
        	return false;
        }     
    }

    public static class GoToRoomActionType extends ObjectParameterizedActionType{

        public GoToRoomActionType(String name, String[] parameterClasses) {
            super(name, parameterClasses);
        }

        @Override
        protected boolean applicableInState(State s, ObjectParameterizedAction a) {
        	AmdpL1State working_state = (AmdpL1State)s;
        	
            //Cast current room as object
            AmdpL1Agent agent = working_state.agent;
            ObjectInstance curRegion = (ObjectInstance)working_state.object(agent.inRoom);
            
            //Cast current room as AmdpL1Room
            if(curRegion instanceof AmdpL1Room){
            	
                //Return true if action moves to connected room
                if(((AmdpL1Room)curRegion).connectedRooms.contains(a.getObjectParameters()[0])){
                    return true;
                }
            }
            return false;
        }
    }
        
//        public static void main(String[] args) {
//
//        	//L0
//        	PropositionalFunction pfL0 = new PF_InCoordinateSpace(PF_AGENT_IN_COORDINATE_SPACE, new String[]{CLASS_COORDINATE_SPACE});
//        	GroundedProp gpL0 =  new GroundedProp(pfL0,new String[]{"room1"}); //Ground generic proposition to goal
//
//        	GroundedPropSC L0sc = new GroundedPropSC(gpL0);
//    	    GoalBasedRF L0rf = new GoalBasedRF(L0sc, 1, -1);
//    	    GoalConditionTF L0tf = new GoalConditionTF(L0sc);
//    	    
//    	    //L1
//    	    PropositionalFunction pfL1 = new PF_InRoom(PF_AGENT_IN_ROOM, new String[]{CLASS_ROOM});
//    	    GroundedProp gpL1 =  new GroundedProp(pfL1,new String[]{"room1"});
//    	    
//    	    GroundedPropSC L1sc = new GroundedPropSC(gpL1);
//    	    GoalBasedRF L1rf = new GoalBasedRF(L1sc, 1, -1);
//    	    GoalConditionTF L1tf = new GoalConditionTF(L1sc);
//    	   
//            AmdpL0Domain gw = new AmdpL0Domain(L0rf, L0tf); // 11x11 grid world
//    		gw.setMapToFourRooms(); // four rooms layout
//
//    		OOSADomain domainL0 = gw.generateDomain(); // generate the grid world domain
//    		domainL0.addPropFunction(pfL0); //IMPORTANT
//    		domainL0.addStateClass(CLASS_COORDINATE_SPACE, AmdpL0Room.class);
//
//    		//Create States
//    		//L0: room object (room assignment numbered top-left proceeding counterclockwise) 
//    		AmdpL0Room r1L0 = new AmdpL0Room("room1", 10, 6, 5, 10, 5, 8);
//    		AmdpL0Room r2L0 = new AmdpL0Room("room2", 10, 0, 6, 4, 1, 5);
//    		AmdpL0Room r3L0 = new AmdpL0Room("room3", 4, 0, 0, 4, 5, 1);
//    		AmdpL0Room r4L0 = new AmdpL0Room("room4", 3, 6, 0, 10, 8, 4);
//    		List<AmdpL0Room> L0_rooms = new ArrayList<AmdpL0Room>(Arrays.asList(r1L0, r2L0, r3L0, r4L0));
//    		
//    		//L0 State-->Starting location(GridAgent), Rooms(AmdpL0Room)
//    		AmdpL0State L0_state = new AmdpL0State(new AmdpL0Agent(0,0, "agent"), L0_rooms);
//
//    		//L1
//    		AmdpL1Domain rw = new AmdpL1Domain(L1rf, L1tf);
//    	
//    		OOSADomain domainL1 = rw.generateDomain();
//    		domainL1.addPropFunction(pfL1);
//    		domainL1.addStateClass(CLASS_ROOM, AmdpL1Room.class);
//
//            State L1_state = new AmdpStateMapper().mapState(L0_state);
////            System.out.print(L0_state.toString());
////            System.out.println(L1_state.toString());
////            System.out.println(domainL1.getActionTypes());
////            System.out.println(ACTION_AGENT_TO_ROOM);
////            System.out.println(domainL1.getAction(ACTION_AGENT_TO_ROOM));
////            
////            System.out.println(((AmdpL1State)L1_state).objectsOfClass(AmdpL1Domain.CLASS_ROOM));
//            
//            ActionType at = domainL1.getAction(ACTION_AGENT_TO_ROOM);
//            ActionType a0 = domainL0.getAction(ACTION_NORTH);
//            
//            Boolean watch = false;
//            if (watch) {
//            List<Action> a_1 = at.allApplicableActions(L1_state);
//            System.out.println("actions: " + a_1.size());
//            //System.out.println(L1_state.toString());
//            
//            EnvironmentOutcome eo_1 = domainL1.getModel().sample(L1_state, a_1.get(0));
//            System.out.println("+++++++++++++++++++++++");
//            System.out.println(eo_1.o);
//            System.out.println(a_1.get(0).toString());
//            System.out.println(eo_1.op);
//            }
//            else {
//            List<Action> a_0 = a0.allApplicableActions(L0_state);
//            System.out.println("actions: " + a_0.size());
//            //System.out.println(L0_state.toString());
//            
//            EnvironmentOutcome eo_0 = domainL0.getModel().sample(L0_state, a_0.get(0));
//            System.out.println("+++++++++++++++++++++++");
//            System.out.println(eo_0.o);
//            System.out.println(a_0.get(0).toString());
//            System.out.println(eo_0.op);
//            }
//            
//            HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
//            String outputPath = "";
//        	Planner planner = new ValueIteration(domainL0, 0.99, hashingFactory, 0.001, 100);
//        	Policy p = planner.planFromState(L0_state);
//        	PolicyUtils.rollout(p, L0_state, domainL0.getModel()).write(outputPath + "vi");
//  
//      		List<State> allStates = StateReachability.getReachableStates(
//    			L0_state, domainL0, hashingFactory);
//    		ValueFunctionVisualizerGUI gui = GridWorldVisualizer.getGridWorldValueFunctionVisualization(
//    			allStates, 11, 11, (ValueFunction)planner, p);
//    		gui.initGUI();
//        	
//        	System.out.println(outputPath);
//        }
    }

