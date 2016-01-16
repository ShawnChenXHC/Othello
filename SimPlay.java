/**
 * Write a description of class Play here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SimPlay  
{
    private int x_;
    private int y_;
    private int[][] board_;
    private int currentPlayer_;//Represents the player number who will make the next move with the board in its current state

    /**
     * Constructor for objects of class SimPlay
     */
    public SimPlay(int[][] board, int currentPlayer)
    {
        x_ = -1;
        y_ = -1;
        board_ = board;
        currentPlayer_ = currentPlayer;
    }
    
    /**
     * Returns the best score player can achieve in depth number of rounds should both players play their most optimal moves each round
     * In addition, if this object's currentPlayer can make a move, this function will also store in x_ and y_ the x and y coordinates
     * that the current player must play in order to get that best score.
     */
    public int bestCase(int player, int depth)
    {
        if( depth == 0 )
        {
            return getScore(player);
        }
        else
        {
            boolean playMade = false;
            int bestScore = -1;
            //Starting traversal through the board, seeing if the current player can play at any of the squares
            for( int x = 0; x < board_.length; x++ )
            {
                for( int y = 0; y < board_.length; y++ )
                {
                    if( canAttempt(x, y) )
                    {
                        int[][] copyOfBoard = copyBoard();
                        if( checkPlay(currentPlayer_, copyOfBoard, x, y) )//The board changes when the current player plays at (x, y)
                        {
                            playMade = true;
                            int opponent = currentPlayer_ == 1? 2:1;
                            SimPlay neighbour = new SimPlay(copyOfBoard, opponent);
                            int branchScore = neighbour.bestCase(player, depth--);
                            if( branchScore > bestScore )
                            {
                                bestScore = branchScore;
                                x_ = x;//The x coordinates of this move is stored
                                y_ = y;//The y coordinates of this move is stored
                            }
                        }
                    }
                }
            }
            
            //If at any point in the previous traversal, the current player had a valid move
            if( playMade )
            {
                return bestScore;
            }
            //Otherwise, it is certain that the current player has no valid moves.
            //Therefore, a player switch occurs
            currentPlayer_ = currentPlayer_ == 1? 2:1;
            //After the player switch, the same logic is applied with the other player
            for( int x = 0; x < board_.length; x++ )
            {
                for( int y = 0; y < board_.length; y++ )
                {
                    if( canAttempt(x, y) )
                    {
                        int[][] copyOfBoard = copyBoard();
                        if( checkPlay(currentPlayer_, copyOfBoard, x, y) )//The board changes when the current player plays at (x, y)
                        {
                            playMade = true;
                            int opponent = currentPlayer_ == 1? 2:1;
                            SimPlay neighbour = new SimPlay(copyOfBoard, opponent);
                            int branchScore = neighbour.bestCase(player, depth--);
                            if( branchScore > bestScore )
                            {
                                bestScore = branchScore;
                                //The x and y coordinates are not stored since they are not moves that the original current player will make
                            }
                        }
                    }
                }
            }
            if( playMade )
            {
                return bestScore;
            }
            else//Neither player has any valid moves, game is over at this level
            {
                return getScore(player);
            }
        }
    }

    /**
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
    public void claimFor(int attacker, int[][] board, int x, int y, int xDif, int yDif)
    {
        int defender = attacker == 1? 2:1;//The player being attacked upon

        int n = 1;
        while( board[x + xDif * n][y + yDif * n] == defender )
        {
            board[x + xDif * n][y + yDif * n] = attacker;
            n++;
        }
        board[x][y] = attacker;//Changes the first square to the attacker
    }
    
    private boolean checkPlay(int player, int[][] board, int x, int y)
    {
        boolean boardChanged = false;
        if( hasChain(player, board, x, y, 0, -1) )//North
        {
            claimFor(player, board, x, y, 0, -1);
            boardChanged = true;
        }
        if( hasChain(player, board, x, y, 1, -1) )//NorthEast
        {
            claimFor(player, board, x, y, 1, -1);
            boardChanged = true;
        }
        if( hasChain(player, board, x, y, 1, 0) )//East
        {
            claimFor(player, board, x, y, 1, 0);
            boardChanged = true;
        }
        if( hasChain(player, board, x, y, 1, 1) )//SouthEast
        {
            claimFor(player, board, x, y, 1, 1);
            boardChanged = true;
        }
        if( hasChain(player, board, x, y, 0, 1) )//South
        {
            claimFor(player, board, x, y, 0, 1);
            boardChanged = true;
        }
        if( hasChain(player, board, x, y, -1, 1) )//SouthWest
        {
            claimFor(player, board, x, y, -1, 1);
            boardChanged = true;
        }
        if( hasChain(player, board, x, y, -1, 0) )//West
        {
            claimFor(player, board, x, y, -1, 0);
            boardChanged = true;
        }
        if( hasChain(player, board, x, y, -1, -1) )//NorthWest
        {
            claimFor(player, board, x, y, -1, -1);
            boardChanged = true;
        }
        return boardChanged;
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
    public boolean hasChain(int attacker, int[][] board, int x, int y, int xDif, int yDif)
    {
        int defender = attacker == 1? 2:1;//The player being attacked upon

        int chainSize = 0;//The size of a chain, if there is one
        int n = 1;
        while( board[x + xDif * n][y + yDif * n] == defender )
        {
            //Attempts to find a chain by looping through and seeing if consecutive chips are defenders
            chainSize++;
            n++;
        }
        int terminalChip = board[x + xDif * n][y + yDif * n];//The chip that stopped the loop, aka the last chip
        return terminalChip == attacker && chainSize != 0;//Last chip was the same colour as the attacker and there were defender chips in between them
    }

    /**
     * Determines whether or not if currentPlayer_ can attempt a play at the space (x, y)
     * on board_.
     * 
     * canAttempt returns true if:
     *    - (x, y) is an empty space and
     *    - At least one of the squares surrounding (x, y) is within the bounds 
     *      of the board and belongs to the opponent of the current player
     * canAttempt returns false otherwise
     * 
     * Requires that (x, y) is within the bounds of board_.
     * 
     * @param x The x coordinates of the space
     * @param y The y coordinates of the space
     */
    public boolean canAttempt(int x, int y)
    {
        if( board_[x][y] != 0 )//If the space (x, y) is not an empty space
        {
            return false;
        }
        //All logic from here on assumes (x, y) is an empty space
        int opponent = currentPlayer_ == 1? 2:1;
        int xP1 = x + 1;
        int xS1 = x - 1;
        int yP1 = y + 1;
        int yS1 = y - 1;
        if( xP1 < board_.length )
        {
            if( yP1 < board_.length && board_[xP1][yP1] == opponent )
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
            if( yP1 < board_.length && board_[xS1][yP1] == opponent )
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
        if( yP1 < board_.length && board_[x][yP1] == opponent )
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
     * Returns the total number of squares in this SimPlay's board that is of player's colour.
     * That is to say, returns the total number of elements in SimPlay's board that is equal
     * to player.
     * 
     * @param player The number representing the player that this function is going to find the
     *               the score of.
     */
    public int getScore(int player)
    {
        int result = 0;
        for( int x = 0; x < board_.length; x++ )
        {
            for( int y = 0; y < board_.length; y++ )
            {
                if( board_[x][y] == player )
                {
                    result++;
                }
            }
        }
        return result;
    }
    
    /**
     * Creates a deep copy of this SimPlay's board_
     * 
     * @return A 2D int array representing this SimPlay's board
     */
    public int[][] copyBoard()
    {
        int sideLength = board_.length;
        int[][] copy = new int[sideLength][sideLength];
        for( int x = 0; x < sideLength; x++ )
        {
            for( int y = 0; y < sideLength; y++ )
            {
                copy[x][y] = board_[x][y];
            }
        }
        return copy;
    }
    
    public int getX()
    {
        return x_;
    }
    
    public int getY()
    {
        return y_;
    }
    
    public int[][] getBoard()
    {
        return board_;
    }
    
    public int getCurrentPlayer()
    {
        return currentPlayer_;
    }
}
