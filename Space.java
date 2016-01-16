import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class emptySpace here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Space extends Actor
{
    private int x_;
    private int y_;
    
    public Space(int x, int y)
    {
        x_ = x;
        y_ = y;
    }
    
    /**
     * Act - do whatever the emptySpace wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        if( Greenfoot.mouseClicked(this) )
        {
            OthelloWorld world = (OthelloWorld)getWorld();
            world.getController().playAt(x_, y_);
        }
    }
    
    public int getX()
    {
        return x_;
    }
    
    public int getY()
    {
        return y_;
    }
    
    public void turnWhite()
    {
        setImage("whitePiece.gif");
    }
    
    public void turnBlack()
    {
        setImage("blackPiece.gif");
    }
}
