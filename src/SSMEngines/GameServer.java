package SSMEngines;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author matei
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class GameServer {
    
    private ServerSocket ss;
    private int numPlayers;
    private int maxPlayers;
    
    private boolean readyToPlay;
    private boolean p1ReadyInGame;
    private boolean p2ReadyInGame;
    private boolean p1PlayAgain, p2PlayAgain;
    private boolean p1DC, p2DC;
    
    private int port;
    
    private Socket p1Socket;
    private Socket p2Socket;
    private ReadFromClient p1ReadRunnable;
    private ReadFromClient p2ReadRunnable;
    private WriteToClient p1WriteRunnable;
    private WriteToClient p2WriteRunnable;
    
    private String p1Info,p2Info;
    private String p1GameInfo,p2GameInfo;
    private String bossCode;
    
    private ArrayList<String> p1PList, p2PList;
    
    public GameServer(){
        System.out.println("==== Game Server ====");
        numPlayers = 0;
        maxPlayers=2;
        
        p1Info = "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1";
        p2Info = "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1";
        p1GameInfo = "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1";
        p2GameInfo = "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1";
        
        p1PList = new ArrayList<>();
        p2PList = new ArrayList<>();
        
        port = 80;
        System.out.println("The port is: "+port);
        try{
            ss = new ServerSocket(port);
        } catch(IOException ex){
            System.out.println(ex);
        }
        
        bossCode = JOptionPane.showInputDialog("What is the Boss Code");
    }

    public void acceptConnections(){
        try{
            System.out.println(bossCode);
            System.out.println("Waiting for connections...");
            while(numPlayers<maxPlayers){
                Socket s = ss.accept();
                
                InputStream inStream = s.getInputStream();
                OutputStream outStream = s.getOutputStream();
                
                ObjectOutputStream out = new ObjectOutputStream(outStream);
                ObjectInputStream in = new ObjectInputStream(inStream);
                
                numPlayers++;
                out.writeInt(numPlayers);
                out.writeUTF(bossCode);
                System.out.println("Player #"+numPlayers+ " has connected");
                
                ReadFromClient rfc = new ReadFromClient(numPlayers, in);
                WriteToClient wtc = new WriteToClient(numPlayers, out);
                
                if(numPlayers == 1) {
                    p1Socket = s;
                    p1ReadRunnable = rfc;
                    p1WriteRunnable = wtc;
                    Thread readThread1 = new Thread(p1ReadRunnable);
                    try{
                        Thread.sleep(16);
                    }catch(InterruptedException ex){
                        System.out.println(ex);
                    }
                    readThread1.start();
                    Thread writeThread1 = new Thread(p1WriteRunnable);
                    try{
                        Thread.sleep(16);
                    }catch(InterruptedException ex){
                        System.out.println(ex);
                    }
                    writeThread1.start();
                } else {                    
                    p2Socket = s;
                    p2ReadRunnable = rfc;
                    p2WriteRunnable = wtc;
                    Thread readThread2 = new Thread(p2ReadRunnable);
                    try{
                        Thread.sleep(16);
                    }catch(InterruptedException ex){
                        System.out.println(ex);
                    }
                    readThread2.start();
                    Thread writeThread2 = new Thread(p2WriteRunnable);
                    try{
                        Thread.sleep(16);
                    }catch(InterruptedException ex){
                        System.out.println(ex);
                    }
                    writeThread2.start();
                }
            }
            System.out.println("No longer accepting connections");
            
        }catch(IOException ex){
            System.out.println(ex);
        }
    }
    
    private class ReadFromClient implements Runnable{
        
        private int playerID;
        private ObjectInputStream dataIn;
        
        public ReadFromClient(int pID, ObjectInputStream in){
            playerID = pID;
            dataIn = in;
        }
        
        public void run(){
            try{
                
                while(true){
                    if(playerID == 1){
                        p1Info = dataIn.readUTF();
                        p1GameInfo = dataIn.readUTF();
                        
                        p1PList = (ArrayList<String>)dataIn.readObject();
                        
                        p1ReadyInGame = dataIn.readBoolean();
                        p1PlayAgain = dataIn.readBoolean();
                    }else{
                        p2Info = dataIn.readUTF();
                        p2GameInfo = dataIn.readUTF();
                        
                        p2PList = (ArrayList<String>)dataIn.readObject();
                        
                        p2ReadyInGame = dataIn.readBoolean();
                        p2PlayAgain = dataIn.readBoolean();
                    }
                }
                
            }catch(IOException | ClassNotFoundException ex){
                System.out.println(ex);
                if(playerID == 1)
                    p1DC = true;
                else
                    p2DC = true;
            }
        }
    }
    
        private class WriteToClient implements Runnable{
        
        private int playerID;
        private ObjectOutputStream dataOut;
        
        public WriteToClient(int pID, ObjectOutputStream in){
            playerID = pID;
            dataOut = in;
        }
        
        public void run(){
            try{
                
                while(true){
                    if(playerID == 1){
                        dataOut.writeUTF(p2Info);
                        dataOut.writeUTF(p2GameInfo);
                        
                        dataOut.writeObject(p2PList);
                        
                        dataOut.writeBoolean(p2ReadyInGame);
                        dataOut.writeBoolean(p2PlayAgain);
                        dataOut.writeBoolean(p2DC);
                    } else{
                        dataOut.writeUTF(p1Info);
                        dataOut.writeUTF(p1GameInfo);
                        
                        dataOut.writeObject(p1PList);
                        
                        dataOut.writeBoolean(p1ReadyInGame);
                        dataOut.writeBoolean(p1PlayAgain);
                        dataOut.writeBoolean(p1DC);
                    }      
                    if(numPlayers >= 2)
                        readyToPlay = true;                    
                    
                    dataOut.writeBoolean(readyToPlay);
                    
                    dataOut.flush();
                    
                    try{
                        Thread.sleep(16);
                    }catch(InterruptedException ex){
                        System.out.println(ex);
                    }
                }
                
            }catch(IOException ex){
                System.out.println(ex);
            }
        }        
    }
    
    public void printArray(ArrayList<String> a){
        for(String s: a){
            System.out.print(s + ", ");
        }
        System.out.println("");
    }
        
        
    
    public static void main(String[] args){
        GameServer gs = new GameServer();
        gs.acceptConnections();
    }
}
