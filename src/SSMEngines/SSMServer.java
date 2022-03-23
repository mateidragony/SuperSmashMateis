package SSMEngines;

import SSMEngines.util.Animator;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SSMServer {

    private ServerSocket ss;
    private int numPlayers;
    private final int maxPlayers;

    List<List<Boolean>> playerMoves;
    Animator animator;

    public SSMServer(){
        System.out.println("==== Game Server ====");
        numPlayers = 0;
        maxPlayers = 1;

        animator = new Animator();

        playerMoves = new ArrayList<>();
        playerMoves.add(new ArrayList<>());
        playerMoves.add(new ArrayList<>());
        playerMoves.add(new ArrayList<>());
        playerMoves.add(new ArrayList<>());

        int port = 80;
        System.out.println("The port is: "+port);
        try{
            ss = new ServerSocket(port);

        } catch(IOException ex){
            ex.printStackTrace();
        }
    }


    public static void runServer(){
        try {
            SSMServer server = new SSMServer();
            server.acceptConnections();
            server.animate();
        } catch(IOException | InterruptedException ex){
            ex.printStackTrace();
            System.exit(666);
        }
    }

    public void animate(){

        while(true){

            animator.animate(playerMoves);

            try{Thread.sleep(2000/SSMRunner.FPS);}catch (InterruptedException ex){ex.printStackTrace();}

        }

    }


    public void acceptConnections() throws IOException, InterruptedException{

        System.out.println("Waiting for connections...");
        while(numPlayers<maxPlayers) {
            Socket s = ss.accept();
            s.setTcpNoDelay(true);
            InputStream inStream = s.getInputStream();
            OutputStream outStream = s.getOutputStream();

//            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(outStream));
//            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(inStream));

            
            ObjectOutputStream out = new ObjectOutputStream(outStream);
            ObjectInputStream in = new ObjectInputStream(inStream);
            out.writeInt(numPlayers);
            System.out.println("Player #" + numPlayers + " has connected");

            ReadFromClient rfc = new ReadFromClient(numPlayers, in);
            WriteToClient wtc = new WriteToClient(numPlayers, out);

            Thread readThread1 = new Thread(rfc);
        //    Thread.sleep(16);
            readThread1.start();

            Thread writeThread1 = new Thread(wtc);
          //  Thread.sleep(16);
            writeThread1.start();

            numPlayers++;
        }

        System.out.println("All Connected!");

    }


    private class ReadFromClient implements Runnable{

        private final int playerID;
        private final ObjectInputStream dataIn;

        public ReadFromClient(int pID, ObjectInputStream in){
            playerID = pID;
            dataIn = in;
        }

        public void unpack(String str, int pID){
            String[] strData = str.split(SSMClient.parseChar);
            List<Boolean> data = new ArrayList<>();

            for(String s:strData){
                data.add(Boolean.parseBoolean(s));
            }

            playerMoves.set(pID,data);
        }

        public void run(){

            while(true){
                try {
                    String str = dataIn.readUTF();
                    unpack(str, playerID);

                   // Thread.sleep(1000/SSMRunner.FPS);
                } catch(IOException ex){
                    ex.printStackTrace();
                    System.exit(666);
                }
            }

        }
    }

    private class WriteToClient implements Runnable{

        private final int playerID;
        private final ObjectOutputStream dataOut;

        public WriteToClient(int pID, ObjectOutputStream in){
            playerID = pID;
            dataOut = in;
        }

        public void run(){

            while(true){
                try {
                    dataOut.writeUTF(animator.pack());
                   dataOut.flush();
                  //  Thread.sleep(1000/SSMRunner.FPS);
                } catch(IOException  ex){
                    ex.printStackTrace();
                    System.exit(666);
                }
            }

        }
    }


//    public static void main(String[] args){
//        runServer();
//    }
//

}
