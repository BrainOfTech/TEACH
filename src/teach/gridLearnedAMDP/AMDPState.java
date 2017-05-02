package teach.gridLearnedAMDP;

import java.util.ArrayList;
import java.util.List;

import burlap.mdp.core.oo.state.MutableOOState;
import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;


//TODO: THIS CLASS IS UNFINISHED AND CURRENTLY UNUSED.
//TODO: MAKE THIS A USEFUL ABSTRACTION THAT CAN BE USED FOR ARBITRARY TASKS.
public class AMDPState implements MutableOOState{
	ArrayList<ObjectInstance> amdpObjects;
	
	/**
	 * This constructor does a shallow copy of the array of objects stored in the other.
	 * @param other
	 */
	public AMDPState(AMDPState other){
		for(int i = 0; i < other.amdpObjects.size(); i++){
			amdpObjects.add(other.amdpObjects.get(i));
		}
	}
	
	@Override
	public int numObjects() {
		return amdpObjects.size();
	}

	@Override
	public ObjectInstance object(String oname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ObjectInstance> objects() {
		return amdpObjects;
	}

	@Override
	public List<ObjectInstance> objectsOfClass(String oclass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> variableKeys() {
		// TODO Auto-generated method stub
		return OOStateUtilities.flatStateKeys(this);
	}

	@Override
	public Object get(Object variableKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MutableState set(Object variableKey, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MutableOOState addObject(ObjectInstance o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MutableOOState removeObject(String oname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MutableOOState renameObject(String objectName, String newName) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
