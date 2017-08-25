package Server.Util.WordManager;

import Server.Util.Exceptions.DatabaseParsingException;
import Server.Util.Exceptions.PropertiesFileNotFoundException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.Random;

/**
 * Class responsible to manage the databaseFile.
 *
 * @see WordTuple
 * @author Allen Hichard
 * @author Daniel Andrade
 */
public class WordManager {

    private final String databaseFile; //File containing all words and tips in the format "word - tip"
    private final int cacheSize; //Amount of words to keep in ram.
    private final int datasetNumberOfWords; //Dataset's size.
    private final List<WordTuple> cache; //Tuple's cache.

    /**
     * Loads the databaseFile properties.
     *
     * @param datasetProperties location of the dataset properties file
     * @throws PropertiesFileNotFoundException if the properties file was not
     * found
     */
    public WordManager(String datasetProperties) throws PropertiesFileNotFoundException {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(datasetProperties));
        } catch (IOException ex) {
            throw new PropertiesFileNotFoundException();
        }
        this.cacheSize = Integer.parseInt(properties.getProperty("cacheSize"));
        this.datasetNumberOfWords = Integer.parseInt(properties.getProperty("datasetSize"));
        this.databaseFile = properties.getProperty("datasetLocation");
        this.cache = new ArrayList<>();
    }

    /**
     * Reads the databaseFile and load 'this.cacheSize' entries to the cache.
     *
     * @throws DatabaseParsingException if there's a error while parsing the dataset.
     */
    private void fillCache() throws DatabaseParsingException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.databaseFile)));
            Random random = new Random();
            List<Integer> numbers = new ArrayList<>();

            for (int i = 0; i < this.cacheSize; i++) {
                numbers.add(random.nextInt(this.datasetNumberOfWords));
            }

            Collections.sort(numbers);

            String line;
            Iterator<Integer> numberIterator = numbers.iterator();
            int number = numberIterator.next();

            for (int i = 0; (line = reader.readLine()) != null; i++) {
                if (i == number) {
                    if (numberIterator.hasNext()) {
                        number = numberIterator.next();
                        this.cache.add(new WordTuple(line));
                    } else {
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            throw new DatabaseParsingException(this.databaseFile);
        }
    }

    /**
     * Get a databaseFile entry
     *
     * @return a WordTuple containg a word and the correspondent tip.
     * @throws DatabaseParsingException
     */
    public WordTuple getTuple() throws DatabaseParsingException {
        if (this.cache.isEmpty()) {
            this.fillCache();
        }

        Random random = new Random();
        return this.cache.remove(random.nextInt(this.cache.size()));
    }
    
    public Queue<WordTuple> getTuples(int amount) throws DatabaseParsingException{
        Queue<WordTuple> queue = new LinkedList<>();
        for(int i=0; i<amount; i++){
            queue.add(this.getTuple());
        }
        return queue;
    }

    public static void main(String[] args) throws IOException {
        WordManager f = new WordManager("dataset.properties");

        System.out.println(f.getTuple());
        System.out.println(f.getTuple());
        System.out.println(f.getTuple());
        System.out.println(f.getTuple());
    }
}
