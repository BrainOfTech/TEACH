package tutorialDemos;

import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.visualizer.Visualizer;

public class HelloGridWorld {

	public static void main(String[] args) {

		GridWorldDomain gw = new GridWorldDomain(11, 11); // 11x11 grid world
		gw.setMapToFourRooms(); // four rooms layout
		gw.setDeterministicTransitionDynamics();
		//gw.setProbSucceedTransitionDynamics(1); 
		OOSADomain domain = gw.generateDomain(); // generate the grid world domain
		
		
		// setup initial state
		MutableState s = new GridWorldState(new GridAgent(0, 10), new GridLocation(10, 10, "loc0"));

		// create visualizer and explorer
		Visualizer v = GridWorldVisualizer.getVisualizer(gw.getMap());
		VisualExplorer exp = new VisualExplorer(domain, v, s);

		// set control keys to use w-s-a-d
		exp.addKeyAction("w", GridWorldDomain.ACTION_NORTH, "");
		exp.addKeyAction("s", GridWorldDomain.ACTION_SOUTH, "");
		exp.addKeyAction("a", GridWorldDomain.ACTION_WEST, "");
		exp.addKeyAction("d", GridWorldDomain.ACTION_EAST, "");

		exp.initGUI();

	}

}