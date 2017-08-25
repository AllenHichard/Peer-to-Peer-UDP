/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Tests;

import Client.Model.Client;
import Client.Model.Game;
import Client.Model.Player;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

/**
 *
 * @author dcandrade
 */
public class ClientTestModel extends Thread {

    private final Client client;
    private final InetAddress address;

    public ClientTestModel(String username, String password, String ip) throws IOException {
        InetAddress address = InetAddress.getByName(ip);
        this.client = new Client(username, password, address);
        this.address = address;
    }

    @Override
    public void run() {
        synchronized (this) {
            try {
                if (client.loginOnServer()) {
                    System.out.println("Conectado ao servidor");
                } else {
                    System.out.println("Cadastre-se primeiro");
                }

                client.waitForPlayers(2);
                /*
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ClientTestModel.class.getName()).log(Level.SEVERE, null, ex);
                }*/
                while(!this.client.isGameSetted());
                
                Player p = client.getPlayer();

                Scanner read = new Scanner(System.in);
                System.out.println(p.isMyTurn());
                while (!p.isGameFinished()) {
                    if (p.isMyTurn() && !p.isRoundFinished()) {
                        System.err.println("Round: " + p.roundNumber());
                        System.out.println("Palavra: " + p.getWord());
                        System.out.println("Dica: " + p.getTip());
                        int roulette = p.roullete();
                        if (roulette != Game.SKIP_PLAYER && roulette != Game.RESET_VALUE) {
                            System.out.println("Cada letra vale: " + roulette);
                            System.out.print("Sua letra é: ");
                            char c = read.nextLine().toUpperCase().charAt(0);
                            int number = p.tryCharacter(c);
                            System.out.println(p.getWord());
                            System.out.println("\nAcertou " + number + " letras");
                        } else {
                            System.out.println("Passou a vez ou Perdeu Tudo!");
                        }

                    }
                    if (p.isRoundFinished()) {
                        System.out.println("Palavra era: " + p.getWord());
                        p.nextRound();
                    }

                    if (p.isGameFinished()) {
                        System.err.println("COMEÇOU");
                        p.sendResult();
                        System.err.println("ENVIOU RESULTADO");
                        p.getRankingInfo(this.address);
                        System.out.println("ENVIOU/RECEBEU");
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
            }

        }

    }
}
