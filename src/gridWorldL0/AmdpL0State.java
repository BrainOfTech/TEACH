package gridWorldL0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.core.oo.state.ObjectInstance;

public class AmdpL0State extends GridWorldState {
	
    public List<AmdpL0Room> rooms = new ArrayList<AmdpL0Room>();
    
    public AmdpL0State(GridAgent agent, List<AmdpL0Room> rooms, List<GridLocation> locations) {
        super.agent = agent;
        this.rooms = rooms;
        if(locations.size() == 0){
			super.locations = new ArrayList<GridLocation>();
		}
		else {
			super.locations = locations;
		}
    }
    
    @Override
    //returns a single object given a Object label e.g. "room1"
	public ObjectInstance object(String oname) {
		if(oname.equals(agent.name())){
			return agent;
		}
		int ind = this.locationInd(oname);
		if(ind != -1){
			return locations.get(ind);
		}
		ind = this.roomInd(oname);
		if(ind != -1){
			return rooms.get(ind);
		}
		return null;
	}
    
   //compares object give a specific field ^name
	protected int roomInd(String oname){
		int ind = -1;
		for(int i = 0; i < rooms.size(); i++){
			if(rooms.get(i).name().equals(oname)){
				ind = i;
				break;
			}
		}
		return ind;
	}
	
	@Override
	//returns list of objects given a Class label e.g. "CLASS_AGENT"
    public List<ObjectInstance> objectsOfClass(String oclass) {

        if(oclass.equals(AmdpL0Domain.CLASS_AGENT)){
            return Arrays.<ObjectInstance>asList(agent);
        }
		else if(oclass.equals(AmdpL0Domain.CLASS_LOCATION)){
			return new ArrayList<ObjectInstance>(locations);
		}
        else if(oclass.equals(AmdpL0Domain.CLASS_COORDINATE_RECTANGLE)){
            return new ArrayList<ObjectInstance>(rooms);
        }
        throw new RuntimeException("Unknown class type " + oclass);
    }
}
