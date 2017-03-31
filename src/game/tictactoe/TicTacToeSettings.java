/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.tictactoe;

import framework.View;

/**
 *
 * @author Talitha
 */
public class TicTacToeSettings implements View {
    
    private String location;
    
    public TicTacToeSettings(){
        location = "../game/tictactoe/SettingsFXML.fxml";
    }

    @Override
    public String getLocation() {
        return location;
 
    }
    
}
