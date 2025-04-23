import java.util.ArrayList;

// Manages players in a game of Connect 4 & the board they are playing on.
public class Game{

    // Save the threads of the players
    Server.ClientThread player1 = null;
    Server.ClientThread player2 = null;

    // Game state; If the game has been won and who won
    int winner = -1;
    boolean gameOver = false;

    //hold board information
    Board gameBoard;
    int currentMove;

    /**
     * Game Constructor
     * Called by Server, creates a new game for users to connect to!
     */
    public Game(){
        // Initialize new Connect 4 Board
        this.gameBoard = new Board();
        // No moves made yet!
        this.currentMove = 0;
    }

    /**
     * Attempt to connect a user to this game of Connect 4!
     * @param player
     *  The ClientThread to allow to participate
     * @return
     *  whether the connection was successful
     */
    public boolean connect(Server.ClientThread player){
        // If the first player slot is open, set this player as the first player.
        if(this.player1 == null){
            this.player1 = player;
            return true;
        }
        // If the second player slot is open, set this player as the first player.
        else if(this.player2 == null){
            this.player2 = player;
            return true;
        }
        // Game is full! You can't connect!
        else{
            return false;
        }
    }

    /**
     * isFull
     * @return
     *  True if the current game is full, false if it is not
     */
    public boolean isFull(){
        // If either slot is empty, the game is not full, otherwise it must be
        return(!(this.player1 == null || this.player2 == null));
    }

    /**
     * getTurn
     * Figures out which player's turn it is
     * @return
     *  The ClientThread coorelating to the player whose turn it currently is.
     */
    public Server.ClientThread getTurn(){
        if(currentMove % 2 == 0){
            return player1;
        }
        return player2;
    }

}
