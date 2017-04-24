package gridWorldL0;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import burlap.debugtools.RandomFactory;
import burlap.domain.singleagent.gridworld.state.GridAgent;
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

	@Override
	public List<StateTransitionProb> stateTransitions(State s, Action a) {

		double [] directionProbs = transitionDynamics[actionInd(a.actionName())];

		List <StateTransitionProb> transitions = new ArrayList<StateTransitionProb>();
		for(int i = 0; i < directionProbs.length; i++){
			double p = directionProbs[i];
			if(p == 0.){
				continue; //cannot transition in this direction
			}
			State ns = s.copy();
			int [] dcomps = movementDirectionFromIndex(i);
			ns = move(ns, dcomps[0], dcomps[1]);

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

		s = s.copy();

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
		return move(s, dcomps[0], dcomps[1]);

	}
	
	protected State move(State s, int xd, int yd){

		AmdpL0State gws = (AmdpL0State)s;

		int ax = gws.agent.x;
		int ay = gws.agent.y;

		int nx = ax+xd;
		int ny = ay+yd;

		//hit wall, so do not change position
		if(nx < 0 || nx >= map.length || ny < 0 || ny >= map[0].length || map[nx][ny] == 1 ||
				(xd > 0 && (map[ax][ay] == 3 || map[ax][ay] == 4)) || (xd < 0 && (map[nx][ny] == 3 || map[nx][ny] == 4)) ||
				(yd > 0 && (map[ax][ay] == 2 || map[ax][ay] == 4)) || (yd < 0 && (map[nx][ny] == 2 || map[nx][ny] == 4)) ){
			nx = ax;
			ny = ay;
		}

		AmdpL0Agent nagent = gws.touchAgent();
		nagent.x = nx;
		nagent.y = ny;

		return s;
	}
	
	public static int [] movementDirectionFromIndex(int i){
		int [] result = null;
		switch (i) {
			case 0:
				result = new int[]{0,1};
				break;
			case 1:
				result = new int[]{0,-1};
				break;
			case 2:
				result = new int[]{1,0};
				break;
			case 3:
				result = new int[]{-1,0};
				break;
			default:
				break;
		}
		return result;
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

}

