public class Board {
    private class Chip{
        String color;
        int player;

        Chip(String color, int player){
            this.color = color;
            this.player = player;
        }
    }

    Chip player1;
    Chip player2;
    // Should never be that many moves to overflow an int.
    // Tracks the count of moves in game & determines whose turn it is
    int moveCount;
    Chip[][] board;

    public Board(){
        // Player/Game Info Init
        this.player1 = new Chip("Red", 0);
        this.player2 = new Chip("Yellow", 1);
        this.moveCount = 0;

        // init new board
        board = new Chip[7][6];
    }

    // Clears the current board in play for a new round.
    public void clearBoard(){
        board = new Chip[7][6];
        moveCount = 0;
    }

    /**
     * printBoard()
     * Prints the current board to the Server Terminal
     * Format:
     *  [ ][ ][ ][ ]...
     *  .
     *  .
     *  .
     */
    public void printBoard(){
        for(int i = 5; i >= 0; i--){
            for(int j = 0; j < 6; j++){
                System.out.print("[");
                if(board[i][j] == null){
                    System.out.print(" ");
                }
                else{
                    // Red = R, Yellow = Y, etc.
                    System.out.print(board[i][j].color.substring(0,1).toUpperCase());
                }
                System.out.print("]");
            }
            System.out.println();
        }
    }

    public boolean makeMove(int player, int column){
        // Invalid row handling
        if(column < 0 || column >= 7){
            return false;
        }
        int row = 0;
        // Find lowest free cell in current row.
        while(row < 7 && board[row][column] != null){
            row++;
        }
        // If such a cell is available...
        if (board[row][column] == null){
            // player1 logically goes first
            if(moveCount % 2 == 0){
                board[row][column] = player1;
            }
            else{
                board[row][column] = player2;
            }
            // successful move
            moveCount++;
            return true;
        }
        // while loop ended bcs col no longer satisfies col < 6.
        // i.e. row is full, invalid move!
        return false;
    }

    // Win states can only be caused by updates to the board.
    // Therefore, do not check the WHOLE board, just use the last piece as place to begin.
    public boolean checkBoard(int collom){
        // Find top column in row, must be last added piece.
        int row = 0;
        boolean winFlag = false;
        while(row < 7 && board[row][collom] != null){
            row++;
        }
        // Either col is at top (violated first case)
        // or [row][col] was null, therefore [row][col - 1] will have the last chip :)
        row--;
        // TODO: Implement Board checking
        // Vertical checking
        if(row > 0){
            int lowVert = collom;
            // Find lowest chip in a potential vertical connection
            while(lowVert > 0 && board[lowVert][collom].color.compareTo(board[lowVert][collom].color) == 0){
                lowVert--;
            }
            // 2 + 3 = 5 = max col height
            if(lowVert <= 2){
                winFlag = verticalCheck(lowVert, collom);
                if(winFlag){
                    return true;
                }
            }
        }
        // col == 0
        else{
            winFlag = verticalCheck(row, collom);
            if(winFlag){
                return true;
            }
        }
        return false;
    }

    // CHECK BOARD HELPER FUNCTIONS //

    // Vertical Checking
    // Supply bottom most-possible piece
    // Assumes at minimum that four cells exist above it
    private boolean verticalCheck(int row, int col){
        Chip player = board[row][col];
        String color = player.color;
        // check 3 above columns
        for(int i = 1; i < 4; i++) {
            if (board[row][col + i].color.compareTo(color) != 0) {
                return false;
            }
        }
        // if code still running, no mismatch found
        return true;
    }
}
