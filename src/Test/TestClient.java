package Test;

import SSMCode.PlayerAttacks.Projectile;
import SSMEngines.old.GameEngine;
import SSMEngines.old.PlayerOld;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.security.Key;

public class TestClient extends JPanel implements KeyListener {

    private String playerData;
    private int playerID;
    private String playerInputs;
    private boolean up,right,left,down;

    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;


    public TestClient(){
        this.setPreferredSize(new Dimension(1100,650));
        //this.setSize(new Dimension(width,height));
        this.setLocation(80,80);    //move to the right
        this.setVisible (true);         // make it visible to the user
        this.setFocusable(true);
        this.addKeyListener(this);

        connectToServer();
    }




    public void paintComponent(Graphics g){
        String[] pData = playerData.split(",");

        if(pData.length > 0) {
            g.setColor(Color.red);

            if(!pData[0].equals(""))
                g.fillRect(Integer.parseInt(pData[0]), Integer.parseInt(pData[1]), 30, 60);
        }

        if(pData.length > 2) {
            g.setColor(Color.blue);
            g.fillRect(Integer.parseInt(pData[2]), Integer.parseInt(pData[3]), 30, 60);
        }
    }


    public static void main(String[] args) {
        JFrame myFrame = new JFrame();
        TestClient world = new TestClient();

        myFrame.add(world);
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        myFrame.setSize(world.getPreferredSize());
        myFrame.setVisible(true);
        myFrame.setResizable(false);
        Timer t = new Timer(1000/60, e->world.repaint());
        t.start();
    }


    public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_W)
            up = true;
        if(e.getKeyCode() == KeyEvent.VK_S)
            down = true;
        if(e.getKeyCode() == KeyEvent.VK_A)
            left = true;
        if(e.getKeyCode() == KeyEvent.VK_D)
            right = true;
    }
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_W)
            up = false;
        if(e.getKeyCode() == KeyEvent.VK_S)
            down = false;
        if(e.getKeyCode() == KeyEvent.VK_A)
            left = false;
        if(e.getKeyCode() == KeyEvent.VK_D)
            right = false;
    }
    public void keyTyped(KeyEvent e){

    }

    private void connectToServer(){
        try{
            String port = "80";

            //Networking variables
            String ipAddress = JOptionPane.showInputDialog("Enter IP");
            Socket socket = new Socket(ipAddress, Integer.parseInt(port));
            socket.setTcpNoDelay(true);
            System.out.println("Connected!");

            InputStream inStream = socket.getInputStream();
            OutputStream outStream = socket.getOutputStream();

            ObjectOutputStream out = new ObjectOutputStream(outStream);
            ObjectInputStream in = new ObjectInputStream(inStream);

            playerID = in.readInt();
            System.out.println("You are player #"+playerID);
            if(playerID == 1)
                System.out.println("Waiting for player 2 to connect...");
            rfsRunnable = new ReadFromServer(in);
            wtsRunnable = new WriteToServer(out);
            rfsRunnable.startThreads();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private class ReadFromServer implements Runnable {

        private final ObjectInputStream dataIn;

        public ReadFromServer(ObjectInputStream in){
            dataIn = in;
        }

        public void run(){
            try{

                while(true){
                    playerData = dataIn.readUTF();
                }

            }catch(IOException ex){
                ex.printStackTrace();
            }
        }

        public void startThreads(){
            Thread readThread = new Thread(rfsRunnable);
            Thread writeThread = new Thread(wtsRunnable);
            readThread.start();
            writeThread.start();
        }
    }
    private class WriteToServer implements Runnable{

        private final ObjectOutputStream dataOut;

        public WriteToServer(ObjectOutputStream out){
            dataOut = out;
        }

        public void run(){
            try{

                while(true){
                    dataOut.writeUTF(up+","+down+","+left+","+right+",");
                    dataOut.flush();
                    try{
                        Thread.sleep(17);
                    }catch(InterruptedException ex){
                        ex.printStackTrace();
                    }
                }

            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }


}
