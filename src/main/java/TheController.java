import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class TheController implements Initializable {

    static TheController aTheController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {aTheController = this;}

    @FXML private VBox chatContent;

    public static void updateChat(String message) {
        // Update GUI
        VBox chatContent = aTheController.chatContent;
        if(chatContent != null) {
            chatContent.getChildren().add(new Text(message));
        }
        else{
            System.out.println("Chat content is null");
        }
    }
}
