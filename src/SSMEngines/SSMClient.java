package SSMEngines;

import SSMEngines.util.Drawer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import javax.swing.*;

public class SSMClient extends AnimationPanel{

    private final Drawer drawer;


    public SSMClient() {
        super("Super Smash Mateis", 1100,650);

        drawer = new Drawer(1100,650);

        setUpLauncher();
        if(playerMode > 1)
            connectToServer();
    }




    public void renderFrame(Graphics g){
        drawer.draw(g);
    }








    //------------------------------------------------------------
    //Networking
    //------------------------------------------------------------

    private int playerMode;
    private int playerID;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    private String ipAddress;

    private void connectToServer(){
        try{
            String port = "80";
            Socket socket = new Socket(ipAddress, Integer.parseInt(port));
            System.out.println("Connected!");

            InputStream inStream = socket.getInputStream();
            OutputStream outStream = socket.getOutputStream();

            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(outStream));
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(inStream));

            playerID = in.readInt();
            System.out.println("You are player #"+playerID);
            if(playerID == 1)
                System.out.println("Waiting for other player(s) to connect...");
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

            while(true){
                try {
                  //  Thread.sleep(1000/SSMRunner.FPS);
                    drawer.unpack(dataIn.readUTF());
                } catch(IOException e){
                    e.printStackTrace();
                }
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

            while(true){
                try {
                    dataOut.writeUTF(packCommands());
                    dataOut.flush();
                  //  Thread.sleep(1000/SSMRunner.FPS);
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }


    public static final String parseChar = ",";

    public String packCommands(){
        String str = "";
        str += up + parseChar;
        str += down + parseChar;
        str += left + parseChar;
        str += right + parseChar;
        str += j + parseChar;
        str += k + parseChar;
        str += l + parseChar;
        str += p + parseChar;
        str += enter + parseChar;

        return str;
    }

    //------------------------------------------------------------
    //Launcher
    //------------------------------------------------------------
    public void setUpLauncher(){
        SSMLauncher launcher = new SSMLauncher();

        JFrame myFrame = new JFrame("SSM Launcher");
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setVisible(true);
        myFrame.add(launcher);
        myFrame.setResizable(false);
        myFrame.setSize(launcher.getPreferredSize());
        myFrame.setLocation(300,50);

        launcher.idkWhyIHaveToDoThisButThisIsKindaDum();

        while(!launcher.shouldLaunch()){
            myFrame.getComponent(0).repaint();
            launcher.setFrameLocations(myFrame);

            try{Thread.sleep(16);}catch(InterruptedException ex){ex.printStackTrace();}
        }

        myFrame.setVisible(false);
        launcher.noVisible();

        playerMode = launcher.getPlayerMode();
        myName = launcher.getPlayerName();
        ipAddress =  launcher.getIP();
    }


    //------------------------------------------------------------
    //Respond to Keyboard Events
    //------------------------------------------------------------


    private boolean up,down,left,right;
    private boolean j,k,l,p,enter;

    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
    }
    public void keyPressed(KeyEvent e) {
        int v = e.getKeyCode();

        if(v==KeyEvent.VK_J)
            j=true;
        if(v==KeyEvent.VK_K)
            k=true;
        if(v==KeyEvent.VK_L)
            l=true;
        if(v==KeyEvent.VK_W || v==KeyEvent.VK_UP)
            up=true;
        if(v==KeyEvent.VK_A || v==KeyEvent.VK_LEFT)
            left=true;
        if(v==KeyEvent.VK_S || v==KeyEvent.VK_DOWN)
            down=true;
        if(v==KeyEvent.VK_D || v==KeyEvent.VK_RIGHT)
            right=true;
        if(v==KeyEvent.VK_ENTER)
            enter=true;
        if(v==KeyEvent.VK_P)
            p=true;
    }
    public void keyReleased(KeyEvent e) {
        int v = e.getKeyCode();

        if(v==KeyEvent.VK_J)
            j=false;
        if(v==KeyEvent.VK_K)
            k=false;
        if(v==KeyEvent.VK_L)
            l=false;
        if(v==KeyEvent.VK_W || v==KeyEvent.VK_UP)
            up=false;
        if(v==KeyEvent.VK_A || v==KeyEvent.VK_LEFT)
            left=false;
        if(v==KeyEvent.VK_S || v==KeyEvent.VK_DOWN)
            down=false;
        if(v==KeyEvent.VK_D || v==KeyEvent.VK_RIGHT)
            right=false;
        if(v==KeyEvent.VK_ENTER)
            enter=false;
        if(v==KeyEvent.VK_P)
            p=false;
    }


}
