package SSMEngines;

import SSMCode.Player;
import SSMCode.PlayerAttacks.*;
import SSMEngines.util.Animator;
import SSMEngines.util.Drawer;
import SSMEngines.util.MapHandler;
import SSMEngines.util.Poolkit;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.*;

public class SSMClient extends AnimationPanel{

    private final Drawer drawer;
    private final int width = AnimationPanel.width;
    private final int height = AnimationPanel.height;

    private int screenNumber;
    private boolean allConnected;
    private String connecting;

    public SSMClient() {
        super("Super Smash Mateis");

        setUpLauncher();
        connectToServer();

        drawer = new Drawer(playerID,playerMode);
        connecting = "to Connect ";

        screenNumber = Animator.INTRO_SCREEN;
    }


    public void renderFrame(Graphics g){

        if(screenNumber == Animator.INTRO_SCREEN){
            g.setColor(Color.black);
            g.fillRect(0,0,width,height);
            g.drawImage(introScreen, 0,0,width,height,this);
            //if you press enter, move on to the next screen
            if(enter) {
                screenNumber++;
                enter = false;
            }
        }else if(screenNumber == Animator.LOADING_SCREEN){
            //draws background and sets up writing color/font
            g.setColor(Color.black);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.white);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 60));

            //if not all players are connected, write waiting for players
            if(!allConnected){
                g.drawString("Waiting for Player(s)", 70, 150);

                if(frameNumber%30==0)
                    connecting+=".";
                if(frameNumber%120==0)
                    connecting = "to Connect";

                g.drawString(connecting, 200, 250);
            }
            else {
                g.drawString("All Players Have Connected", 70, 150);
                g.drawString("Press Enter to Start", 70, 250);

                if(enter)
                    screenNumber++;
            }
        }else{
            drawer.draw(g,this, new Point(mouseX,mouseY));
        }
    }


    //------------------------------------------------------------
    //Networking
    //------------------------------------------------------------

    private int playerMode;
    private int playerID;
    private String playerName;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    private String ipAddress;

    private void connectToServer(){
        try{
            String port = "80";
            Socket socket = new Socket(ipAddress, Integer.parseInt(port));
            System.out.println("Connected!");
            socket.setTcpNoDelay(true);

            InputStream inStream = socket.getInputStream();
            OutputStream outStream = socket.getOutputStream();
            
            ObjectOutputStream out = new ObjectOutputStream(outStream);
            ObjectInputStream in = new ObjectInputStream(inStream);

            playerID = in.readInt();
            System.out.println("You are player #"+playerID);

            if(playerID != playerMode-1)
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
                    allConnected = dataIn.readBoolean();
                    String str = dataIn.readUTF();
                    if(drawer != null)
                        drawer.unpack(str);
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
                    dataOut.writeUTF(packInfo());
                    dataOut.flush();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }


    /*
    SSMClient Parse Char = "," (Separates each individual primitive data type)
    Animator Parse Char = ";" (Separates the game data and each individual players' data)
    Player Parse Char = "/" (Separates player data and each attack/attack array)
    Projectile Parse Char = "&" (Separates each attack in an array of attacks)
     */

    public static final String parseChar = ",";

    public String packInfo(){
        String str = "";
        str += up + parseChar; //0
        str += down + parseChar; //1
        str += left + parseChar; //2
        str += right + parseChar; //3
        str += j + parseChar; //4
        str += k + parseChar; //5
        str += l + parseChar; //6
        str += p + parseChar; //7
        str += enter + parseChar; //8
        str += mouseX + parseChar; //9
        str += mouseY + parseChar; //10
        str += mousePressed + parseChar; //11

        str += playerName + parseChar; //12

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


        while(!launcher.shouldLaunch()){
            myFrame.getComponent(0).repaint();
            launcher.setComponentLocations();

            try{Thread.sleep(16);}catch(InterruptedException ex){ex.printStackTrace();}
        }

        playerMode = launcher.getPlayerMode();
        playerName = launcher.getPlayerName().equals("Enter Player Name") ? "UmerMain123" : launcher.getPlayerName();
        ipAddress =  launcher.getIP();

        if(launcher.hostServer()){
            Thread server = new Thread(() -> SSMServer.runServer(playerMode));
            server.start();
        }

        for(int i=0; i<125; i++) {
            try {
                myFrame.getComponent(0).repaint();
                launcher.setComponentLocations();
                Thread.sleep(16);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        myFrame.setVisible(false);
    }

    //------------------------------------------------------------
    //Respond to Keyboard Events
    //------------------------------------------------------------

    private boolean up,down,left,right;
    private boolean j,k,l,p,enter,mousePressed;

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

    public void mousePressed(MouseEvent e){
        mousePressed = true;
    }
    public void mouseReleased(MouseEvent e){
        mousePressed = false;
    }



    //------------------------------------------------------------
    //Respond to Keyboard Events
    //------------------------------------------------------------

    private Image introScreen;

    public void initGraphics(){
        Poolkit poolkit = new Poolkit();

        Player.initImages();
        Drawer.initImages(poolkit);
        Rocket.initImage();
        Lightning.initImages();
        GrowingLAttack.initImage();
        Motorcycle.initImages();
        Stick.initImages();
        Projectile.initImages();
        Boomerang.initImages(poolkit);
        RainingCode.initImage(poolkit);

        try {
            introScreen = ImageIO.read(new File("SSMImages/introScreen.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
