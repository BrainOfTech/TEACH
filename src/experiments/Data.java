package experiments;

import burlap.behavior.singleagent.Episode;

public class Data {

   	 public Episode episode;
   	 public long timing;
   	 public int action;
   	 
   	 public Data(){
   		 
   	 }
   	 
   	public Data(Episode e, long t){
   		 this.episode = e;
   		 this.timing = t;
   		 this.action = e.actionSequence.size();
   	 }
}
