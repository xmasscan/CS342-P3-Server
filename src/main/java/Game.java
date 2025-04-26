import java.io.IOException;
import java.util.ArrayList;

// Manages players in a game of Connect 4 & the board they are playing on.
public class Game{

    // Save the threads of the players
    Server.ClientThread player1 = null;
    Server.ClientThread player2 = null;
    // Save the server thread to send a termination request
    Server.TheServer server;

    // Game state; If the game has been won and who won
    int winner = -1;
    boolean gameOver = false;

    //hold board information
    Board gameBoard;
    // int representation of the player who is currently moving
    int currentMove;

    /**
     * Game Constructor
     * Called by Server, creates a new game for users to connect to!
     */
    public Game(Server.TheServer server){
        // Initialize new Connect 4 Board
        this.gameBoard = new Board();
        // No moves made yet!
        this.currentMove = 0;
        // Link to server
        this.server = server;
    }
    
    public boolean makeMove(int player, int row) {
        // Player 1 == move when even
        // Player 2 == move when odd
        boolean validMove = false;
        try{
            if(player == 1){
                System.out.println("Player1");
                if(gameBoard.makeMove(player, row)) {
                    validMove = true;
                    player2.out.writeObject(ServerMessage.updateBoard(row));
                }
            }
            else {
                System.out.println("Player2");
                if(gameBoard.makeMove(player, row)) {
                    validMove = true;
                    player1.out.writeObject(ServerMessage.updateBoard(row));
                }
            }
            currentMove++;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        // Attempt to make a move!
        // If the move was successful, check the state of the board!
        if(validMove){
            System.out.println("Player #" + player + ": Valid move!");
            // If the move caused the player to win...
            if(gameBoard.checkBoard(row)){
                gameOver = true;
                // if the player who cast the winning move was player 1, assign them the winner!
                Server.ClientThread winner;
                Server.ClientThread loser;
                if(player == 1){
                    winner = player1;
                    loser = player2;
                }
                // otherwise, the winner must be player 2, then assign them as the winner!
                else{
                    winner = player2;
                    loser = player1;
                }
                // Alert each player if they won or lost!
                try{
                    // Alerts winner that they won.
                    winner.out.writeObject(ServerMessage.endGame(true));
                    // Alerts the loser that they lost.
                    loser.out.writeObject(ServerMessage.endGame(false));
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                // Game is over, end it!
                endGame();
            }
            return true;
        }
        else{
            return false;
        }
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

    public void sendMessage(String username, String message) {
        if(this.player1 != null){
            try{
                player1.out.writeObject(ServerMessage.updateChat(username, message));
            }
            catch(Exception e){
                this.handleDC(player1);
            }
        }
        if(this.player2 != null){
            try{
                player2.out.writeObject(ServerMessage.updateChat(username, message));
            }
            catch(Exception e){
                this.handleDC(player2);
            }
        }
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
                gameOver = true;
                endGame();
            }
            catch (Exception e) {
                // If this fails, other user DC'd, just leave it.
                winner = -1;
                return;
            }
        }

    }

    /**
     * endGame
     * Alerts the server thread when the game is complete.
     * Requests termination
     */
    public void endGame(){
        server.endGame(this);
    }
}
