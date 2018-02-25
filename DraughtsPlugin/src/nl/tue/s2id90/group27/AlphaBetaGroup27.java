package nl.tue.s2id90.group27;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.Collections;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import nl.tue.s2id90.group27.evaluate.CompositeEvaluater;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface.
 * @author huub
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class AlphaBetaGroup27 extends DraughtsPlayer{
    private int bestValue=0;
    private CompositeEvaluater evaluater;
    int maxSearchDepth;

    /** boolean that indicates that the GUI asked the player to stop thinking. */
    private boolean stopped;

    public AlphaBetaGroup27(int maxSearchDepth) {
        super("best.png"); // ToDo: replace with your own icon
        this.maxSearchDepth = maxSearchDepth;
        evaluater = (new CompositeEvaluater());
    }

    @Override public Move getMove(DraughtsState s) {
        Move bestMove = null;
        bestValue = 0;
        DraughtsNode node = new DraughtsNode(s);    // the root of the search tree
        try {
            for (int i = 1; i <= maxSearchDepth; i++) {
                // compute bestMove and bestValue in a call to alphabeta
                bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, i);

                // store the bestMove found uptill now
                // NB this is not done in case of an AIStoppedException in alphaBeta()
                bestMove  = node.getBestMove();
                
                // print the results for debugging reasons
                System.err.format(
                    "%s: depth= %2d, best move = %5s, value=%d\n", 
                    this.getClass().getSimpleName(),i, bestMove, bestValue
                );
            }
        } catch (AIStoppedException ex) {}
        if (bestMove==null) {
            System.err.println("no valid move found!");
            return getRandomValidMove(s);
        } else {
            return bestMove;
        }
    }

    /** This method's return value is displayed in the AICompetition GUI.
     *
     * @return the value for the draughts state s as it is computed in a call to getMove(s).
     */
    @Override public Integer getValue() {
       return bestValue;
    }

    /** Tries to make alphabeta search stop. Search should be implemented such that it
     * throws an AIStoppedException when boolean stopped is set to true;
    **/
    @Override public void stop() {
       stopped = true;
    }

    /** returns random valid move in state s, or null if no moves exist. */
    Move getRandomValidMove(DraughtsState s) {
        List<Move> moves = s.getMoves();
        Collections.shuffle(moves);
        return moves.isEmpty()? null : moves.get(0);
    }

    /** Implementation of alphabeta that automatically chooses the white player
     *  as maximizing player and the black player as minimizing player.
     * @param node contains DraughtsState and has field to which the best move can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the computed value of this node
     * @throws AIStoppedException
     **/
    int alphaBeta(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException
    {
        if (node.getState().isWhiteToMove()) {
            return alphaBetaMax(node, alpha, beta, depth, true);
        } else  {
            return alphaBetaMin(node, alpha, beta, depth, true);
        }
    }

    /** Does an alphabeta computation with the given alpha and beta
     * where the player that is to move in node is the minimizing player.
     *
     * <p>Typical pieces of code used in this method are:
     *     <ul> <li><code>DraughtsState state = node.getState()</code>.</li>
     *          <li><code> state.doMove(move); .... ; state.undoMove(move);</code></li>
     *          <li><code>node.setBestMove(bestMove);</code></li>
     *          <li><code>if(stopped) { stopped=false; throw new AIStoppedException(); }</code></li>
     *     </ul>
     * </p>
     * @param node contains DraughtsState and has field to which the best move can be assigned.
     * @param alpha
     * @param beta
     * @param depth  maximum recursion Depth
     * @return the compute value of this node
     * @throws AIStoppedException thrown whenever the boolean stopped has been set to true.
     */
     int alphaBetaMin(DraughtsNode node, int alpha, int beta, int depth, boolean isRoot)
            throws AIStoppedException {
        if (stopped) { stopped = false; System.err.println("stop"); throw new AIStoppedException(); }
        DraughtsState state = node.getState();
        if (depth == 0 || state.isEndState()) {
            return evaluate(state);
        }
        for (Move move : state.getMoves()) {
            state.doMove(move);
            int result = alphaBetaMax(node, alpha, beta, depth - 1, false);
            state.undoMove(move);
            if (result < beta) {
                beta = result;
                if (isRoot)
                    node.setBestMove(move);
            }
            if (beta <= alpha) {
                return beta;
            }
        }
        return beta;
     }

    int alphaBetaMax(DraughtsNode node, int alpha, int beta, int depth, boolean isRoot)
            throws AIStoppedException {
        if (stopped) { stopped = false; System.err.println("stop"); throw new AIStoppedException(); }
        DraughtsState state = node.getState();
        if (depth == 0 || state.isEndState()) {
            return evaluate(state);
        }
        for (Move move : state.getMoves()) {
            state.doMove(move);
            int result = alphaBetaMin(node, alpha, beta, depth - 1, false);
            state.undoMove(move);
            if (result > alpha) {
                alpha = result;
                if (isRoot)
                    node.setBestMove(move);
            }
            if (alpha >= beta) {
                return alpha;
            }
        }
        return alpha;
    }

    /**
     * A method that evaluates the given state.
     * Note: White wants to maximize this function, black to minimize.
     */
    // ToDo: write an appropriate evaluation function
    int evaluate(DraughtsState state) {
        int [] pieces  = state.getPieces();
        int whiteValue = 0;
        int blackValue = 0;

        /* @todo Use cases instead of double loop */
        for (int i = 1; i < pieces.length; i++) {
            if (pieces[i] == DraughtsState.WHITEPIECE) {
                whiteValue ++;
            } else if (pieces[i] == DraughtsState.WHITEKING){
                whiteValue += 3;
            }
        }

        for (int i = 1; i < pieces.length; i++) {
            if (pieces[i] == DraughtsState.BLACKPIECE) {
                blackValue ++;
            } else if (pieces[i] == DraughtsState.BLACKKING){
                blackValue += 3;
            }
        }
        
        return (int) ((whiteValue - blackValue) * evaluater.evaluate(state));
    }
}
