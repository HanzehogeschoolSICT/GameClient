package game.reversi;

import java.util.*;
import java.awt.Point;
import javafx.util.Pair;


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

	public char getWinner() {
		int b = 0;
		int w = 0;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (getSymbol(x, y) == 'b') b++;
				if (getSymbol(x, y) == 'w') w++;
			}
		}
		if (b > w) {
			return 'b';
		}
		if (w > b) {
			return 'w';
		}
		return '\u0000';
	}

	private static Point[] dirs = new Point[]{
        new Point(-1, -1), new Point( 0, -1), new Point( 1, -1),
        new Point(-1,  0),                    new Point( 1,  0),
        new Point(-1,  1), new Point( 0,  1), new Point( 1,  1),
    };

    public List<Pair<Point, Integer>> legalMoves(char me, char op) {
        List<Pair<Point, Integer>> options = new ArrayList<Pair<Point, Integer>>();

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                if (board[x][y] != '\u0000') {
                    continue;
                }

                int points = 0;
                for (Point dir : dirs) {
                    int count = 0;
                    int en = 0;
                    while(true) {
                        count++;

                        Point n = new Point(x + (count * dir.x), y + (count * dir.y));
                        if (n.x < 0 || n.y < 0 || n.x >= board.length || n.y >= board.length) {
                            break;
                        }

                        if (board[n.x][n.y] == op) {
                            en++;
                            continue;
                        }
                        if (board[n.x][n.y] == me && en > 0) {
                            points += en;
                        }
                        break;
                    }
                }
                if (points > 0) options.add(new Pair<>(new Point(x, y), new Integer(points)));
            }
        }

        return options;
    }

	public void move(int x, int y, char me, char op) {
		if (legal(x, y, me, op, true) == false) {
			System.out.println("This move is not valid");
		}
	}

	private boolean legal(int x, int y, char me, char op, boolean flip) {
        if (board[x][y] != '\u0000') {
            return false;
        }

		char[][] board = new Model(this).getBoard();

        boolean valid = false;
        for (Point dir : dirs) {
            int count = 0;
            int en = 0;
			board = new Model(this).getBoard();

            while(true) {
                count++;

                Point n = new Point(x + (count * dir.x), y + (count * dir.y));
                if (n.x < 0 || n.y < 0 || n.x >= board.length || n.y >= board.length) {
                    break;
                }

                if (board[n.x][n.y] == op) {
					if (flip) {
						board[n.x][n.y] = me;
					}
                    en++;
                    continue;
                }
                if (board[n.x][n.y] == me && en > 0) {
					this.board = board;
					valid = true;
                }
                break;
            }
        }
		if (valid) {
			this.board[x][y] = me;
		}
		return valid;
    }

    char[][] getBoard() {
        return board;
    }
}
