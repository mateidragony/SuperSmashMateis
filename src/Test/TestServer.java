package Test;

import java.io.*;
import java.net.*;
import javax.swing.Timer;

public class TestServer {

    private ServerSocket ss;
    private int numPlayers;
    private final int maxPlayers;

    private String playerData = "";
    private int p1x,p1y,p2x,p2y;

    private String player1Inputs = "";
    private String player2Inputs = "";

    public TestServer(){
        System.out.println("==== Game Server ====");
        numPlayers = 0;
        this.maxPlayers = 2;

        //ss.setReceiveBufferSize(2000);
        //ss.setReceiveBufferSize(2000);

        int port = 80;
        System.out.println("The port is: "+port);
        try{
            ss = new ServerSocket(port);

        } catch(IOException ex){
            ex.printStackTrace();
            System.exit(666);
        }
    }

    public void acceptConnections() throws IOException{
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

            numPlayers++;
            ReadFromClient rfc = new ReadFromClient(in,numPlayers);
            WriteToClient wtc = new WriteToClient(out,numPlayers);

            Thread readThread1 = new Thread(rfc);
            try{
                Thread.sleep(16);
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
            readThread1.start();

            Thread writeThread1 = new Thread(wtc);
            try{
                Thread.sleep(16);
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
            writeThread1.start();
        }
        System.out.println("All Connected!");
    }


    public void animate(){
        String[] p1data = player1Inputs.split(",");
        String[] p2data = player2Inputs.split(",");

        if(p1data.length == 4) {
            if (Boolean.parseBoolean(p1data[0]))
                p1y-=5;
            if (Boolean.parseBoolean(p1data[1]))
                p1y+=5;
            if (Boolean.parseBoolean(p1data[2]))
                p1x-=5;
            if (Boolean.parseBoolean(p1data[3]))
                p1x+=5;
        }

        if(p2data.length == 4) {
            if (Boolean.parseBoolean(p2data[0]))
                p2y-=5;
            if (Boolean.parseBoolean(p2data[1]))
                p2y+=5;
            if (Boolean.parseBoolean(p2data[2]))
                p2x-=5;
            if (Boolean.parseBoolean(p2data[3]))
                p2x+=5;
        }

        playerData = p1x+","+p1y+","+p2x+","+p2y+",";
    }



    private class ReadFromClient implements Runnable {

        private final ObjectInputStream dataIn;
        private final int playerID;

        public ReadFromClient(ObjectInputStream in, int pID){
            dataIn = in;
            playerID = pID;
        }

        public void run(){
            while(true){
                try {
                    if(playerID == 1) {
                        player1Inputs = dataIn.readUTF();
                        //System.out.println("Player Inputs: " + player1Inputs);
                    } else
                        player2Inputs = dataIn.readUTF();
                } catch(IOException ex){
                    ex.printStackTrace();
                    System.exit(666);
                }
            }
        }
    }

    private class WriteToClient implements Runnable {

        private final ObjectOutputStream dataOut;
        private final int playerID;

        public WriteToClient(ObjectOutputStream out, int pID){
            dataOut = out;
            playerID = pID;
        }

        public void run(){
            while(true){
                try {
                    dataOut.writeUTF(playerData);
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

    public static void main(String[] args) throws IOException{

        TestServer ts = new TestServer();
        ts.acceptConnections();
        Timer t = new Timer(1000/60, e -> ts.animate());
        t.start();
    }

}
