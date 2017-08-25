/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Model;

import Server.Util.Engine.Ranking;
import Server.Util.Engine.RankingItem;
import Server.Util.PlayerTuple;
import Server.Util.Protocol.Protocol;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

/**
 * Client treatment, receives the client and redirects it to a game.
 *
 * @author dcandrade
 */
public class PlayerSetup extends Thread {

    private final LoginEngine login;
    private final PlayerDistribution distributor;
    private final Ranking ranking;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private boolean loggedIn;
    private static int ID = 0;

    public PlayerSetup(LoginEngine login, PlayerDistribution distributor, Ranking ranking, ObjectInputStream input, ObjectOutputStream output) {
        this.loggedIn = false;
        this.login = login;
        this.distributor = distributor;
        this.ranking = ranking;
        this.input = input;
        this.output = output;
    }
    
    /**
     * o Run é utilizado para fazer a comunicação entre Servidor-cliente, onde a depender
     * dos protocolos de comunicação envia ou recebe pacotes pela rede.
     */
    @Override
    public void run() {
        try {
            String username = "";
            String password;
            while (true) {
                String userData = this.readMessage(this.input).toString();

                StringTokenizer tokenizer = new StringTokenizer(userData, Protocol.SEPARATOR);

                int operation = Integer.parseInt(tokenizer.nextToken());

                switch (operation) {
                    case Protocol.SIGN_IN:
                        username = tokenizer.nextToken();
                        password = tokenizer.nextToken();
                        if (this.login.signIn(username, password)) {
                            this.sendMessage(Protocol.OK, this.output);
                            this.loggedIn = true;
                            System.out.println("Jogador entrou.");
                        } else {
                            this.sendMessage(Protocol.NOT_OK, this.output);
                        }
                        break;

                    case Protocol.SIGN_UP:
                        username = tokenizer.nextToken();
                        password = tokenizer.nextToken();
                        if (this.login.signUp(username, password)) {
                            this.sendMessage(Protocol.OK, this.output);
                        } else {
                            this.sendMessage(Protocol.NOT_OK, this.output);
                        }
                        break;

                    case Protocol.PLAY:
                        int gameMode = Integer.parseInt(tokenizer.nextToken());
                        if (this.loggedIn) {
                            int playerID = PlayerSetup.ID++;
                            this.sendMessage(playerID, this.output);
                            PlayerTuple tuple = new PlayerTuple(output, playerID, username);
                            //Player distribution, the game start criteria is on the PlayerDistribution class
                            this.distributor.addPlayer(gameMode, tuple);
                            System.out.println("Jogador esperando na fila.");
                            this.distributor.run();
                            return;
                        }
                        break;
                    case Protocol.UPDATE_RANKING:
                        username = tokenizer.nextToken();
                        int score = Integer.parseInt(tokenizer.nextToken());

                        this.ranking.refreshUserHighscore(username, score);
                        return;

                    case Protocol.RANKING_INFO:
                        System.err.println("Ranking solicitado");
                        RankingItem[] top3 = this.ranking.getTop3();
                        StringBuilder message = new StringBuilder();
                        for (RankingItem item : top3) {
                            message.append(item.getUsername()).append(Protocol.SEPARATOR);
                            message.append(item.getScore()).append(Protocol.SEPARATOR);
                        }
                        System.err.println(message);
                        this.sendMessage(message, this.output);
                        return;
                }
            }
            //Does it automatomatically close? Needs explict close instruction?
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }

    }

    /**
     * sendMessage envia mensagem para o servidor.
     *
     * @param data
     * @param clientOutput
     * @throws IOException
     */
    private void sendMessage(Object data, OutputStream clientOutput) throws IOException {
        output.reset();
        output.writeObject(data);
        output.flush();
    }
    
    /**
     * Recebe mensagem do servidor.
     * @param clientInput
     * @return
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    private Object readMessage(InputStream clientInput) throws IOException, ClassNotFoundException {
        return this.input.readObject();
    }

}
