/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Model;

/**
 *
 * @author dcandrade
 */
public class ClientNotConnectedException extends Exception {

    public ClientNotConnectedException() {
        super("O cliente deve se conectar ao servidor primeiro");
    }
}
