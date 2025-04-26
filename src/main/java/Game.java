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

    /**
     * resetGame
     * Cleaner way to reset the state of the game when a rematch occurs.
     */
    public void resetGame(){
        this.gameBoard.clearBoard();
        this.currentMove = 0;
        this.gameOver = false;
        this.winner = -1;
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
            // If the move caused the player to win...
            if(gameBoard.checkBoard(row)){
                gameOver = true;
                // if the player who cast the winning move was player 1, assign them the winner!
                Server.ClientThread winner;
                Server.ClientThread loser;

                if(player == 1){
                    this.winner = 1;
                    try{
                        this.player1.out.writeObject(ServerMessage.rematch());
                    }
                    catch(Exception e){
                        this.player1.handleDC();
                    }
                    try{
                        if (player1 != null && player2 != null){
                                int hi =  this.winner;
                                double prob1 = 1.0 / (1.0 + Math.pow(10, (( player1.elo -  player2.elo) / 400.0))); 
                                double prob2 = 1.0 / (1.0 + Math.pow(10, (( player2.elo -  player1.elo) / 400.0))); 
                                
                                if (hi == 1) {
                                     player1.elo +=Math.round(50*(1-prob1));
                                     player2.elo +=Math.round(50*(0-prob2));
                                } else if (hi == 2) {
                                     player2.elo += Math.round(50*(1-prob2));
                                     player1.elo += Math.round(50*(0-prob1));
                                } else {
                                     player2.elo += Math.round(50*(0.5-prob2));
                                     player1.elo += Math.round(50*(0.5-prob1));
                                }
                
                                try {
                                     player1.out.writeObject(ServerMessage.updateInformation( player1.username, 0,  player1.elo, 0));
                                     player2.out.writeObject(ServerMessage.updateInformation( player2.username, 0,  player2.elo, 0));
                                
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }
                            }
                        this.player2.out.writeObject(ServerMessage.rematch());
                    }
                    catch(Exception e){
                        this.player2.handleDC();
                    }
                }
                // otherwise, the winner must be player 2, then assign them as the winner!
                else{
                    this.winner = 2;

                    if (player1 != null && player2 != null){
                        int hi =  this.winner;
                        double prob1 = 1.0 / (1.0 + Math.pow(10, (( player1.elo -  player2.elo) / 400.0))); 
                        double prob2 = 1.0 / (1.0 + Math.pow(10, (( player2.elo -  player1.elo) / 400.0))); 
                        
                        if (hi == 1) {
                             player1.elo +=Math.round(50*(1-prob1));
                             player2.elo +=Math.round(50*(0-prob2));
                        } else if (hi == 2) {
                             player2.elo += Math.round(50*(1-prob2));
                             player1.elo += Math.round(50*(0-prob1));
                        } else {
                             player2.elo += Math.round(50*(0.5-prob2));
                             player1.elo += Math.round(50*(0.5-prob1));
                        }
        
                        try {
                             player1.out.writeObject(ServerMessage.updateInformation( player1.username, 0,  player1.elo, 0));
                             player2.out.writeObject(ServerMessage.updateInformation( player2.username, 0,  player2.elo, 0));
                        
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }

                    try{
                        this.player1.out.writeObject(ServerMessage.rematch());
                    }
                    catch(Exception e){
                        this.player1.handleDC();
                    }
                    try{
                        this.player2.out.writeObject(ServerMessage.rematch());
                    }
                    catch(Exception e){
                        this.player2.handleDC();
                    }
                }
                // Alert each player if they won or lost!
            }
            else if (gameBoard.checkFull()) {
                try {
                    gameOver = true;
                    this.winner = -1;
                    if (player1 != null && player2 != null){
                        int hi =  this.winner;
                        double prob1 = 1.0 / (1.0 + Math.pow(10, (( player1.elo -  player2.elo) / 400.0))); 
                        double prob2 = 1.0 / (1.0 + Math.pow(10, (( player2.elo -  player1.elo) / 400.0))); 
                        
                        if (hi == 1) {
                             player1.elo +=Math.round(50*(1-prob1));
                             player2.elo +=Math.round(50*(0-prob2));
                        } else if (hi == 2) {
                             player2.elo += Math.round(50*(1-prob2));
                             player1.elo += Math.round(50*(0-prob1));
                        } else {
                             player2.elo += Math.round(50*(0.5-prob2));
                             player1.elo += Math.round(50*(0.5-prob1));
                        }
        
                        try {
                             player1.out.writeObject(ServerMessage.updateInformation( player1.username, 0,  player1.elo, 0));
                             player2.out.writeObject(ServerMessage.updateInformation( player2.username, 0,  player2.elo, 0));
                        
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                    try{
                        this.player1.out.writeObject(ServerMessage.rematch());
                    }
                    catch(Exception e){
                        this.player1.handleDC();
                    }
                    try{
                        this.player2.out.writeObject(ServerMessage.rematch());
                    }
                    catch(Exception e){
                        this.player2.handleDC();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
                
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
        // Reset rematchState when user joins a new game!
        player.rematchState = -1;
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
                e.printStackTrace();
                this.handleDC(player1);
            }
        }
        if(this.player2 != null){
            try{
                player2.out.writeObject(ServerMessage.updateChat(username, message));
            }
            catch(Exception e){
                e.printStackTrace();
                this.handleDC(player2);
            }
        }
    }


    /**
     * handleRematch
     * Update appropriate ClientThread.matched value based on acceptRematch response.
     * After value has been updated, boot user back to waiting where controller will handle players thusly.
     * @param player
     *  Server.ClientThread - the ClientThread which send the message
     * @param accepted
     *  boolean - Whether the client accepted the rematch or not.
     */
    public void handleRematch(Server.ClientThread player, boolean accepted){
        // Update ClientThread value based on acceptance of rematch
        if(accepted){
            player.rematchState = 1;
        }
        else{
            player.rematchState = 0;
            // Alert other player of rejection if they would be waiting;
            if(this.player1 != null && this.player1.rematchState == 1){
                int winState = -1;
                if(winner == 1){
                    winState = 1;
                }
                else if (winner == 2){
                    winState = 0;
                }
                try{
                    this.player1.out.writeObject(ServerMessage.endGame(winState));
                }
                catch(Exception e){
                    this.player1.handleDC();
                }
                // Now let player 2 leave too!
                if(winState == 1){
                    winState = 0;
                }
                else if(winState == 0){
                    winState = 1;
                }
                try {
                    this.player2.out.writeObject(ServerMessage.endGame(winState));
                }
                catch(Exception e){
                    this.player2.handleDC();
                }
            }
            // Player 2 != player case
            if(this.player2 != null && this.player2.rematchState == 1){
                int winState = -1;
                if(winner == 2){
                    winState = 1;
                }
                else if (winner == 1){
                    winState = 0;
                }
                try{
                    this.player2.out.writeObject(ServerMessage.endGame(winState));
                }
                catch(Exception e){
                    this.player2.handleDC();
                }
                // Let Player 1 Free!
                if(winState == 1){
                    winState = 0;
                }
                else if(winState == 0){
                    winState = 1;
                }
                try {
                    this.player1.out.writeObject(ServerMessage.endGame(winState));
                }
                catch(Exception e){
                    this.player1.handleDC();
                }
            }
            // Initial Rejection case
            // When the first message back is a rejection of the rematch
            else{
                // alert player1
                if(this.player1 != null){
                    int winState = -1;
                    if(winner == 1){
                        winState = 1;
                    }
                    else if (winner == 2){
                        winState = 0;
                    }
                    try {
                        this.player1.out.writeObject(ServerMessage.endGame(winState));
                    }
                    catch(Exception e){
                        this.player1.handleDC();
                    }
                    // End the game.
                    this.endGame();
                }
                // alert player 2
                if(this.player2 != null){
                    int winState = -1;
                    if(winner == 2){
                        winState = 1;
                    }
                    else if (winner == 1){
                        winState = 0;
                    }
                    try{
                        this.player2.out.writeObject(ServerMessage.endGame(winState));
                    }
                    catch(Exception e){
                        this.player2.handleDC();
                    }
                }
            }
            return;
        }

        // Code still running = player wanted a rematch!

        // If either player has already disconnected, no rematch possible.
        if(this.player1 == null || this.player2 == null){
            // Tell the other player that the game is over.
            // This can only be received after the game is over already, so just send the final state.
            int winState = -1;
            if(winner == 1){
                if(this.player1 == player)
                    winState = 1;
                else
                    winState = 0;
            }
            else if (winner == 2){
                if(this.player2 == player)
                    winState = 1;
                else
                    winState = 0;
            }
            try{
                player.out.writeObject(ServerMessage.endGame(winState));
            }
            catch (Exception e){
                // if player has disconnected mid-transfer, disconnect them
                player.handleDC();
            }
        }
        // Rejection Case
        // Both players are still connected to the server, but the other player rejected
        else if(this.player1.rematchState == 0 || this.player2.rematchState == 0){
            // If other player has rejected, they won't be waiting for a response.
            // Send the endGame() message so their state is updated.
            try{
                int winState = -1;
                if(winner == 1){
                    winState = 1;
                }
                else if (winner == 2){
                    winState = 0;
                }
                player.out.writeObject(ServerMessage.endGame(winState));
            }
            catch (Exception e){
                player.handleDC();
            }
        }
        // Indeterminate Case; Other player still has not decided.
        else if(this.player1.rematchState == -1 || this.player2.rematchState == -1){
            // If a player has initiated a rematch and the other one hasn't responded yet
            // They should be waiting on the waiting screen!
            try{
                player.out.writeObject(ServerMessage.waiting());
            }
            catch (Exception e){
                player.handleDC();
            }
        }
        // Accept State
        // Both players are still connected AND nobody has rejected the rematch.
        else{
            // We only know the players are still connected as of the if statement.
            // Assume the player has dc'd if an exception is thrown.
            try{
                this.player1.out.writeObject(ServerMessage.inMatch(0));
            }
            catch (Exception e){
                this.player1.handleDC();
                int winState = -1;
                if(winner == 2){
                    winState = 1;
                }
                else if (winner == 1){
                    winState = 0;
                }
                try{
                    // Update Player 2 with state if they are still connected & waiting
                    this.player2.out.writeObject(ServerMessage.endGame(winState));
                }
                catch (Exception e2){
                    // If Player 2 also dc'd, handle it.
                    this.player2.handleDC();
                }
            }
            // Now attempt to reconnect Player 2 to this game!
            try{
                this.player2.out.writeObject(ServerMessage.inMatch(1));
            }
            catch (Exception e){
                this.player2.handleDC();
                int winState = -1;
                if(winner == 1){
                    winState = 1;
                }
                else if (winner == 2){
                    winState = 0;
                }
                try{
                    this.player1.out.writeObject(ServerMessage.endGame(winState));
                }
                catch (Exception e2){
                    this.player1.handleDC();
                }
            }

            // Code still running == Both players accepted the rematch!
            // Reset state of board.
            if(this.player1 != null){
                this.player1.rematchState = -1;
            }
            if(this.player2 != null){
                this.player2.rematchState = -1;
            }
            this.resetGame();
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
        Server.ClientThread me;
        if(this.player1 == player){
            me = player1;
            this.player1 = null;
            other = player2;
            
        }
        else{
            me = player1;
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
                other.out.writeObject(ServerMessage.endGame(1));
                gameOver = true;
                endGame();
            }
            catch (Exception e) {
                other.handleDC();
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
