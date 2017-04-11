package teach;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import burlap.behavior.singleagent.Episode;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;

public class TestsDriver {
	//The primary purpose of this class is to hold the main method and run
	//	the helper tests in the following methods.
	public static void main(String[] args) {
		final String file_directory = System.getProperty("user.dir") + "/trajectory";
		
		Trajectory[] Traj_array = generateTrajectories(file_directory);
		// TODO Auto-generated method stub
	}
	
	public static boolean testTrajectories(){
		return false;//TODO: Replace with actual tests
	}
	
	public static List<Action> getActionSequence(Trajectory t) {
		return t.e.actionSequence;
	}
	public static String getActionName(Action a) {
		return a.actionName();
	}
	public static List<State> getStateSequence(Trajectory t){
		return t.e.stateSequence;
	}
	//Function may not be robust to different state formulations
	public static int[] getStateXY(State s) {
		List<Object> o = s.variableKeys();
		int x = (Integer) s.get(o.get(0));
		int y = (Integer) s.get(o.get(1));
		
		int[] a = {x,y};
		return a;
	}
	
	//Takes in directory of episode saved under annotated file names
	//and returns an array of Trajectories. 
	public static Trajectory[] generateTrajectories(String directoryName){
        File directory = new File(directoryName);
		List<Episode> episodes = Episode.readEpisodes(directoryName);

        File[] fList = directory.listFiles(); 
		Trajectory[] T = new Trajectory[episodes.size()];
		
		for (int i = 0; i < episodes.size(); i++) {
			String[] split = fList[i].getName().split("_");
			String start_symbol = split[1], end_symbol = split[3];
			
			//Convert symbol e.g. 'a' into number 1 via ascii
			int start_code = (int) start_symbol.charAt(0) - 96;
			int end_code = (int) end_symbol.charAt(0) - 96;
		
			Trajectory t = new Trajectory();
			t.e = episodes.get(i);
			t.start = start_code;
			t.end = end_code;
			T[i] = t; //add to trajectories 
		}
		
		return T;
	}
}
