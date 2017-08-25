/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dcandrade
 */
public class WaitingRoom implements Runnable{
    private final Client client;
    private final int gameMode;
    private final ObjectInputStream input;
    
    public WaitingRoom(Client client, int gameMode, ObjectInputStream input){
        this.client = client;
        this.gameMode = gameMode;
        this.input = input;
    }
            
    @Override
    public void run() {
        try {
            //Espera pelo IP do Multicast
            InetAddress multicastIP = (InetAddress) input.readObject();
            //Envia para o client
            this.client.listen(multicastIP);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
