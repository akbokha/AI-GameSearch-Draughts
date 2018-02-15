package nl.tue.s2id90.contest;

/**
 *
 * @author huub
 * @param <Move>
 */
public interface HumanMoveListener<Move> {

    /**
     * This method is called when a human plays a move.
     * @param m played move
     */
    void onMove(Move m);
}
