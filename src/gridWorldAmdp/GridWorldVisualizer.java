package gridWorldAmdp;

import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.visualizer.OOStatePainter;
import burlap.visualizer.ObjectPainter;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;
import gridWorldL0.AmdpL0Domain;
import gridWorldL0.AmdpL0Room;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author James MacGlashan.
 */
public class GridWorldVisualizer {

    public static Visualizer getVisualizer(String...agentImagePath){

        Visualizer v = new Visualizer(getStateRenderLayer(agentImagePath));
//        v.addObjectClassPainter(CleanupDomain.CLASS_ROOM, new RoomPainter());
//        v.addObjectClassPainter(CleanupDomain.CLASS_DOOR, new DoorPainter());
//        if(agentImagePath.length == 0){
//            v.addObjectClassPainter(CleanupDomain.CLASS_AGENT, new AgentPainter());
//        }
//        else{
//            v.addObjectClassPainter(CleanupDomain.CLASS_AGENT, new AgentPainterWithImages(agentImagePath[0]));
//        }
//        v.addObjectClassPainter(CleanupDomain.CLASS_BLOCK, new BlockPainter(agentImagePath[0]));

        return v;

    }

    public static StateRenderLayer getStateRenderLayer(String...agentImagePath){

        StateRenderLayer v = new StateRenderLayer();
        OOStatePainter oopainter = new OOStatePainter();

        oopainter.addObjectClassPainter(AmdpL0Domain.CLASS_COORDINATE_RECTANGLE, new RoomPainter());
        if(agentImagePath.length == 0){
            oopainter.addObjectClassPainter(AmdpL0Domain.CLASS_AGENT, new AgentPainter());
        }
        else{
            oopainter.addObjectClassPainter(AmdpL0Domain.CLASS_AGENT, new AgentPainterWithImages(agentImagePath[0]));
        }
        v.addStatePainter(oopainter);
        return v;
    }


    public static Visualizer getVisualizer(int maxX, int maxY, String...agentImagePath){
        Visualizer v = new Visualizer(getStateRenderLayer(maxX, maxY, agentImagePath ));
        return v;
    }


    public static StateRenderLayer getStateRenderLayer(int maxX, int maxY, String...agentImagePath){

        StateRenderLayer v = new StateRenderLayer();
        OOStatePainter oopainter = new OOStatePainter();

        oopainter.addObjectClassPainter(AmdpL0Domain.CLASS_COORDINATE_RECTANGLE, new RoomPainter(maxX, maxY));
        if(agentImagePath.length == 0){
            oopainter.addObjectClassPainter(AmdpL0Domain.CLASS_AGENT, new AgentPainter());
        }
        else{
            oopainter.addObjectClassPainter(AmdpL0Domain.CLASS_AGENT, new AgentPainterWithImages(agentImagePath[0],maxX, maxY));
        }
        v.addStatePainter(oopainter);
        return v;
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

            GridAgent oba = (GridAgent)ob;
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


    public static class AgentPainterWithImages implements ObjectPainter, ImageObserver {

        protected int maxX = -1;
        protected int maxY = -1;

        Map<String, BufferedImage> dirToImage;

        public AgentPainterWithImages(String pathToImageDir){
            if(!pathToImageDir.endsWith("/")){
                pathToImageDir = pathToImageDir + "/";
            }

            dirToImage = new HashMap<String, BufferedImage>(4);
            try {
                dirToImage.put("north", ImageIO.read(new File(pathToImageDir + "robotNorth.png")));
                dirToImage.put("south", ImageIO.read(new File(pathToImageDir + "robotSouth.png")));
                dirToImage.put("east", ImageIO.read(new File(pathToImageDir + "robotEast.png")));
                dirToImage.put("west", ImageIO.read(new File(pathToImageDir + "robotWest.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public AgentPainterWithImages(String pathToImageDir, int maxX, int maxY){
            this.maxX = maxX;
            this.maxY = maxY;

            if(!pathToImageDir.endsWith("/")){
                pathToImageDir = pathToImageDir + "/";
            }

            dirToImage = new HashMap<String, BufferedImage>(4);
            try {
                dirToImage.put("north", ImageIO.read(new File(pathToImageDir + "robotNorth.png")));
                dirToImage.put("south", ImageIO.read(new File(pathToImageDir + "robotSouth.png")));
                dirToImage.put("east", ImageIO.read(new File(pathToImageDir + "robotEast.png")));
                dirToImage.put("west", ImageIO.read(new File(pathToImageDir + "robotWest.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void paintObject(Graphics2D g2, OOState s, ObjectInstance ob, float cWidth, float cHeight) {


        	GridAgent oba = (GridAgent) ob;
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

            String dir = "n/a";

            BufferedImage img = this.dirToImage.get(dir);
            g2.drawImage(img, (int)rx, (int)ry, (int)width, (int)height, this);

        }

        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y,
                                   int width, int height) {
            return false;
        }
    }
}
