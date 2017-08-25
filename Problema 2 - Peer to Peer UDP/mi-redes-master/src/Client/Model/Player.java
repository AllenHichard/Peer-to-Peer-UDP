/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Model;

import Client.Util.MulticastReceiver;
import Client.Util.MulticastSender;
import Client.Util.Protocols.CSProtocol;
import Client.Util.Protocols.P2PProtocol;
import Client.Util.WordTuple;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author dcandrade
 */
public class Player {

    private final String username;
    private int id;
    private Game game;
    private MulticastReceiver receiver;
    private MulticastSender sender;
    private final Queue<String> instructions;
    private boolean isGameReady;
    private final InetAddress address;
    private boolean gameSetted;

    /**
     * O construtor Player recebe o username "nome do jogador" e address
     * "endereço" do servidor ao qual o player irá jogar. Cada player tem uma
     * lista de instruções que devem ser seguidas durante o jogo.
     *
     * @param username
     * @param address
     */
    public Player(String username, InetAddress address) {
        this.username = username;
        this.instructions = new LinkedList<>();
        this.isGameReady = false;
        this.address = address;
        this.gameSetted = false;

    }

    public synchronized boolean isIsGameReady() {
        return isGameReady;
    }

    public Game getGame() {
        return this.game;
    }

    public List<Integer> getIDS() {
        return this.game.getPlayersID(this.id);
    }

    /**
     * isMyTurn verifica se é a vez do player fazer sua jogada.
     *
     * @return
     */
    public synchronized boolean isMyTurn() {
        return this.isGameReady && this.game.isMyTurn();
    }

  
    public void setID(int id) {
        this.id = id;
    }

    /**
     * O método setAddress recebe um IO, utilizado para preparar o envio e a leitura 
     * de dados de um determinado grupo multicasting
     * @param Address
     * @throws IOException 
     */
    public void setAddress(InetAddress Address) throws IOException {
        this.receiver = new MulticastReceiver(Address, this);
        this.sender = new MulticastSender(Address);
    }

    public int getID() {
        return this.id;
    }

    public synchronized void addInstruction(String instruction) {
        // System.err.println("intrução adicionada");
        this.instructions.add(instruction);
        //System.err.println(this.instructions.size());
    }

    public String getUsername() {
        return this.username;
    }

    /**
     * O método processInstruction analisa a fila de Instruções que armazena protocolos, e
     * vai fazendo as requisições de dados enquanto a fila não estiver vária, caso a mesma
     * esteja vazia, não existe nenhum protocolo sendo transmetido para envio e recebimento de 
     * pacotes.
     * @param instruction
     * @throws UnknownHostException
     * @throws IOException 
     */
    public synchronized void processInstruction(String instruction) throws UnknownHostException, IOException {
        StringTokenizer tokens = new StringTokenizer(instruction, P2PProtocol.SEPARATOR);
        //System.err.println("Instrução recebida");
        int instructionType = Integer.parseInt(tokens.nextToken());
        //System.err.println("Instrução recebida: " + instructionType);
        int id;

        switch (instructionType) {
            case P2PProtocol.CONNECTION_DATA:
                //System.err.println("Dados do jogo recebidos");
                //Recuperando número de players
                int numPlayers = Integer.parseInt(tokens.nextToken());
                LinkedList<PlayerTuple> players = new LinkedList<>();

                //Recuperando usernames dos players
                for (int i = 0; i < numPlayers; i++) {
                    id = Integer.parseInt(tokens.nextToken());
                    String username = tokens.nextToken();

                    players.add(new PlayerTuple(id, username));
                }

                //Recuperando palavras
                Queue<WordTuple> words = new LinkedList<>();
                while (tokens.hasMoreTokens()) {
                    String word = tokens.nextToken();
                    String tip = tokens.nextToken();

                    words.add(new WordTuple(word, tip));
                }

                this.setupGame(players, words);
                /*
                 synchronized (thread) {
                 thread.notify();
                 }*/
                this.gameSetted = true;
                break;

            case P2PProtocol.ROULETTE_VALUE:
                id = Integer.parseInt(tokens.nextToken());

                if (id == this.id) {
                    //System.err.println("Ignorando instrução, origem = destino");
                    break;
                } else {
                    int roulette = Integer.parseInt(tokens.nextToken());
                    // System.err.println("Valor da roleta: " + roulette);

                    this.game.setRouletteValue(roulette);

                    //  System.out.println("Valor da roleta do adversário: " + roulette);
                }
                break;
            case P2PProtocol.TRY_CHARACTER:
                id = Integer.parseInt(tokens.nextToken());
                char ch = tokens.nextToken().charAt(0);
                if (id != this.id) {
                    this.game.tryCharacter(ch);

                }
                break;
            case P2PProtocol.SKIP_PLAYER:
                id = Integer.parseInt(tokens.nextToken());
                if (id == this.id) {
                    break;
                }
                if (tokens.hasMoreTokens()) {
                    this.game.updateScore(id, Game.RESET_VALUE);
                }
                this.game.nextPlayer();
                break;

            case P2PProtocol.NEXT_ROUND:
                this.game.nextRound();
                break;
        }
    }

    public synchronized boolean isGameSetted() {
        return gameSetted;
    }

    /**
     *
     * @param playersID
     * @param words
     * @throws java.io.IOException
     */
    private void setupGame(LinkedList<PlayerTuple> players, Queue<WordTuple> words) throws IOException {
        this.game = new Game(players, this.id, words);
        this.isGameReady = true;
    }

    public synchronized String getWord() {
        return this.game.getUserWord();
    }

    public synchronized String getTip() {
        return this.game.getTip();
    }

    /**
     * O método roullete retorna o valor atual da roleta, quando o player roda a
     * roleta do jogo.
     * @return
     * @throws IOException 
     */
    public synchronized int roullete() throws IOException {
        int value = this.game.roulette();
        if (value == Game.SKIP_PLAYER) {
            this.send(P2PProtocol.SKIP_PLAYER, "");
            this.game.nextPlayer();
        } else if (value == Game.RESET_VALUE) {
            this.send(P2PProtocol.SKIP_PLAYER, "" + value);
            this.game.nextPlayer();
        } else {
            this.send(P2PProtocol.ROULETTE_VALUE, "" + value);
        }

        return value;
    }

    public synchronized boolean isGameFinished() {
        return this.game.isGameFinished();
    }

    public synchronized int roundNumber() {
        return this.game.getRoundNumber();
    }

    /**
     * O método tryCharacter verifica quantas letras existem na palavra da rodada
     * pela letra informada pelo Player.
     * @param ch
     * @return
     * @throws IOException 
     */
    public synchronized int tryCharacter(char ch) throws IOException {
        this.send(P2PProtocol.TRY_CHARACTER, "" + ch);

        int occurrences = this.game.tryCharacter(ch);

        if (this.game.isRoundFinished()) {
            this.send(P2PProtocol.NEXT_ROUND, "");
        }

        return occurrences;
    }

    
    
    public synchronized boolean hasNextRound() {
        return this.game.hasNextRound();
    }

   
    public synchronized boolean nextRound() throws IOException {
        if (this.game.isMyTurn() && this.game.nextRound()) {
            this.send(P2PProtocol.NEXT_ROUND, "");

            return true;
        }

        return false;
    }

    public synchronized boolean isRoundFinished() {
        return this.game.isRoundFinished();
    }

    /**
     * O método sendResult envia os dados da partida e os armazena em arquivo.
     * Quando uma partida acaba ele é utilizado para pegar os dados do vencedor e
     * os armazenar em um arquivo.
     * @throws IOException 
     */
    public synchronized void sendResult() throws IOException {
        Set<Map.Entry<Integer, Integer>> entrySet = this.game.getScores().entrySet();

        PlayerTuple[] p = new PlayerTuple[entrySet.size()];
        int i = 0;
        for (Map.Entry<Integer, Integer> x : entrySet) {
            p[i] = new PlayerTuple(x.getKey(), this.game.getUsername(x.getKey()));
            p[i].setScore(x.getValue());
            i++;
        }

        Arrays.sort(p);

        if (p[0].getId() == this.id) {
            Socket s = new Socket(this.address, CSProtocol.PORT);
            String message = CSProtocol.UPDATE_RANKING + CSProtocol.SEPARATOR
                    + p[0].getUsername() + CSProtocol.SEPARATOR + p[0].getScore();

            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            oos.reset();
            oos.writeObject(message);
            oos.flush();
            oos.close();
        }
    }
    
    /**
     * O método getRankingInfo faz a leitura do arquivo e retorna os 3 melhores
     * jogadores do programa.
     * @param address
     * @return
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public synchronized List<PlayerTuple> getRankingInfo(InetAddress address) throws IOException, ClassNotFoundException {
        Socket s = new Socket(address, CSProtocol.PORT);
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

        oos.reset();
        oos.writeObject(CSProtocol.RANKING_INFO);
        oos.flush();
        String message = ois.readObject().toString();
        System.err.println(message);
        StringTokenizer token = new StringTokenizer(message, CSProtocol.SEPARATOR);
        List<PlayerTuple> list = new ArrayList<>();
        System.err.println();

        while (token.hasMoreTokens()) {
            String username = token.nextToken();
            int score = Integer.parseInt(token.nextToken());
            PlayerTuple playerTuple = new PlayerTuple(username, score);
            list.add(playerTuple);
        }

        return list;
    }

    /**
     * send envia uma mensagem para o multicasting.
     * @param protocol
     * @param dataToSend
     * @throws IOException 
     */
    private synchronized void send(int protocol, String dataToSend) throws IOException {
        //Envia somente se for a vez desse jogador, evitando loop na conexão.
        if (this.game.isMyTurn()) {
            String data = protocol + P2PProtocol.SEPARATOR + this.id
                    + P2PProtocol.SEPARATOR + dataToSend;
            this.sender.sendPacket(protocol, data);
        }
    }

}
