/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Model;

import Server.Util.Exceptions.DatabaseParsingException;
import Server.Util.PlayerTuple;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author dcandrade
 */
public class PlayerDistribution {

    private final LinkedList<PlayerTuple> twoPlayersGame;
    private final LinkedList<PlayerTuple> threePlayersGame;
    private final GameStarter gameStarter = new GameStarter();

    public PlayerDistribution() {
        this.twoPlayersGame = new LinkedList<>();
        this.threePlayersGame = new LinkedList<>();
    }

  /**
   * addPlayer adiciona jogadores em umas das filas de espera do modo jogo.
   * @param numPlayers
   * @param tuple 
   */
    public synchronized void addPlayer(int numPlayers, PlayerTuple tuple) {
        if (numPlayers == 2) {
            this.twoPlayersGame.add(tuple);
            System.out.println("Jogador adicionado na fila de jogo de duplas");
        } else {
            this.threePlayersGame.add(tuple);
            System.out.println("Jogador adicionado na fila de jogo de trios");
        }
    }

    /**
     * o run inicia o jogo tanto para o modo 2 jogadores quanto para o modo de 3
     * jogadores.
     * @throws DatabaseParsingException
     * @throws UnknownHostException
     * @throws SocketException
     * @throws IOException 
     */
    public void run() throws DatabaseParsingException, UnknownHostException, SocketException, IOException {
        while (!twoPlayersGame.isEmpty() && twoPlayersGame.size() % 2 == 0) {
            System.out.println("Iniciando jogo com 2 jogadores");
            Queue<PlayerTuple> players = this.poolKElements(2, twoPlayersGame);
            this.gameStarter.startGame(players, 2);
        }
        while (!threePlayersGame.isEmpty() && threePlayersGame.size() % 3 == 0) {
            System.out.println("Iniciando jogo com 2 jogadores");
            Queue<PlayerTuple> players = this.poolKElements(3, threePlayersGame);
            this.gameStarter.startGame(players, 3);
        }
    }

    public Queue<PlayerTuple> poolKElements(int k, Queue<PlayerTuple> queue) {
        Queue<PlayerTuple> firstK = new LinkedList<>();

        for (int i = 0; i < k; i++) {
            firstK.add(queue.poll());
        }

        return firstK;
    }

}
