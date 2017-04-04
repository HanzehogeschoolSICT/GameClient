package game.tictactoe;

import javafx.application.Application;

/**
 *
 * @author Mark
 */
public class TicTacToe{
    public TicTacToe() {
        Controller controller = new Controller();
    }
    public static void main(String[] args) {
        Application.launch(View.class, args);
    }
}