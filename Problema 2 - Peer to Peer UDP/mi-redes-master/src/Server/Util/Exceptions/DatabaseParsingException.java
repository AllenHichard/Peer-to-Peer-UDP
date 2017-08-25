

package Server.Util.Exceptions;

import java.io.IOException;

/**
 *
 * @author Daniel Andrade
 */
public class DatabaseParsingException extends IOException{
    public DatabaseParsingException(String path){
        super("Error while parsing the dataset file. Check if the file is correct.\n"
                + "Path: "+path);
    }
}
