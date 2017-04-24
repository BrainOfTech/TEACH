package gridWorldL1;

import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.annotations.DeepCopyState;

import static gridWorldL1.AmdpL1Domain.*;

import java.util.Arrays;
import java.util.List;

@DeepCopyState
public class AmdpL1Room implements ObjectInstance {
    public String name;
    public List<String> connectedRooms;

    private final static List<Object> keys = Arrays.<Object>asList(VAR_CONNECTED);

    public AmdpL1Room(String name, List<String> connectedRooms) {
        this.name = name;
        this.connectedRooms = connectedRooms;
    }

    public String className() {
        return AmdpL1Domain.CLASS_ROOM;
    }

    public String name() {
        return name;
    }

    public AmdpL1Room copyWithName(String objectName) {
        return new AmdpL1Room(objectName, connectedRooms);
    }

    public List<Object> variableKeys() {
        return keys;
    }

    public Object get(Object variableKey) {
        if(!(variableKey instanceof String)){
            throw new RuntimeException("Cleanup Door variable key must be a string");
        }

        String key = (String)variableKey;
        if(key.equals(VAR_CONNECTED)){
            return connectedRooms;
        }

        throw new RuntimeException("Unknown key for Cleanup Door: " + key);
    }

    public AmdpL1Room copy() {
        return new AmdpL1Room(name, connectedRooms);
    }

    @Override
    public String toString() {
        return OOStateUtilities.objectInstanceToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        AmdpL1Room that = (AmdpL1Room) o;
        for(int i=0;i<connectedRooms.size();i++){
            if(!this.connectedRooms.get(i).equals(that.connectedRooms.get(i))){
                return false;
            }
        }
        return true;
    }
}
