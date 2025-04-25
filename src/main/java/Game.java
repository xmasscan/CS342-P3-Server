import java.io.IOException;
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
    
    public boolean makeMove(int player, int row) {
        // Player 1 == move when even
        // Player 2 == move when odd
        if(player == currentMove % 2){
            if(makeMove(player, row)){
                try{
                    player1.out.writeObject(ServerMessage.accept());
                    player2.out.writeObject(ServerMessage.accept());
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                try{
                    currentMove++;
                    player1.out.writeObject(ServerMessage.updateBoard(row));
                    player2.out.writeObject(ServerMessage.updateBoard(row));
                    return true;
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return false;
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
            // This code running = there exists a player1 & player2 => they are both in a match
            // Alert both players they are in a match!
            try{
                player1.out.writeObject(ServerMessage.inMatch(0));
                player2.out.writeObject(ServerMessage.inMatch(1));
            } catch (Exception e) {
                e.printStackTrace();
            }

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

    /**
     * Handles Game Behavior when a player disconnects
     * @param player
     *  The player who disconnected
     */
    public void handleDC(Server.ClientThread player){
        // Determine which player DC'd
        Server.ClientThread other;
        if(this.player1 == player){
            this.player1 = null;
            other = player2;
        }
        else{
            this.player2 = null;
            other = player1;
        }

        // Game is over
        gameOver = true;

        // Check if other player is still connected
        if(other != null){
            // Determine who won
            if(other == player1){
                winner = 0;
            }
            else{
                winner = 1;
            }
            // Attempt to inform winner they won, assuming they still are connected.
            try {
                other.out.writeObject(ServerMessage.endGame(true));
            }
            catch (Exception e) {
                // If this fails, other user DC'd, just leave it.
                winner = -1;
                return;
            }
        }

    }

}
