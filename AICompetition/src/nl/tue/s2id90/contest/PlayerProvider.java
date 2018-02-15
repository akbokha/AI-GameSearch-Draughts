package nl.tue.s2id90.contest;

import java.util.List;
import nl.tue.s2id90.game.Player;

/**
 * A Plugin that delivers a Player implementation.
 * @author huub
 * @param <P> Player
 */
public class PlayerProvider<P extends Player> implements Provider {
       
    /** final empty implementation.  **/
    @Override
    final public void start() { }

    /** final empty implementation. **/
    @Override
    final public void stop () { }

    /**
     * Get the value of name.
     * Override this method to give this plugin a different name.
     * @return the value of name; default "<unknown>".
     */
    @Override
    public String getName() {
        return getClass().getPackage().getName();
    }

    /**
     * @return a (computer) player
     */
    public List<P> getPlayers() {
        return null;
    }
}