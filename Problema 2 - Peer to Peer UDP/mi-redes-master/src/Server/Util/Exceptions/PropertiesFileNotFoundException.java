

package Server.Util.Exceptions;

import java.io.IOException;

/**
 *
 * @author Daniel Andrade
 */
public class PropertiesFileNotFoundException extends IOException{
    public PropertiesFileNotFoundException(){
        super("dataset.properties was not found. Check the file path.");
    }
}
