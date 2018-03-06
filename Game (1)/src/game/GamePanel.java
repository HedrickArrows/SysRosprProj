package game;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import static java.lang.Math.*;
import java.net.Socket;
import static java.sql.JDBCType.NULL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GamePanel extends JPanel implements Runnable, KeyListener{
    public static int WIDTH=600;
    public static int HEIGHT=400;
    protected Socket s;
    private final static int PORT = 27015;//SET A CONSTANT VARIABLE PORT
    private final static String HOST = "localhost";//SET A CONSTANT VARIABLE HOST
    private ExecutorService exec;// = Executors.newSingleThreadExecutor();
    private Task<Void> t;
    private OutputStreamWriter osw;
    //protected Integer[] e;
    protected Integer ID;
    
    private Thread thread;
    private boolean running;
    private BufferedImage image;
    private Graphics2D g;
    private int FPS =60;
    private double averageFPS;
    
    private int playerNumber;
    public static ArrayList<Player> players;
    private static ArrayList<ArrayList<Tile>> tiles;
    private Camera camera;
    //tymczasowo
    public int rows;
    public int cols;
    private int tileWidth;
    boolean wygral=false;
    
    Color tab[] = {Color.BLUE, Color.RED, Color.BLACK, Color.MAGENTA, Color.ORANGE };
    
    public GamePanel(){
        super();
        //e = new Integer[5];
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        playerNumber=0;
        exec = Executors.newSingleThreadExecutor();

    }

    public GamePanel(ArrayList<Player> p, Socket sock, int pn){
        super();
        //e = new Integer[5];
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        playerNumber=0;
        exec = Executors.newSingleThreadExecutor();
        players = p;
        s = sock;
        playerNumber = pn;
        players.get(playerNumber).setReady(1);
        for(Player pl : players)
            if(pl.getCon())
                pl.connect();
            else pl.disconnect();
    }
    
    public void addNotify(){
        super.addNotify();
        if(thread==null){
            thread= new Thread(this);
            thread.start();
        }
        addKeyListener(this);
    }
    public void run(){
        try {
            running=true;
            
            for(Player pl : players)
            if(pl.getCon()) pl.connect();
            players.get(playerNumber).setReady(1);
            image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            g=(Graphics2D)image.getGraphics();
            /*
            players=new ArrayList<Player>(5);
            for(int i=0;i<5;i++)players.add(new Player(tab[i]));*/
            cols=0;
            rows=0;
            tileWidth=10;
            tiles=new ArrayList<ArrayList<Tile>>();
            loadMap();
            long startTime;
            long URDTimeMillis;
            long waitTime;
            long totalTime=0;
            int frameCount=0;
            int maxFrameCount=60;
            long targetTime=1000/FPS;
            
            camera=new Camera(players.get(playerNumber), WIDTH/4, HEIGHT/4*3, cols*2*tileWidth, rows*2*tileWidth);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            osw = new OutputStreamWriter(s.getOutputStream(), "UTF-8");
           
            //x=GamePanel.WIDTH/2; y=GamePanel.HEIGHT/2;
            //osw.write(String.format("%04d", GamePanel.WIDTH/2)+" "+String.format("%04d", GamePanel.HEIGHT/2)+ " " + true);
            //osw.flush();
            /*try{
            s = new Socket(HOST, PORT);//CONNECT TO THE SERVER
            osw = new OutputStreamWriter(s.getOutputStream(), "UTF-8");
            System.out.println("You connected to " + HOST +":" + Integer.toString(PORT));//IF CONNECTED THEN PRINT IT OUT
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String msg ="";
            while(!msg.contains("Welcome"))
            msg = br.readLine();
            String parts[] = msg.split(" ");
            playerNumber = Integer.valueOf(parts[1]);
            //layers[Integer.valueOf(parts[1])] = new Player();
            camera=new Camera(players.get(playerNumber), WIDTH/4, HEIGHT/4*3, cols*2*tileWidth, rows*2*tileWidth);
            t = new Receive(s, players, playerNumber/*, this*//*);
            exec.submit(t);
            
            //playerNumber=ID;
            
            }catch (Exception noServer)//IF DIDNT CONNECT PRINT THAT THEY DIDNT
            {
            System.out.println("The server might not be up at this time.");
            System.out.println("Please try again later.");
            }*/
            //camera=new Camera(players.get(playerNumber), WIDTH/4, HEIGHT/4*3, cols*2*tileWidth, rows*2*tileWidth);
            //GAME LOOP
            
            while(running){
                startTime=System.nanoTime();
                
                gameUpdate();
                gameRender();
                gameDraw();
                
                URDTimeMillis=(System.nanoTime()-startTime)/1000000;
                waitTime=targetTime-URDTimeMillis;
                
                try{
                    Thread.sleep(waitTime);
                }
                catch(Exception e){}
                totalTime+=System.nanoTime()-startTime;
                frameCount++;
                if(frameCount==maxFrameCount){
                    averageFPS=1000.0/((totalTime/frameCount)/1000000);
                    frameCount=0;
                    totalTime=0;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void gameUpdate(){
        /*for(int i=0; i<players.size();i++){
            //player-map collision checking
            Player p=players.get(i);
            int px=p.getX();
            int py=p.getY();
            int ph=p.getHeight();
            int pw=p.getWidth();
            int pdx=p.getDx();
            int pdy=p.getDy();
            for(int j=0; j<tiles.size();j++){
                for(int k=0; k<tiles.get(j).size();k++){
                    Tile t=tiles.get(j).get(k);
                    if(t.canCollide()){
                        int tx=t.getX();
                        int ty=t.getY();
                        int th=t.getHeight();
                        int tw=t.getWidth();
                        
                        if(abs(ty-py)<ph+th){
                            if(px<tx && px+pw+tw+pdx>=tx)
                                players.get(i).rightWall();
                            if(px>tx && px-(pw+tw)+pdx<=tx )
                                players.get(i).leftWall();
                        }
                        if(abs(tx-px)<pw+tw){
                            if(py>ty && py-(ph+th)+pdy<=ty && abs(tx-px)<pw+tw)
                                players.get(i).upWall();
                            if(py<ty && py+ph+th+pdy>=ty && abs(tx-px)<pw+tw)
                                players.get(i).downWall();
                        }
                    }
                }
            }
            //move players
            players.get(i).update();
        }*/
        
        //player-map collision checking
            Player p=players.get(playerNumber);
            int px=p.getX();
            int py=p.getY();
            int ph=p.getHeight();
            int pw=p.getWidth();
            int pdx=p.getDx();
            int pdy=p.getDy();
            for(int j=0; j<tiles.size();j++){
                for(int k=0; k<tiles.get(j).size();k++){
                    Tile t=tiles.get(j).get(k);
                    if(t.canCollide()){
                        int tx=t.getX();
                        int ty=t.getY();
                        int th=t.getHeight();
                        int tw=t.getWidth();
                        
                        if(abs(ty-py)<ph+th){
                            if(px<tx && px+pw+tw+pdx>=tx)
                                p.rightWall();
                            if(px>tx && px-(pw+tw)+pdx<=tx )
                                p.leftWall();
                        }
                        if(abs(tx-px)<pw+tw){
                            if(py>ty && py-(ph+th)+pdy<=ty && abs(tx-px)<pw+tw)
                                players.get(playerNumber).upWall();
                            if(py<ty && py+ph+th+pdy>=ty && abs(tx-px)<pw+tw)
                                players.get(playerNumber).downWall(th, ty);
                        }
                    }
                }
            }
            //move players
            p.update();
        /*try {
            //x=GamePanel.WIDTH/2; y=GamePanel.HEIGHT/2;
            osw.write(String.format("%04d", p.getX())+" "+String.format("%04d", p.getY())+ " " + 1);
            osw.flush();
        } catch (IOException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    private void gameRender(){
        while(camera==null){}
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        int temp=10;
        for(int i=0; i<players.size();i++){
            players.get(i).draw(g, camera.getX(), camera.getY());
            if((players.get(i).getX()<=1735 && players.get(i).getX()>=1725 && players.get(i).getY()<=195 && players.get(i).getY()>=185) 
                    || players.get(i).getReady() == 2){
                wygral=true;
                temp=i;
                players.get(i).setReady(2);
            }
        }
        int firstTileX=((camera.getX()-tileWidth)/2)/tileWidth;
        int firstTileY=((camera.getY()-tileWidth)/2)/tileWidth;
        for(int i=firstTileY; i<min(firstTileY+HEIGHT/(tileWidth*2)+2,tiles.size());i++){
            for(int j=firstTileX; j<min(firstTileX+WIDTH/(tileWidth*2)+2, tiles.get(i).size());j++){
                tiles.get(i).get(j).draw(g, camera.getX(), camera.getY());
            }
        }
        g.setColor(Color.BLACK);
        g.drawString("FPS: "+averageFPS, 10, 10);
        if(wygral==true){
            g.setColor(Color.RED);
            g.drawString("WYGRAŁ GRACZ NR: "+temp, 10, 20);
            wygral=false;
            JFrame mini=new JFrame();
            mini.setTitle("WYGRANA");
            mini.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JLabel napis = new JLabel ("WYGRAŁ GRACZ NR: "+temp, JLabel.CENTER );
            napis.setForeground(Color.red);
            mini.getContentPane().add( napis);
            mini.setResizable(false);
            mini.setSize(200,200);
            mini.setLocation(500, 150);
            mini.setVisible(true);
            try {
                Thread.sleep(5000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            try {
                s.close();
                System.out.println("Done.");
            } catch (IOException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.exit(0);
        }
    }
    private void gameDraw(){
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }
    public void keyTyped(KeyEvent key){
        
    }
    public void keyPressed(KeyEvent key){
        //try {
            int keyCode=key.getKeyCode();
            if(keyCode==KeyEvent.VK_LEFT){
                players.get(playerNumber).setLeft(true);         
                //osw.write(Integer.toString(20));
            }
            if(keyCode==KeyEvent.VK_RIGHT){
                players.get(playerNumber).setRight(true);
                //osw.write(Integer.toString(21));
            }
            if(keyCode==KeyEvent.VK_SPACE){
                players.get(playerNumber).setJump(true);
                //osw.write(Integer.toString(22));
            }
            /*osw.flush();
        } catch (IOException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    public void keyReleased(KeyEvent key){
       // try {
            int keyCode=key.getKeyCode();
            if(keyCode==KeyEvent.VK_LEFT){
                players.get(playerNumber).setLeft(false);
                //osw.write(Integer.toString(10));
            }
            if(keyCode==KeyEvent.VK_RIGHT){
                players.get(playerNumber).setRight(false);
                //osw.write(Integer.toString(11));
            }
            if(keyCode==KeyEvent.VK_SPACE){
                players.get(playerNumber).setJump(false);
                //osw.write(Integer.toString(12));
            }
            /*osw.flush();
        } catch (IOException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    public void loadMap(){
        FileReader fr = null;
   String linia = "";

   // OTWIERANIE PLIKU:
   try {
     fr = new FileReader("map");
   } catch (FileNotFoundException e) {
       System.out.println("BŁĄD PRZY OTWIERANIU PLIKU!");
       System.exit(1);
   }

   BufferedReader bfr = new BufferedReader(fr);
   // ODCZYT KOLEJNYCH LINII Z PLIKU:
   try {
     for(int i=0; i<1000; i++){
        //System.out.println(linia);
        linia = bfr.readLine();
        if(linia==null)break;
        tiles.add(new ArrayList<Tile>());
        cols=linia.length();
        rows++;
        for(int j=0; j<linia.length();j++){
            if(linia.charAt(j)=='0'){
                tiles.get(i).add(new Tile(j*2*tileWidth+tileWidth, i*2*tileWidth+tileWidth, tileWidth, false));
            }else if(linia.charAt(j)=='1'){
                tiles.get(i).add(new Tile(j*2*tileWidth+tileWidth, i*2*tileWidth+tileWidth, tileWidth, true));
            }//System.out.println("X:"+j+" Y:"+i);
        }
     }
    } catch (IOException e) {
        System.out.println("BŁĄD ODCZYTU Z PLIKU!");
        System.exit(2);
   }

   // ZAMYKANIE PLIKU
   try {
     fr.close();
    } catch (IOException e) {
         System.out.println("BŁĄD PRZY ZAMYKANIU PLIKU!");
         System.exit(3);
        }
    
    }
    public void SetPlayerNum(int ID){
        playerNumber=ID;
        //camera=new Camera(players.get(playerNumber), WIDTH/4, HEIGHT/4*3, cols*2*tileWidth, rows*2*tileWidth);
    }

}
