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

    Model(Model src) {
        board = new char[8][8];
        for (int i = 0; i < 8*8; i++) {
            setSymbol(i%8, i/8, src.getSymbol(i%8, i/8));
        }
    }

    public char getSymbol(int row, int column) {
        return board[row][column];
    }

    void setSymbol(int row, int column, char symbol) {
        board[row][column] = symbol;
        System.out.println("Added on " + row + "," + column);
    }

    char[][] getBoard() {

        return board;
    }
}
