package SSMEngines;

import javax.swing.*;

public class SSMRunner {

    public static final int FPS = 60;   //Frames per second (animation speed)
    SSMClient world;
    JFrame myFrame;

    public SSMRunner() throws Exception
    {
        myFrame = new JFrame();
        world = new SSMClient();
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addFrameComponents();

        startAnimation();
        myFrame.setSize(world.getPreferredSize());
        myFrame.setVisible(true);

    }

    public void addFrameComponents()
    {
        myFrame.setTitle(world.getMyName() + " - " + "Player #");

        myFrame.add(world);
    }

    public void startAnimation()
    {
        javax.swing.Timer t = new javax.swing.Timer(1000/FPS, e -> {
           myFrame.getComponent(0).repaint();
            myFrame.setSize(myFrame.getComponent(0).getPreferredSize());
        });
        t.start();
    }


    public static void main(String[] args) throws Exception
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ex){ ex.printStackTrace(); }

//        Thread player2 = new Thread(() -> {
//            try {
//                new SSMRunner();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        player2.start();
//        Thread player3 = new Thread(() -> {
//            try {
//                new SSMRunner();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        player3.start();
//        Thread player4 = new Thread(() -> {
//            try {
//                new SSMRunner();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        player4.start();

        new SSMRunner();

    }
}
