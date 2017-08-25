package Server.Model;

import Server.Util.Engine.Ranking;
import Server.Util.Exceptions.RankingLoadException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server entity, redirects a new client to one Client Host.
 * @author Allen Hichard
 * @author Daniel Andrade
 */
public class Server {
    private final int port;
    private final PlayerDistribution distributor;
    private final LoginEngine login;
    private final Ranking ranking;
    public final static String DATASET_LOCATION = "dataset.properties";
    
    public Server(int port) throws RankingLoadException, IOException {
        this.port = port;
        this.ranking = new Ranking();
        this.ranking.loadRankings("ranking.data", "top3.data");
        this.distributor = new PlayerDistribution();
        this.login = new LoginEngine();
    }
    
 
    
    /**
     * Run the server. Wait for a client and redirects it to the game thread.
     * @throws IOException 
     */
    public void run() throws IOException{
        System.out.print("Iniciando Servidor... ");
        ServerSocket server = new ServerSocket(this.port);
        System.out.println("100%\n");
        
        while(true){
            System.out.println("Aguardando por jogadores...");
            Socket client = server.accept();
            
            System.out.println("Novo jogador conectado: " + client.getInetAddress().getHostAddress());

            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
            
            PlayerSetup player = new PlayerSetup(login, distributor, ranking, ois, oos) ;
            player.start();
            
        }
    }
    
    
    public static void main(String[] args) throws IOException {
        new Server(8888).run();
    }
}
