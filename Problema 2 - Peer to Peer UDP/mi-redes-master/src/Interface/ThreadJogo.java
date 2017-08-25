/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import Client.Model.Player;
import java.awt.Color;


/**
 *
 * @author allen
 */
public class ThreadJogo implements Runnable {

    private Player p;
    private Game2 jogo;
    private static int valor;
    private int round = 1;

    public ThreadJogo(Player p, Game2 jogo) {
        this.p = p;
        this.jogo = jogo;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    @Override
    public void run() {
        while (true) {
            jogo.getRodada().setText("Rodada Atual: " + p.roundNumber());
            jogo.getWord().setText(p.getWord());
            jogo.getDica().setText(p.getTip());
            jogo.getValor().setText("Cada letra vale: " + valor);
            jogo.getP1().setText(p.getUsername());
            jogo.getP2().setText(p.getGame().getUsername(p.getIDS().get(0)));
            if(this.round != p.getGame().getRoundNumber()){
                this.round = p.getGame().getRoundNumber();
                jogo.carregarBotao();
            }

            try {
                jogo.getPontuacao1().setText("" + p.getGame().getPlayerScore(p.getID()));
            } catch (Exception x) {
                jogo.getPontuacao1().setText(""+0);
            }
            try {
                jogo.getPontuacao2().setText("" + p.getGame().getPlayerScore(p.getIDS().get(0)));
            } catch (Exception x) {
                jogo.getPontuacao2().setText("" + 0);
            }
            if (p.isMyTurn()) {
                jogo.getVez().setText("Infome uma letra");
                jogo.getVez().setBackground(Color.red);
            } else {
                int i = p.getGame().currentPlayer();
                jogo.getVez().setText("Vez do " + p.getGame().getUsername(i));
                jogo.getVez().setBackground(Color.white);
            }
        }
    }

}
