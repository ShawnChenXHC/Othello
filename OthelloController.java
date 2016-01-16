import java.util.*;
import java.io.*;

/**
 * Write a description of class OthelloLogic here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class OthelloController 
{
    private int[][] board_;
    private int sideLength_;//Accounts for the buffer spaces as well, ie a 8x8 board would have sidelength of 10
    private int currentPlayer_;
    private int difficulty_; //Must be 1 or greater, describes how many moves the ai looks ahead

    private int p1Score_;
    private int p2Score_;

    private int emptySpacesLeft_;
    private int totalMovesPossible_;

    private OthelloInterface screen_;
    private OthelloAI ai_;

    //Final fields
    public final int EMPTY_SPACE_ = 0;
    public final int P1_ = 1;
    public final int P2_ = 2;
    public final int BUFFER_CHIP_ = -1;
    private final String valueSeparator_ = "\t";

    private final int STANDARD_SIDELENGTH_ = 8;//The standard size of an Othello board, with no buffer

    /**
     * Constructs an OthelloController with a standard side length of 8.
     * 
     * @param i An OthelloInterface object through which the OthelloController can control the visuals with.
     */
    public OthelloController(OthelloInterface i)
    {
        sideLength_ = STANDARD_SIDELENGTH_ + 2;//Plus 2 for the buffer zone
        screen_ = i;

        ai_ = null;

        constructBoard();
        initializeValues();
    }

    public OthelloController(OthelloInterface i, int sideLength)
    {
        sideLength_ = sideLength + 2;//Plus 2 for the buffer zone
        screen_ = i;
        constructBoard();
        initializeValues(); 

        ai_ = null;

        constructBoard();
        initializeValues();
    }

    public OthelloController(OthelloInterface i, int sideLength, boolean aiFirst)
    {
        sideLength_ = sideLength + 2;//Plus 2 for the buffer zone
        screen_ = i;
        constructBoard();
        initializeValues(); 
        difficulty_ = 10;

        if( aiFirst )//If the AI is to go first, sets AI's player to 1 and asks it to make a play
        {
            ai_ = new OthelloAI(this, 1, difficulty_);
        }
        else
        {
            ai_ = new OthelloAI(this, 2, difficulty_);
        }
    }

    /**
     * Constructs the initial state of the game board.
     * Initializes the "board" by creating the 2D array "board_"
     */
    public void constructBoard()
    {
        board_ = new int[sideLength_][sideLength_];

        for( int x = 0; x < sideLength_; x++ )//The length of the first dimension, loop for the vertical columns
        {
            if( x == 0 || x == sideLength_ - 1 )//Is first or last column
            {
                for( int y = 0; y < sideLength_; y++ )
                {
                    board_[x][y] = BUFFER_CHIP_;//Fills the entirety of the first and last columns with Buffer spaces
                }
            }
            else//Not the first or last column
            {
                for( int y = 0; y < sideLength_; y++ )
                {
                    if( y == 0 || y == sideLength_ - 1 )
                    {
                        board_[x][y] = BUFFER_CHIP_;//Buffer chip if first or last row
                    }
                    else
                    {
                        board_[x][y] = EMPTY_SPACE_;//Empty space if otherwise
                    }
                }
            }
        }
        //Fills in the starting pieces
        int firstHalf = sideLength_/2 - 1;
        int secondHalf = sideLength_/2;
        board_[firstHalf][firstHalf] = P1_;
        board_[firstHalf][secondHalf] = P2_;
        board_[secondHalf][firstHalf] = P2_;
        board_[secondHalf][secondHalf] = P1_;
    }

    /**
     * Initializes the values of several private fields.
     * 
     * These are the fields initialized:
     * Sets currentPlayer_ to 1
     * Sets p1Score_ to 2
     * Sets p2Score_ to 2
     * Sets movesLeft_ to the difference of the side length squared minus 4
     */
    public void initializeValues()
    {
        currentPlayer_ = 1;
        p1Score_ = 2;
        p2Score_ = 2;
        totalMovesPossible_ = (sideLength_ - 2) * (sideLength_ - 2) - 4;
        emptySpacesLeft_ = totalMovesPossible_;
    }

    public void save(File target)
    {
        try
        {
            target.createNewFile();//Creates a new file, if one doesn't exist already
            PrintWriter writer = new PrintWriter(target);
            //Prints some game info
            writer.println(sideLength_);
            writer.println(currentPlayer_);
            //Printing the state of the board
            for( int y = 0; y < sideLength_; y++ )
            {
                for( int x = 0; x < sideLength_; x++ )
                {
                    writer.print(board_[x][y]);
                    if( x != sideLength_ - 1 )
                    {
                        writer.print(valueSeparator_);
                    }
                }
                if( y != sideLength_ - 1 )//Not yet the last line
                {
                    writer.println();
                }
            }
            writer.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public void load(File target)
    {
        try
        {
            Scanner input = new Scanner(target);
            //Loads in the initial information, resets some values, such as scores, as well
            sideLength_ = input.nextInt();
            currentPlayer_ = input.nextInt();
            board_ = new int[sideLength_][sideLength_];
            totalMovesPossible_ = (sideLength_ - 2) * (sideLength_ - 2) - 4;
            p1Score_ = 0;
            p2Score_ = 0;
            emptySpacesLeft_ = 0;

            input.nextLine();//Extra nextLine method to get past the first two lines
            for( int y = 0; input.hasNextLine(); y++ )
            {
                String[] stringLine = input.nextLine().split(valueSeparator_);
                for( int x = 0; x < stringLine.length; x++ )
                {
                    int value = Integer.parseInt(stringLine[x]);
                    board_[x][y] = value;
                    if( value == 1 )
                    {
                        p1Score_ ++;
                    }
                    else if( value == 2 )
                    {
                        p2Score_ ++;
                    }
                    else if( value == 0 )
                    {
                        emptySpacesLeft_++;
                    }
                }
            }
            input.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * Determines what should happen when the current player makes an attempt to
     * play at a certain space.
     * 
     * Should be called upon by the interface whenever it detects an input from
     * the user.
     * 
     * Will determine if the play being attempted is a legitimate move or not.
     * 
     * If the attempted play is legitimate, method will flip the appropriate
     * pieces and change the current player. If play is illegitimate, nothing happens.
     * 
     * In addition, invokes the "" method every time a 
     * legitimate play is made to see if the game should be ended or if a 
     * forced player change is necessary to keep the game going.
     * 
     * Update: This method will now also invoke the ai to make a move after the player 
     * has finished their move. 
     * 
     * @param x
     * @param y 
     */
    public void playAt(int x, int y)
    {
        if( board_[x][y] == EMPTY_SPACE_ )
        {
            boolean playMade = false;

            int[][] North = findChain(currentPlayer_, x, y, 0, -1);
            int[][] NorthEast = findChain(currentPlayer_, x, y, 1, -1);
            int[][] East = findChain(currentPlayer_, x, y, 1, 0);
            int[][] SouthEast = findChain(currentPlayer_, x, y, 1, 1);
            int[][] South = findChain(currentPlayer_, x, y, 0, 1);
            int[][] SouthWest = findChain(currentPlayer_, x, y, -1, 1);
            int[][] West = findChain(currentPlayer_, x, y, -1, 0);
            int[][] NorthWest = findChain(currentPlayer_, x, y, -1, -1);

            if( North.length != 0 )
            {
                claimChainForCurrentPlayer(North);
                playMade = true;
            }
            if( NorthEast.length != 0 )
            {
                claimChainForCurrentPlayer(NorthEast);
                playMade = true;
            }
            if( East.length != 0 )
            {
                claimChainForCurrentPlayer(East);
                playMade = true;
            }
            if( SouthEast.length != 0 )
            {
                claimChainForCurrentPlayer(SouthEast);
                playMade = true;
            }
            if( South.length != 0 )
            {
                claimChainForCurrentPlayer(South);
                playMade = true;
            }
            if( SouthWest.length != 0 )
            {
                claimChainForCurrentPlayer(SouthWest);
                playMade = true;
            }
            if( West.length != 0 )
            {
                claimChainForCurrentPlayer(West);
                playMade = true;
            }
            if( NorthWest.length != 0 )
            {
                claimChainForCurrentPlayer(NorthWest);
                playMade = true;
            }
            if( playMade )
            {
                claimSpaceForCurrentPlayer(x, y);
                switchPlayers();//The current player has been switched
                emptySpacesLeft_--;
                checkEnd();

                if( ai_ != null && currentPlayer_ == ai_.getPlayerNum() )//AI's turn to move
                {
                    ai_.play();
                }
            }
        }
    }

    public boolean canPlayAt(int player, int x, int y)
    {
        if( board_[x][y] == EMPTY_SPACE_ && nextToOpponent(player, x, y) )
        {
            if( hasChain(player, x, y, 0, -1) ||
                hasChain(player, x, y, 1, -1) ||
                hasChain(player, x, y, 1, 0) ||
                hasChain(player, x, y, 1, 1) ||
                hasChain(player, x, y, 0, 1) ||
                hasChain(player, x, y, -1, 1) ||
                hasChain(player, x, y,  -1, 0) ||
                hasChain(player, x, y, -1, -1) )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public boolean nextToOpponent(int player, int x, int y)
    {
        int opponent = player == 1? 2:1;
        int xP1 = x + 1;
        int xS1 = x - 1;
        int yP1 = y + 1;
        int yS1 = y - 1;
        if( xP1 < sideLength_ )
        {
            if( yP1 < sideLength_ && board_[xP1][yP1] == opponent )
            {
                return true;
            }
            else if( yS1 >= 0 && board_[xP1][yS1] == opponent )
            {
                return true;
            }
            else if( board_[xP1][y] == opponent )
            {
                return true;
            }
        }
        if( xS1 >= 0 )
        {
            if( yP1 < sideLength_ && board_[xS1][yP1] == opponent )
            {
                return true;
            }
            else if( yS1 >= 0 && board_[xS1][yS1] == opponent )
            {
                return true;
            }
            else if( board_[xS1][y] == opponent )
            {
                return true;
            }
        }
        if( yP1 < sideLength_ && board_[x][yP1] == opponent )
        {
            return true;
        }
        if( yS1 >= 0 && board_[x][yS1] == opponent )
        {
            return true;
        }
        return false;
    }

    /**
     * Determines whether or not if the game has ended.
     * 
     * Game will end when either there are no empty spaces left or if neither
     * player can make a legitimate move in the empty spaces remaining.
     * 
     * This method will shift the game into a "critical stage" when the number 
     * of empty spaces left on the board is less than the half of the initial
     * number of empty spaces.
     * 
     * In the critical stage, every time a move has been made, the method will
     * check to see if either player still has legitimate moves remaining.
     * If both players have legitimate moves remaining, nothing is done.
     * If only one player has legitimate moves remaining, it becomes the "currentPlayer_"
     * If no players have legitimate moves remaining, the game is ended.
     */
    public void checkEnd()
    {
        if( emptySpacesLeft_ == 0 )
        {
            int winner = 0;
            if( p1Score_ == p2Score_ )
            {
                screen_.endGame(winner);
            }
            else
            {
                winner = p1Score_ > p2Score_ ? P1_:P2_;
                screen_.endGame(winner);
            }
        }
        else// if( emptySpacesLeft_ <= (totalMovesPossible_)/2 )//Enters the "critical stage"
        {
            int otherPlayer = currentPlayer_ == 1? 2:1;
            if( !canStillPlay(currentPlayer_) )//If the current player has no more moves
            {
                if( canStillPlay(otherPlayer) )//And the other player does
                {
                    switchPlayers();
                    screen_.forcedSwitchPlayers();//Makes the interface change the currently active player
                }
                else//The other player has no moves either
                {
                    int winner = 0;
                    if( p1Score_ == p2Score_ )
                    {
                        screen_.endGame(winner);
                    }
                    else
                    {
                        winner = p1Score_ > p2Score_ ? P1_:P2_;
                        screen_.endGame(winner);
                    }
                }
            }
        }
    }

    /**
     * Determines if the player in question still have any legitimate moves 
     * remaining.
     * 
     * @param player Integer representing the player.
     * @return True if the player still has additional possible moves.
     *         False if the player has no additional moves.
     */
    public boolean canStillPlay(int player)
    {
        for( int x = 0; x < sideLength_; x++ )
        {
            for( int y = 0; y < sideLength_; y++ )
            {
                if( canPlayAt(player, x, y) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds a chain of pieces that may be claimed in a certain direction
     * Below is a guide to the directions:
     * 
     * North: xDif = 0, yDif = -1;
     * NorthEast: xDif = 1, yDif = -1;
     * East: xDif = 1, yDif = 0;
     * SouthEast: xDif = 1, yDif = 1;
     * South: xDif = 0, yDif = 1;
     * SouthWest: xDif = -1, yDif = 1;
     * West: xDif = -1, yDif = 0;
     * NorthWest: xDif = -1, yDif = -1;
     * 
     * @param attacker The integer representing the player attempting to make a play
     * @param x The x coordinate of the starting piece
     * @param y The y coordinate of the starting piece
     * @param xDif The difference in x values in order to go a certain direction
     * @param yDif The difference in y values in order to go a certain direction
     * @return A 2D array containing all of the coordinates of pieces in the chain, if there is one
     *         Array of zero length is returned if no chain is found
     */
    public int[][] findChain(int attacker, int x, int y, int xDif, int yDif)
    {
        int defender = attacker == 1? 2:1;//The player being attacked upon

        int chainSize = 0;//The size of a chain, if there is one
        int n = 1;
        while( board_[x + xDif * n][y + yDif * n] == defender )
        {
            //Attempts to find a chain by looping through and seeing if consecutive chips are defenders
            chainSize++;
            n++;
        }
        int terminalChip = board_[x + xDif * n][y + yDif * n];//The chip that stopped the loop, aka the last chip
        if( terminalChip == attacker && chainSize != 0)//Last chip was the same colour as the attacker and there were defender chips in between them
        {
            //2D array of the coordinates of the defender pieces that make up the chain
            //First dimension decribes how many coordinates in total, second dimensions contains the actual X and Y values
            int[][] chain = new int[chainSize][2];
            for( int m = 1; m <= chainSize; m++ )
            {
                //m - 1 because zero base counting when indexing arrays
                chain[m - 1][0] = x + xDif*m;//X value in the first index
                chain[m - 1][1] = y + yDif*m;//Y value in the second index
            }
            return chain;
        }
        else//No chains to be found
        {
            return new int[0][0];
        }
    }
    
    /**
     * Founds out whether or not if there is a chain for the attacker to
     * claim in a certain direction.
     * Below is a guide to the directions:
     * 
     * North: xDif = 0, yDif = -1;
     * NorthEast: xDif = 1, yDif = -1;
     * East: xDif = 1, yDif = 0;
     * SouthEast: xDif = 1, yDif = 1;
     * South: xDif = 0, yDif = 1;
     * SouthWest: xDif = -1, yDif = 1;
     * West: xDif = -1, yDif = 0;
     * NorthWest: xDif = -1, yDif = -1;
     * 
     * @param attacker The integer representing the player attempting to make a play
     * @param x The x coordinate of the starting piece
     * @param y The y coordinate of the starting piece
     * @param xDif The difference in x values in order to go a certain direction
     * @param yDif The difference in y values in order to go a certain direction
     * @return true if there is a chain, false otherwise
     */
    public boolean hasChain(int attacker, int x, int y, int xDif, int yDif)
    {
        int defender = attacker == 1? 2:1;//The player being attacked upon

        int chainSize = 0;//The size of a chain, if there is one
        int n = 1;
        while( board_[x + xDif * n][y + yDif * n] == defender )
        {
            //Attempts to find a chain by looping through and seeing if consecutive chips are defenders
            chainSize++;
            n++;
        }
        int terminalChip = board_[x + xDif * n][y + yDif * n];//The chip that stopped the loop, aka the last chip
        return terminalChip == attacker && chainSize != 0;//Last chip was the same colour as the attacker and there were defender chips in between them
    }

    /**
     * Changes the pieces along a given chain to the "color" of the current player.
     * 
     * @param chain The x and y values of all of the pieces within the desired chain.
     */
    public void claimChainForCurrentPlayer(int[][] chain)
    {
        for( int n = 0; n < chain.length; n++ )
        {
            claimSpaceForCurrentPlayer(chain[n][0], chain[n][1]);
        }
    }

    /**
     * Changes the "color" of a single piece to that of the current player.
     * 
     * Additionally, will call upon the screen to also make the appropriate
     * changes. For example, if a piece was changed to 1, the screen must also
     * change the color of that particular piece/space to the color representative
     * of 1.
     * 
     * Will also update the score accordingly.
     * 
     * @param x The x coordinate of the space
     * @param y The y coordinate of the space
     */
    public void claimSpaceForCurrentPlayer(int x, int y)
    {
        changeScores(x, y);
        board_[x][y] = currentPlayer_;
        if( currentPlayer_ == P1_ )
        {
            screen_.claimForPlayerOneVisually(x, y);
        }
        else
        {
            screen_.claimForPlayerTwoVisually(x, y);
        }
    }

    /**
     * Updates the score.
     * 
     * In addition, this method will also call upon the interface to update the
     * scores being visually displayed.
     * 
     * This method will assume that the space described by the coordinates
     * given is being claimed by the current player.
     * Thus it will only make decisions based on the status of the said space
     * prior to said claim.
     * For example, if player one was claiming an empty space, this method will
     * add one to player one's score and do nothing else. However, if the space
     * being claimed had belonged to player two, this method will add one to
     * player one's score and take one away from player two's.
     * 
     * That being said, this method should not be called upon by itself, it is
     * meant to only be used as a part of the "claimSpace" method.
     * 
     * @param x The x coordinate of the space.
     * @param y The y coordinate of the space.
     */
    public void changeScores(int x, int y)
    {
        if( currentPlayer_ == P1_ )//Player 1 is in play
        {
            if( board_[x][y] == EMPTY_SPACE_ )
            {
                p1Score_++;
            }
            else
            {
                p1Score_++;
                p2Score_--;
            }
        }
        else//Player 2 is in play
        {
            if( board_[x][y] == EMPTY_SPACE_ )
            {
                p2Score_++;
            }
            else
            {
                p2Score_++;
                p1Score_--;
            }
        }
        screen_.updateScores(p1Score_, p2Score_);
    }

    public void setInterface(OthelloInterface i)
    {
        screen_ = i;
    }

    /**
     * Switches the player currently in play.
     * Switches to player 2 if player 1 is in play and vice versa.
     * 
     * Also calls upon the interface to change the indicator displaying which
     * player is the current player.
     * 
     */
    public void switchPlayers()
    {
        currentPlayer_ = currentPlayer_ == 1? 2:1;//Switches the current player
        screen_.switchPlayers();
    }

    /**
     * Gets the integer representing the player currently in play.
     * 
     * @return Integer representing the current player.
     */
    public int getCurrentPlayer()
    {
        return currentPlayer_;
    }

    /**
     * Gets the 2D array storing the data for the game board.
     * 
     * @return 2D array representing the board.
     */
    public int[][] getBoard()
    {
        return board_;
    }

    /**
     * Gets the score of player 1.
     * 
     * @return Player 1's score.
     */
    public int getP1Score()
    {
        return p1Score_;
    }

    /**
     * Gets the score of player 2.
     * 
     * @return Player 2's score.
     */
    public int getP2Score()
    {
        return p2Score_;
    }

    /**
     * Returns the side length of the board, aka how many sqaces on each side
     * Accounts for the buffer zone as well
     * 
     * @return The board's side length
     */
    public int getSideLength()
    {
        return sideLength_;
    }

    public int getEmptySpacesLeft()
    {
        return emptySpacesLeft_;
    }

    public int getTotalMovesPossible()
    {
        return totalMovesPossible_;
    }

    public OthelloInterface getScreen()
    {
        return screen_;
    }

    public OthelloAI getAI()
    {
        return ai_;
    }

    public int getCurrentPlayerScore()
    {
        if( currentPlayer_ == P1_ )
        {
            return getP1Score();
        }
        else
        {
            return getP2Score();
        }
    }
}
