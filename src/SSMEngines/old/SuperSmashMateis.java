package SSMEngines.old;


/**
 * Class ArcadeRunner
 * Runs and animates subclasses of MotionPanel
 * 
 * @author Travis Rother 
 * @version 2-25-2008
*/

import java.awt.event.*;
import javax.swing.*;


public class SuperSmashMateis
{

    int FPS = 60;   //Frames per second (animation speed)
    GameEngine world = new GameEngine();
    
    //==============================================================================
    //--- Typically you will never need to edit any of the code below this line. ---
    //==============================================================================
    
    JFrame myFrame;

    public SuperSmashMateis() 
    {
        myFrame = new JFrame();
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addFrameComponents();
        
        if(world.getPlayerID() == 0)
            FPS = 60;
        startAnimation();
        myFrame.setSize(world.getPreferredSize());
        
        
        if(world.getPlayerID() == 1)
            myFrame.setLocation(0,100);
        else
            myFrame.setLocation(275,0);
        //myFrame.setUndecorated(true);
        myFrame.setVisible(true);
    }

    public void addFrameComponents() 
    {
        myFrame.setTitle(world.getMyName() + " - " + "Player #" + world.getPlayerID());
        
        myFrame.add(world);
    }
    
    public void startAnimation() 
    {
        javax.swing.Timer t = new javax.swing.Timer(1000/FPS, new ActionListener() 
        {   //This is something you may not have seen before...
            //We are coding a method within the ActionListener object during it's construction!
            public void actionPerformed(ActionEvent e) 
            {
                try {
                    myFrame.getComponent(0).repaint();
                    myFrame.setSize(myFrame.getComponent(0).getPreferredSize());
                } catch (NullPointerException ex){

                }
            }
        }); //--end of construction of Timer--
        t.start();
    }
    
    public static void main(String[] args) 
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ex){ ex.printStackTrace(); }

        new SuperSmashMateis();
    }    
    
    
    private static class Closer extends java.awt.event.WindowAdapter 
    {   
        public void windowClosing (java.awt.event.WindowEvent e) 
        {   System.exit (0);
        }   //======================
    }      
    
    
}
