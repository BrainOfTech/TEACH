package gridWorldL1;

import burlap.mdp.core.oo.state.MutableOOState;
import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.annotations.ShallowCopyState;

import static gridWorldL1.AmdpL1Domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ShallowCopyState
public class AmdpL1State  implements MutableOOState {

    public AmdpL1Agent agent;
    public List<AmdpL1Room> rooms = new ArrayList<AmdpL1Room>();

    public AmdpL1State(AmdpL1Agent agent, List<AmdpL1Room> rooms) {
        this.agent = agent;
        this.rooms = rooms;
    }

    public ObjectInstance object(String objectName) {
        if(objectName.equals(agent.name())){
            AmdpL1Agent nagent = agent.copy();
            return nagent;
        }

        int indL = this.roomInd(objectName);
        if(indL != -1) {
            //copy on write
        	AmdpL1Room nroom = this.rooms.get(indL).copy();
            return nroom;
        }
        throw new RuntimeException("Cannot find object: " + objectName);
    }

    public List<ObjectInstance> objects() {
        List<ObjectInstance> obs = new ArrayList<ObjectInstance>(1+rooms.size());
        obs.add(agent);
        obs.addAll(rooms);
        return obs;
    }

    public List<ObjectInstance> objectsOfClass(String oclass) {

        if(oclass.equals(AmdpL1Domain.CLASS_AGENT)){
            return Arrays.<ObjectInstance>asList(agent);
        }
        else if(oclass.equals(AmdpL1Domain.CLASS_ROOM)){
            return new ArrayList<ObjectInstance>(rooms);
        }
        throw new RuntimeException("Unknown class type " + oclass);
    }

    public List<Object> variableKeys() {
        return OOStateUtilities.flatStateKeys(this);
    }

    public Object get(Object variableKey) {
        OOVariableKey key = OOStateUtilities.generateKey(variableKey);

        if(key.obName.equals(agent.name())){
            return  agent.get(key.obVarKey);
        }
        int indL = this.roomInd(key.obName);
        if(indL != -1) {
            //copy on write
            return this.rooms.get(indL).get(key.obVarKey);
        }
        throw new RuntimeException("Cleanup State L1: cannot find object " + key.obName);
    }

    public AmdpL1State copy() {
        return new AmdpL1State(agent,rooms);
    }

    public String toString() {
        return OOStateUtilities.ooStateToString(this);
    }
    
    public int numObjects() {
        return 1+rooms.size();
    }

    public AmdpL1Agent touchAgent(){
        this.agent = agent.copy();
        return agent;
    }

    public List<AmdpL1Room> touchRooms(){
        this.rooms = new ArrayList<AmdpL1Room>(rooms);
        return rooms;
    }

    public AmdpL1Room touchRoom(int ind){
    	AmdpL1Room n = rooms.get(ind).copy();
        touchRooms().remove(ind);
        rooms.add(ind, n);
        return n;
    }

    public int roomInd(String oname){
        int ind = -1;
        for(int i = 0; i < rooms.size(); i++){
            if(rooms.get(i).name().equals(oname)){
                ind = i;
                break;
            }
        }
        return ind;
    }
    
    public MutableState set(Object variableKey, Object value) {
        throw new RuntimeException("Should not execute");
    }
    
    public MutableOOState addObject(ObjectInstance o) {
    	throw new RuntimeException("Should not execute");
     }

     public MutableOOState removeObject(String objectName) {
    	 throw new RuntimeException("Should not execute");
     }

     public MutableOOState renameObject(String objectName, String newName) {
    	 throw new RuntimeException("Should not execute");
     }

}
