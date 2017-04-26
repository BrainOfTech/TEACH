package gridWorldAmdp;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer.CellPainter;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer.LocationPainter;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer.MapPainter;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.vardomain.VariableDomain;
import burlap.visualizer.OOStatePainter;
import burlap.visualizer.ObjectPainter;
import burlap.visualizer.StatePainter;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;
import gridWorldL0.AmdpL0Agent;
import gridWorldL0.AmdpL0Domain;
import gridWorldL0.AmdpL0Room;
import gridWorldL0.AmdpL0State;

import java.util.List;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static burlap.domain.singleagent.gridworld.GridWorldDomain.VAR_X;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.VAR_Y;
import static gridWorldL0.AmdpL0Domain.*;

/**
 * @author Arthur Wandzel
 */
public class GridWorldVisualizer {
	
	public static ValueFunctionVisualizerGUI getGridWorldValueFunctionVisualization(List <State> states, int maxX, int maxY, ValueFunction valueFunction, Policy p){
	return ValueFunctionVisualizerGUI.createGridWorldBasedValueFunctionVisualizerGUI(states, valueFunction, p,
			new OOVariableKey(CLASS_AGENT, VAR_X), new OOVariableKey(CLASS_AGENT, VAR_Y), new VariableDomain(0, maxX), new VariableDomain(0, maxY), 1, 1,
			ACTION_NORTH, ACTION_SOUTH, ACTION_EAST, ACTION_WEST);
	}

	public static Visualizer getVisualizer(int [][] map, String gr, AmdpL0State state){
		Visualizer v = new Visualizer(getRenderLayer(map, gr, state));
		return v;
	}


//    public static Visualizer getVisualizer2(int maxX, int maxY){
//        Visualizer v = new Visualizer(getStateRenderLayer(maxX, maxY));
//        return v;
//    }

    /**
	 * Returns state render layer for a gird world domain with the provided wall map.
	 * @param map the wall map matrix where 0s indicate it is clear of walls, 1s indicate a full cell wall in that cell, 2s indicate a 1D north wall, 3s indicate a 1D east wall, and 4s indicate a 1D north and east wall.
	 * @return a grid world domain state render layer
	 */
	public static StateRenderLayer getRenderLayer(int [][] map, String gr, AmdpL0State state){
		
		StateRenderLayer v = new StateRenderLayer();
		OOStatePainter oopainter = new OOStatePainter();
		v.addStatePainter(new MapPainter(map, gr, state));
		
		
		//oopainter.addObjectClassPainter(AmdpL0Domain.CLASS_COORDINATE_RECTANGLE, new RoomPainter(11, 11));
        //oopainter.addObjectClassPainter(AmdpL0Domain.CLASS_AGENT, new AgentPainter());
		oopainter.addObjectClassPainter(AmdpL0Domain.CLASS_AGENT, new CellPainter(1, Color.gray, map));
		v.addStatePainter(oopainter);
		
		return v;
		
	}
	
//  public static StateRenderLayer getStateRenderLayer(int maxX, int maxY){
//
//      StateRenderLayer v = new StateRenderLayer();
//      OOStatePainter oopainter = new OOStatePainter();
//
//      oopainter.addObjectClassPainter(AmdpL0Domain.CLASS_COORDINATE_RECTANGLE, new RoomPainter(maxX, maxY));
//      oopainter.addObjectClassPainter(AmdpL0Domain.CLASS_AGENT, new AgentPainter());
//      v.addStatePainter(oopainter);
//      return v;
//  }
	

	public static class MapPainter implements StatePainter {

		protected int 				dwidth;
		protected int 				dheight;
		protected int [][] 			map;
		String goalRoom;
		AmdpL0State sta;
		
		/**
		 * Initializes for the domain and wall map
		 * @param map the wall map matrix where 1s indicate a wall in that cell and 0s indicate it is clear of walls
		 */
		public MapPainter(int [][] map, String gr, AmdpL0State state) {
			this.dwidth = map.length;
			this.dheight = map[0].length;
			this.map = map;
			this.goalRoom = gr;
			this.sta = state;
		}

		@Override
		public void paint(Graphics2D g2, State s, float cWidth, float cHeight) {
			
			
			
			//set stroke for 1d walls
			g2.setStroke(new BasicStroke(4));
			
			float domainXScale = this.dwidth;
			float domainYScale = this.dheight;
			
			//determine then normalized width
			float width = (1.0f / domainXScale) * cWidth;
			float height = (1.0f / domainYScale) * cHeight;
			
			//pass through each cell of the map and if it is a wall, draw it
			for(int x = 0; x < this.dwidth; x++){
				for(int y = 0; y < this.dheight; y++){
					//draw the walls; make them black
					g2.setColor(Color.black);
					
					boolean drawNorthWall = false;
					boolean drawEastWall = false;
					
					if(this.map[x][y] == 1){
					
						float rx = x*width;
						float ry = cHeight - height - y*height;
					
						g2.fill(new Rectangle2D.Float(rx, ry, width, height));
						
					}
					else if(this.map[x][y] == 2){
						drawNorthWall = true;
					}
					else if(this.map[x][y] == 3){
						drawEastWall = true;
					}
					else if(this.map[x][y] == 4){
						drawNorthWall = true;
						drawEastWall = true;
					}
					
					int left = (int)(x*width);
					int top = (int)(cHeight - height - y*height);
					
					if(drawNorthWall){
						g2.drawLine(left, top, (int)(left+width), top);
					}
					if(drawEastWall){
						g2.drawLine((int)(left+width), top, (int)(left+width), (int)(top + height));
					}
					
					//Color Goal Room
					g2.setColor(Color.blue);
					ObjectInstance o = sta.object(goalRoom);
					
					float rx = x*width;
					float ry = cHeight - height - y*height;
					
			        //in designated room doorway
			        if(x == (Integer)o.get(VAR_DOORX) && y == (Integer)o.get(VAR_DOORY)){
			        	g2.fill(new Rectangle2D.Float(rx, ry, width, height));
			        }
			        //in room
			        if(y >= (Integer)o.get(VAR_BOTTOM) && y <= (Integer)o.get(VAR_TOP) && x >= (Integer)o.get(VAR_LEFT) && x <= (Integer)o.get(VAR_RIGHT)){
			        	g2.fill(new Rectangle2D.Float(rx, ry, width, height));
			        }
					
				}
			}
		}
	}
	
	public static class CellPainter implements ObjectPainter{

		protected Color			col;
		protected int			dwidth;
		protected int			dheight;
		protected int [][]		map;
		protected int			shape = 0; //0 for rectangle 1 for ellipse
		
		
		/**
		 * Initializes painter for a rectangle shape cell
		 * @param col the color to paint the cell
		 * @param map the wall map matrix where 1s indicate a wall in that cell and 0s indicate it is clear of walls
		 */
		public CellPainter(Color col, int [][] map) {
			this.col = col;
			this.dwidth = map.length;
			this.dheight = map[0].length;
			this.map = map;
		}
		
		/**
		 * Initializes painter with filling the cell with the given shape
		 * @param shape the shape with which to fill the cell. 0 for a rectangle, 1 for an ellipse.
		 * @param col the color to paint the cell
		 * @param map the wall map matrix where 1s indicate a wall in that cell and 0s indicate it is clear of walls
		 */
		public CellPainter(int shape, Color col, int [][] map) {
			this.col = col;
			this.dwidth = map.length;
			this.dheight = map[0].length;
			this.map = map;
			this.shape = shape;
		}

		@Override
		public void paintObject(Graphics2D g2, OOState s, ObjectInstance ob, float cWidth, float cHeight) {
			
			
			//set the color of the object
			g2.setColor(this.col);
			
			float domainXScale = this.dwidth;
			float domainYScale = this.dheight;
			
			//determine then normalized width
			float width = (1.0f / domainXScale) * cWidth;
			float height = (1.0f / domainYScale) * cHeight;

			float rx = (Integer)ob.get(VAR_X)*width;
			float ry = cHeight - height - (Integer)ob.get(VAR_Y)*height;
			
			if(this.shape == 0){
				g2.fill(new Rectangle2D.Float(rx, ry, width, height));
			}
			else{
				g2.fill(new Ellipse2D.Float(rx, ry, width, height));
			}
		}
	}

    public static class RoomPainter implements ObjectPainter {

        protected int maxX = -1;
        protected int maxY = -1;

        public RoomPainter(){

        }

        public RoomPainter(int maxX, int maxY){
            this.maxX = maxX;
            this.maxY = maxY;
        }


        @Override
        public void paintObject(Graphics2D g2, OOState s, ObjectInstance ob, float cWidth, float cHeight) {

            AmdpL0Room obr = (AmdpL0Room)ob;

            float domainXScale = 10;
            float domainYScale = 10;

            if(maxX != -1){
                domainXScale = maxX;
                domainYScale = maxY;
            }

            //determine then normalized width
            float width = (1.0f / domainXScale) * cWidth;
            float height = (1.0f / domainYScale) * cHeight;

            int top = obr.top;
            int left = obr.left;
            int bottom = obr.bottom;
            int right = obr.right;

            Color rcol = Color.darkGray;
            float [] hsb = new float[3];
            Color.RGBtoHSB(rcol.getRed(), rcol.getGreen(), rcol.getBlue(), hsb);
            hsb[1] = 0.4f;
            rcol = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);

            for(int i = left; i <= right; i++){
                for(int j = bottom; j <= top; j++){

                    float rx = i*width;
                    float ry = cHeight - height - j*height;

                    if(i == left || i == right || j == bottom || j == top){
                            g2.setColor(Color.black);
                            g2.fill(new Rectangle2D.Float(rx, ry, width, height));
                    }
                    else{
                        g2.setColor(rcol);
                        g2.fill(new Rectangle2D.Float(rx, ry, width, height));
                    }
                }
            }
        }
    }


    public static class AgentPainter implements ObjectPainter{

        protected int maxX = -1;
        protected int maxY = -1;

        public AgentPainter(){

        }

        public AgentPainter(int maxX, int maxY){
            this.maxX = maxX;
            this.maxY = maxY;
        }

        @Override
        public void paintObject(Graphics2D g2, OOState s, ObjectInstance ob, float cWidth, float cHeight) {

            AmdpL0Agent oba = (AmdpL0Agent)ob;
            g2.setColor(Color.darkGray);

            float domainXScale = 10 + 1f;
            float domainYScale = 10 + 1f;

            if(maxX != -1){
                domainXScale = maxX;
                domainYScale = maxY;
            }

            //determine then normalized width
            float width = (1.0f / domainXScale) * cWidth;
            float height = (1.0f / domainYScale) * cHeight;

            int x = oba.x;
            int y = oba.y;

            float rx = x*width;
            float ry = cHeight - height - y*height;

            g2.fill(new Rectangle2D.Float(rx, ry, width, height));
        }
    }
}
