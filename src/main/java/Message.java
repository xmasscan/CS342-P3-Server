import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    // Soft requirement for Serializable Interface
    static final long serialVersionUID = 42L;
    int messageType;
    ArrayList<String> arguments;

    /**
     * Message is the wrapper that stores all the data required to make the message subclasses
     *
     * Attributes:
     * int: messageType
     *  The type of message stored within this object!
     *  0 = SignOn ; Init client as a proper user with a username
     *  1 = Connection ; Attempt to connect to a game based on arguments
     *  2 = Move ; send an attempted move to server
     *  3 = Chat ; send chat message to server
     *  4 = StatusUpdate ; TBD, mostly just ambient/bg data that may be important
     * ArrayList<String> arguments
     *  Everything in these arguments will either be a String, int, or bool.
     *  Given the type, we know the type of each value in advance, so this data can be converted again when Deserialized.
     */
    public Message(int messageType, ArrayList<String> argv){
        this.messageType = messageType;
        this.arguments = argv;
    }

    // Functions are static bcs they don't require an object to operate on
    // That is, they just make objects, they don't mess with em

    /**
     * Sends a SignOn request to the Connect4 Server ; Password Variant
     * @param user
     *  Requested Username
     * @param secret
     *  Optional; Requested Password
     * @return
     *  Message containing the data for a SignOn request.
     */
    public static Message signOn(String user, String secret){
        // Identify this message as a "Sign On" message.
        int messageType = 0;
        // Time to build arguments
        ArrayList<String> argv = new ArrayList<>();
        // Arguments: username and totally unencrypted password bcs we are security masters :)
        argv.add(user);
        argv.add(secret);
        // Return new SignOn request!
        return new Message(messageType, argv);
    }

    /**
     * Sends a SignOn request to the Connect4 Server ; Passwordless Variant
     * @param user
     *  Requested Username
     * @return
     *  Message containing the data for a SignOn request.
     */
    public static Message signOn(String user){
        // Identify this message as a "Sign On" message.
        int messageType = 0;
        // Time to build arguments
        ArrayList<String> argv = new ArrayList<>();
        // Arguments: username
        argv.add(user);
        // Return new SignOn request!
        return new Message(messageType, argv);
    }

    /**
     * Sends a connection request to the Connect4 server.
     * Quick match variant; User is matched to first available game.
     * @return
     *  Quick Match Connection Request Message
     */
    public static Message connect()  {
        int messageType = 1;
        ArrayList<String> argv = new ArrayList<>();
        argv.add("CONNECT");
        return new Message(messageType, argv);
    }

    /**
     * DEPRECATED
     * Sends a connection request to the Connect4 server.
     * Specfic match variant; User attempts to connect to a specfic game based on its ID.
     * @param gameID
     *  The ID of the Game to attempt to connect to.
     * @return
     *  Game Connection Request Message
     */
    public static Message connect(int gameID){
        int messageType = 1;
        ArrayList<String> argv = new ArrayList<>();
        argv.add(Integer.toString(gameID));
        return new Message(messageType, argv);
    }

    /**
     * Sends a move attempt to the Connect4 Server.
     * @param row
     *  The row the user is attempting to drop a piece into.
     * @return
     *  Move attempt Message
     */
    public static Message move(int row){
        int messageType = 2;
        ArrayList<String> argv = new ArrayList<>();
        argv.add(Integer.toString(row));
        return new Message(messageType, argv);
    }

    /**
     * Constructs a "Chat Message" message to send to the server.
     * @param message
     *  The chat message for the user to send to the server.
     */
    public static Message chat(String message){
        // ID Message as a "Chat Message" message
        int messageType = 3;

        // Build arguments; Only need to send chat message!
        ArrayList<String> argv = new ArrayList<>();
        argv.add(message);
        // Create Message Object with desired contents
        return new Message(messageType, argv);
    }

    // TBD, do what you will with this later
    // remember to fittingly update the signature and behavior if u do
    public void updateStatus(int coordinate){

    }

}
