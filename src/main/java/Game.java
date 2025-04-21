import java.util.ArrayList;

//contains the information of a current game
public class Game {

    //holds the userid of the game players
    ArrayList<String> users;

    Integer typeOfGame;
    Integer  whoWon;

    //hold board information
    ArrayList<ArrayList<Integer>> Board;
    Integer numColloms, numRows;
    String gameID;

    Game(){
        
    }

    void updateBoard(){

    }
    
    
    //checks who won
    Integer whoWon(){
        return new Integer(0);
    }
    

    
}
