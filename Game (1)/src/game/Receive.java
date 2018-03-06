/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.concurrent.Task;

/**
 *
 * @author Michal
 */
public class Receive extends Task<Void> implements Runnable{
    protected Socket s;
    protected ArrayList<Player> e;
    protected Integer ID;
    protected boolean st;
    protected boolean rd;
    public static OutputStreamWriter osw;
    //protected GamePanel gp;
    public Receive(Socket s, ArrayList<Player> e, Integer ID/*, GamePanel g*/){
        //this.gp = g;
        this.s = s;
        this.e = e;
        this.ID = ID;
        st = false;
    }
 /*
    @Override
    public Void call() throws Exception {
		try //HAVE TO HAVE THIS FOR THE in AND out VARIABLES
		{
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                InputStreamReader isr = new InputStreamReader(s.getInputStream());

                        String msg;
                        String[] hue;
                        String temp;
                        int rec;
                        //msg = br.readLine();
                        //ID = Integer.valueOf(msg);
                        //gp.SetPlayerNum(ID);
                        //System.out.println("ID: " +ID);
                        while(true){
                            msg = br.readLine();
                            System.out.println("Return: " +msg);
                            hue = msg.split(" ");
                            for(int i=0; i < hue.length; i++)System.out.println(hue[i]);
                            //e[Integer.valueOf(hue[0])] = Integer.valueOf(hue[1]);
                                      //for(int o=0; o<players.size();o++){
                /*if(hue[1].equals("20")){e.get(Integer.valueOf(hue[0])).setLeft(true); }
                if(hue[1].equals("21")){e.get(Integer.valueOf(hue[0])).setRight(true);}
                if(hue[1].equals("22")){e.get(Integer.valueOf(hue[0])).setJump(true);}
                if(hue[1].equals("10")){e.get(Integer.valueOf(hue[0])).setLeft(false);}
                if(hue[1].equals("11")){e.get(Integer.valueOf(hue[0])).setRight(false);}
                if(hue[1].equals("12")){e.get(Integer.valueOf(hue[0])).setJump(false);}
                e.get(Integer.valueOf(hue[0])).newPosition(Integer.valueOf(hue[1]),Integer.valueOf(hue[2]));
                //System.out.println(hue);
            //}
                            if(msg.equals("44")) break;
                        }
			s.close();
                        
                        System.out.println("Done.");
                        return null;
                }

		catch (Exception e)
		{
			e.printStackTrace();//MOST LIKELY THERE WONT BE AN ERROR BUT ITS GOOD TO CATCH
                        return null;
		}
               // return null;
	}
*/
 
    @Override
    public void run(){  
		try //HAVE TO HAVE THIS FOR THE in AND out VARIABLES
		{
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                osw = new OutputStreamWriter(s.getOutputStream(), "UTF-8");
                //InputStreamReader isr = new InputStreamReader(s.getInputStream());

                        String msg;
                        String[] hue;
                        int rec;
                        
                       // msg = br.readLine();
                        //ID = Integer.valueOf(msg);
                        //System.out.println("ID: " +ID);
                        
                        Player p = e.get(ID);
                        osw.write(String.format("%04d", p.getX())+" "+String.format("%04d", p.getY())+ " " + Integer.toString(p.getReady()));
                        osw.flush();
                        while(true){
                            
                        osw.write(String.format("%04d", p.getX())+" "+String.format("%04d", p.getY())+ " " + Integer.toString(p.getReady()));
                        osw.flush();    
                        msg = br.readLine();
                        System.out.println("Return: " +msg);
                            
                        hue = msg.split(" ");
                            //for(int i=0; i < hue.length; i++)System.out.println(hue[i]);
                            //e[Integer.valueOf(hue[0])] = Integer.valueOf(hue[1]);
                                      //for(int o=0; o<players.size();o++){
               /* if(hue[1].equals("20")){e.get(//0
                        Integer.valueOf(hue[0])
                ).setLeft(true); }
                if(hue[1].equals("21")){e.get(//0
                        Integer.valueOf(hue[0])
                    ).setRight(true);}
                if(hue[1].equals("22")){e.get(//0
                        Integer.valueOf(hue[0])
                    ).setJump(true);}
                if(hue[1].equals("10")){e.get(//0
                        Integer.valueOf(hue[0])
                    ).setLeft(false);}
                if(hue[1].equals("11")){e.get(//0
                       Integer.valueOf(hue[0])
                    ).setRight(false);}
                if(hue[1].equals("12")){e.get(//0
                        Integer.valueOf(hue[0])
                ).setJump(false);}*/
               
               /*if(msg.contains("Disconnected") && Integer.valueOf(hue[0])== ID){
                       
                       osw.write(String.format("%04d", p.getX())+" "+String.format("%04d", p.getY())+ " " + Integer.toString(p.getReady()));
                       osw.flush();
               }*/
               
               if(!msg.contains("Disconnected")){
                   if(Integer.valueOf(hue[0]) != ID){
                    e.get(Integer.valueOf(hue[0])).newPosition(Integer.valueOf(hue[1]),Integer.valueOf(hue[2]));
                    e.get(Integer.valueOf(hue[0])).setReady(Integer.valueOf(hue[3]));
                   }
                  /* else{
                       osw.write(String.format("%04d", p.getX())+" "+String.format("%04d", p.getY())+ " " + Integer.toString(p.getReady()));
                       osw.flush();
                   }*/
                    
               }
               
               if(msg.contains("Disconnected") && e.get(Integer.valueOf(hue[0])).getCon())
                   e.get(Integer.valueOf(hue[0])).disconnect();
               
               if(!msg.contains("Disconnected") && !e.get(Integer.valueOf(hue[0])).getCon())
                   e.get(Integer.valueOf(hue[0])).connect();
                        if(st == true) break;
                        }
			//s.close();
                        
                        System.out.println("Done.");
                        //return null;
                }

		catch (Exception e)
		{
			e.printStackTrace();//MOST LIKELY THERE WONT BE AN ERROR BUT ITS GOOD TO CATCH
                        //return null;
		}
               // return null;
	}

    @Override
    protected Void call() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
    
  
