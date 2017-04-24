package gridWorldL0;

import java.util.List;

import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.singleagent.oo.OOSADomain;

public class AmdpL0Domain extends GridWorldDomain {

	public AmdpL0Domain(int width, int height) {
		super(width, height);
	}
	
	public AmdpL0Domain(int [][] map){
		super(map);
	}
	
	public static final String CLASS_COORDINATE_RECTANGLE = "room";
	
    public static final String VAR_ROOM = "name";
    public static final String VAR_TOP = "top";
    public static final String VAR_LEFT = "left";
    public static final String VAR_BOTTOM = "bottom";
    public static final String VAR_RIGHT = "right";
    public static final String VAR_DOORX = "doorx";
    public static final String VAR_DOORY = "doory";
    
    public static final String PF_AGENT_IN_COORDINATE_RECTANGLE = "in_coordinate_rectangle";
    
	//PropositionalFunction will negotiate all interactions between
	//Agent and Room state objects
    public static class PF_InCoordinateRectangle extends PropositionalFunction {
        public PF_InCoordinateRectangle(String name, String [] params){
            super(name, params);
        }
        @Override
        public boolean isTrue(OOState s, String... params) {
        	//params = CLASS_COORDINATE_RECTANGLE instance e.g. "room1"
        	AmdpL0State working_state = (AmdpL0State)s; //cast generic to L0State
        	int x = working_state.agent.x;
        	int y = working_state.agent.y;

            ObjectInstance room_object = working_state.object(params[0]);
            return regionContainsPoint(room_object, x, y);
        }     
    }
    
    public static ObjectInstance currentRoom(OOState s, int x, int y, String oclass){
    	AmdpL0State working_state = (AmdpL0State)s;
        List<ObjectInstance> room_objects = working_state.objectsOfClass(oclass);
    	
        for(ObjectInstance o : room_objects){
            if(regionContainsPoint(o, x, y)){
                return o;
            }
        }
    	return null;
    }
    
    public static boolean regionContainsPoint(ObjectInstance o, int x, int y){
        int top = (Integer) o.get(VAR_TOP);
        int left = (Integer)o.get(VAR_LEFT);
        int bottom = (Integer)o.get(VAR_BOTTOM);
        int right = (Integer)o.get(VAR_RIGHT);
        int doorx = (Integer)o.get(VAR_DOORX);
        int doory = (Integer)o.get(VAR_DOORY);
        
        //in designated room doorway
        if(x == doorx && y == doory){
        	return true;
        }
        //in room
        if(y >= bottom && y <= top && x >= left && x <= right){
            return true;
        }
        return false;
    }
	
}
