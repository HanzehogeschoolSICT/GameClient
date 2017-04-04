package game.tictactoe;

/**
 *
 * @author Mark
 */
class Model {
    private char[][] board;

    Model() {

        board = new char[3][3];
    }

    // public char getSymbol(int row, int column) {
    //    return board[row][column];
    //}

    void setSymbol(int row, int column, char symbol) {
        board[row][column] = symbol;
    }

    char[][] getBoard() {

        return board;
    }
}