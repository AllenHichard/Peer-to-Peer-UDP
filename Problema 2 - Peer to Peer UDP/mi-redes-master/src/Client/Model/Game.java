package Client.Model;

import Client.Util.WordTuple;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

/**
 * Roda a Roda's game engine.
 *
 * @author Allen Hichard
 * @author Daniel Andrade
 */
public class Game {
    
    
    private final Queue<WordTuple> words;
    private String word; //Game's word.
    private StringBuilder wordBuilder; //User's version of the game's word.
    private String tip; //Word's tip
    private final int[] rouletteValues;
    private int rouletteValue; //Current roulette values
    private int roundNumber; //Current round's number.
    private boolean isRouletteAvailable;
    private final int numberOfRounds;
    private final LinkedList<Integer> players;
    private final int playerID;
    private Iterator<Integer> playerIndex;
    private int actualPlayer;
    private final Map<Integer, Integer> scores;
    private final Map<Integer, String> usernames;
    private List<Character> chars;

    public final static int NOT_YOUR_TURN = -10;
    public static int RESET_VALUE = 40;
    public static int SKIP_PLAYER = 41;

    /**
     * O construtor Game é onde ocorre o jogo, tendo uma lista de Player onde todos
     * são adversários, o ID da partida e as 4 palavras para os rounds.
     * @param players
     * @param playerID
     * @param words
     * @throws IOException 
     */
    public Game(LinkedList<PlayerTuple> players, int playerID, Queue<WordTuple> words) throws IOException {
        this.words = words;
        this.playerID = playerID;
        this.roundNumber = 1;
        this.numberOfRounds = 4;
        this.isRouletteAvailable = true;
        this.wordBuilder = new StringBuilder();
        this.rouletteValues = new int[]{100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, Game.SKIP_PLAYER, Game.RESET_VALUE};
        this.refreshData();
        this.players = new LinkedList<>();
        this.usernames = new HashMap<>();
       

        Iterator<PlayerTuple> iterator = players.iterator();
        while (iterator.hasNext()) {
            PlayerTuple next = iterator.next();
            this.players.add(next.getId());
            this.usernames.put(next.getId(), next.getUsername());
        }

        this.playerIndex = this.players.iterator();
        this.actualPlayer = this.playerIndex.next();
        this.scores = new HashMap<>();
        this.chars = new LinkedList<>();
    }
    
    /**
     * O método nextPlayer tem a finalidade de pegar o próximo jogador, quando
     * o valor da roleta por passar a vez ou perder tudo.
     */
    public synchronized void nextPlayer() {
        if (!this.playerIndex.hasNext()) {
            this.playerIndex = this.players.iterator();
        }
        this.actualPlayer = playerIndex.next();
    }
    
    public List<Integer> getPlayersID(int myID){
        List<Integer> k = new ArrayList<>();
        
        Iterator<Integer> iterator = this.players.iterator();
        
        while(iterator.hasNext()){
            Integer next = iterator.next();
            if(next != myID){
                k.add(next);
            }
        }
        
        return k;
    }
    
    public synchronized Map<Integer, Integer> getScores(){
        return this.scores;
    }

    public synchronized boolean isGameFinished() {
        return !this.hasNextRound() && this.isRoundFinished();
    }

    public String getUsername(int id) {
        return this.usernames.get(id);
    }

    public synchronized int currentPlayer() {
        return this.actualPlayer;
    }

    public synchronized void setRouletteValue(int value) {
        this.rouletteValue = value;
    }

    public synchronized void updateScore(int id, int value) {
        int score = 0;
        if (this.scores.containsKey(id)) {
            score = this.scores.get(id);
        }
        if (value == Game.RESET_VALUE) {
            this.scores.put(id, 0);
        } else {
            this.scores.put(id, score + value);
        }

    }

    public synchronized int getPlayerScore(int id) {
        return this.scores.get(id);
    }

    public synchronized boolean isMyTurn() {
        return this.currentPlayer() == this.playerID;
    }

    /**
     * Check if it's time to spin the roulette. The roulette can used after try
     * to tryCharacter a word character.
     *
     * @return true if roulette is available, false otherwise.
     */
    public synchronized boolean isIsRouletteAvailable() {
        return this.isRouletteAvailable;
    }

    /**
     * Get the user's word version with hidden characters.
     *
     * @return the user's word
     */
    public synchronized String getUserWord() {
        return this.wordBuilder.toString();
    }

    /**
     * Get current word's tip. The 1st roundNumber doesn't allow tips.
     *
     * @return The current word tip.
     */
    public synchronized String getTip() {
        //return this.word;
        return this.roundNumber == 1 ? "Sem dica na primeira rodada." : this.tip;
    }

    /**
     * Refresh game's word/tip if a new roundNumber is reached.
     */
    private synchronized void refreshData() {
            WordTuple next = this.words.poll();
            this.word = next.getWord();
            this.tip = next.getTip();
            this.wordBuilder = new StringBuilder();

            for (int i = 0; i < this.word.length() - 1; i++) {
                this.wordBuilder.append('-');
            }

    }

    /**
     * Get and replace ch ocurrences in the current word. After the changes, the
     * score is updated.
     *
     * @param ch char to be revealed
     * @return amount of revealed chars
     */
    public synchronized int tryCharacter(char ch) {
         
        String s = Character.toString(ch);
        this.chars.add(ch);
        
        this.isRouletteAvailable = true;

        int occurrences = 0;
        int index = 0;

        while ((index = this.word.indexOf("" + ch, index)) != -1
                && this.wordBuilder.charAt(index) == '-') {
            this.wordBuilder.setCharAt(index, ch);
            index++;
            occurrences++;
        }

        this.updateScore(this.actualPlayer, this.rouletteValue * occurrences);

        if(occurrences == 0)
            this.nextPlayer();

        return occurrences;
    }
    
    public List<Character> getChars(){
        return this.chars;
    }

    /**
     * Random roulette value or the current roullete value in case the roulette
     * isn't available.
     *
     * @return a roulette value
     */
    public synchronized int roulette() {
        if (!this.isMyTurn()) {
            return this.rouletteValue;
        } else if (this.isRouletteAvailable) {
            Random random = new Random();
            this.rouletteValue = this.rouletteValues[random.nextInt(this.rouletteValues.length)];
        }

        return this.rouletteValue;
    }

    /**
     * Check if the turn is over and prepare for another roundNumber.
     *
     * @return true if the roundNumber is finished, false otherwise.
     */
    public synchronized boolean isRoundFinished() {
        return !this.wordBuilder.toString().isEmpty() && 
                this.wordBuilder.indexOf("-") == -1;

    }

    /**
     * Go to the next roundNumber, if it exists. If the game has more rounds,
     * the roundNumber number is updated and the word is refreshed. Also, the
     * accumulated score is updated and the roundNumber's score is set to 0.
     *
     * @return true if a next roundNumber is reached, false otherwise.
     */
    public synchronized boolean nextRound() {
        if (this.isRoundFinished() && this.hasNextRound()) {
            this.refreshData();
            this.roundNumber++;
            this.chars = new LinkedList<>();
            return true;
        }
        return false;
    }

    /**
     * Check if there's a next roundNumber.
     *
     * @return false if the current roundNumber is the last one, true otherwise.
     */
    public synchronized boolean hasNextRound() {
        return this.roundNumber < this.numberOfRounds;
    }

    /**
     *
     * @return current roundNumber number.
     */
    public synchronized int getRoundNumber() {
        if (this.isRoundFinished()) {
            this.nextRound();
        }
        return this.roundNumber;
    }
    
    
}
