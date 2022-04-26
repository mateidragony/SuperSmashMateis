package SSMEngines;

import SSMEngines.util.Animator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class SSMServer {

    private ServerSocket ss;
    private int numPlayers;
    private final int maxPlayers;

    private boolean allConnected;

    Animator animator;

    public SSMServer(int maxPlayers) throws SocketException {
        System.out.println("==== Game Server ====");
        numPlayers = 0;
        this.maxPlayers = maxPlayers;

    //    ss.setReceiveBufferSize(2000);
        //ss.setReceiveBufferSize(2000);
        animator = new Animator(maxPlayers);

        int port = 80;
        System.out.println("The port is: "+port);
        try{
            ss = new ServerSocket(port);

        } catch(IOException ex){
            ex.printStackTrace();
            System.exit(666);
        }
    }


    public static void runServer(int playerNum){
        try {
            SSMServer server = new SSMServer(playerNum);
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

            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(outStream));
            System.out.println("Server oos made");
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(inStream));
            System.out.println("Server ois made");

            out.writeInt(numPlayers);
            System.out.println(numPlayers+1+" player(s) are connected");

            ReadFromClient rfc = new ReadFromClient(numPlayers, in);
            WriteToClient wtc = new WriteToClient(numPlayers, out);

            Thread readThread1 = new Thread(rfc);
            try{
                Thread.sleep(17);
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
            readThread1.start();

            Thread writeThread1 = new Thread(wtc);
            try{
                Thread.sleep(17);
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
            writeThread1.start();

            numPlayers++;
        }

        System.out.println("All Connected!");
        allConnected = true;

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

        private final ObjectOutputStream dataOut;

        public WriteToClient(int pID, ObjectOutputStream in){
            dataOut = in;
        }

        public void run(){

            while(true){
                try {
                    dataOut.writeBoolean(allConnected);
                    dataOut.writeUTF(animator.pack());
                    dataOut.flush();
                    try{
                        Thread.sleep(17);
                    }catch(InterruptedException ex){
                        ex.printStackTrace();
                    }
                } catch(IOException ex){
                    ex.printStackTrace();
                    System.exit(666);
                }
            }

        }
    }
}
