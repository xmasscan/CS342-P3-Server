import java.util.ArrayList;

//contains the information of a current game
public class Game {

    //holds the userid of the game players
    ArrayList<String> users;

    Integer typeOfGame;
    //1 for against another player
    //2 for ai

    Integer  whoWon;
    //states who won

    //hold board information
    Board gameBoard;
    Integer numColloms, numRows;
    String gameID;

    Game(int count){
        users = new ArrayList<String>();
        gameBoard = new Board();
        typeOfGame = 0;
        whoWon = -1;
        numColloms = 6;
        numRows = 7;
        gameID = "" + count;
    }

    ArrayList<ArrayList<Integer>> representBoard(){
        return new ArrayList<ArrayList<Integer>>();
    }

    void updateBoard(){

    }
    
    
    //checks who won
    Integer whoWon(){
        return new Integer(0);
    }
    

    
}
