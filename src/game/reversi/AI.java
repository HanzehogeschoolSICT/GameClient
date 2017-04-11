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

        for (Pair<Point, Integer> pair : options) {
            Point p = pair.getKey();
            double chance = (double)pair.getValue();
            if ((p.x == 0 || p.x == 7) &&
                (p.y == 0 || p.y == 7)) {
                chance += 4.5;
            }

            if ((p.x == 1 || p.x == 6) &&
                (p.y == 1 || p.y == 6)) {
                chance -= 3.1;
            }

            if (((p.x == 1 || p.x == 6) && (p.y == 0 || p.y == 7)) ||
                ((p.y == 1 || p.y == 6) && (p.x == 0 || p.x == 7))) {
                chance -= 2.05;
            }

            n.setSymbol(p.x, p.y, (char)(chance + '0'));
            if (chance > bestChance || bestPoint == null) {
                bestPoint = p;
                bestChance = chance;
            }
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

                        if (board[n.x][n.y] == enemy) {
                            en++;
                            continue;
                        }
                        if (board[n.x][n.y] == whoami && en > 0) {
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
}
