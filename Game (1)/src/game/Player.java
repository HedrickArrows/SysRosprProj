package game;

import java.awt.*;

public class Player{
    private int x;
    private int y;
    private int r;
    
    private int dx;
    private double dy;
    private int speed;
    private double extraHeight=0.3;
    private double gravity=0.5;
    
    private boolean left;
    private boolean right;
    private boolean jump;
    private boolean canGoRight;
    private boolean canGoLeft;
    private boolean canGoUp;
    private boolean canGoDown;
    private boolean flying;
    private Color color1;
    
    private boolean con;
    public boolean getCon(){return con;}
    public void setCon(boolean x){con=x;}
    private int ready;
    public int getReady(){return ready;}
    public void setReady(int e){ready = e;}
    
    private int licznik;
    
    public Player(){
        //x=GamePanel.WIDTH/2;
        //y=GamePanel.HEIGHT/2;
        r=5;
        flying=false;
        canGoRight=true;
        canGoLeft=true;
        canGoUp=true;
        canGoDown=true;
        
        dx=0;
        dy=0;
        speed=5;
        color1=Color.BLUE;
        
        
        x=30000;
        y=30000;
        con = false;
        ready = 0;
        licznik = 10;
    }
    
    public Player(Color c){this(); color1 = c;}
    
    public int getX(){return x;};
    public int getY(){return y;};
    public int getWidth(){return r;};
    public int getHeight(){return r;};
    public int getDx(){return dx;};
    public int getDy(){return (int) dy;};
    public void leftWall(){canGoLeft=false;}
    public void rightWall(){canGoRight=false;}
    public void upWall(){canGoUp=false;}
    public void downWall(int th, int ty){
        y=ty-(th+r);
        canGoDown=false;
    }
    public void setLeft(boolean b){left=b;}
    public void setRight(boolean b){right=b;}
    public void setJump(boolean b){
        jump=b;
        if(dy==0 && flying==false){ //<-----------------------------kolizja z dołu
            dy=-5;
            y+=dy;
        }
    }
    public void newPosition(int nx, int ny){
        x=nx;
        y=ny;
    }
    public void update(){
        /*walking
        if(left&&canGoLeft){
            dx+=-speed;
        }
        if(right&&canGoRight){
            dx+=speed;
        }
        
        x+=dx;
        //JUMPING
        if(!canGoUp)dy=gravity;
        if(!canGoDown){
            dy=0;
            flying=false;
        }
        if(canGoDown){
            if(jump==true)dy-=extraHeight;
            flying=true;
            dy+=gravity;
            y+=dy;
        }
        dx=0;
        canGoRight=true;
        canGoLeft=true;
        canGoUp=true;
        canGoDown=true;*/
        
        //walking
        if(left&&canGoLeft && licznik==10){
            dx+=-speed;
        }else if(left && jump &&!canGoLeft && canGoDown){
            dx+=speed;
            dy=-3;
            licznik=0;
        }
        if(right&&canGoRight && licznik==10){
            dx+=speed;
        }else if(right && jump &&!canGoRight && canGoDown){
            dx+=-speed;
            dy=-3;
            licznik=0;
        }

        x+=dx;
        //JUMPING
        if(!canGoUp)dy=gravity;
        if(!canGoDown){
            dy=0;
            flying=false;
        }
        if(canGoDown){
            if(jump==true)dy-=extraHeight;
            flying=true;
            dy+=gravity;
            y+=dy;
        }
        if (licznik!=10)licznik++;
        else dx=0;
        canGoRight=true;
        canGoLeft=true;
        canGoUp=true;
        canGoDown=true;
    }
    public void draw(Graphics2D g, int x2, int y2){
        g.setColor(color1);
        g.fillOval(x-x2-r, y-y2-r, 2*r, 2*r);
        g.setStroke(new BasicStroke(3));
        g.setColor(color1);
        g.fillOval(x-x2-r, y-y2-r, 2*r, 2*r);
        g.setStroke(new BasicStroke(1));
        
    }
    
    public void connect(){
        x=GamePanel.WIDTH/2;
        y=GamePanel.HEIGHT/2;
        con = true;
    }
    
    public void disconnect(){
        x=30000;
        y=30000;
        con = false;
        ready = 0;}
}
