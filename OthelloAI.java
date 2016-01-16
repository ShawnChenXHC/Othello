import java.util.List;
import java.util.ArrayList;

/**
 * Write a description of class OthelloAI here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class OthelloAI  
{
    private OthelloController controller_;
    private int playerNum_;
    private int depth_;
    
    public OthelloAI(OthelloController oc, int playerNum, int depth)
    {
        controller_ = oc;
        playerNum_ = playerNum;
        depth_ = depth;
    }
    
    public OthelloController getController()
    {
        return controller_;
    }
    
    public int getPlayerNum()
    {
        return playerNum_;
    }
    
    public void play()
    {
        SimPlay logic = new SimPlay(controller_.getBoard(), playerNum_);
        logic.bestCase(playerNum_, depth_);
        controller_.playAt(logic.getX(), logic.getY());
    }
}