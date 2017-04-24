package gridWorldL1;

import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;

import java.util.List;
import java.util.Random;

//agent.inRoom = a.getObjectParameters()[0]; //what does this do?
public class AmdpL1Model implements FullStateModel{
	
    public AmdpL1Model() {}
	
    public List<StateTransitionProb> stateTransitions(State s, Action a) {
    	if(a.actionName().equals(AmdpL1Domain.ACTION_AGENT_TO_ROOM)){
            return FullStateModel.Helper.deterministicTransition(this,s,a);
        }
        throw new RuntimeException("unknown action queried: " + a.actionName());
    }

    public State sample(State s, Action a) {
        List<StateTransitionProb> stpList = this.stateTransitions(s,a);
        Random random = new Random();
        int roll = random.nextInt(5 - 0 + 1) + 0; //Random number between 0 (inclusive) and 5(exclusive)
        return stpList.get(roll).s;
    }
}
