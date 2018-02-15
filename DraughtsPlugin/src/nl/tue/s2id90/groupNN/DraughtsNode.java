package nl.tue.s2id90.groupNN;

import nl.tue.s2id90.draughts.DraughtsState;
import org10x10.dam.game.Move;

/**
 * A class representing a node in the search tree for the draughts game. 
 * An object of this class contains a draughts state. By adapting the draughts state
 * it becomes a representation of a different node in the search tree.
 * The get/setBestMove methods are intended for storing/retrieving the best move
 * as it has been computed for the draughts state in this node.
 * @author huub
 */
public class DraughtsNode {
    private final DraughtsState state;
    private Move move;
    public DraughtsNode(DraughtsState s) {
        this.state = s;
    }
    
    public DraughtsState getState() {
        return state;
    }
    
    public void setBestMove(Move m) {
        this.move = m;
    }
    
    public Move getBestMove() {
        return move;
    }
}
