package gridWorldL0;

import static gridWorldL0.AmdpL0Domain.*;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.ObjectInstance;

public class AmdpL0Room implements ObjectInstance {
    public String name;
    public int top;
    public int left;
    public int bottom;
    public int right;
    public int doorx;
    public int doory;
    
    private final static List<Object> keys = 
    		Arrays.<Object>asList(VAR_ROOM, VAR_TOP, VAR_LEFT, VAR_BOTTOM, VAR_RIGHT, VAR_DOORX, VAR_DOORY);
    
    public AmdpL0Room(String name, int top, int left, int bottom, int right, int doorx, int doory) {
        this.name = name;
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.doorx = doorx;
        this.doory = doory;
    }
    
    public String className() {
        return CLASS_COORDINATE_RECTANGLE;
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
        if(key.equals(VAR_TOP)){
            return top;
        }
        else if(key.equals(VAR_LEFT)){
            return left;
        }
        else if(key.equals(VAR_BOTTOM)){
            return bottom;
        }
        else if(key.equals(VAR_RIGHT)){
            return right;
        }
        else if (key.equals(VAR_DOORX)){
        	return doorx;
        }
        else if (key.equals(VAR_DOORY)){
        	return doory;
        }
        throw new RuntimeException("Unknown key for Cleanup Room: " + key);
    }

    public AmdpL0Room copy() {
        return new AmdpL0Room(name, top, left, bottom, right, doorx, doory);
    }
    
    public AmdpL0Room copyWithName(String objectName) {
        return new AmdpL0Room(objectName, top, left, bottom, right, doorx, doory);
    }

    public String toString() {
        return OOStateUtilities.objectInstanceToString(this);
    }
}
