import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;

/**
 * Write a description of class StartMenu here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class StartMenu extends World
{
    private Visual easy_;
    private Visual normal_;
    private Visual hard_;
    
    /**
     * Constructor for objects of class StartMenu.
     * 
     */
    public StartMenu()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(OthelloWorld.STANDARD_SIDE_LENGTH_ * OthelloWorld.IMAGE_LENGTH_ + OthelloWorld.HORIZONTAL_MARGIN_ * 2 + OthelloWorld.MENU_MARGIN_, OthelloWorld.STANDARD_SIDE_LENGTH_ * OthelloWorld.IMAGE_LENGTH_ + OthelloWorld.VERTICAL_MARGIN_ * 2, 1);
        
        int halfX = getWidth()/2;
        int halfY = getHeight()/2;
        
        addObject( new Visual(new GreenfootImage("Othello", 80, Color.BLACK, null), false), halfX, halfY - 100 );
        
        easy_ = new Visual(new GreenfootImage("4x4", 50, Color.BLACK, null), false );
        normal_ = new Visual(new GreenfootImage("8x8", 50, Color.BLACK, null), false );
        hard_ = new Visual(new GreenfootImage("Russian Winter", 50, Color.BLACK, null), false );
        addObject(easy_, halfX, halfY + 50);
        addObject(normal_, halfX, halfY + 50*2);
        addObject(hard_, halfX, halfY + 50*3);
        addObject(new LoadButton(), halfX, halfY + 50*4);
    }
    
    public void act()
    {
        if( Greenfoot.mouseClicked(easy_) )
        {
            Greenfoot.setWorld(new OthelloWorld(4));
        }
        else if( Greenfoot.mouseClicked(normal_) )
        {
            Greenfoot.setWorld(new OthelloWorld(8));
        }
        else if( Greenfoot.mouseClicked(hard_) )
        {
            Greenfoot.setWorld(new OthelloWorld(20));
        }
    }
}
