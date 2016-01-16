/**
 * Write a description of interface OthelloInterface here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public interface OthelloInterface 
{
    public void claimForPlayerOneVisually(int x, int y);
    
    public void claimForPlayerTwoVisually(int x, int y);
    
    public void switchPlayers();
    
    public void forcedSwitchPlayers();
    
    public void updateScores(int p1, int p2);
    
    public void endGame(int winner);
}
