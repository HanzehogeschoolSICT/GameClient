package game.tictactoe;

import framework.MessageBus;

/**
 *
 * @author Mark
 */
public class TicTacToe{
    MessageBus bus;

    public TicTacToe() {

        Controller controller = new Controller();
        bus = MessageBus.getBus();
        //bus.call("HOI");
    }
    public static void main(String[] args) {
        TicTacToe ttt = new TicTacToe();
    }
}