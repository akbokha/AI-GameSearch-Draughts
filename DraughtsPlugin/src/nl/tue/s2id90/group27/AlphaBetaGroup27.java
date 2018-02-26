package nl.tue.s2id90.group27;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.Collections;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface.
 * @author huub
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class AlphaBetaGroup27 extends DraughtsPlayer{
    private int bestValue=0;
    int maxSearchDepth;

    /** boolean that indicates that the GUI asked the player to stop thinking. */
    private boolean stopped;

    public AlphaBetaGroup27(int maxSearchDepth) {
        super("best.png"); // ToDo: replace with your own icon
        this.maxSearchDepth = maxSearchDepth;
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
        float result = 1000000;
        final int KINGVALUE = 3;
        
        // START COUNTING PIECES
        int [] pieces  = state.getPieces();
        float whiteValue = 0f;
        float blackValue = 0f;

        for (int i = 1; i < pieces.length; i++) {
            switch(pieces[i]) {
                case DraughtsState.WHITEPIECE:
                    whiteValue++;
                    break;
                case DraughtsState.WHITEKING:
                    whiteValue += KINGVALUE;
                    break;
                case DraughtsState.BLACKPIECE:
                    blackValue++;
                    break;
                case DraughtsState.BLACKKING:
                    blackValue += KINGVALUE;
                    break;     
            }
        }
        result *= whiteValue / (whiteValue + blackValue);
        // END COUNTING PIECES
        
        // START BALANCED POSITIONS (Adriaan)
        int[] balanceScores = {0, 0};
        for (int i = 1; i < pieces.length; i++) {
            int pieceValue = 0;
            int index = 0;
            switch(pieces[i]) {
                case DraughtsState.WHITEPIECE:
                    pieceValue = 1;
                    index = 0;
                    break;
                case DraughtsState.WHITEKING:
                    pieceValue = KINGVALUE;
                    index = 0;
                    break;
                case DraughtsState.BLACKPIECE:
                    pieceValue = 1;
                    index = 1;
                    break;
                case DraughtsState.BLACKKING:
                    pieceValue = KINGVALUE;
                    index = 1;
                    break; 
            }
                
            // Decide if the piece is on the left or the right side of the board.
            int relativePosition = (i - 1) % 10;
            if (relativePosition >= 5 == (2 < relativePosition && relativePosition <= 8)) { // Left
                balanceScores[index] += pieceValue;
            } else { // Right
                balanceScores[index] -= pieceValue;
            }
        }
        
        // Take the absolute value of both balance scores
        if (balanceScores[0] < 0) {
            balanceScores[0] = -balanceScores[0];
        }
        if (balanceScores[1] < 0) {
            balanceScores[1] = -balanceScores[1];
        }
        
        /*
        * Vector: (1 - 20% * balanceScore[white] / #white) * (1 + 20% * balanceScore[black] / # black)
        * Rational: A high balance score implies an unbalanced board for the
        *       corresponding color. Hence a high balance score for white should
        *       negatively impact the score, and a high balance score for black
        *       is positive for white and should therefore increase the score.
        */
        result *= (1 - .2f * ((balanceScores[0] / whiteValue) + (balanceScores[1] / blackValue)));
        // END BALANCED POSITIONS
        
        // START OUTPOSTS (Abdel)
        
        /*  The result (float) checks/quantifies if pieces on 6 <= row < 10 are defended
            King moves/situations are excluded from the defending part
        */
        int whiteDefendedOutpostPieces, whiteOutpostPieces, blackDefendedOutpostPieces, blackOutpostPieces;
        whiteDefendedOutpostPieces = whiteOutpostPieces = blackDefendedOutpostPieces = blackOutpostPieces = 0;
        
        int whiteLeftDefender, whiteRightDefender, blackLeftDefender, blackRightDefender;
        whiteLeftDefender = blackRightDefender = 4;
        whiteRightDefender = blackLeftDefender = 5;
        
        int skipLastRow = 5; // for explanatory purposes
        
        for (int i = (1 + skipLastRow); i < 26; i++) { // white's perspective
            int pos = pieces[i];
            if ((pos == DraughtsState.WHITEPIECE) || (pos == DraughtsState.WHITEKING)) {
                whiteOutpostPieces += 4; // each outpost piece can be defended from two sides (excl. king moves)
                if (((pos % 10) == 5) || (pos % 10) == 6){ // is placed on one of the edges
                    whiteDefendedOutpostPieces += 4;
                } else { // check if it is defended
                    if (((pos + whiteLeftDefender) == DraughtsState.WHITEPIECE) || ((pos + whiteLeftDefender) == DraughtsState.WHITEKING)) {
                        whiteDefendedOutpostPieces++; // is defended from the left
                    }
                    if (((pos + whiteRightDefender) == DraughtsState.WHITEPIECE) || ((pos + whiteRightDefender) == DraughtsState.WHITEKING)) {
                        whiteDefendedOutpostPieces++; // is defended from the right
                    }
                    if (((pos - whiteLeftDefender) == DraughtsState.WHITEPIECE) || ((pos - whiteLeftDefender) == DraughtsState.WHITEKING)) {
                        whiteDefendedOutpostPieces++; // is defended from the left
                    }
                    if (((pos - whiteRightDefender) == DraughtsState.WHITEPIECE) || ((pos - whiteRightDefender) == DraughtsState.WHITEKING)) {
                        whiteDefendedOutpostPieces++; // is defended from the right
                    }
                }
            }
        }
        
        for (int i = 26; i < (pieces.length - skipLastRow); i++) { // black's perpective
            int pos = pieces[i];
            if ((pos == DraughtsState.BLACKPIECE) || (pos == DraughtsState.BLACKKING)) {
                blackOutpostPieces += 4; // each outpost piece can be defended from two sides (excl. king moves)
                if (((pos % 10) == 5) || (pos % 10) == 6){ // is placed on one of the edges
                    blackDefendedOutpostPieces += 4;
                } else { // check if it is defended
                    if (((pos - blackLeftDefender) == DraughtsState.BLACKPIECE) || ((pos - blackLeftDefender) == DraughtsState.BLACKKING)) {
                        blackDefendedOutpostPieces++; // is defended from the left
                    }
                    if (((pos - blackRightDefender) == DraughtsState.BLACKPIECE) || ((pos - blackRightDefender) == DraughtsState.BLACKKING)) {
                        blackDefendedOutpostPieces++; // is defended from the right
                    }
                    if (((pos + blackLeftDefender) == DraughtsState.BLACKPIECE) || ((pos + blackLeftDefender) == DraughtsState.BLACKKING)) {
                        blackDefendedOutpostPieces++; // is defended from the left
                    }
                    if (((pos + blackRightDefender) == DraughtsState.BLACKPIECE) || ((pos + blackRightDefender) == DraughtsState.BLACKKING)) {
                        blackDefendedOutpostPieces++; // is defended from the right
                    }
                }
            }
        }
        
        float outpostResult = (whiteDefendedOutpostPieces / whiteOutpostPieces) - (blackDefendedOutpostPieces / blackOutpostPieces);
        outpostResult = (outpostResult == 0) ? 1f : outpostResult;
        
        // END OUTPOSTS
        
        // START BREAK THROUGHS (Abdel)
            // @todo Implement this
        // END BREAK THROUGHS
        
        // START TEMPI (Abdel)
            // @todo Implement this
        // END TEMPI
        
        // START FORMATIONS (Adriaan)
            // @todo Implement this
        // END FORMATIONS
        
        // START QUIET POSITIONS (Optional)
            // @todo Implement this
        // END QUIET POSITIONS
        
        return (int) result;
    }
}
