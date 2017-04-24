package gridWorldL1;

import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.ObjectParameterizedAction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;

import java.util.List;

public class AmdpL1Model implements FullStateModel{
    @Override
    public List<StateTransitionProb> stateTransitions(State s, Action a) {
        return FullStateModel.Helper.deterministicTransition(this,s,a);
    }

    @Override
    public State sample(State s, Action a) {
        AmdpL1State ns = ((AmdpL1State)s).copy();
        if(a.actionName().equals(AmdpL1Domain.ACTION_AGENT_TO_ROOM)){
            return agentToRoom(ns, (ObjectParameterizedAction) a);
        }
        throw new RuntimeException("Should not execute");
    }

    private State agentToRoom(AmdpL1State ns, ObjectParameterizedAction a) {
        AmdpL1Agent agent = ns.touchAgent();
        agent.inRoom = a.getObjectParameters()[0];
        return ns;
    }
}
