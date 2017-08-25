/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Util;

import Client.Model.Player;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dcandrade
 */
public class MulticastReceiver extends Thread {

    private static final int PORT = 8888;
    private final MulticastSocket socket;
    private final Player player;

       /**
        * A classe MulticastReceiver é a responsável para receber mensagens que são
        * emitidas pelo grupo.
        * @param address
        * @param player
        * @throws IOException 
        */
    public MulticastReceiver(InetAddress address, Player player) throws IOException {
        this.socket = new MulticastSocket(PORT);
        this.socket.joinGroup(address);
        this.player = player;
    }

    @Override
    public void run() {
        byte[] inBuffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(inBuffer, inBuffer.length);
        String message;

        while (true) {
            try {
                this.socket.receive(packet);
                message = new String(inBuffer, 0, packet.getLength());
                //System.err.println(message);
                //encaminhar mensagem
                this.player.processInstruction(message);

            } catch (IOException ex) {
                Logger.getLogger(MulticastReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
