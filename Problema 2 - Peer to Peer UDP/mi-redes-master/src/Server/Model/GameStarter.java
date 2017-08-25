/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Model;

import Server.Util.Exceptions.DatabaseParsingException;
import Server.Util.Exceptions.PropertiesFileNotFoundException;
import Server.Util.PlayerTuple;
import Server.Util.Protocol.P2PProtocol;
import Server.Util.Protocol.Protocol;
import Server.Util.WordManager.WordManager;
import Server.Util.WordManager.WordTuple;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dcandrade
 */
public class GameStarter {

    private final static int PORT = 8888;
    private final static int SLEEP_TIME = 50;
    private final static int NUMBER_OF_WORDS = 4;
    private final static MulticastIPManager IP = new MulticastIPManager();

    /**
     * getWords retorna para o Jogo as 4 palavras concatenadas para o inicio das partidas.
     * O arquivo contém mais de 20 mil palavras onde das mesmas são pré-selecionadas 4 para cada partida.
     * @return
     * @throws PropertiesFileNotFoundException
     * @throws DatabaseParsingException 
     */
    private String getWords() throws PropertiesFileNotFoundException, DatabaseParsingException {
        WordManager wm = new WordManager(Server.DATASET_LOCATION);
        Iterator<WordTuple> tuples = wm.getTuples(GameStarter.NUMBER_OF_WORDS).iterator();

        StringBuilder gameData = new StringBuilder();

        while (tuples.hasNext()) {
            WordTuple tuple = tuples.next();
            gameData.append(tuple.getWord()).append(Protocol.SEPARATOR).append(tuple.getTip());
            gameData.append(Protocol.SEPARATOR);
        }

        return gameData.toString();
    }
    
    /**
     * startGame é onde o jogo é inicializado e todas as informações são enviadas
     * do servidor para o multicasting.
     * @param players
     * @param numPlayers
     * @throws PropertiesFileNotFoundException
     * @throws DatabaseParsingException
     * @throws UnknownHostException
     * @throws SocketException
     * @throws IOException 
     */
    public void startGame(Queue<PlayerTuple> players, int numPlayers) throws PropertiesFileNotFoundException, DatabaseParsingException, UnknownHostException, SocketException, IOException {
        InetAddress address = GameStarter.IP.getMulticastIP();
        //Inicializa a preparação da mensagem a ser enviada
        StringBuilder data = new StringBuilder();
        data.append(P2PProtocol.CONNECTION_DATA).append(P2PProtocol.SEPARATOR); //Protocol info
        data.append(numPlayers).append(P2PProtocol.SEPARATOR);

        System.out.print("Enviando IP de Multicasting...");
        for (PlayerTuple player : players) {
            //Enviar ip do multicast via TCP
            player.getOutput().reset();
            player.getOutput().writeObject(address);
            player.getOutput().flush();
            
            //O socket não é mais necessário, fechando...
            player.getOutput().close();
            
            //adiciona na string para ser enviada
            data.append(player.getId()).append(P2PProtocol.SEPARATOR);
            data.append(player.getUsername()).append(P2PProtocol.SEPARATOR);
        }
        System.out.println("100%");
        
        System.out.print("Preparando e enviando informações do jogo...");
        //Finaliza a mensagem a ser enviada anexando as palavras e as dicas
        String words = this.getWords();
        data.append(words);
        //Mensagem pronta, atribuindo a string tornando-a imutável
        String message = data.toString();

        //Preparação do pacote a ser enviado
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, GameStarter.PORT);
        DatagramSocket socket = new DatagramSocket();

        try {
            Thread.sleep(GameStarter.SLEEP_TIME);
        } catch (InterruptedException ex) {
            Logger.getLogger(PlayerDistribution.class.getName()).log(Level.SEVERE, null, ex);
        }

        socket.send(packet);
        System.out.println("100%");
    }


}
