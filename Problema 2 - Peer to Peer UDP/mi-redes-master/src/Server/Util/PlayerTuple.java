/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Util;

import java.io.ObjectOutputStream;

/**
 *
 * @author dcandrade
 */
public class PlayerTuple {
    private final ObjectOutputStream output;
    private final int id;
    private final String username;

    public PlayerTuple(ObjectOutputStream output, int id, String username) {
        this.output = output;
        this.id = id;
        this.username = username;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public int getId() {
        return id;
    }

   
    public String getUsername() {
        return username;
    }
    
    
  
}
