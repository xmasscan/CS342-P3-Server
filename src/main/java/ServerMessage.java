import java.io.Serializable;
import java.util.ArrayList;

public class ServerMessage implements Serializable {
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
     * @param argv
     */
    ServerMessage(int messageType, ArrayList<String> argv) {
        this.messageType = messageType;
        this.argv = argv;
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
}
