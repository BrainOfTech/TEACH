package gridAmdpFramework;


import burlap.behavior.policy.Policy;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableState;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a task node from the MAXQ paper. Such a task node is associated with a grounded action,
 * and has a MAXQNodes as children.
 * Created by ngopalan on 5/6/16.
 */
public abstract class NonPrimitiveTaskNode implements TaskNode{

    protected TaskNode[] childTaskNodes;

//    protected Policy policy;

    protected String name;

    protected OOSADomain oosaDomain;



    @Override
    public boolean isTaskPrimitive(){
        return false;
    }

    @Override
    public String getName(){
        return name;
    }

    public TaskNode[] getChildren(){
        return childTaskNodes;
    }

    public List<GroundedTask> getGroundedChildren(State s){
        List<GroundedTask> gtList = new ArrayList<GroundedTask>();
        for(TaskNode t: childTaskNodes){
            gtList.addAll(t.getApplicableGroundedTasks(s));
        }
        return gtList;
    }

    public void setChildTaskNodes(TaskNode[] childTaskNodes) {
        this.childTaskNodes = childTaskNodes;
    }


    public abstract Object parametersSet(State s);

    public boolean hasHashingFactory(){
        return false;
    }

    public HashableState hashedState(State s, GroundedTask childTask){
        System.err.println("Tried to get hashable state when not set at the node!");
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NonPrimitiveTaskNode that = (NonPrimitiveTaskNode) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    // this is the reward function for the grounded task
//    public double pseudoRewardFunction(State s, GroundedTask gt){
//        return 0.;
//    }


    public OOSADomain domain(){
        return oosaDomain;
    }


}
