package teach.gridLearnedAMDP;

import static teach.gridLearnedAMDP.L_AmdpL0Domain.*;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.ObjectInstance;

public class L_AmdpL0Room implements ObjectInstance {
    public String name;
    public int[][] learnedMap;
    
    private final static List<Object> keys = 
    		Arrays.<Object>asList(VAR_ROOM, LEARNED_MAP);
    
    public L_AmdpL0Room(String name, int[][] lm) {
        this.name = name;
        this.learnedMap = lm;
    }
    
    public String className() {
        return CLASS_COORDINATE_SPACE;
    }

    public String name() {
        return name;
    }

    public List<Object> variableKeys() {
        return keys;
    }

    public Object get(Object variableKey) {
        if(!(variableKey instanceof String)){
            throw new RuntimeException("Cleanup Room variable key must be a string");
        }

        String key = (String)variableKey;
        if (key.equals(VAR_ROOM)){
        	return name;
        }
        else if (key.equals(LEARNED_MAP)){
        	return learnedMap;
        }
        throw new RuntimeException("Unknown key for Cleanup Room: " + key);
    }

    public L_AmdpL0Room copy() {
        return new L_AmdpL0Room(name, learnedMap);
    }
    
    public L_AmdpL0Room copyWithName(String objectName) {
        return new L_AmdpL0Room(objectName, learnedMap);
    }

    public String toString() {
        return OOStateUtilities.objectInstanceToString(this);
    }
}
