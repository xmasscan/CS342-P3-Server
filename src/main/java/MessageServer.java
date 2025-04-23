import java.io.Serializable;
import java.util.ArrayList;
import java.lang.Math;

public class MessageServer implements Serializable {
    static final long serialVersionUID = 42L;
    
    boolean good;
    Integer messageType;
    String message;

    MessageServer() {
        messageType = new Integer(-1);
        message = new String();
    }

    public void newMove(Integer move){
        message = move.toString();
        messageType = 6;
    }   

    public Integer getMove() {
        return Integer.parseInt(message);
    }

    public void newChat(String player, String chat){
        messageType = new Integer(5);
        good = true;
        message = player + ": " + chat;

    }

    public String getChat() {
        return message;
    }

    public void setUpGame(String GameID, ArrayList<ArrayList<Integer>> board, Integer numColumns, Integer numRows, ArrayList<String> chat, Integer numChats, Integer currentPlayer){
        messageType = new Integer(1);
        good = true;
        message = "1:" + GameID + "," + "2:";
        for (int i = 0; i <numColumns; i++) {
            message += "{";
            for (int j = 0; j < numRows; j++) {
                message += " " + board.get(i).get(j).toString();
            }
            message += "}";
        } 
        message += ",3:" + numColumns.toString() + ",4:" + numRows.toString();

        message += ",5:";


        for (int i = 0; i < numChats; i++) {
            message += " " + chat.get(i);
        }   

        message += " ,6:" + numChats.toString();
        message += " ,7:" + currentPlayer.toString();
    }


    public Integer getCurrentPlayer(){
        if (!messageType.equals(new Integer(1))) {
            return null;
        }
        int gameBegins = message.indexOf("7:", 0) + 2;
        return new Integer(Integer.parseInt(message.substring(gameBegins)));

    }


    public String getGameId(){
        if (!messageType.equals(new Integer(1))) {
            return null;
        }
        int gameBegins = message.indexOf("1:", 0) + 2;
        int gameEnds = message.indexOf(",2:");
         
        return message.substring(gameBegins, gameEnds-1);

    }


    public ArrayList<Integer> getBoardInformation(){
        if (!messageType.equals(new Integer(1))) {
            return null;
        }


        //elo gold visual

        int collomBegins = message.indexOf("3:", 0) + 2;
        int collomEnds = message.indexOf(",4:");
    
        int rowBegins = message.indexOf("5:", 0) + 2;
        int rowEnds = message.indexOf(",6:");
    

        ArrayList<Integer> info = new ArrayList<Integer>();
        info.add(Integer.parseInt(message.substring(collomBegins, collomEnds)));
        info.add(Integer.parseInt(message.substring(rowBegins, rowEnds)));
        
        return info;
    }


    //implement giving player their gold/elo/ and a active client list
    public void signInReturn(Integer elo, Integer gold, Integer visual, ArrayList<String> clients, Integer numClients) {
        good = true;
        messageType = 2;
        message = "1:" + elo.toString() + ",2:" + gold.toString() + ",3:" + visual.toString();

        message += ",4:";

        for (int i = 0; i < numClients; i++) {
            message += "_" + clients.get(i) + "}";
        }

        message += ",5:" + numClients.toString();


    }

    //implement giving replies

    public void reply(String err, boolean recieved) {
        good = recieved;
        messageType = 3;
        message = err;
    }

    public void winStatus(Boolean hasWon) {
        good = true;
        messageType = 4;
        message = hasWon.toString();
    }

    public Boolean Processed() {
        return good;
    }

    public String GetReply() {
        return message;
    }

    public Integer GetMessageType() {
        return messageType;
    }

    public Boolean winCheck() {
        if(!messageType.equals(new Integer(4))){
            return null;
        }

        return Boolean.parseBoolean(message);

    }


    public Integer Type(){
        return messageType;
    }

    public ArrayList<ArrayList<Integer>> getBoard(){
        if(!messageType.equals(new Integer(1))){
            return null;
        }

        int boardBegins = message.indexOf("2:", 0) + 2;
        int boardEnds = message.indexOf(",3:", boardBegins);

        String board = new String(message.substring(boardBegins, boardEnds));

        ArrayList<ArrayList<Integer>> realBoard = new ArrayList<ArrayList<Integer>>();

        while (board.indexOf("}") != -1) {
            String collom = new String(board.substring(board.indexOf("{")+1, board.indexOf("}")+1));
            board = new String(board.substring(board.indexOf("}") + 1));

            ArrayList<Integer> newCollum = new ArrayList<Integer>();
            while(collom.indexOf(" ") != -1) {
                int nextRow =  collom.substring(1).indexOf(" ");
                if (nextRow == -1) {
                    collom = new String("");
                    break;
                }
                String type = new String(collom.substring(1,nextRow));
                collom = collom.substring(nextRow+1);

                newCollum.add(Integer.parseInt(type));
                
            }
            realBoard.add(newCollum);
        }

        return realBoard;

    }

    public ArrayList<Integer> getPlayerInformation(){
        if (!messageType.equals(new Integer(2))) {
            return null;
        }


        //elo gold visual

        int eloBegins = message.indexOf("1:", 0) + 2;
        int eloEnds = message.indexOf(",2:", eloBegins);

        int goldBegins = message.indexOf("2:", 0) + 2;
        int goldEnds = message.indexOf(",3:", eloBegins);
    
        int visualBegins = message.indexOf("3:", 0) + 2;
        int visualEnds = message.indexOf(",4:", eloBegins);
    

        ArrayList<Integer> info = new ArrayList<Integer>();
        info.add(Integer.parseInt(message.substring(eloBegins, eloEnds)));
        info.add(Integer.parseInt(message.substring(goldBegins, goldEnds)));
        info.add(Integer.parseInt(message.substring(visualBegins, visualEnds)));
        
        return info;
    }


    public ArrayList<String> getPlayers(){
        if (!messageType.equals(new Integer(2))) {
            return null;
        }

        

        int chatsBegins = message.indexOf("4:", 0) + 2;
        int chatEnds = message.indexOf(",5:");
        String chats = message.substring(chatsBegins, chatEnds);

        ArrayList<String> clients = new ArrayList<String>();

        while (chats.length() > 0) {
            String chat = message.substring(chats.indexOf("_"),chats.indexOf("}"));
            chats = chats.substring(chats.indexOf("}")+1);
            clients.add(chats);
        }

        return clients;

    }


    public Integer getNumPlayers() {
        if (!messageType.equals(new Integer(2))) {
            return null;
        }


        //elo gold visual

        int numPlayersBegins = message.indexOf(",5:", 0) + 2;

        return new Integer(Integer.parseInt(message.substring(numPlayersBegins)));
        


    }





    




}
