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
public class PlayerTuple implements Comparable<PlayerTuple>{
    private final int id;
    private final String username;
    private Integer score;

    public PlayerTuple(int id, String username) {
        this.id = id;
        this.username = username;
    }
    
     public PlayerTuple(String username, int score) {
        this.id = 0;
        this.username = username;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
    
    public void setScore(int score){
        this.score = score;
    }

    public Integer getScore() {
        return score;
    }

    @Override
    public int compareTo(PlayerTuple o) {
        return this.score.compareTo(o.score);
    }
}
