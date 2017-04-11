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

    public Point nextMove() {
        List<Pair<Point, Integer>> options = getOptions(model, whoami, enemy);

        Point bestPoint = null;
        double bestChance = -1;

        for (Pair<Point, Integer> pair : options) {
            Point p = pair.getKey();
            double chance = (double)pair.getValue() + getBias(model, p);

			Model n = new Model(model);
			n.setSymbol(p.x, p.y, whoami);

			// Calculate best enemy move
			Pair<Point, Double> ep = getBestMove(n, p, enemy, whoami);
			if (ep.getKey() != null) {
				chance -= (ep.getValue() * 0.9);

				n.setSymbol(ep.getKey().x, ep.getKey().y, enemy);

				// Calculate best next move
				Pair<Point, Double> np = getBestMove(n, p, whoami, enemy);
				if (ep.getKey() != null) {
					chance += (np.getValue() * 0.6);
				}
			}

            if (chance > bestChance || bestPoint == null) {
                bestPoint = p;
                bestChance = chance;
            }
        }

        return bestPoint;
    }

    private double getBias(Model m, Point p) {
		int xc = ((int)p.x/4)*7;
		int yc = ((int)p.y/4)*7;

		if (m.getBoard()[xc][yc] == '\u0000') {
			if ((p.x == 0 || p.x == 7) &&
            	(p.y == 0 || p.y == 7)) {
            	return 4;
        	}

        	if ((p.x == 1 || p.x == 6) &&
            	(p.y == 1 || p.y == 6)) {
            	return -3.25;
        	}

        	if (((p.x == 1 || p.x == 6) && (p.y == 0 || p.y == 7)) ||
            	((p.y == 1 || p.y == 6) && (p.x == 0 || p.x == 7))) {
            	return -2.125;
        	}

		}

		if (p.x == 0 || p.y == 0 || p.x == 7 || p.y == 7) {
			return 1.1;
		}

        return 0;
    }

    private Point[] dirs = new Point[]{
        new Point(-1, -1), new Point( 0, -1), new Point( 1, -1),
        new Point(-1,  0),                    new Point( 1,  0),
        new Point(-1,  1), new Point( 0,  1), new Point( 1,  1),
    };

    private List<Pair<Point, Integer>> getOptions(Model m, char me, char op) {
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

    private Pair<Point, Double> getBestMove(Model m, Point p, char me, char op) {
        List<Pair<Point,Integer>> enemyOptions = getOptions(m, me, op);

        Double bestChance = 0.0;
		Point bestPoint = null;

        for (Pair<Point, Integer> ep : enemyOptions) {
            double chance = (double)ep.getValue() + (0.70*getBias(m, ep.getKey()));
            if (chance > bestChance || bestPoint == null) {
				bestChance = chance;
				bestPoint = ep.getKey();
			}
        }

        return new Pair<Point, Double>(bestPoint, bestChance);
    }
}
