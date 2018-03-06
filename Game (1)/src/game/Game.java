package game;

import static game.GamePanel.HEIGHT;
import static game.GamePanel.WIDTH;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingWorker;

public class Game {
    private final static int PORT = 27015;//SET A CONSTANT VARIABLE PORT
    private static String HOST = "localhost";//SET A CONSTANT VARIABLE HOST
    public static ArrayList<Player> players;
    public static Color tab[] = {Color.BLUE, Color.RED, Color.BLACK, Color.MAGENTA, Color.ORANGE };
    public static JTextField txt;
    public static ExecutorService exec ;//= Executors.newSingleThreadExecutor();
    public static Socket s = new Socket();
    public static OutputStreamWriter osw;
    public static BufferedReader br;
    public static int playerNumber;
    public static Task<Void> t;
    public static boolean isGame;
    
    public static void main(String[] args) {
        GamePanel g = new GamePanel();
        
        exec = Executors.newSingleThreadExecutor();
        
        isGame = false;
        
        players=new ArrayList<Player>(5);
        for(int i=0;i<5;i++)players.add(new Player(tab[i]));
        
        JFrame mini=new JFrame();
        mini.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mini.setTitle("MEMU GŁÓWNE");
        mini.setResizable(false);
        mini.setSize(300,300);
        mini.setLocation(500, 150);
        mini.setVisible(true);
        mini.setLayout(new FlowLayout());
        //JButton b2= new JButton("Nowa gra");
        JButton b3= new JButton("Nowa Gra (Połącz się z serwerem)");
        JButton b4= new JButton("Wyjdz z gry");
        //b2.setFocusable(false);
        b3.setFocusable(false);
        b4.setFocusable(false);
        ActionListener a = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                System.out.println(event.getSource());
                Object source=event.getSource();
            /*if(source==b2){
                    GamePanel g = new GamePanel();
                    JFrame window = new JFrame("First Game");
                    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    window.setVisible(true);
                    window.setLocation(320, 150);
                    window.setContentPane(g);       
                    window.pack();
                    mini.setVisible(false);
            }*/
            if(source==b3){
                JFrame wait = new JFrame("Waiting Box");
                //Socket s;
                wait.setLocation(320, 150);
                wait.setSize(300,200);
                
                mini.setVisible(false);
                wait.setVisible(true);
                
                wait.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        try {
                            mini.setVisible(true);
                            wait.setVisible(false);
                            if(s.isConnected())
                                s.close();
                            for(Player p : players){
                                    p.setReady(0);
                                    p.setCon(false);
                                }
                            /*if(t.isRunning())
                               t.cancel();*/
                            wait.dispose();
                        } catch (IOException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
});
                //String[] data = {"Player 1", "Player 2", "Player 3", "Player 4", "Player 5"};
                JList k = new JList(players.toArray());
                //k.setModel(new DefaultListModel());
                k.setSelectionModel(new DefaultListSelectionModel(){
                    @Override
                    public void setAnchorSelectionIndex(final int anchorIndex) {}

                    @Override
                    public void setLeadAnchorNotificationEnabled(final boolean flag) {}

                    @Override
                    public void setLeadSelectionIndex(final int leadIndex) {}

                    @Override
                    public void setSelectionInterval(final int index0, final int index1) { }
                });
                k.setCellRenderer(new DefaultListCellRenderer() {
                    
                     @Override
                     public Component getListCellRendererComponent(JList list, Object value, int index,
                               boolean isSelected, boolean cellHasFocus) {
                          Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                          if (value instanceof Player) {
                               String nextUser = "Player #" + index;
                               setText(nextUser);
                               if (((Player) value).getReady() != 0) {
                                    setForeground(Color.GREEN);
                               } else {
                                    setForeground(Color.RED);
                               }
                               if (!((Player) value).getCon()) {
                                    setForeground(Color.WHITE);
                               }
                               SwingWorker worker = new SwingWorker() {
                                    @Override
                                    public Object doInBackground() {
                                        try {
                                            Thread.sleep(15);
                                        } catch (InterruptedException e) { /*Who cares*/ }
                                        return null;
                                    }
                                    @Override
                                    public void done() {
                                        //match = null;
                                        
                                        if(allReady() && !isGame){
                                            GamePanel g = new GamePanel(players, s, playerNumber);
                                            JFrame window = new JFrame("First Game");
                                            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                            window.setVisible(true);
                                            window.setLocation(320, 150);
                                            window.setContentPane(g);       
                                            window.pack();
                                            wait.setVisible(false);
                                            isGame = true;
                                        }
                                        
                                        list.repaint();
                                    }
                                };
                            worker.execute();
                            } else {
                                 setText("whodat?");
                            }
                          return c;
                     }

                });
                
                txt= new JTextField(10);
                txt.setText(HOST);
                JButton cnt = new JButton("Connect");
                
                cnt.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
             
                        connect();
                      // handle the jbutton event here             
                        /*try{
                            HOST = String.valueOf(txt.getText());
                            Socket s = new Socket(HOST, PORT);//CONNECT TO THE SERVER
                            OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream(), "UTF-8");
                            System.out.println("You connected to " + HOST +":" + Integer.toString(PORT));//IF CONNECTED THEN PRINT IT OUT
                            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                                        String msg ="";
                                        while(!msg.contains("Welcome"))
                                            msg = br.readLine();
                                        String parts[] = msg.split(" ");
                                        int playerNumber = Integer.valueOf(parts[1]);
                                        //layers[Integer.valueOf(parts[1])] = new Player();
                                        //camera=new Camera(players.get(playerNumber), WIDTH/4, HEIGHT/4*3, cols*2*tileWidth, rows*2*tileWidth);
                            Task<Void> t = new Receive(s, players, playerNumber);
                            exec.submit(t);
                            Player p = players.get(playerNumber);
                            osw.write(String.format("%04d", p.getX())+" "+String.format("%04d", p.getY())+ " " + Boolean.toString(p.getReady()));
                            osw.flush();

                            //playerNumber=ID;

                        }catch (Exception noServer)//IF DIDNT CONNECT PRINT THAT THEY DIDNT
                            {
                            System.out.println("The server might not be up at this time.");
                            System.out.println("Please try again later.");
                        }*/
                    } 
                });
                
                JButton rdy = new JButton("Ready");
                rdy.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        if(s.isConnected()){
                            //try {
                                Player p = players.get(playerNumber);
                                if(p.getReady() < 1){
                                    p.setReady(1);
                                }
                                else
                                    p.setReady(0);
                               // p.setReady((p.getReady() +0 )%2);
                                /*
                                osw.write(String.format("%04d",(WIDTH/2)) + " " + String.format("%04d",(HEIGHT/2)) + " " +Integer.toString(p.getReady()));
                                osw.flush();
                            } catch (IOException ex) {
                                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                            }*/
                        }
                    }
                });
                
                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                panel.add(k, c);
                c.gridx = 0;
                c.gridy = 7;
                panel.add(txt,c);
                c.gridx = 1;
                panel.add(cnt,c);
                c.gridx = 0;
                c.gridy=12;
                panel.add(rdy,c);
                wait.getContentPane().add(panel);
                //wait.pack();
                //wait.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                wait.setVisible(true);
                
                     
            }
            else if(source==b4){
                System.exit(0);
                    /*try {
                        g.exit();
                    } catch (IOException ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    }*/
            } 
            }
        };
        //b2.addActionListener(a);
        b3.addActionListener(a);
        b4.addActionListener(a);
        mini.setLayout(new GridLayout(2, 1));
        //b2.setSize(10, 10);
        b3.setSize(10, 10);
        b4.setSize(10, 10);
	//mini.add(b2);
	mini.add(b3);
        mini.add(b4);

        mini.setVisible(true);
    }
    
    public static void connect(){
        try{
            if(s.isConnected())
            s.close();
                                            
            HOST = String.valueOf(txt.getText());
            s = new Socket(HOST, PORT);//CONNECT TO THE SERVER
            osw = new OutputStreamWriter(s.getOutputStream(), "UTF-8");
            System.out.println("You connected to " + HOST +":" + Integer.toString(PORT));//IF CONNECTED THEN PRINT IT OUT
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String msg ="";
            while(!msg.contains("Welcome"))
            msg = br.readLine();
            String parts[] = msg.split(" ");
            playerNumber = Integer.valueOf(parts[1]);
            //layers[Integer.valueOf(parts[1])] = new Player();
            //camera=new Camera(players.get(playerNumber), WIDTH/4, HEIGHT/4*3, cols*2*tileWidth, rows*2*tileWidth);
            t = new Receive(s, players, playerNumber);
            exec.submit(t);
            Player p = players.get(playerNumber);
            /*osw.write(String.format("%04d", p.getX())+" "+String.format("%04d", p.getY())+ " " + Integer.toString(p.getReady()));
             osw.flush();*/

                            //playerNumber=ID;

        }catch (Exception noServer)//IF DIDNT CONNECT PRINT THAT THEY DIDNT
        {
            System.out.println("The server might not be up at this time.");
            System.out.println("Please try again later.");
        }
    } 
    
    public static boolean allReady(){
        int i =0;
        for(Player p : players){
            if(p.getCon() && p.getReady() < 1)
                return false;
            if(p.getReady() >0)
                i++;
        }
        if(i<1) return false;
        return true;
    }
    
}
