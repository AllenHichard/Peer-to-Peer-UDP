/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author dcandrade
 */
public class MulticastSender {
    private static final int PORT = 8888;
    private final InetAddress address;
    
    /**
     * A Classe MulticastSender é responsavél por enviar mensagem para todos os usuários
     * que fazem parte de um determinado grupo multicasting.
     * @param address 
     */
    public MulticastSender(InetAddress address){
        this.address = address;
    }
    
    public void sendPacket(int protocolId, String data) throws SocketException, IOException{
        DatagramSocket socket = new DatagramSocket();
        byte[] buffer = data.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.address, MulticastSender.PORT);
        
        socket.send(packet);
    }
    
}
