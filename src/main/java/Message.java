import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;

    int messageType;
    ArrayList<String> arguments;

    /**
     * Server Message
     * Same principles as the client message.
     * @param messageType
     *  IDs the type of message for handling
     *  0 = ACCEPT ; Server was able to parse last message and you can continue
     *  1 = REJECT ; Last message was invalid one way or another.
     *  2 = UPDATE ; Update the board based on the given information! (Row to update)
     * @param argv
     *  Arguments provided in message as strings. Convert as needed as per spec.
     */
    public Message(int messageType, ArrayList<String> argv) {
        this.messageType = messageType;
        this.arguments = argv;
    }

    // NOTE: accept and reject must return empty ArrayLists.
    // Serialization doesn't play well with nulls.
    public static Message accept(){
        int messageType = 0;
        ArrayList<String> argv = new ArrayList<>();
        argv.add("accept");
        return new Message(messageType, argv);
    }

    public static Message reject(){
        int messageType = 1;
        ArrayList<String> argv = new ArrayList<>();
        argv.add("reject");
        return new Message(messageType, argv);    }

    public static Message update(int row){
        int messageType = 2;
        ArrayList<String> argv = new ArrayList<>();
        argv.add(((Integer) row).toString());
        return new Message(messageType, argv);
    }

}
