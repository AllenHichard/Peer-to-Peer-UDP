/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Util.Protocols;

/**
 *
 * @author dcandrade
 */
public class P2PProtocol {

    public static final int ROULETTE_VALUE = 7; //Formato: ID_PLAYER-VALOR_ROLETA
    public static final int TRY_CHARACTER = 8; //Formato: ID_PLAYER-CARACTERE
    public static final int CONNECTION_DATA = 9; 
//Formato: NUM_PLAYERS -  ID_PLAYER1 - ID_PLAYERX - PALAVRAS 
    public final static String SEPARATOR = "-";
    public static final int NEXT_ROUND = 10;
    public static final int SKIP_PLAYER = 11;
}
