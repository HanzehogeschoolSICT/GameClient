package game.tictactoe;

/**
 *
 * @author Mark
 */
public class Model {
    char[][] board;

    public Model() {

        board = new char[3][3];
    }

    // public char getSymbol(int row, int column) {
    //    return board[row][column];
    //}

    public void setSymbol(int row, int column, char symbol) {
        board[row][column] = symbol;
    }

    public char[][] getBoard() {

        return board;
    }
}