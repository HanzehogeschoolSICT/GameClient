/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package debug;

import framework.View;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author Talitha
 */
public class TestView implements View
{
    private String location;
    
    public TestView(){
        location = "../debug/TestFXML.fxml";
    }

    @Override
    public String getLocation() {
        return location;
    }   
}
