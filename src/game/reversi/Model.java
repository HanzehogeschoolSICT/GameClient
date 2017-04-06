package game.reversi;

/**
 *
 * @author Mark
 */
class Model {
    private char[][] board;

    Model() {
        board = new char[8][8];
        board[3][3] = 'b';
        board[3][4] = 'w';
        board[4][3] = 'w';
        board[4][4] = 'b';
    }

    public char getSymbol(int row, int column) {
        return board[row][column];
    }

    void setSymbol(int row, int column, char symbol) {
        board[row][column] = symbol;
    }

    char[][] getBoard() {

        return board;
    }
}