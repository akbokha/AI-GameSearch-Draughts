package nl.tue.s2id90.game;

import java.util.List;

/**
 * class that keeps the state information of a Game.
 * @author huub
 * @param <Move>
 */
public interface GameState<Move> {

    /**
     * @return a list of valid moves in this state.
     */
    List<Move> getMoves();

    /**
     * @return whether or not there are any valid moves in this state
     */
    boolean isEndState();

    /**
     * applies move m in this state.
     * @param m move to be applied
     */
    void doMove(Move m);

    /**
     * undoes the effect of move m.
     * @param m
     */
    void undoMove(Move m);

    /**
     *
     * @return whether or not the white player is to move.
     */
    boolean isWhiteToMove();

    /**
     * resets state to initial game state.
     */
    void reset();  
    
    /**
     * clones this state.
     * @return a clone of this state.
     */
    GameState<Move> clone();
}