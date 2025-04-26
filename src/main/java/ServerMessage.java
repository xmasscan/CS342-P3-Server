import java.io.Serializable;
import java.util.ArrayList;

public class ServerMessage implements Serializable {
    static final long serialVersionUID = 42L;


    int messageType;
    ArrayList<String> argv;

    /**
     * ServerMessage Constructor
     *  A Message, from the server.
     * @param messageType
     *  integer representing the type of message sent by the server.
     *  0 = accept
     *  1 = reject
     *  2 = update board state
     *  3 = update chat
     *  4 - end Game
     *  5 - update Information
     *  6 - in Match
     *  7 - does move
     *  8 - Login
     *  9 - Waiting
     *  10 - Trigger Rematch
     * @param argv
     */
    ServerMessage(int messageType, ArrayList<String> argv) {
        this.messageType = messageType;
        this.argv = argv;
    }

    public static ServerMessage waiting(){
        int messageType = 9;
        ArrayList<String> argv = new ArrayList<>();
        argv.add("OK");
        return new ServerMessage(messageType, argv); 
    }

    /**
     * Accept Message
     * @return
     *  A ServerMessage informing the client their last request was accepted.
     */
    public static ServerMessage accept(){
        int messageType = 0;
        ArrayList<String> argv = new ArrayList<>();
        argv.add("OK");
        return new ServerMessage(messageType, argv);
    }

    public static ServerMessage Login(String username){
        int messageType = 8;
        ArrayList<String> argv = new ArrayList<>();
        argv.add(username);
        return new ServerMessage(messageType, argv);
    }


    public static ServerMessage move(Integer column){
        int messageType = 7;
        ArrayList<String> argv = new ArrayList<String>();
        argv.add(column.toString());
        return new ServerMessage(messageType, argv);
    }
    

    /**
     * Reject Message
     * @return
     *  A ServerMessage informing the client their last request was rejected.
     */
    public static ServerMessage reject(){
        int messageType = 1;
        ArrayList<String> argv = new ArrayList<>();
        argv.add("NO");
        return new ServerMessage(messageType, argv);
    }

    /**
     * Board Update Message
     * @param row
     *  The row the last move was made in.
     * @return
     *  A ServerMessage informing the client to update their Board.
     */
    public static ServerMessage updateBoard(int row){
        int messageType = 2;
        ArrayList<String> argv = new ArrayList<>();
        // Casts int to Integer, then runs toString() on Integer object
        argv.add(((Integer) row).toString());
        return new ServerMessage(messageType, argv);
    }

    /**
     * Chat Update Message
     * @param username
     *  The username of who sent the message
     * @param message
     *  The message the user sent
     * @return
     *  A ServerMessage containing the username and message sent!
     */
    public static ServerMessage updateChat(String username, String message){
        int messageType = 3;
        ArrayList<String> argv = new ArrayList<>();
        argv.add(username);
        argv.add(message);
        return new ServerMessage(messageType, argv);
    }

    /**
     * endGame
     * When the game ends, informs each user if they lost or won.
     * @param winState
     *  Boolean value determining whether the user won.
     * @return
     *  A ServerMessage containing a string informing the user if they won or lost.
     */
    public static ServerMessage endGame(int winState){
        int messageType = 4;
        ArrayList<String> argv = new ArrayList<>();
        if(winState == 1){
            argv.add("Winner");
        }
        else if (winState == 0){
            argv.add("Loser");
        }
        else {
            argv.add("Draw");
        }
        return new ServerMessage(messageType, argv);
    }

   

    public static ServerMessage updateInformation(String username, Integer gold, Integer elo, Integer visual) {
        int messageType = 5;
        ArrayList<String> argv = new ArrayList<>();
        argv.add(username);
        argv.add(gold.toString());
        argv.add(elo.toString());
        argv.add(visual.toString());
        return new ServerMessage(messageType, argv);
    }

    /**
     * inMatch
     * Alerts the user that the current game they are in has been populated and begun.
     * Informs user if they go first or second.
     * @param order
     *  0 = First
     *  1 = Second
     * @return
     *  A ServerMessage informing the user what their turn is.
     */
    public static ServerMessage inMatch(int order){
        int messageType = 6;
        ArrayList<String> argv = new ArrayList<String>();
        // Cast order to an Integer object, then invoke toString on it
        // conversion from int to string
        argv.add(((Integer) order).toString());
        return new ServerMessage(messageType, argv);
    }

    /**
     * rematch
     * Does not INITIATE a Rematch, just redirects client to go to rematch screen.
     * @return
     *  A ServerMessage informing the user to go to the rematch screen.
     */
    public static ServerMessage rematch(){
        int messageType = 10;
        ArrayList<String> argv = new ArrayList<>();
        argv.add("Rematch");
        return new ServerMessage(messageType, argv);
    }
}
