import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import javax.swing.JFileChooser;
import java.awt.Color;
import java.io.*;

/**
 * Write a description of class LoadButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LoadButton extends Visual
{
    public LoadButton()
    {
        super( new GreenfootImage("Load", 35, Color.BLACK, null), false );
    }
    
    /**
     * Act - do whatever the LoadButton wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        if( Greenfoot.mouseClicked(this) )
        {
            JFileChooser fileChooser = new JFileChooser();
            if( fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION ) 
            {
                File f = fileChooser.getSelectedFile();
                
                OthelloWorld world = new OthelloWorld();
                world.loadGame(f);
            }
        }
    }    
}
