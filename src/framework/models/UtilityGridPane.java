/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.models;

import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Talitha
 */
public class UtilityGridPane extends GridPane{
    
    public void removeRow(int rowIndex){
        ArrayList<Node> remove = new ArrayList();
        for(Node n : getChildren()){
            Integer ri = getRowIndex(n);
            if(ri != null){
                if(ri == rowIndex){
                    remove.add(n);
                }
            }
        }
        for(Node n : remove){
            getChildren().remove(n);
        }
    }
    
    public void removeRowsExcept(int rowIndex){
        ArrayList<Node> remove = new ArrayList();
        for(Node n : getChildren()){
            Integer ri = getRowIndex(n);
            if(ri != null){
                if(ri != rowIndex){
                    remove.add(n);
                }
            }
        }
        for(Node n : remove){
            getChildren().remove(n);
        }
    }
}
