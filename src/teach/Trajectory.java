package teach;

import java.util.List;

import burlap.behavior.singleagent.Episode;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;

public class Trajectory {
	public Episode e;
	public int start;
	public int end;

	public List<Action> getActionSequence() {
		return e.actionSequence;
	}

	public List<State> getStateSequence() {
		return e.stateSequence;
	}
}
