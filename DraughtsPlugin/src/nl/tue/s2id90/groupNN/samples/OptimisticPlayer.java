package nl.tue.s2id90.groupNN.samples;


import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * A simple draughts player that plays the first moves that comes to mind
 * and values all moves with value 0.
 * @author huub
 */
public class OptimisticPlayer extends DraughtsPlayer {

    public OptimisticPlayer() {
        super(UninformedPlayer.class.getResource("optimist.png"));
    }
    @Override
    /** @return a random move **/
    public Move getMove(DraughtsState s) {
        List<Move> moves = s.getMoves();
        return moves.get(0);
    }

    @Override
    public Integer getValue() {
        return 0;
    }
}
