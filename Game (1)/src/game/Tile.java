package game;

import java.awt.*;

public class Tile {
    private int x;
    private int y;
    private int WIDTH;
    private boolean collision;
    
    public Tile(int x, int y, int width, boolean temp){
        this.x=x;
        this.y=y;
        WIDTH=width;
        collision=temp;
    }
    
    public int getX(){return x;};
    public int getY(){return y;};
    public int getWidth(){return WIDTH;};
    public int getHeight(){return WIDTH;};
    public boolean canCollide(){return collision;};
    
    public void draw(Graphics2D g, int x2, int y2){
        if(collision){
            g.setColor(Color.GREEN);
            g.fillRect(x-x2-WIDTH,y-y2-WIDTH,2*WIDTH,2*WIDTH);
        }
    }
    
}
