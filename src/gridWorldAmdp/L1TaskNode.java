package gridWorldAmdp;


import amdp.cleanup.PullCostGoalRF;
import burlap.mdp.auxiliary.common.GoalConditionTF;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.oo.ObjectParameterizedAction;
import burlap.mdp.core.oo.propositional.GroundedProp;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import gridAmdpFramework.GroundedPropSC;
import gridAmdpFramework.GroundedTask;
import gridAmdpFramework.NonPrimitiveTaskNode;
import gridAmdpFramework.TaskNode;
import gridWorldL0.AmdpL0Domain;
import gridWorldL1.AmdpL1Domain;

import java.util.ArrayList;
import java.util.List;

public class L1TaskNode extends NonPrimitiveTaskNode {

    ActionType actionType;


    public L1TaskNode(ActionType actionType, OOSADomain l0Domain, TaskNode[] children) {
        this.childTaskNodes =children;
        this.oosaDomain = l0Domain;
        this.actionType = actionType;
    }

    @Override
    public Object parametersSet(State s) {
        List<String[]> params = new ArrayList<String[]>();
        List<Action> gtActions = actionType.allApplicableActions(s);
        for(Action a:gtActions){
            params.add(((ObjectParameterizedAction)a).getObjectParameters());
        }
        return params;
    }

    public boolean terminal(State s, Action action) {
        return getL0Tf((ObjectParameterizedAction)action).isTerminal(s);
    }

    public List<GroundedTask> getApplicableGroundedTasks(State s) {
        List<GroundedTask> gtList = new ArrayList<GroundedTask>();
        List<Action> gtActions = actionType.allApplicableActions(s);
        for(Action a:gtActions){
            gtList.add(new GroundedTask(this,a));
        }
        return gtList;
    }

    public RewardFunction rewardFunction(Action action) {
        return getL0Rf((ObjectParameterizedAction) action);
    }

    public TerminalFunction getL0Tf(ObjectParameterizedAction oga){
        StateConditionTest sc = null;
        if(oga.actionName().equals(AmdpL1Domain.ACTION_AGENT_TO_ROOM)){
            sc = new GroundedPropSC(new GroundedProp(this.oosaDomain.propFunction(AmdpL0Domain.PF_AGENT_IN_COORDINATE_RECTANGLE), new String[]{oga.getObjectParameters()[0]}));
        }
        return new GoalConditionTF(sc);
    }


    public RewardFunction getL0Rf(ObjectParameterizedAction oga){
        StateConditionTest sc = null;
        if(oga.actionName().equals(AmdpL1Domain.ACTION_AGENT_TO_ROOM)){
            sc = new GroundedPropSC(new GroundedProp(this.oosaDomain.propFunction(AmdpL0Domain.PF_AGENT_IN_COORDINATE_RECTANGLE), new String[]{oga.getObjectParameters()[0]}));
        }
        return new PullCostGoalRF(sc, 1., 0.);
    }
}
