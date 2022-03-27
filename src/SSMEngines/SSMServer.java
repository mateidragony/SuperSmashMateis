package SSMEngines;

import SSMEngines.util.Animator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SSMServer {

    private ServerSocket ss;
    private int numPlayers;
    private final int maxPlayers;

    Animator animator;

    public SSMServer(){
        System.out.println("==== Game Server ====");
        numPlayers = 0;
        maxPlayers = 1;

        animator = new Animator();

        int port = 80;
        System.out.println("The port is: "+port);
        try{
            ss = new ServerSocket(port);

        } catch(IOException ex){
            ex.printStackTrace();
            System.exit(666);
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

            animator.animate();

            try{Thread.sleep(1000/SSMRunner.FPS);}catch (InterruptedException ex){ex.printStackTrace();}

        }

    }


    public void acceptConnections() throws IOException, InterruptedException{

        System.out.println("Waiting for connections...");
        while(numPlayers<maxPlayers) {
            Socket s = ss.accept();
            s.setTcpNoDelay(true);
            InputStream inStream = s.getInputStream();
            OutputStream outStream = s.getOutputStream();

            ObjectOutputStream out = new ObjectOutputStream(outStream);
            ObjectInputStream in = new ObjectInputStream(inStream);
            out.writeInt(numPlayers);
            System.out.println("Player #" + numPlayers + " has connected");

            ReadFromClient rfc = new ReadFromClient(numPlayers, in);
            WriteToClient wtc = new WriteToClient(numPlayers, out);

            Thread readThread1 = new Thread(rfc);
            readThread1.start();

            Thread writeThread1 = new Thread(wtc);
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

        public void run(){

            while(true){
                try {
                    String str = dataIn.readUTF();
                    animator.unpack(str, playerID);

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
                } catch(IOException ex){
                    ex.printStackTrace();
                    System.exit(666);
                }
            }

        }
    }
}
