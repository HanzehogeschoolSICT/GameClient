package game.reversi;
import java.util.*;
import java.awt.Point;
import javafx.util.Pair;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AI {
    private char whoami;
    private char enemy;
    private Model model;

	public char getWhoami() {
		return whoami;
	}

	public char getEnemy() {
		return enemy;
	}

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
        List<Pair<Point, Integer>> options = model.legalMoves(whoami, enemy);

		ExecutorService ex = Executors.newFixedThreadPool(options.size());

        for (Pair<Point, Integer> pair : options) {
            ex.execute(new AIWorker(model, pair, this));
        }
		ex.shutdown();
		try {
			ex.awaitTermination(8, TimeUnit.SECONDS);
		} catch (Exception e) {}

        return bestPoint;
    }

	private Point  bestPoint = null;
	private double bestChance = 0.0;

	protected synchronized void checkBestMove(Point p, double c) {
		if (c > bestChance || bestPoint == null || (bestPoint.x == p.x && bestPoint.y == p.y)) {
			System.out.println("Found better point " + p.x + "," + p.y + ": " + c);
			bestPoint = p;
			bestChance = c;
		}
	}

    private static double getBias(Model m, Point p) {
        int xc = ((int)p.x/4)*7;
        int yc = ((int)p.y/4)*7;

        if (m.getBoard()[xc][yc] == '\u0000') {
            if ((p.x == 0 || p.x == 7) &&
                (p.y == 0 || p.y == 7)) {
                return 10;
            }

            if ((p.x == 1 || p.x == 6) &&
                (p.y == 1 || p.y == 6)) {
                return -8;
            }

            if (((p.x == 1 || p.x == 6) && (p.y == 0 || p.y == 7)) ||
                ((p.y == 1 || p.y == 6) && (p.x == 0 || p.x == 7))) {
                return -6;
            }
        }

        if (p.x == 0 || p.y == 0 || p.x == 7 || p.y == 7) {
            return 3;
        }

        return 0;
    }

    protected static Pair<Point, Double> getBestMove(Model m, char me, char op) {
        List<Pair<Point,Integer>> enemyOptions = m.legalMoves(me, op);

        Double bestChance = 0.0;
        Point bestPoint = null;

        for (Pair<Point, Integer> ep : enemyOptions) {
            double chance = (double)ep.getValue() + getBias(m, ep.getKey());
            if (chance > bestChance || bestPoint == null) {
                bestChance = chance;
                bestPoint = ep.getKey();
            }
        }

        return new Pair<Point, Double>(bestPoint, bestChance);
    }
}

class AIWorker implements Runnable {
	private Point original;
	private double chance;
	private Model m;
	private double mult = 1.0;
	private double MULT_DROPOFF = 0.075;
	private int skip = 0;
	private char whoami;
	private char enemy;
	private char me;
	private char op;
	private AI ai;

	public AIWorker(Model m, Pair<Point, Integer> p, AI ai) {
		original = p.getKey();
		this.ai = ai;
		this.whoami = ai.getWhoami();
		this.enemy = ai.getEnemy();
		me = whoami;
		op = enemy;
		this.m = new Model(m);
		update(p.getKey(), (Double)(double)p.getValue());
		switchSides();
	}

	public void run() {
		while (next()) {
			ai.checkBestMove(original, (Double)chance);
		}
		ai.checkBestMove(original, (Double)chance);
	}

	public boolean next() {
		Pair<Point, Double> n = AI.getBestMove(m, me, op);
		if (n.getKey() == null) {
			skip++;
		} else {
			update(n.getKey(), n.getValue());
			skip = 0;
		}

		if (skip < 2 && mult > 0) {
			switchSides();
			return true;
		}
		if (skip == 2) {
			System.out.println("Skip == 2");
			if (m.getWinner() == whoami) {
				chance += 15 * mult;
			} else {
				chance -= 15 * mult;
			}
		}
		return false;
	}

	private void update(Point p, Double c) {
		m.move(p.x, p.y, me, op);
		chance += (double)c * mult * (me == enemy ? -1 : 1);
		mult -= MULT_DROPOFF;
	}

	private void switchSides() {
		me = ((me == whoami) ? enemy : whoami);
		op = ((op == enemy) ? whoami : enemy);
	}
}
