import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;

    int messageType;
    ArrayList<String> arguments;

    public Message(int messageType, ArrayList<String> argv) {
        this.messageType = messageType;
        this.arguments = argv;
    }

    public String handle(){
        if(this.messageType == 3){
            return arguments.get(0);
        }
        else{
            return "Invalid message!";
        }
    }
}
