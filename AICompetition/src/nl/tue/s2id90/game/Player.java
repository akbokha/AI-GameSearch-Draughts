package nl.tue.s2id90.game;

import nl.tue.s2id90.contest.util.Identity;

/**
 *
 * @author huub
 * @param <Move>
 * @param <State>
 */
public interface Player<Move,State extends GameState<Move>>  extends Identity
{
    /** computes a valid move in the given game state s. This method should be prepared
     * to immediately return a Move when stop() has been called.
     * @param s game state
     * @return a valid move in State s.
     * @see stop()
     **/
    Move getMove(State s);
    
    /** @return the computed value of the last Move. Returns null if that value
      * is not available.
     **/
    Integer getValue();
    
    /** as a result of this call the Player should as soon as possible
     * return a value in getMove().
     */
    void stop();
    
    /** @return whether or not this is a human player **/
    boolean isHuman();
}