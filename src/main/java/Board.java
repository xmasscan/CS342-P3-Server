public class Board {
    private class Chip{
        String color;
        int player;

        Chip(String color, int player){
            this.color = color;
            this.player = player;
        }
    }

    Chip player1 = new Chip("Red", 0);
    Chip player2 = new Chip("Yellow", 1);
    // Should never be that many moves to overflow an int.
    // Tracks the count of moves in game & determines whose turn it is
    int moveCount = 0;
    Chip[][] board = new Chip[7][6];

    // Clears the current board in play for a new round.
    public void clearBoard(){
        board = new Chip[7][6];
        moveCount = 0;
    }

    public boolean makeMove(int player, int row){
        // Invalid row handling
        if(row < 0 || row >= 7){
            return false;
        }
        int col = 0;
        // Find lowest free cell in current row.
        while(col < 6 && board[row][col] != null){
            col++;
        }
        // If such a cell is available...
        if (board[row][col] == null){
            // player1 logically goes first
            if(moveCount % 2 == 0){
                board[row][col] = player1;
            }
            else{
                board[row][col] = player2;
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
    public boolean checkBoard(int row){
        // Find top column in row, must be last added piece.
        int col = 0;
        boolean winFlag = false;
        while(col < 6 && board[row][col] != null){
            col++;
        }
        // Either col is at top (violated first case)
        // or [row][col] was null, therefore [row][col - 1] will have the last chip :)
        col--;
        // TODO: Implement Board checking
        // Vertical checking
        if(col > 0){
            int lowVert = col;
            // Find lowest chip in a potential vertical connection
            while(lowVert > 0 && board[row][lowVert-1].color.compareTo(board[row][col].color) == 0){
                lowVert--;
            }
            // 2 + 3 = 5 = max col height
            if(lowVert <= 2){
                winFlag = verticalCheck(row, lowVert);
                if(winFlag){
                    return true;
                }
            }
        }
        // col == 0
        else{
            winFlag = verticalCheck(row, col);
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
