package gridWorldL0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import amdp.cleanup.state.CleanupAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.mdp.core.oo.state.MutableOOState;
import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;

/**
 * @author James MacGlashan.
 */
public class AmdpL0State implements MutableOOState {

	public AmdpL0Agent agent;
	public List<GridLocation> locations = new ArrayList<GridLocation>();
	public List<AmdpL0Room> rooms = new ArrayList<AmdpL0Room>();

	public AmdpL0State(AmdpL0Agent agent,  List<AmdpL0Room> rooms, List<GridLocation> locations){
		this.agent = agent;
		this.rooms = rooms;
        if(locations.size() == 0){
			this.locations = new ArrayList<GridLocation>();
		}
		else {
			this.locations = locations;
		}
	}

	@Override
	public int numObjects() {
		return 1 + this.locations.size() + this.rooms.size();
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

	@Override
	public List<ObjectInstance> objects() {
		List<ObjectInstance> obs = new ArrayList<ObjectInstance>(1+locations.size());
		obs.add(agent);
		obs.addAll(locations);
		obs.addAll(rooms);
		return obs;
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

	@Override
	public List<Object> variableKeys() {
		return OOStateUtilities.flatStateKeys(this);
	}

	@Override
	public Object get(Object variableKey) {
		OOVariableKey key = OOStateUtilities.generateKey(variableKey);
		if(key.obName.equals(agent.name())){
			return agent.get(key.obVarKey);
		}
		int ind = this.locationInd(key.obName);
		if(ind == -1){
			throw new RuntimeException("Cannot find object " + key.obName);
		}
		return locations.get(ind).get(key.obVarKey);
	}

	@Override
	public AmdpL0State copy() {
		return new AmdpL0State(agent, rooms, locations);
	}

	protected int locationInd(String oname){
		int ind = -1;
		for(int i = 0; i < locations.size(); i++){
			if(locations.get(i).name().equals(oname)){
				ind = i;
				break;
			}
		}
		return ind;
	}

	@Override
	public String toString() {
		System.out.println(getClass());
		System.out.println(getClass().getName());
		System.out.println(Integer.toHexString(hashCode()));
		return OOStateUtilities.ooStateToString(this);
	}

    public AmdpL0Agent touchAgent(){
        this.agent = agent.copy();
        return agent;
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
	public MutableState set(Object variableKey, Object value) {
		throw new RuntimeException("Should not execute");
	}
	@Override
	public MutableOOState addObject(ObjectInstance o) {
		throw new RuntimeException("Should not execute");
	}

	@Override
	public MutableOOState removeObject(String oname) {
		throw new RuntimeException("Should not execute");
	}

	@Override
	public MutableOOState renameObject(String objectName, String newName) {
		throw new RuntimeException("Should not execute");
	}
}
    

    

	

    


