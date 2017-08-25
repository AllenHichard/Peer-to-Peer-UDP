/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Util.Engine;

import Server.Util.Exceptions.RankingLoadException;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jacy Marques
 */
public class RankingTest {

    private Ranking r;

    public RankingTest() throws RankingLoadException {
        r = new Ranking();
    }

    @Test
    public void testLoadRankings() throws RankingLoadException {
        assertEquals(true, r.loadRankings("ranking.data", "top3.data"));
        assertEquals(false, r.loadRankings("ranking.data", "top3.data"));
    }

    @Test
    public void testTop3() throws RankingLoadException, IOException {
        r.loadRankings("ranking.data", "top3.data");
        assertEquals("allen", r.getTop3()[0].getUsername());
        assertEquals("dca", r.getTop3()[1].getUsername());
        assertEquals("daniel", r.getTop3()[2].getUsername());
        assertEquals(6000, r.getTop3()[0].getScore().intValue());
        assertEquals(3500, r.getTop3()[1].getScore().intValue());
        assertEquals(3000, r.getTop3()[2].getScore().intValue());
        r.refreshUserHighscore("khaick", 5000);
        assertEquals("khaick", r.getTop3()[1].getUsername());
        assertEquals(5000, r.getUserHighscore("khaick"));

    }

}
