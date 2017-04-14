package game.tictactoe;
import java.util.*;
import java.awt.Point;

public class AI {
    private char whoami;
    private char enemy;
    private Model model;

    public AI(Model model, char whoami) {
        this.model = model;
        this.whoami = whoami;
        if (whoami == 'x') {
            enemy = 'o';
        } else {
            enemy = 'x';
        }
    }

    public Point nextMove() {
        List<Point> options = getOptions(model);

        Point bestPoint = null;
        double bestChance = -1;

        for (Point p : options) {
            double chance = calcMoveChance(model, p);
            if (chance == 100.0) {
                System.out.println("Found winning move! x: " + p.x + ", y: " + p.y);
                return p;
            }
            if (chance > bestChance || bestPoint == null) {
                bestPoint = p;
                bestChance = chance;
            }
        }

        if (bestPoint != null) {
            return bestPoint;
        }
        return null;
    }

    private List<Point> getOptions(Model m) {
        List<Point> options = new ArrayList<Point>();

        char[][] board = m.getBoard();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                if (board[x][y] == '\u0000') {
                    options.add(new Point(x, y));
                }
            }
        }

        return options;
    }

    private double calcMoveChance(Model m, Point p) {
        Model next = new Model(m);
        next.setSymbol(p.x, p.y, whoami);
        if (next.getWinner() == whoami) {
            return 100.0;
        }

        List<Point> enemyOptions = getOptions(next);

        double totalChance = 0.0;
        int total = 0;

        for (Point ep : enemyOptions) {
            Model after = new Model(next);
            after.setSymbol(ep.x, ep.y, enemy);
            if (after.getWinner() == enemy) {
                totalChance = -1000;
                total = 1;
                break;
            }

            List<Point> ownOptions = getOptions(after);
            for (Point op : ownOptions) {
                totalChance += calcMoveChance(after, op);
                total++;
            }
        }

        if (total == 0) {
            return 0.0;
        } else {
            return totalChance/total;
        }
    }
}
