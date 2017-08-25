package Client.Model;

import Client.Util.MulticastReceiver;
import Client.Util.Protocols.CSProtocol;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Client-side implementation of "Roda-a-Roda" game. This class is responsable
 * to give an abstraction layer between the client's choice and the
 * corresponding request to the server. A request to the serve is represented by
 * a message established on the Protocol class.
 *
 * @see CSProtocol
 * @author Allen Hichard
 * @author Daniel Andrade
 */
public class Client {

    private final String username; //Client's username
    private final String password;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private boolean isConnected;
    private boolean isLogged;
    private Player player;
    private final InetAddress address;
    
    /**
     * O Construtor Cliente recebe como parâmetros o username, password, address,
     * Utilizado para identificar o cliente e conectato ao endereço de rede. O address
     * é utilizado para abrir uma conexão com o servidor em uma porta padrão;
     * @param username
     * @param password
     * @param address
     * @throws IOException 
     */
    public Client(String username, String password, InetAddress address) throws IOException {
        this.username = username;
        this.password = password;
        this.player = null;
        this.isConnected = false;
        this.isLogged = false;
        this.address = address;

        Socket socket = new Socket(address, CSProtocol.PORT);
        this.input = new ObjectInputStream(socket.getInputStream());
        this.output = new ObjectOutputStream(socket.getOutputStream());
    }
    
    /**
     * O método isGameSetted é utilizado para identificar um possível início de jogo,
     * caso a partida já tenha o número ideal de jogadores para o seu começo.
     * 
     * @return 
     */
    public boolean isGameSetted(){
        if(player == null){
            return false;
        }
        return this.player.isGameSetted();
    }

    /**
     * O getUsername retorna apenas o nome do jogador.
     * @return 
     */
    public String getUsername() {
        return username;
    }

    /**
     * O método loginOnServidor verifica se o cliente teve êxito ao logar no servidor,
     * assim o mesmo cria um Player, preparando-o inicialmente para o início de uma partida.
     * @return
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public boolean loginOnServer() throws IOException, ClassNotFoundException {
        String request = CSProtocol.SIGN_IN + CSProtocol.SEPARATOR + this.username
                + CSProtocol.SEPARATOR + this.password;

        this.sendMessage(request);
        int response = (int) this.readMessage();

        if(response == CSProtocol.OK){
            this.player = new Player(username, this.address);
            this.isLogged=true;
            return true;
        }
        
        return false;
    }
    
    /**
     * O Método registerPlayer verifica se o Client está registrado no sistema.
     * @return
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public boolean registerPlayer() throws IOException, ClassNotFoundException {
        String request = CSProtocol.SIGN_UP + CSProtocol.SEPARATOR + this.username
                + CSProtocol.SEPARATOR + this.password;

        this.sendMessage(request);
        int response = (int) this.readMessage();

        return response == CSProtocol.OK;
    }
    
    /**
     * O método waitForPlayer recebe um gameMode como parâmetro, parâmetro esse que pdoe ser
     * 2 e 3, que seria uma opção para direcioná-lo a uma fila, fila essa utilizada para
     * iniciar o jogo com os client no modo de espera.
     * @param gameMode
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public void waitForPlayers(int gameMode) throws IOException, ClassNotFoundException{
        this.sendMessage(CSProtocol.PLAY + CSProtocol.SEPARATOR + gameMode);
        int id = (int) this.readMessage();
        this.player.setID(id);
        
        WaitingRoom room = new WaitingRoom(this, gameMode, this.input);
        Thread wait = new Thread(room);
        
        wait.start();
    }
    
    /**
     * O método listen recebe um IP para conectado ao multicasting e o prepara para receber
     * dados.
     * @param address
     * @throws IOException 
     */
    public void listen(InetAddress address) throws IOException{
        System.err.println("IP de multicast recebido: " + address);
        MulticastReceiver receiver = new MulticastReceiver(address, this.player);
        this.player.setAddress(address);
        receiver.start();
        
        this.isConnected = true;
        System.err.println("Conectado no grupo de multicast");
    }
    
    /**
     * Verifica se o CLient está logado;
     * @return 
     */
    public boolean isLogged(){
        return this.isLogged;
    }
    
    /**
     * VErifica se o cliente está conectado;
     * @return 
     */
    public boolean isConnected(){
        return this.isConnected;
    }
   
    
    /**
     * getPlayer retorna o Player;
     * @return 
     */
    public Player getPlayer() {
        return player;
    }

    
    
    /**
     * ReadMessage recebe uma mensagem do servidor.
     *
     */
    private Object readMessage() throws IOException, ClassNotFoundException {
        return this.input.readObject();
    }

    /**
     * SendMessage envia uma mensagem para o servidor.
     *
     * @param message message to be sent.
     * @throws IOException
     */
    private void sendMessage(Object message) throws IOException {
        this.output.reset();
        this.output.writeObject(message);
        this.output.flush();
    }
            
}
