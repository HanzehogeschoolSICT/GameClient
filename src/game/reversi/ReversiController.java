package game.reversi;

import framework.interfaces.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Created by markshizzle on 6-4-2017.
 */
public class ReversiController implements Controller {
    @FXML
    private
    GridPane grid;
    private Model model;
    private char currentTurn;
    private boolean isFull = false;
    char[][] board;
    boolean[][] legalMoves = new boolean[8][8];

    //private boolean[][] legalMovesW = new boolean[8][8];
    //private boolean[][] legalMovesB = new boolean[8][8];

    public ReversiController(char CurrentTurn) {
        this.model = new Model();
        this.currentTurn = currentTurn;
        board = model.getBoard();
        getLegalMoves();

    }

    public void doMove(int row, int column) {
        System.out.println("\n");
        if(legalMove(row, column, currentTurn, true)) {
            model.setSymbol(row, column, currentTurn);
            changeTurns();
            drawBoard();

        }
        else {
            System.out.println("Deze zet kan helaas niet..");
            return;
        }
        getLegalMoves();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(legalMoves[i][j]) {
                    String kleur = currentTurn == 'w' ? "Wit" : "Zwart";
                    System.out.println("Succes " + kleur +", jij bent aan de beurt en dit zijn je zetten:" + "\n" + "Legal move is row: " + i + " en kolom: " + j);
                }
            }
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (model.getSymbol(i, j) == 'w' || model.getSymbol(i, j) == 'b') {
                    System.out.println("Op row: " + i + " en kolom: " + j + " staat de kleur: " + model.getSymbol(i, j) + "\n");
                }
            }
        }
    }

     void drawBoard() {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (model.getSymbol(i,j) == 'b') {
                        drawO('b', i, j);
                    }
                    if (model.getSymbol(i,j) == 'w') {
                        drawO('w', i, j);
                    }
                }
            }

        }
    private void drawO(char colour, int row, int column) {
        Circle c1 = new Circle(0, 0, 35);
        c1.setStroke(Color.BLACK);
        if(colour == 'b') {
            c1.setFill(Color.BLACK);
        }
        if(colour == 'w') {
            c1.setFill(Color.WHITE);
        }
        c1.setStrokeWidth(3);
        grid.add(c1,column,row);

    }

    public void getLegalMoves() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                legalMoves[i][j] = legalMove(i,j,currentTurn,false);
            }
        }
    }

    public boolean legalMove(int r, int c, char color, boolean flip) {
        boolean legal = false;
        char oppSymbol;

        // Als de cel leeg is begin met zoeken
        // Als de cel niet leeg is wordt het afgebroken
        if (model.getSymbol(r,c) != 'w' && model.getSymbol(r,c) != 'b')
        {
            // Initialize variables
            int posX;
            int posY;
            boolean found;
            char current;

            // Zoekt in elke richting
            // x en y beschrijven alle richtingen
            for (int x = -1; x <= 1; x++)
            {
                for (int y = -1; y <= 1; y++) {
                    //Variabelen om de positie bij te houden en of het een valid zet is.
                    posX = c + x;
                    posY = r + y;
                    found = false;
                    if (posX > -1 && posX < 8 && posY > -1 && posY < 8) {
                        current = model.getSymbol(posY,posX);
                        oppSymbol = color == 'b' ? 'w' : 'b';

                        if (current != oppSymbol) {
                            continue;
                        }

                        while (!found) {
                            posX += x;
                            posY += y;
                            if (posX < 0 || posX > 8 || posY < 0 || posY > 8) {
                                found = true;
                            }
                            else {
                                current = model.getSymbol(posY, posX);
                                oppSymbol = currentTurn == 'b' ? 'w' : 'b';

                                if (current == color) {
                                    found = true;
                                    legal = true;

                                    // Als flip op true staat mag er omgedraaid worden.
                                    // De algoritme draait dan om een gaat alles omdraaien
                                    // tot de oorspronkelijke positie bereikt is.
                                    if (flip) {
                                        posX -= x;
                                        posY -= y;
                                        if (posX > -1 && posX < 8 && posY > -1 && posY < 8) {
                                            current = model.getSymbol(posY, posX);
                                            oppSymbol = color == 'b' ? 'w' : 'b';

                                            while (current == oppSymbol) {
                                                model.setSymbol(posY, posX, color);
                                                posX -= x;
                                                posY -= y;
                                                current = model.getSymbol(posY, posX);
                                            }
                                        }
                                    }
                                }
                                // Als het algoritme een cel vind dat leeg is
                                // wordt de loop afgebroken en op zoek gegaan naar een andere plek
                                else if (current != 'w' && current != 'b') {
                                    found = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return legal;
    }

    private void changeTurns() {
        if(currentTurn == 'b') {
            currentTurn = 'w';
        }
        else {
            currentTurn = 'b';
        }
    }
    @FXML
    public void squareClicked(MouseEvent event) {
        Label l = (Label) event.getSource();
        doMove(GridPane.getRowIndex(l),GridPane.getColumnIndex(l));
    }

    @Override
    public String getLocation() {
        return "../game/reversi/ReversiBoard.fxml";
    }
}


