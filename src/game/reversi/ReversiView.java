/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.reversi;

import framework.View;

/**
 *
 * @author Talitha
 */
public class ReversiView implements View{
    
    private String location;
    
    public ReversiView(){
        location = "../game/reversi/ReversiGameFXML.fxml";
    }
    @Override
    public String getLocation() {
        return location;
    }
    
}
