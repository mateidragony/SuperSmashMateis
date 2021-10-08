package SSMEngines;


/**
 * Class AnimationPanel
 * Parent class for all AnimationPanels
 * Do not make changes to this class, only extend
 * 
 * @author Travis Rother
 * adapted by Spock 3-2-08, 1-31-21
 */

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;

public abstract class AnimationPanel extends JPanel implements KeyListener, MouseListener,
        MouseMotionListener
{
    
    // Global variables
    // -- do not need to be set in subclasses
    public int frameNumber;
    public int mouseX;
    public int mouseY;
    public String myName;
    
    
    public AnimationPanel(String n, int width, int height)
    {
        frameNumber = 0;
        mouseX = 0;
        mouseY = 0;
        myName = n;
        
        initGraphics();
        initMusic();
        
        this.setPreferredSize(new Dimension(width,height));
        //this.setSize(new Dimension(width,height));
        this.setLocation(80,80);    //move to the right
        this.setVisible (true);         // make it visible to the user
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    public String getMyName() { return myName; }
    
    
    /* Built in init methods */
    // -- init all of your images and sounds from here
    public void initGraphics() {}       
    
    public void initMusic() {}
    // --()-- //
    
///////////////////////////////////////////////////    
    /* Method renderFrame()
     * This is what is repeatedly animated,
     * it paints your graphics to the frame.
     * Don't forget to extend this!
     */
    protected void renderFrame(Graphics g) 
    {
        
    }
///////////////////////////////////////////////////       

    
    

    
     
////// Event Listener Methods //////
    // -- Inherited in all subclasses of MotionPanel
    // -- Extend these however you like!
    
    // KeyListener
    public void keyPressed (KeyEvent e) {}
    public void keyTyped (KeyEvent e) {}
    public void keyReleased (KeyEvent e) {}  
    
    // MouseListener
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}   
    public void mousePressed(MouseEvent e) {}    
    public void mouseReleased(MouseEvent e) {}     
    /**/    
    
    // MouseMotionListener
    // -- mouse position tracked
    // -- you shouldn't have to extend these methods, but if you do,
    // -- remember to call super() to keep mouse tracking functionality
    public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }    
    public void mouseDragged(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }   
    /**/
    
    //MouseWheelListener
    
////// -- end Event Listeners () -- //////
    
    
    // method paintComponent
    // inherited from Class JPanel
    // does the repaint() of your panel
    // --- DO NOT MODifY ---
    public void paintComponent(Graphics g) 
    {
        frameNumber++;
        this.requestFocusInWindow();
        renderFrame(g);
    }
    
    //A utility to load audio clips!
    public Clip loadClip(String fnm)
    {
        AudioInputStream audioInputStream; 
        Clip clip = null;
        try
        {
            audioInputStream =  
                AudioSystem.getAudioInputStream(new File(fnm).getAbsoluteFile());
            // create clip reference 
            clip = AudioSystem.getClip(); 
            clip.open(audioInputStream);
        }
        catch(Exception e)
        {
            System.out.println("Error building audio clip from file="+fnm);
        }
        return clip;
     }
    
} //-- End AnimationPanel() Class
