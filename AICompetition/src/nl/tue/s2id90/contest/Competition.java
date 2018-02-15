package nl.tue.s2id90.contest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import nl.tue.s2id90.game.Game;
import nl.tue.s2id90.game.Player;

/**
 *
 * @author huub
 */
public class Competition {

    private List<Player> players = new ArrayList<>();
    private static final Logger LOG = Logger.getLogger(Competition.class.getName());
    private final List<Game> schedule;

    /**
     *
     * @param players
     */
    public Competition(List<? extends Player> players) {
        int counter = 0;
        for (Player player : players) {
            this.players.add(player);
            counter++;
        }
        if (counter == 0) {
            LOG.severe("no players were found!!");
        }
        schedule = createSchedule();
    }

    /**
     * @returns a randomizes list of the games in a full competition. *
     */
    private List<Game> createSchedule() {
        List<Game> games = new ArrayList<>();
        for (Player white : players) {
            for (Player black : players) {
                if (white != black) {
                    games.add(new Game(white, black));
                }
            }
        }
        Collections.shuffle(games);
        return games;
    }
    
    public List<Game> getSchedule() {
        return schedule;
    }
}
