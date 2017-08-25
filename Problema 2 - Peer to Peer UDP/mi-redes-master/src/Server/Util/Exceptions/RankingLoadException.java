

package Server.Util.Exceptions;

import java.io.IOException;

/**
 *
 * @author Daniel Andrade
 */
public class RankingLoadException extends IOException{
    public RankingLoadException(String path){
        super("Error while loading ranking. Check the path below:\n"
                + path);
    }
}
