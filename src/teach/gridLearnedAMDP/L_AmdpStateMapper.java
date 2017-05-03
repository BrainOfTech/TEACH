package teach.gridLearnedAMDP;

import burlap.mdp.auxiliary.StateMapping;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import gridWorldL0.AmdpL0Agent;
import gridWorldL0.AmdpL0State;
import gridWorldL1.AmdpL1Agent;
import gridWorldL1.AmdpL1Room;
import gridWorldL1.AmdpL1State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class L_AmdpStateMapper implements StateMapping{

    public L_AmdpStateMapper() {}

    public State mapState(State sIn) {

    	AmdpL0State L0_state = (AmdpL0State)sIn;
    	
    	AmdpL0Agent L0_agent = L0_state.agent;
        int ax = L0_agent.x;
        int ay = L0_agent.y;
        
        //Get room name
        ObjectInstance inRoom = L_AmdpL0Domain.currentRoom(sIn, ax, ay, "rooms");
        String roomName = inRoom.name();
        
        //Construct L1 rooms object
		List<String> r1L1_connectedRooms = Arrays.asList("room1", "room2", "room4"); 
		List<String> r2L1_connectedRooms = Arrays.asList("room2", "room1", "room3");
		List<String> r3L1_connectedRooms = Arrays.asList("room3", "room2", "room4"); 
		List<String> r4L1_connectedRooms = Arrays.asList("room4", "room3", "room1"); 
		AmdpL1Room r1L1 = new AmdpL1Room("room1", r1L1_connectedRooms);
		AmdpL1Room r2L1 = new AmdpL1Room("room2", r2L1_connectedRooms);
		AmdpL1Room r3L1 = new AmdpL1Room("room3", r3L1_connectedRooms);
		AmdpL1Room r4L1 = new AmdpL1Room("room4", r4L1_connectedRooms);
		List<AmdpL1Room> L2_rooms = new ArrayList<AmdpL1Room>(Arrays.asList(r1L1, r2L1, r3L1, r4L1));

        //Construct L1 agent object
        AmdpL1Agent L1_agent = new AmdpL1Agent(L0_agent.name(), roomName);

        //Construct L1 state
        return new AmdpL1State(L1_agent,L2_rooms);
    }
}
