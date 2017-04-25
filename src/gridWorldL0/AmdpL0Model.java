package gridWorldL0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import burlap.debugtools.RandomFactory;
import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;

import static gridWorldL0.AmdpL0Domain.*;

public class AmdpL0Model implements FullStateModel{

	int [][] map;
	protected double[][] transitionDynamics;
	protected Random rand = RandomFactory.getMapped(0);

	public AmdpL0Model(int[][] map, double[][] transitionDynamics) {
		this.map = map;
		this.transitionDynamics = transitionDynamics;
	}
	
	public AmdpL0Model(){
		
	}

	@Override
	public List<StateTransitionProb> stateTransitions(State s, Action a) {
		AmdpL0State ns = ((AmdpL0State)s);

		double [] directionProbs = transitionDynamics[actionInd(a.actionName())];

		List <StateTransitionProb> transitions = new ArrayList<StateTransitionProb>();
		for(int i = 0; i < directionProbs.length; i++){
			double p = directionProbs[i];
			if(p == 0.){
				continue; //cannot transition in this direction
			}

			int [] dcomps = movementDirectionFromIndex(i);
			ns = (AmdpL0State) move(ns, dcomps[0], dcomps[1]);

			//make sure this direction doesn't actually stay in the same place and replicate another no-op
			boolean isNew = true;
			for(StateTransitionProb tp : transitions){
				if(tp.s.equals(ns)){
					isNew = false;
					tp.p += p;
					break;
				}
			}

			if(isNew){
				StateTransitionProb tp = new StateTransitionProb(ns, p);
				transitions.add(tp);
			}
		}
		return transitions;
	}

    
	@Override
	public State sample(State s, Action a) {
		AmdpL0State ns = ((AmdpL0State)s).copy();

		double [] directionProbs = transitionDynamics[actionInd(a.actionName())];
		double roll = rand.nextDouble();
		double curSum = 0.;
		int dir = 0;
		for(int i = 0; i < directionProbs.length; i++){
			curSum += directionProbs[i];
			if(roll < curSum){
				dir = i;
				break;
			}
		}

		int [] dcomps = movementDirectionFromIndex(dir);
		return move(ns, dcomps[0], dcomps[1]);

	}
	
	protected State move(State s, int xd, int yd){
		AmdpL0State ns = ((AmdpL0State)s);

		int ax = ns.agent.x;
		int ay = ns.agent.y;

		int nx = ax+xd;
		int ny = ay+yd;

		//hit wall, so do not change position
		if(nx < 0 || nx >= map.length || ny < 0 || ny >= map[0].length || map[nx][ny] == 1 ||
				(xd > 0 && (map[ax][ay] == 3 || map[ax][ay] == 4)) || (xd < 0 && (map[nx][ny] == 3 || map[nx][ny] == 4)) ||
				(yd > 0 && (map[ax][ay] == 2 || map[ax][ay] == 4)) || (yd < 0 && (map[nx][ny] == 2 || map[nx][ny] == 4)) ){
			nx = ax;
			ny = ay;
		}

		AmdpL0Agent nagent = ns.touchAgent();
		nagent.x = nx;
		nagent.y = ny;

		return s;
	}
    
	protected int actionInd(String name){
		if(name.equals(ACTION_NORTH)){
			return 0;
		}
		else if(name.equals(ACTION_SOUTH)){
			return 1;
		}
		else if(name.equals(ACTION_EAST)){
			return 2;
		}
		else if(name.equals(ACTION_WEST)){
			return 3;
		}
		throw new RuntimeException("Unknown action " + name);
	}

	
//    @Override
//    public State sample(State s, Action a) {
//        List<StateTransitionProb> stpList = this.stateTransitions(s,a);
//        double roll = rand.nextDouble();
//        double curSum = 0.;
//        for(int i = 0; i < stpList.size(); i++){
//            curSum += stpList.get(i).p;
//            if(roll < curSum){
//                return stpList.get(i).s;
//            }
//        }
//        throw new RuntimeException("Probabilities don't sum to 1.0: " + curSum);
//    }
//	
//	 @Override
//	    public List<StateTransitionProb> stateTransitions(State s, Action a) {
//		 	AmdpL0State ns = ((AmdpL0State)s).copy();
//	        int actionInd = actionInd(a.actionName());
//	        if(actionInd<4){
//	            if(actionInd==0){
//	                //north
//	                int dx = 0;
//	                int dy = 1;
//	                return move(ns,dx,dy);
//	            }
//	            else if(actionInd==1){
//	                //south
//	                int dx = 0;
//	                int dy = -1;
//	                return move(ns,dx,dy);
//	            }
//	            else if(actionInd==2){
//	                //east
//	                int dx = 1;
//	                int dy = 0;
//	                return move(ns,dx,dy);
//
//	            }
//	            else if(actionInd==3){
//	                //west
//	                int dx = -1;
//	                int dy = 0;
//	                return move(ns,dx,dy);
//	            }
//	        }
//	        throw new RuntimeException("Unknown action ind: " +actionInd );
//	    }
//
//    protected List<StateTransitionProb> move(State state, int xdelta, int ydelta){
//    	AmdpL0State ns = ((AmdpL0State)state);
//        return Arrays.asList(new StateTransitionProb(ns, 1.));
//    }
	
	
	
	
	
	
	
	
	
	
	
	
	
}

