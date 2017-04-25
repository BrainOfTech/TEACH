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

	public AmdpL0Model(int[][] map) {
		this.map = map;
	}

	@Override
	public List<StateTransitionProb> stateTransitions(State s, Action a) {
		return FullStateModel.Helper.deterministicTransition(this, s, a);
	}

    
	@Override
	public State sample(State s, Action a) {
		AmdpL0State ns = ((AmdpL0State)s).copy();

		int i = actionInd(a.actionName());
			int [] dcomps = null;
			switch (i) {
				case 0:
					dcomps = new int[]{0,1};
					break;
				case 1:
					dcomps = new int[]{0,-1};
					break;
				case 2:
					dcomps = new int[]{1,0};
					break;
				case 3:
					dcomps = new int[]{-1,0};
					break;
				default:
					break;
			}

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

