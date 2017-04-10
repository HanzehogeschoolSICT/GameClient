package game.reversi;
import java.util.*;
import java.awt.Point;
import javafx.util.Pair;

public class AI {
    private char whoami;
    private char enemy;
    private Model model;

    public AI(Model model, char whoami) {
        this.model = model;
        this.whoami = whoami;
        if (whoami == 'w') {
            enemy = 'b';
        } else {
            enemy = 'w';
        }
    }

    Point nextMove() {
        List<Pair<Point, Integer>> options = getOptions(model);

        Point bestPoint = null;
        double bestChance = -1;

        Model n = new Model(model);

        for (Pair<Point, Integer> p : options) {
            n.setSymbol(p.getKey().x, p.getKey().y, (char)(p.getValue() + '0'));
            double chance = (double)p.getValue();
            if (chance > bestChance || bestPoint == null) {
                bestPoint = p.getKey();
                bestChance = chance;
            }
        }

        for (char[] c : n.getBoard()) {
            for (char r : c) {
                if (r == '\u0000') r = '.';
                System.out.print(r + " ");
            }
            System.out.println();
        }

        return bestPoint;
    }

    private Point[] dirs = new Point[]{
        new Point(-1, -1), new Point( 0, -1), new Point( 1, -1),
        new Point(-1,  0),                    new Point( 1,  0),
        new Point(-1,  1), new Point( 0,  1), new Point( 1,  1),
    };

    private List<Pair<Point, Integer>> getOptions(Model m) {
        List<Pair<Point, Integer>> options = new ArrayList<Pair<Point, Integer>>();

        char[][] board = m.getBoard();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                if (board[x][y] != '\u0000') {
                    continue;
                }
                boolean valid = false;
                int points = 0;

                for (Point dir : dirs) {
                    int count = 0;
                    int en = 0;
                    while(true) {
                        count++;

                        Point n = new Point(x + (count * dir.x), y + (count * dir.y));
                        if (n.x < 0 || n.y < 0 || n.x >= board.length || n.y >= board.length) {
                            en = 0;
                            count = 0;
                            break;
                        }

                        if (board[n.x][n.y] == enemy) {
                            en++;
                            continue;
                        }
                        if (board[n.x][n.y] == whoami && en > 0) {
                            valid = true;
                            points += en;
                        }
                        en = 0;
                        count = 0;
                        break;
                    }
                }
                if (valid) options.add(new Pair<>(new Point(x, y), new Integer(points)));
            }
        }

        return options;
    }
}
