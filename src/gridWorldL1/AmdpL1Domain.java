package gridWorldL1;

import amdp.amdpframework.GroundedPropSC;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.StateEnumerator;
import burlap.behavior.valuefunction.ConstantValueFunction;
import burlap.debugtools.RandomFactory;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.auxiliary.common.GoalConditionTF;
import burlap.mdp.auxiliary.common.NullTermination;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.oo.ObjectParameterizedAction;
import burlap.mdp.core.oo.propositional.GroundedProp;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.common.UniformCostRF;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.mdp.singleagent.oo.ObjectParameterizedActionType;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import gridWorldL0.AmdpL0Domain;
import gridWorldL0.AmdpL0Room;
import gridWorldL0.AmdpL0State;

import java.util.List;
import java.util.Random;

public class AmdpL1Domain implements DomainGenerator{
    public static final String VAR_CONNECTED = "connectedObjects";
    public static final String VAR_IN_ROOM = "inRoom";
    
    public static final String CLASS_ROOM = "room";
    public static final String CLASS_AGENT = "agent";
    
    public static final String ACTION_AGENT_TO_ROOM = "agentToRoom";
    
    public static final String PF_AGENT_IN_ROOM = "in_room";

    // this is the lower level domain from which we need propositional functions
    protected static OOSADomain L0;
    private static RewardFunction rf;
    private static TerminalFunction tf;

    public AmdpL1Domain(OOSADomain L0In, RewardFunction rf, TerminalFunction tf){
        L0 = L0In;
        this.rf = rf;
        this.tf= tf;
    }

    public OOSADomain generateDomain() {
        OOSADomain domain = new OOSADomain();
        
        domain.addStateClass(AmdpL0Domain.CLASS_AGENT, AmdpL1Agent.class).addStateClass(AmdpL0Domain.CLASS_COORDINATE_RECTANGLE, AmdpL0Room.class);
        
        domain.addActionTypes(
        		new GoToRoomActionType(ACTION_AGENT_TO_ROOM, 
        		new String[]{AmdpL0Domain.CLASS_COORDINATE_RECTANGLE}, L0));
        
        if (rf == null) {
            rf = new UniformCostRF();
        }
        if (tf == null) {
            tf = new NullTermination();
        }

        AmdpL1Model smodel = new AmdpL1Model();
        FactoredModel model = new FactoredModel(smodel, rf, tf);
        domain.setModel(model);
        return domain;
    }
    
    public static class PF_InRoom extends PropositionalFunction {
        public PF_InRoom(String name, String [] params){
            super(name, params);
        }
        @Override
        public boolean isTrue(OOState s, String... params) {
        	AmdpL1State working_state = (AmdpL1State)s; //cast generic to L0State
        	if (working_state.agent.inRoom == params[0]) {
        		return true;
        	}
        	return false;
        }     
    }

    public static class GoToRoomActionType extends ObjectParameterizedActionType{

        protected OOSADomain L0;

        public GoToRoomActionType(String name, String[] parameterClasses, OOSADomain L0In) {
            super(name, parameterClasses);
            L0 = L0In;
        }

        @Override
        protected boolean applicableInState(State s, ObjectParameterizedAction a) {
        	AmdpL1State working_state = (AmdpL1State)s;
        	
            //Cast current room as object
            AmdpL1Agent agent = working_state.agent;
            ObjectInstance curRegion = (ObjectInstance)working_state.object(agent.inRoom);
            
            //Cast current room as AmdpL1Room
            if(curRegion instanceof AmdpL1Room){
            	
                //Return true if action moves to connected room
                if(((AmdpL1Room)curRegion).connectedRooms.contains(a.getObjectParameters()[0])){
                    return true;
                }
            }
            return false;
        }
    }
    
    public static class InRegionSC implements StateConditionTest {

        public String srcOb;
        public String targetOb;

        public InRegionSC(String srcOb, String targetOb) {
            this.srcOb = srcOb;
            this.targetOb = targetOb;
        }

        public boolean satisfies(State s) {
            ObjectInstance src = ((AmdpL1State)s).object(this.srcOb);
            return src.get(VAR_IN_ROOM).equals(targetOb);
        }
    }
}
