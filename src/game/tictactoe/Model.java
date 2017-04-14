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

    Model(Model src) {
            board = new char[3][3];
            for (int i = 0; i < 9; i++) {
                    setSymbol(i%3, i/3, src.getSymbol(i%3, i/3));
            }
    }

    public char getSymbol(int row, int column) {
        return board[row][column];
    }

    void setSymbol(int row, int column, char symbol) {
        board[row][column] = symbol;
    }

    public char getWinner() {
        char[][] pos = new char[8][3];
        for (int i = 0; i < 3; i++) {
            pos[i] = board[i];
            pos[3+i] = new char[] { board[0][i], board[1][i], board[2][i] };
        }
        pos[6] = new char[] { board[0][0], board[1][1], board[2][2] };
        pos[7] = new char[] { board[0][2], board[1][1], board[2][0] };

        for (char[] p : pos) {
            if (p[0] == p[1] && p[1] == p[2] && p[0] != ' ' && p[0] != '\u0000') {
                return p[0]; // A winner
            }
        }
        
        // Checking for a draw
        boolean isAllSet = true;
        for (int i = 0; i < 3; i++) {
            for (int y = 0; y < 3; y++){
                if(getSymbol(i,y) != 'x' && getSymbol(i,y) != 'o') // Wat zit er in eerste instantie in?
                    isAllSet = false;
            }
        }
        if(isAllSet)
            return 'd'; // It's a draw, return 'd'
        return ' '; // No winner yet, return ' '
    }

    char[][] getBoard() {
        return board;
    }
}
