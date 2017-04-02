/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.interfaces;

/**
 *
 * @author Wouter
 */
public interface Messagable {
    public void call(String message, Object[] args);
}
