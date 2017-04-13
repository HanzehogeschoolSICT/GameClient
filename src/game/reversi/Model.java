package game.reversi;

/**
 *
 * @author Mark
 */
class Model {
    private char[][] board;

    Model() {
        board = new char[8][8];
        board[3][3] = 'w';
        board[3][4] = 'b';
        board[4][3] = 'b';
        board[4][4] = 'w';
    }

    Model(Model src) {
        board = new char[8][8];
        for (int i = 0; i < 8*8; i++) {
            setSymbol(i/8, i%8, src.getSymbol(i/8, i%8));
        }
    }

    public char getSymbol(int column, int row) {
        return board[column][row];
    }

    void setSymbol(int column, int row, char symbol) {
        board[column][row] = symbol;
    }

    char[][] getBoard() {
        return board;
    }
}
