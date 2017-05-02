package teach.gridLearnedAMDP;

import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.OOState;
import gridWorldL1.AmdpL1State;

public class InRoomPropFunc extends PropositionalFunction{//TODO: Define Prop Function based on grounding?
	String goalLoc;
	LearnedStateMapping lsm;
	
	public InRoomPropFunc(String goal, LearnedStateMapping lsm) {
		super(null, null, new String[]{goal});
		//Assumes the first (and only parameter) is the goal location.
		goalLoc = goal;
		this.lsm = lsm;
	}

	@Override
	public boolean isTrue(OOState s, String... params) {
//		if(((AmdpL1State)lsm.mapState(s)).agent.inRoom.equals( goalLoc )){
//			System.out.println("Debugging: Arrived at " + goalLoc);
//		} else {
//			System.out.println("Debugging: Trying to get to: " + goalLoc);
//		}
		return ((AmdpL1State)lsm.mapState(s)).agent.inRoom.equals( goalLoc );
	}
	
}