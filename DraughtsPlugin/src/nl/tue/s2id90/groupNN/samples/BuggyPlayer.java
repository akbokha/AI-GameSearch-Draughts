package nl.tue.s2id90.groupNN.samples;


import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * A simple draughts player that always returns an invalid move.
 * @author huub
 */
public class BuggyPlayer extends DraughtsPlayer {

    public BuggyPlayer() {
        super(BuggyPlayer.class.getResource("smiley.png"));
    }
    @Override
    /** @return an illegal move **/
    public Move getMove(DraughtsState s) {
       return null;
    }

    @Override
    public Integer getValue() {
        return 0;
    }
}
