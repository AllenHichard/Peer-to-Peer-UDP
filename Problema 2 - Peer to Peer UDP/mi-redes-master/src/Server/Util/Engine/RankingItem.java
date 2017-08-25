package Server.Util.Engine;


/**
 * Tuple to represent a item stored in the ranking. All ranking entries are 
 * represented by an username and a score.
 * 
 * @author Allen Hichard
 * @author Daniel Andrade
 */
public class RankingItem implements Comparable<RankingItem>{

    private final String username;
    private Integer score;

    public RankingItem(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
    
    

    @Override
    public String toString() {
        return this.username +": "+this.score;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RankingItem){
            RankingItem o = (RankingItem) obj;
            return this.username.equals(o.username);
        }
        return false;
    }
    
    /**
     * Compares two tuples (descending order).
     * @param tuple
     * @return 
     */
    @Override
    public int compareTo(RankingItem tuple) {
        return tuple.getScore().compareTo(this.score);
    }
}
