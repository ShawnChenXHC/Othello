import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
import java.io.*;
import java.util.ArrayList;

/**
 * Write a description of class OthelloWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class OthelloWorld extends World implements OthelloInterface
{
    private OthelloController controller_;
    private Space[][] visualBoard_;
    private int[][] board_;
    private Visual hud_;
    
    private GreenfootImage background_;
    private GreenfootImage player1Image_;
    private GreenfootImage player2Image_;
    
    int paintX_;
    int paintY_;
    
    public static final int STANDARD_SIDE_LENGTH_ = 8;
    public static final int IMAGE_LENGTH_ = 55;
    public static final int VERTICAL_MARGIN_ = 20;
    public static final int HORIZONTAL_MARGIN_ = 20;
    public static final int MENU_MARGIN_ = 150;
    
    /**
     * Constructor for objects of class OthelloWorld.
     * 
     */
    public OthelloWorld()
    {
        super(STANDARD_SIDE_LENGTH_ * IMAGE_LENGTH_ + HORIZONTAL_MARGIN_ * 2 + MENU_MARGIN_, STANDARD_SIDE_LENGTH_ * IMAGE_LENGTH_ + VERTICAL_MARGIN_ * 2, 1);
        controller_ = new OthelloController(this);
        board_ = controller_.getBoard();
        visualBoard_ = new Space[board_.length][board_.length];
        
        player1Image_ = new GreenfootImage("whitePiece.gif");
        player2Image_ = new GreenfootImage("blackPiece.gif");
        background_ = new GreenfootImage("background.jpg");
        background_.scale(STANDARD_SIDE_LENGTH_ * IMAGE_LENGTH_ + HORIZONTAL_MARGIN_ * 2 + MENU_MARGIN_, STANDARD_SIDE_LENGTH_ * IMAGE_LENGTH_ + VERTICAL_MARGIN_ * 2);
        
        paintX_ = STANDARD_SIDE_LENGTH_ * IMAGE_LENGTH_ + HORIZONTAL_MARGIN_ * 2;
        paintY_ = 2 * VERTICAL_MARGIN_;
        
        constructMenu();
        constructBoard();
    }
    
    public OthelloWorld(int sideLength)
    {
        super(sideLength * IMAGE_LENGTH_ + HORIZONTAL_MARGIN_ * 2 + MENU_MARGIN_, sideLength * IMAGE_LENGTH_ + VERTICAL_MARGIN_ * 2, 1);
        controller_ = new OthelloController(this, sideLength);
        board_ = controller_.getBoard();
        visualBoard_ = new Space[board_.length][board_.length];
        
        player1Image_ = new GreenfootImage("whitePiece.gif");
        player2Image_ = new GreenfootImage("blackPiece.gif");
        background_ = new GreenfootImage("background.jpg");
        background_.scale(sideLength * IMAGE_LENGTH_ + HORIZONTAL_MARGIN_ * 2 + MENU_MARGIN_, sideLength * IMAGE_LENGTH_ + VERTICAL_MARGIN_ * 2);
        
        paintX_ = sideLength * IMAGE_LENGTH_ + HORIZONTAL_MARGIN_ * 2;
        paintY_ = 2 * VERTICAL_MARGIN_;
        
        constructMenu();
        constructBoard();
    }
    
    public OthelloWorld(int sideLength, boolean aiFirst)
    {
        super(sideLength * IMAGE_LENGTH_ + HORIZONTAL_MARGIN_ * 2 + MENU_MARGIN_, sideLength * IMAGE_LENGTH_ + VERTICAL_MARGIN_ * 2, 1);
        controller_ = new OthelloController(this, sideLength, aiFirst);
        board_ = controller_.getBoard();
        visualBoard_ = new Space[board_.length][board_.length];
        
        player1Image_ = new GreenfootImage("whitePiece.gif");
        player2Image_ = new GreenfootImage("blackPiece.gif");
        background_ = new GreenfootImage("background.jpg");
        background_.scale(sideLength * IMAGE_LENGTH_ + HORIZONTAL_MARGIN_ * 2 + MENU_MARGIN_, sideLength * IMAGE_LENGTH_ + VERTICAL_MARGIN_ * 2);
        
        paintX_ = sideLength * IMAGE_LENGTH_ + HORIZONTAL_MARGIN_ * 2;
        paintY_ = 2 * VERTICAL_MARGIN_;
        
        constructMenu();
        constructBoard();
    }
    
    public OthelloWorld(OthelloController controller, int sideLength)
    {
        super(sideLength * IMAGE_LENGTH_ + HORIZONTAL_MARGIN_ * 2 + MENU_MARGIN_, sideLength * IMAGE_LENGTH_ + VERTICAL_MARGIN_ * 2, 1);
        controller_ = controller;
        board_ = controller_.getBoard();
        visualBoard_ = new Space[board_.length][board_.length];
        
        player1Image_ = new GreenfootImage("whitePiece.gif");
        player2Image_ = new GreenfootImage("blackPiece.gif");
        background_ = new GreenfootImage("background.jpg");
        background_.scale(sideLength * IMAGE_LENGTH_ + HORIZONTAL_MARGIN_ * 2 + MENU_MARGIN_, sideLength * IMAGE_LENGTH_ + VERTICAL_MARGIN_ * 2);
        
        paintX_ = sideLength * IMAGE_LENGTH_ + HORIZONTAL_MARGIN_ * 2;
        paintY_ = 2 * VERTICAL_MARGIN_;
        
        constructMenu();
        constructBoard();
    }
    
    public void constructBoard()
    {
        int xStartLocation = HORIZONTAL_MARGIN_ + IMAGE_LENGTH_/2;
        int yStartLocation = VERTICAL_MARGIN_ + IMAGE_LENGTH_/2;
        
        int xInc = 0;
        for( int x = 1; x < controller_.getSideLength(); x++ )//Starts at one to ignore the buffer zone
        {
            int yInc = 0;
            for( int y = 1; y < controller_.getSideLength(); y++ )
            {
                if( board_[x][y] != controller_.BUFFER_CHIP_ )
                {
                    visualBoard_[x][y] = new Space(x, y);
                    addObject(visualBoard_[x][y], xStartLocation + IMAGE_LENGTH_ * xInc, yStartLocation + IMAGE_LENGTH_ * yInc );
                    if( board_[x][y] == controller_.P1_ )
                    {
                        visualBoard_[x][y].turnWhite();
                    }
                    else if( board_[x][y] == controller_.P2_ )
                    {
                        visualBoard_[x][y].turnBlack();
                    }
                    yInc++;
                }
            }
            xInc++;
        }
         
    }
    
    private void constructMenu()
    {
        hud_ = new Visual(getWidth(), getHeight());
        addObject(hud_, getWidth()/2, getHeight()/2);
        
        //Adds in the HUD in the background, for displaying the current player, scores and such
        hud_.paintImage(background_, 0, 0);
        hud_.paintText("Player", paintX_, paintY_);
        if( controller_.getCurrentPlayer() == controller_.P1_ )
        {
            hud_.paintImage(player1Image_, paintX_, paintY_ + (int)(0.5 * paintY_));
        }
        else
        {
            hud_.paintImage(player2Image_, paintX_, paintY_ + (int)(0.5 * paintY_));
        }
        hud_.paintText("Putin: " + controller_.getP1Score(), paintX_, 4 * paintY_);
        hud_.paintText("Obama: " + controller_.getP2Score(), paintX_, 5 * paintY_);
        
        //Adds in two buttons, save and load, to the bottom of the side menu margin.
        SaveButton s = new SaveButton();
        LoadButton l = new LoadButton();
        //The y coordinats of both buttons, is calculated so that they are exactly one margin and half the image height above the bottom of the screen
        int buttonsY = getHeight() - VERTICAL_MARGIN_ - s.getImage().getHeight()/2;
        //The xGap is the length of half of the button's image, which should be the same for both buttons
        int xGap = s.getImage().getWidth()/2;
        addObject(s, paintX_ + xGap, buttonsY);
        addObject(l, paintX_ + 3 * xGap + 10, buttonsY);
    }
    
    public void updateHud()
    {
        hud_.clear();
        hud_.paintImage(background_, 0, 0);
        hud_.paintText("Player", paintX_, paintY_);
        if( controller_.getCurrentPlayer() == controller_.P1_ )
        {
            hud_.paintImage(player1Image_, paintX_, paintY_ + (int)(0.5 * paintY_));
        }
        else
        {
            hud_.paintImage(player2Image_, paintX_, paintY_ + (int)(0.5 * paintY_));
        }
        hud_.paintText("Putin: " + controller_.getP1Score(), paintX_, 4 * paintY_);
        hud_.paintText("Obama: " + controller_.getP2Score(), paintX_, 5 * paintY_);
    }
    
    public OthelloController getController()
    {
        return controller_;
    }
    
    public void claimForPlayerOneVisually(int x, int y)
    {
        visualBoard_[x][y].turnWhite();
    }
    
    public void claimForPlayerTwoVisually(int x, int y)
    {
        visualBoard_[x][y].turnBlack();
    }
    
    public void switchPlayers()
    {
        updateHud();
    }
    
    public void forcedSwitchPlayers()
    {
        hud_.clear();
        hud_.paintImage(background_, 0, 0);
        hud_.paintText("Player", paintX_, paintY_);
        if( controller_.getCurrentPlayer() == controller_.P1_ )
        {
            hud_.paintImage(player1Image_, paintX_, paintY_ + (int)(0.5 * paintY_));
        }
        else
        {
            hud_.paintImage(player2Image_, paintX_, paintY_ + (int)(0.5 * paintY_));
        }
        hud_.paintText("Putin: " + controller_.getP1Score(), paintX_, 4 * paintY_);
        hud_.paintText("Obama: " + controller_.getP2Score(), paintX_, 5 * paintY_);
        hud_.paintText("Forced Change", paintX_, 6 * paintY_ );
    }
    
    public void updateScores(int p1, int p2)
    {
        updateHud();
    }
    
    public void endGame(int winner)
    {
        if( winner == controller_.P1_ )//Putin wins
        {
            Greenfoot.playSound("ruWin.mp3");
            GreenfootImage text = new GreenfootImage("RUSSIA IS WIN", 40, Color.WHITE, Color.RED);
            Visual winMsg = new Visual(text, true);
            addObject(winMsg, getWidth()/2, getHeight()/2);
        }
        else if( winner == controller_.P2_ )//Obama wins
        {
            Greenfoot.playSound("usWin.mp3");
            GreenfootImage text = new GreenfootImage("AMERICA IS WIN", 40, Color.WHITE, Color.BLUE);
            Visual winMsg = new Visual(text, true);
            addObject(winMsg, getWidth()/2, getHeight()/2);
        }
        else//No one wins
        {
            GreenfootImage text = new GreenfootImage("NONE IS WIN", 40, Color.WHITE, Color.GREEN);
            Visual winMsg = new Visual(text, true);
            addObject(winMsg, getWidth()/2, getHeight()/2);
        }
    }
    
    public void saveGame(File f)
    {
        controller_.save(f);
    }
    
    public void loadGame(File f)
    {
        controller_.load(f);
        
        OthelloWorld newGame = new OthelloWorld(controller_, controller_.getSideLength() - 2);
        controller_.setInterface(newGame);
        Greenfoot.setWorld(newGame);
    }
    
}
