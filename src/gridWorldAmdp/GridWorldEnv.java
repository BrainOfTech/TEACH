package gridWorldAmdp;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.environment.extensions.EnvironmentObserver;
import burlap.mdp.singleagent.oo.OOSADomain;

public class GridWorldEnv extends SimulatedEnvironment{
	
	 public GridWorldEnv(OOSADomain domain, State initialState) {
	        super(domain, initialState);
	    }

	 @Override
	    public EnvironmentOutcome executeAction(Action a) {

	        Action simGA = a.copy();
//	        simGA.action = this.domain.getAction(ga.actionName());
//	        if(simGA.action == null){
//	            throw new RuntimeException("Cannot execute action " + ga.toString() + " in this SimulatedEnvironment because the action is to known in this Environment's domain");
//	        }
	        for(EnvironmentObserver observer : this.observers){
	            observer.observeEnvironmentActionInitiation(this.currentObservation(), a);
	        }

	        EnvironmentOutcome eo;
//	        State nextState;
	        if(!this.isInTerminalState()) {
                eo= model.sample(this.curState, a);
//	            nextState = simGA.executeIn(this.curState);
//	            this.lastReward = this.rf.reward(this.curState, simGA, nextState);
	        }
	        else{
	            eo = new EnvironmentOutcome(this.curState, a, this.curState.copy(), 0., true);
	        }

	        this.lastReward = eo.r;
	        this.terminated = eo.terminated;
	        this.curState = eo.op;

	        for(EnvironmentObserver observer : this.observers){
	            observer.observeEnvironmentInteraction(eo);
	        }

	        return eo;
	    }
}
