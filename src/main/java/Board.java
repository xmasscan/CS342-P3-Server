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
                if(board[j][i] == null){
                    System.out.print(" ");
                }
                else{
                    // Red = R, Yellow = Y, etc.
                    System.out.print(board[j][i].color.substring(0,1).toUpperCase());
                }
                System.out.print("]");
            }
            System.out.println();
        }
        System.out.println();
    }

    public boolean makeMove(int player, int row){
        // Invalid row handling
        if(row < 0 || row > 6){
            return false;
        }
        int col = 0;
        // Find the lowest free cell in current row.
        while(col < 5 && board[row][col] != null){
            col++;
        }
        // If such a cell is available...
        if (board[row][col] == null){
            // player1 logically goes first
            if(player == 1){
                board[row][col] = player1;
            }
            else if(player == 2){
                board[row][col] = player2;
            }
            // Move has successfully been made!
            moveCount++;
            this.printBoard();
            // check
            return true;
        }
        // while loop ended bcs col no longer satisfies col < 6.
        // i.e. row is full, invalid move!
        return true;

    }


    public boolean checkFull(){
        for(int i = 5; i >= 0; i--){
            for(int j = 0; j < 6; j++){
                if(board[j][i] == null){
                    return false;
                }
            }
        }

        return true;
    }

    // Win states can only be caused by updates to the board.
    // Therefore, do not check the WHOLE board, just use the last piece as place to begin.
    public boolean checkBoard(int row){
        System.out.println("Checking...");
        // Find top column in row, must be last added piece.
        int col = 0;
        boolean winFlag = false;
        while(col < 7 && board[row][col] != null){
            col++;
        }
        // Either col is at top (violated first case)
        // or [row][col] was null, therefore [row][col - 1] will have the last chip :)
        col--;
        // Vertical Check (|)
        if(verticalCheck(row, col)){
            return true;
        }
        // Horizontal Check (-)
        else if(horizontalCheck(row, col)){
            return true;
        }
        // Diagonal Left-to-Right (\)
        else if(diagonalCheckLR(row,col)){
            return true;
        }
        // Diagonal Right-to-Left (/)
        else if(diagonalCheckRL(row,col)){
            return true;
        }
        else {
            return false;
        }
    }

    // CHECK BOARD HELPER FUNCTIONS //

    /**
     * Vertical Connection Check
     * Definition: 4 Chips of the same type must be present, vertically.
     * Example: (Chips represented by "O")
     * O
     * O
     * O
     * O
     * @param row
     *  The row the last updated chip is present in
     * @param col
     *  The column the last updated chip is present in
     * @return
     *  Whether the pattern (vertical connection) is present
     */
    private boolean verticalCheck(int row, int col){
        System.out.println("Vertical Check...");

        String color = board[row][col].color;
        int lowVert = col;
        // Find the lowest chip in a potential vertical connection
        while(lowVert > 0 && board[row][lowVert].color.compareTo(color) == 0){
            lowVert--;
        }
        // 2 + 3 = 5 = max col height
        if(lowVert < 0 || lowVert > 2){
            return false;
        }

        // check 3 above columns
        for(int i = 1; i < 4; i++) {
            if (board[row][lowVert + i] == null || board[row][lowVert + i].color.compareTo(color) != 0) {
                return false;
            }
        }
        // if code still running, no mismatch found
        return true;
    }

    /**
     * Horizontal Check
     * Definition: 4 Chips of the same type must be present in sequence, horizontally
     * Example: (Chips represented by "O"s)
     * O O O O
     * @param row
     *  The row the last updated chip is present in
     * @param col
     *  The column the last updated chip is present in
     * @return
     *  Whether the pattern (horizontal connection) is present
     */
    private boolean horizontalCheck(int row, int col){
        System.out.println("Horizontal Check...");
        // Calculate the leftmost viable chip
        int leftMost = row;
        String color = board[row][col].color;

        // Keep moving left while the "leftMost" chip is within acceptable bounds AND the chip to its left is ALSO the same color.
        while(leftMost > 0 && board[leftMost-1][col] != null && board[leftMost - 1][col].color.compareTo(color) == 0){
            leftMost--;
        }

        // 7 rows == largest infex leftMost can be is leftMost = 3 i.e. the fourth row
        // Example
        // O O O X X X X
        if(leftMost < 0 || leftMost > 3){
            return false;
        }

        // Actual check. Verify there are FOUR chips that come after leftmost of the same color.
        // Also verifies current piece matches pattern, for extra security :)
        for(int i = 0; i < 4; i++) {
            // If board at this index == null (i.e. no move there) OR the color doesn't match, report failure to match.
            if (board[leftMost + i][col] == null || board[leftMost + i][col].color.compareTo(color) != 0) {
                return false;
            }
        }
        // If code is still running, no discrepancy was found, therefore pattern must hold
        return true;
    }

    /**
     * Diagonal Check (LR Variant)
     * Definition: 4 Chips of the same type must be present in sequence, diagonally.
     * This variant checks for a diagonal match wherein the lowest chip is at the left.
     * Example: (Chips represented by "O"s)
     *       O
     *     O
     *   O
     * O
     * @param row
     *  The row the last updated chip is present in
     * @param col
     *  The column the last updated chip is present in
     * @return
     *  Whether the pattern (diagonal LR connection) is present
     */
    private boolean diagonalCheckLR(int row, int col) {
        System.out.println("DiagonalLR Check...");
        // Internal reminder: 7 rows, 6 cols
        int lowestRow = row;
        int lowestCol = col;
        String color = board[row][col].color;

        // Calculate lowest piece in LR diagonal pattern
        // While lowestRow & lowestCol in acceptable bounds...
        while(lowestRow > 0 && lowestCol > 0 && board[lowestRow - 1][lowestCol -1] != null && board[lowestRow - 1][lowestCol - 1].color.compareTo(color) == 0){
            // Broken into two loops for readability
            // While potential next lowest chip matches color of current...
            lowestRow--;
            lowestCol--;
        }

        // Check lowestRow & lowestCol against the maximums we calculated in previous functions
        // lowestRow max = 3 - as per checkHorizontal
        // lowestCol max = 2 - as per checkVertical

        // Check row first
        if(lowestRow < 0 || lowestRow > 3){
            return false;
        }
        // Then check the column position!
        if(lowestCol < 0 || lowestCol > 2){
            return false;
        }

        // Code still running means that there is a viable lowest chip stored in memory.
        // Check the pattern for correctness!
        for(int i = 1; i < 4; i++) {
            // Breaking down larger if statement into multiple for readability.
            // Effectively the same logic as the above checks

            // Null check
            if(board[lowestRow + i][lowestCol + i] == null){
                return false;
            }
            // Similarity check
            else if(board[lowestRow + i][lowestCol + i].color.compareTo(color) != 0){
                return false;
            }
        }
        // If code is still running, pattern must match. Return true!
        return true;
    }

    /**
     * Diagonal Check (RL Variant)
     * Definition: 4 Chips of the same type must be present in sequence, diagonally.
     * This variant checks for a diagonal match wherein the lowest chip is at the right.
     * Example: (Chips represented by "O"s)
     * O
     *   O
     *     O
     *       O
     * @param row
     *  The row the last updated chip is present in
     * @param col
     *  The column the last updated chip is present in
     * @return
     *  Whether the pattern (diagonal RL connection) is present
     */
    private boolean diagonalCheckRL(int row, int col) {
        System.out.println("DiagonalRL Check...");
        // Internal reminder: 7 rows, 6 cols
        int lowestRow = row;
        int lowestCol = col;
        System.out.println("\t" + row + "," + col);
        String color = board[row][col].color;

        // Calculate the lowest piece in LR diagonal pattern
        // While lowestRow & lowestCol in acceptable bounds...
        while(lowestRow < 6 && lowestCol > 0 && board[lowestRow + 1][lowestCol -1] != null && board[lowestRow + 1][lowestCol - 1].color.compareTo(color) == 0){
            // Broken into two loops for readability
            // While potential next lowest chip matches color of current...
            lowestRow++;
            lowestCol--;
        }

        // Check lowestCol against the maximum we calculated in previous function
        // lowestCol max = 2 - as per checkVertical
        // lowestRow goes right and then back left, so it needs 3 rows behind it to check
        // therefore the minimum row it can be in is the 4th at row = 3
        // therefore the previous max is the new minimum :)

        // Check row first
        if(lowestRow < 3 || lowestRow > 6){
            return false;
        }
        // Then check the column position!
        if(lowestCol < 0 || lowestCol > 2){
            return false;
        }

        // Check iterates from bottom right to top left, so row is going physically backwards!

        // Code still running means that there is a viable lowest chip stored in memory.
        // Check the pattern for correctness!
        for(int i = 1; i < 4; i++) {
            // Breaking down larger if statement into multiple for readability.
            // Effectively the same logic as the above checks

            // Null check
            if(board[lowestRow - i][lowestCol + i] == null){
                return false;
            }
            // Similarity check
            else if(board[lowestRow - i][lowestCol + i].color.compareTo(color) != 0){
                return false;
            }
        }
        // If code is still running, pattern must match. Return true!
        return true;
    }
}
