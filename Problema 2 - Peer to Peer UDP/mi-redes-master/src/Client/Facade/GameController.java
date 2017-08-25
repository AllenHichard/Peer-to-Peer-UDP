/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Facade;

import Client.Model.Client;
import Client.Model.Player;
import Client.Model.PlayerTuple;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 *
 * @author dcandrade
 */
public class GameController {

    private Client client;
    private final InetAddress address;

    public GameController(String ip) throws UnknownHostException {
        this.address = InetAddress.getByName(ip);
    }

    public void setupClient(String username, String password) throws IOException {
        this.client = new Client(username, password, address);
    }
    
    public boolean login() throws IOException, ClassNotFoundException{
        return this.client.loginOnServer();
    }
    
    public boolean register() throws IOException, ClassNotFoundException{
        return this.client.registerPlayer();
    }
    
    public boolean isLogged(){
        return this.client.isLogged();
    }
    
    public boolean isConnected(){
        return this.client.isConnected();
    }
    
    public String getUsername(){
        return this.client.getUsername();
    }
    
    public void waitForPlayers(int gameMode) throws IOException, ClassNotFoundException{
        this.client.waitForPlayers(gameMode);
    }
    
    public boolean isGameReady(){
        return this.client.getPlayer().isIsGameReady();
    }
    
    public boolean isMyTurn(){
        return this.client.getPlayer().isMyTurn();
    }
    
    public String getWord(){
        return this.client.getPlayer().getWord();
    }
    
    public String getTip(){
        return this.client.getPlayer().getTip();
    }
    
    public int roullete() throws IOException{
        return this.client.getPlayer().roullete();
    }
    
    public boolean isGameFinished(){
        return this.client.getPlayer().isGameFinished();
    }
    
    public int roundNumber(){
        return this.client.getPlayer().roundNumber();
    }
    
    public int tryCharacter(char ch) throws IOException{
        return this.client.getPlayer().tryCharacter(ch);
    }
    
    public boolean isRoundFinished(){
        return this.client.getPlayer().isRoundFinished();
    }
    
    public void sendResult() throws IOException{
         this.client.getPlayer().sendResult();
    }
    
    public List<PlayerTuple> getRanking() throws IOException, ClassNotFoundException{
        List<PlayerTuple> rankingInfo = this.client.getPlayer().getRankingInfo(this.address);
        return rankingInfo;
    }
    
    public boolean isGameSetted(){
        return this.client.isGameSetted();
    }
    
    public Player getPlayer(){
        return client.getPlayer();
    }
    
   
}
