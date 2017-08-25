/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author dcandrade
 */
public class LoginEngine {
    private final Properties players;
    
    public LoginEngine() throws IOException{
        this.players = new Properties();
        this.players.load(new FileInputStream("login.data"));
    }
    
    /**
     * O método signUp registra o novo jogador para o sistema.
     * @param name
     * @param password
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public synchronized boolean signUp(String name, String password) throws FileNotFoundException, IOException{
        if(this.players.getProperty(name) == null){
            this.players.setProperty(name, password);
            this.players.store(new FileOutputStream("login.data"), "");
            
            return true; //Sucessfully registered
        }
        
        return false; //Player name already exists
    }
    
    /**
     * signIn analisa se o jogador já está registrado no sistema, caso esteja
     * permite que o mesmo possa jogar.
     * @param name
     * @param password
     * @return 
     */
    public synchronized boolean signIn(String name, String password){
        String passwordData = this.players.getProperty(name);
     
        return password.equals(passwordData);
    }
    
}
