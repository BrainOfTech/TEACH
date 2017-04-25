package gridWorldAmdp;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.valuefunction.ConstantValueFunction;
import burlap.behavior.valuefunction.QProvider;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.debugtools.DPrint;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.mdp.auxiliary.StateMapping;
import burlap.mdp.auxiliary.common.GoalConditionTF;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.oo.propositional.GroundedProp;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
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

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.PullCostGoalRF;
import amdp.cleanup.state.CleanupAgent;
import amdp.cleanup.state.CleanupDoor;
import amdp.cleanup.state.CleanupState;
import amdp.cleanupamdpdomains.cleanupamdp.CleanupDriver.AgentToRegionHeuristic;
import amdp.cleanupamdpdomains.cleanupamdp.CleanupDriver.BlockToRegionHeuristic;

public class AmdpDriver {
    static protected MutableGlobalInteger bellmanBudget = new MutableGlobalInteger(-1);
    static protected MutableGlobalInteger bellmanBudgetL0 = new MutableGlobalInteger(-1);
    static protected MutableGlobalInteger bellmanBudgetL1 = new MutableGlobalInteger(-1);

    static double L1Ratio = 0.65; //not sure why this value
    static double L0Ratio = 0.3;

    static int maxTrajectoryLength = 1000;

    public static List<BoundedRTDPForTests> brtdpList= new ArrayList<BoundedRTDPForTests>();
    public static Map<String, BoundedRTDPForTests> brtdpMap =  new HashMap<String, BoundedRTDPForTests>();

	public static void main(String[] args) {
		DPrint.toggleCode(3214986, true);
		
		//Create Identifiers
		String agent_name = "agent";
		int[] start_location = {0,0};
		String goal_location = "room1";
		
		
		//Create Termination/Reward Functions via the propositional function
		//L0
		PropositionalFunction pfL0 = new PF_InCoordinateRectangle(PF_AGENT_IN_COORDINATE_RECTANGLE, new String[]{CLASS_COORDINATE_RECTANGLE});
		GroundedProp gpL0 =  new GroundedProp(pfL0,new String[]{goal_location}); //Ground generic proposition to goal
		
	    GroundedPropSC L0sc = new GroundedPropSC(gpL0);
	    GoalBasedRF L0rf = new GoalBasedRF(L0sc, 1, 0.);
	    GoalConditionTF L0tf = new GoalConditionTF(L0sc);
	    
	    //L1
	    PropositionalFunction pfL1 = new PF_InRoom(PF_AGENT_IN_ROOM, new String[]{CLASS_ROOM});
	    GroundedProp gpL1 =  new GroundedProp(pfL1,new String[]{goal_location});
	    
	    GroundedPropSC L1sc = new GroundedPropSC(gpL1);
	    GoalBasedRF L1rf = new GoalBasedRF(L1sc, 1, 0.);
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
		AmdpL0Room r1L0 = new AmdpL0Room("room1", 10, 6, 5, 10, 5, 8);
		AmdpL0Room r2L0 = new AmdpL0Room("room2", 10, 0, 6, 4, 1, 5);
		AmdpL0Room r3L0 = new AmdpL0Room("room3", 4, 0, 0, 4, 5, 1);
		AmdpL0Room r4L0 = new AmdpL0Room("room4", 3, 6, 0, 10, 8, 4);
		List<AmdpL0Room> L0_rooms = new ArrayList<AmdpL0Room>(Arrays.asList(r1L0, r2L0, r3L0, r4L0));
		List<GridLocation> locations = new ArrayList<GridLocation>(); 
		//locations = make_color_map(r1L0, "goal-location"); //unusable location doubles as color map
		locations.add(new GridLocation(0,0,"goal"));
		locations.add(new GridLocation(10,10, "goal")); //weird bug will break program if < 2 locations...
		
		//L0 State-->Starting location(GridAgent), Rooms(AmdpL0Room), Ending Location(GridLocation)
		AmdpL0State L0_state = new AmdpL0State(new AmdpL0Agent(start_location[0],start_location[1], agent_name), L0_rooms, locations);
		
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
        
       
        //Timing and performance data
        System.out.println("start location: " + goal_location);
        
        long startTime = System.currentTimeMillis();
//        Episode e = agent.actUntilTermination(env, maxTrajectoryLength);
        Episode e = agent.actUntilTermination(env, 2);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        //Visualizer v = GridWorldVisualizer.getVisualizer("amdp/data/resources/robotImages"); //have to write
        //		System.out.println(ea.getState(0).toString());
        //new EpisodeSequenceVisualizer(v, domainL0, Arrays.asList(e));
        
        System.out.println(e.actionSequence.size());
//        System.out.println(e.actionSequence);
        System.out.println(e.discountedReturn(1.));

        int count=0;
        for(int i=0;i<brtdpList.size();i++) {
            int numUpdates = brtdpList.get(i).getNumberOfBellmanUpdates();
            count += numUpdates;
        }

        System.out.println(count);
//        System.out.println( brtd.getNumberOfBellmanUpdates());
        System.out.println("Total planners used: " + brtdpList.size());
        System.out.println("AMDP Gridworld");
//        System.out.println("room number: " + rooms);
//        System.out.println("backups: " + totalBudget);

//        System.out.println("CleanUp with AMDPs \n Backups by individual planners:");
        System.out.println("Gridworld with AMDPs \n Backups by individual planners:");
        for(BoundedRTDPForTests b: brtdpList){
            System.out.println(b.getNumberOfBellmanUpdates());
        }
        System.out.println("total duration: " +duration);	
		
//		// create visualizer and explorer
//		Visualizer v = GridWorldVisualizer.getVisualizer(gw.getMap());
//		VisualExplorer exp = new VisualExplorer(domainL1, v, L0_state);
//
//		// set control keys to use w-s-a-d
//		exp.addKeyAction("w", GridWorldDomain.ACTION_NORTH, "");
//		exp.addKeyAction("s", GridWorldDomain.ACTION_SOUTH, "");
//		exp.addKeyAction("a", GridWorldDomain.ACTION_WEST, "");
//		exp.addKeyAction("d", GridWorldDomain.ACTION_EAST, "");
//
//		exp.initGUI();

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

            ValueFunction heuristic = getL0Heuristic(s, gt.rewardFunction());
            BoundedRTDPForTests brtd = new BoundedRTDPForTests(l0, this.discount,
                    new SimpleHashableStateFactory(false),
                    new ConstantValueFunction(0.), new ConstantValueFunction(1.0), 0.1, 50);
            brtd.setRemainingNumberOfBellmanUpdates(bellmanBudgetL0);
            brtd.setMaxRolloutDepth(50);
            brtd.toggleDebugPrinting(true);
            brtdpList.add(brtd);
            brtd.planFromState(s);
            return new GreedyReplan(brtd);
        }

        public State generateAbstractState(State s) {
        	return null;
        }

        @Override
        public QProvider getQProvider(State s, GroundedTask gt) {
            l0 = ((NonPrimitiveTaskNode)gt.getT()).domain();
            l0.setModel(new FactoredModel(((FactoredModel)l0.getModel()).getStateModel(),gt.rewardFunction(), gt.terminalFunction()));

            ValueFunction heuristic = getL0Heuristic(s, gt.rewardFunction());
            BoundedRTDPForTests brtd = new BoundedRTDPForTests(l0, this.discount,
                    new SimpleHashableStateFactory(false),
                    new ConstantValueFunction(0.),
                    new ConstantValueFunction(1.0), 0.1, 50);
            brtd.setRemainingNumberOfBellmanUpdates(bellmanBudgetL0);
            brtd.setMaxRolloutDepth(50);
            brtd.toggleDebugPrinting(true);
            brtdpList.add(brtd);
            brtd.planFromState(s);
            return brtd;
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


            SimpleHashableStateFactory shf = new SimpleHashableStateFactory(false);
            BoundedRTDPForTests brtdp = new BoundedRTDPForTests(l1, discount, shf,
                    new ConstantValueFunction(0.),
                    new ConstantValueFunction(1.),
                    0.1,
                    100);

            brtdp.setRemainingNumberOfBellmanUpdates(bellmanBudgetL1);
            brtdp.setMaxRolloutDepth(50);
            brtdp.toggleDebugPrinting(true);

         //   Policy p = brtdp.planFromState(s);
            brtdpList.add(brtdp);
            return new GreedyReplan(brtdp);
        }

        @Override
        public State generateAbstractState(State s) {
            return sm.mapState(s);
        }

        @Override
        public QProvider getQProvider(State s, GroundedTask gt) {

            l1 = ((NonPrimitiveTaskNode)gt.getT()).domain();
            l1.setModel(new FactoredModel(((FactoredModel)l1.getModel()).getStateModel(),gt.rewardFunction(), gt.terminalFunction()));

            SimpleHashableStateFactory shf = new SimpleHashableStateFactory(false);
            BoundedRTDPForTests brtdp = new BoundedRTDPForTests(l1, discount, shf,
                    new ConstantValueFunction(0.),
                    new ConstantValueFunction(1.),
                    0.1,
                    2000);

            brtdp.setRemainingNumberOfBellmanUpdates(bellmanBudgetL1);
            brtdp.setMaxRolloutDepth(100);
            brtdp.toggleDebugPrinting(true);

           // Policy p = brtdp.planFromState(s);
            brtdpList.add(brtdp);
            return brtdp;
        }
    }
    
    public static ValueFunction getL0Heuristic(State s, RewardFunction rf){

        double discount = 0.99;
        // prop name if block -> block and room if
        GroundedPropSC rfCondition = (GroundedPropSC)((PullCostGoalRF)rf).getGoalCondition();
        String PFName = rfCondition.gp.pf.getName();
        String[] params = rfCondition.gp.params;
        if(PFName.equals(AmdpL0Domain.PF_AGENT_IN_COORDINATE_RECTANGLE)){
            return new AgentToRoomHeuristic(params[0], discount);
        }
        throw new RuntimeException("Unknown Reward Function with propositional function " + PFName + ". Cannot construct l0 heuristic.");
    }
    
    public static class AgentToRoomHeuristic implements ValueFunction{

        String goalRoom;
        double discount;

        public AgentToRoomHeuristic(String goalRoom, double discount) {
            this.goalRoom = goalRoom;
            this.discount = discount;
        }

        //@Override
        //public double qValue(State s, AbstractGroundedAction a) {
        //    return value(s);
        //}

        @Override
        public double value(State s) {

            int delta = 1;
            ObjectInstance region = ((AmdpL0State)s).object(this.goalRoom);

            //get the agent
            AmdpL0Agent agent = ((AmdpL0State)s).agent;
            int ax = agent.x;
            int ay = agent.y;


            int l = (Integer) region.get(AmdpL0Domain.VAR_LEFT);
            int r = (Integer)region.get(AmdpL0Domain.VAR_RIGHT);
            int b = (Integer)region.get(AmdpL0Domain.VAR_BOTTOM);
            int t = (Integer)region.get(AmdpL0Domain.VAR_TOP);

            int dist = toRegionManDistance(ax, ay, l, r, b, t, delta);

            double fullChanceV = Math.pow(discount, dist-1);
            double v = fullChanceV;

            return v;
        }
    }
    
    public static int toRegionManDistance(int x, int y, int l, int r, int b, int t, int delta){
        int dist = 0;

        //use +1s because boundaries define wall, which is not sufficient to be in the room
        if(x <= l){
            dist += l-x + delta;
        }
        else if(x >= r){
            dist += x - r + delta;
        }

        if(y <= b){
            dist += b - y + delta;
        }
        else if(y >= t){
            dist += y - t + delta;
        }

        return dist;
    }

 
    //returns a grid of locations (for coloring)
    public static List<GridLocation> make_color_map(AmdpL0Room r, String flag){
    	int top = r.top;
    	int bottom = r.bottom;
    	int left = r.left;
    	int right = r.right;
    	
    	List<GridLocation> locations = new ArrayList<GridLocation>();
    	for (int i = left; i <= right; i++){
    		for (int j = bottom; j <= top; j++){
    			GridLocation l = new GridLocation(i,j, flag);
    			locations.add(l);
    		}
    	}
    	locations.add(new GridLocation(r.doorx, r.doory, flag));
		return locations;
    }
}
