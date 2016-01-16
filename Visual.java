import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
import java.awt.Font;
/**
 * Write a description of class Text here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Visual extends Actor
{
    private GreenfootImage display_;
    private int size_;
    private Color wordColour_;
    private Color backColour_;
    
    private final int DEFAULT_SIZE_ = 36;
    
    private boolean animate_;
    private boolean empty_;
    private int flashCount_;
    
    private final GreenfootImage CLEAR_IMAGE_ = new GreenfootImage(1, 1);
    private final int FLASH_DELAY_ = 40;
    
    /**
     * Used only for the underlying HUD of the Othello World.
     * Not meant to be used by anything else.
     * So please don't use it for anything else.
     */
    public Visual(int x, int y)
    {
        display_ = new GreenfootImage(x, y);
        display_.setFont(new Font("Times New Roman", Font.PLAIN, 30));
        setImage(display_);
    }
    
    /**
     * Creates a visual object with a given GreenfootImage.
     * Can choose whether or not if you want it to flash.
     */
    public Visual(GreenfootImage img, boolean animate)
    {
        display_ = img;
        animate_ = animate;
        setImage(display_);
        empty_ = false;
    }
    
    public void act()
    {
        if( animate_ )
        {
            if( flashCount_ % FLASH_DELAY_ == 0 )
            {
                if( !empty_ )
                {
                    setImage(CLEAR_IMAGE_);
                    empty_ = true;
                }
                else if( empty_ )
                {
                    setImage(display_);
                    empty_ = false;
                }
            }
            flashCount_ ++;
        }
    }
    
    public void paintText(String text, int x, int y)
    {
        display_.drawString(text, x, y);
    }
    
    public void paintImage(GreenfootImage img, int x, int y)
    {
        display_.drawImage(img, x, y);
    }
    
    public void clear()
    {
        getImage().clear();
    }
    
    public GreenfootImage getDisplay()
    {
        return display_;
    }
}
