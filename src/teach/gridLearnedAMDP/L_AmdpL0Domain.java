package teach.gridLearnedAMDP;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.debugtools.RandomFactory;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.auxiliary.common.NullTermination;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.oo.OODomain;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.vardomain.VariableDomain;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.singleagent.common.UniformCostRF;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import gridWorldL0.AmdpL0Model;
import gridWorldL0.AmdpL0State;
import gridWorldL1.AmdpL1State;
import teach.LearningFunctions;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

//See GridWorldDomain for documentation 

public class L_AmdpL0Domain implements DomainGenerator {
    public static final String VAR_ROOM = "name";
	public static final String VAR_X = "x";
	public static final String VAR_Y = "y";
	public static final String LEARNED_MAP = "learnedMap";
	
	public static final String CLASS_AGENT = "agent";
	public static final String CLASS_COORDINATE_SPACE = "rooms";

	public static final String ACTION_NORTH = "north";
	public static final String ACTION_SOUTH = "south";
	public static final String ACTION_EAST = "east";
	public static final String ACTION_WEST = "west";
	
	public static final String PF_WALL_NORTH = "wallToNorth";
	public static final String PF_WALL_SOUTH = "wallToSouth";
	public static final String PF_WALL_EAST = "wallToEast";
	public static final String PF_WALL_WEST = "wallToWest";
    public static final String PF_AGENT_IN_COORDINATE_SPACE = "coordinate_space";
    
	protected int										width;
	protected int										height;
	protected int [][]									map;
	
	protected RewardFunction rf;
	protected TerminalFunction tf;
	
	public L_AmdpL0Domain(RewardFunction rf, TerminalFunction tf){
        this.rf = rf;
        this.tf = tf;
	}
	public List<PropositionalFunction> generatePfs(){
		List<PropositionalFunction> pfs = Arrays.asList(
			new WallToPF(PF_WALL_NORTH, new String[]{CLASS_AGENT}, 0),
			new WallToPF(PF_WALL_SOUTH, new String[]{CLASS_AGENT}, 1),
			new WallToPF(PF_WALL_EAST, new String[]{CLASS_AGENT}, 2),
			new WallToPF(PF_WALL_WEST, new String[]{CLASS_AGENT}, 3),
			new PF_InCoordinateSpace(PF_AGENT_IN_COORDINATE_SPACE, new String[]{CLASS_COORDINATE_SPACE})
		); //IMPORTANT PF_InCoordinateRectangle

		return pfs;
	}

	@Override
	public OOSADomain generateDomain() {

		OOSADomain domain = new OOSADomain();

		domain.addStateClass(CLASS_AGENT, GridAgent.class);
		domain.addStateClass(CLASS_COORDINATE_SPACE, L_AmdpL0Room.class);
		
		OODomain.Helper.addPfsToDomain(domain, this.generatePfs());
		
		domain.addActionTypes(
				new UniversalActionType(ACTION_NORTH),
				new UniversalActionType(ACTION_SOUTH),
				new UniversalActionType(ACTION_EAST),
				new UniversalActionType(ACTION_WEST));

		AmdpL0Model smodel = new AmdpL0Model(this.map);
		RewardFunction rf = this.rf;
		TerminalFunction tf = this.tf;

		if(rf == null){
			rf = new UniformCostRF();
		}
		if(tf == null){
			tf = new NullTermination();
		}

		FactoredModel model = new FactoredModel(smodel, rf, tf);
		domain.setModel(model);
		return domain;
	}
    
    //LEARNED FUNCTIONS///////////////////////////////////////////////////////////////
    public static ObjectInstance currentRoom(State s, int x, int y, String oclass){
    	AmdpL0State working_state = (AmdpL0State)s;
        List<ObjectInstance> room_objects = working_state.objectsOfClass(oclass);
    	
        for(ObjectInstance o : room_objects){
            if(learnedRegionContainsPoint(o, x, y)){
                return o;
            }
        }
    	return null;
    }
	
    public static class PF_InCoordinateSpace extends PropositionalFunction {
        public PF_InCoordinateSpace(String name, String [] params){
            super(name, params);
        }
        
        @Override
        public boolean isTrue(OOState s, String... params) {
        	//params = CLASS_COORDINATE_RECTANGLE instance e.g. "room1"
        	AmdpL0State working_state = (AmdpL0State)s; //cast generic to L0State
        	int x = working_state.agent.x;
        	int y = working_state.agent.y;
        	
        	ObjectInstance o = working_state.object(params[0]);
        	
        	return learnedRegionContainsPoint(o, x, y);
        }     
    }
    
    public static boolean learnedRegionContainsPoint(ObjectInstance o, int x, int y){
    	int [][] lm = (int[][]) o.get(LEARNED_MAP);
    	int key = -1;
    	if (o.name().equals("room1")){
    		key = 1;
    	}
    	else if (o.name().equals("room2")){
    		key = 2;
    	} 
    	else if (o.name().equals("room3")){
    		key = 3;
    	} 
    	else if (o.name().equals("room4")){
    		key = 4;
    	} 
    	else {
    		throw new RuntimeException("Mismatch for Learned Region");
    	}
        return key == lm[x][y];
    }

    //GRIDWORLD FUNCTIONS///////////////////////////////////////////////////////////////
	public class WallToPF extends PropositionalFunction{

		protected int xdelta;
		protected int ydelta;
		
		public WallToPF(String name, String[] parameterClasses, int direction) {
			super(name, parameterClasses);
			int [] dcomps = L_AmdpL0Domain.movementDirectionFromIndex(direction);
			xdelta = dcomps[0];
			ydelta = dcomps[1];
		}

		@Override
		public boolean isTrue(OOState st, String... params) {
			
			ObjectInstance agent = st.object(params[0]);
			
			int ax = (Integer)agent.get("x");
			int ay = (Integer)agent.get("y");
			
			int cx = ax + xdelta;
			int cy = ay + ydelta;
			
			if(cx < 0 || cx >= L_AmdpL0Domain.this.width || cy < 0 || cy >= L_AmdpL0Domain.this.height || L_AmdpL0Domain.this.map[cx][cy] == 1 || 
					(xdelta > 0 && (L_AmdpL0Domain.this.map[ax][ay] == 3 || L_AmdpL0Domain.this.map[ax][ay] == 4)) || (xdelta < 0 && (L_AmdpL0Domain.this.map[cx][cy] == 3 || L_AmdpL0Domain.this.map[cx][cy] == 4)) ||
					(ydelta > 0 && (L_AmdpL0Domain.this.map[ax][ay] == 2 || L_AmdpL0Domain.this.map[ax][ay] == 4)) || (ydelta < 0 && (L_AmdpL0Domain.this.map[cx][cy] == 2 || L_AmdpL0Domain.this.map[cx][cy] == 4)) ){
				return true;
			}
			
			return false;
		}
	}
	
	public RewardFunction getRf() {
		return rf;
	}
	public TerminalFunction getTf() {
		return tf;
	}
	
	public void makeEmptyMap(){
		this.map = new int[this.width][this.height];
		for(int i = 0; i < this.width; i++){
			for(int j = 0; j < this.height; j++){
				this.map[i][j] = 0;
			}
		}
	}
	public void setMapToFourRooms(){
		this.width = 11;
		this.height = 11;
		this.makeEmptyMap();
		
		horizontalWall(0, 0, 5);
		horizontalWall(2, 4, 5);
		horizontalWall(6, 7, 4);
		horizontalWall(9, 10, 4);
		
		verticalWall(0, 0, 5);
		verticalWall(2, 7, 5);
		verticalWall(9, 10, 5);
	}
	public void horizontalWall(int xi, int xf, int y){
		for(int x = xi; x <= xf; x++){
			this.map[x][y] = 1;
		}
	}
	public void horizontal1DNorthWall(int xi, int xf, int y){
		for(int x = xi; x <= xf; x++){
			int cur = this.map[x][y];
			if(cur != 3 && cur != 4){
				this.map[x][y] = 2;
			}
			else{
				this.map[x][y] = 4;
			}
		}
	}
	public void verticalWall(int yi, int yf, int x){
		for(int y = yi; y <= yf; y++){
			this.map[x][y] = 1;
		}
	}
	public void vertical1DEastWall(int yi, int yf, int x){
		for(int y = yi; y <= yf; y++){
			int cur = this.map[x][y];
			if(cur != 2 && cur != 4){
				this.map[x][y] = 3;
			}
			else{
				this.map[x][y] = 4;
			}
		}
	}
	public void setObstacleInCell(int x, int y){
		this.map[x][y] = 1;
	}
	public void set1DNorthWall(int x, int y){
		int cur = this.map[x][y];
		if(cur != 3 && cur != 4){
			this.map[x][y] = 2;
		}
		else{
			this.map[x][y] = 4;
		}
	}
	public void set1DEastWall(int x, int y){
		int cur = this.map[x][y];
		if(cur != 2 && cur != 4){
			this.map[x][y] = 3;
		}
		else{
			this.map[x][y] = 4;
		}
	}
	public void clearLocationOfWalls(int x, int y){
		this.map[x][y] = 0;
	}
	public void setCellWallState(int x, int y, int wallType){
		this.map[x][y] = wallType;
	}
	public int [][] getMap(){
		int [][] cmap = new int[this.map.length][this.map[0].length];
		for(int i = 0; i < this.map.length; i++){
			for(int j = 0; j < this.map[0].length; j++){
				cmap[i][j] = this.map[i][j];
			}
		}
		return cmap;
	}
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}
	
	public static int [] movementDirectionFromIndex(int i){
		int [] result = null;
		switch (i) {
			case 0:
				result = new int[]{0,1};
				break;
			case 1:
				result = new int[]{0,-1};
				break;
			case 2:
				result = new int[]{1,0};
				break;
			case 3:
				result = new int[]{-1,0};
				break;
			default:
				break;
		}
		return result;
	}
}
