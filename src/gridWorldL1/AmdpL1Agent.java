package gridWorldL1;

import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.annotations.DeepCopyState;
import gridWorldL0.AmdpL0Domain;

import static gridWorldL1.AmdpL1Domain.*;

import java.util.Arrays;
import java.util.List;

@DeepCopyState
public class AmdpL1Agent implements ObjectInstance {

    public String name;
    public String inRoom;

    private final static List<Object> keys = Arrays.<Object>asList(VAR_IN_ROOM);

    public AmdpL1Agent(String name, String inRoom){
        this.name = name;
        this.inRoom = inRoom;
    }

    public String className() {
        return AmdpL0Domain.CLASS_AGENT;
    }

    public String name() {
        return name;
    }

    public AmdpL1Agent copyWithName(String objectName) {
        return new AmdpL1Agent(objectName, inRoom);
    }

    public List<Object> variableKeys() {
        return keys;
    }

    public Object get(Object variableKey) {

        if(!(variableKey instanceof String)){
            throw new RuntimeException("Cleanup Agent variable key must be a string");
        }

        String key = (String)variableKey;
        if(key.equals(VAR_IN_ROOM)){
            return inRoom;
        }


        throw new RuntimeException("Unknown key for Cleanup Agent: " + key);
    }

    public AmdpL1Agent copy() {
        return new AmdpL1Agent(name,inRoom);
    }

    public String toString() {
        return OOStateUtilities.objectInstanceToString(this);
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        AmdpL1Agent that = (AmdpL1Agent) o;
        return (this.inRoom.equals(that.inRoom))
                && (this.name == that.name());
    }
}
