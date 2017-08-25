/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Util;

import java.util.StringTokenizer;

/**
 * Tuple to represent a 'database' entry, each entry is composed by a word and
 * the correspondent tip.
 * 
 * @author Allen Hichard
 * @author Daniel Andrade
 */
public class WordTuple {

    private final String word;
    private final String tip;

    public WordTuple(String line) {
        //Split's the 'line' String where '-' occurs.
        StringTokenizer token = new StringTokenizer(line, "-");
        this.word = token.nextToken(); //First occurrence
        this.tip = token.nextToken(); //Second occurrence
    }

    public WordTuple(String word, String tip) {
        this.word = word;
        this.tip = tip;
    }

    public String getWord() {
        return word;
    }

    public String getTip() {
        return tip;
    }

    @Override
    public String toString() {
        return this.word + " - " + this.tip;
    }

}
