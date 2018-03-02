package nl.tue.s2id90.group27;

import evolve.Properties.AbstractGene;
import evolve.Properties.FloatGene;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface.
 * @author huub
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class AlphaBetaGroup27 extends DraughtsPlayer implements evolve.Evolvable {
    private int bestValue=0;
    int maxSearchDepth;
    
    Map<StateInfo, Integer> transposTable;
    Map<StateInfo, Move> bestMoves;
    Map<String, AbstractGene> genome;

    /** boolean that indicates that the GUI asked the player to stop thinking. */
    private boolean stopped;

    public AlphaBetaGroup27(int maxSearchDepth) {
        super("best.png"); // ToDo: replace with your own icon
        this.maxSearchDepth = maxSearchDepth;
        
        genome = new HashMap(10);
        genome.put("king-value", (new FloatGene()).setMax(4f).setMin(2f));
    }
    
    @Override
    public Map<String, AbstractGene> getGenome() {
        return genome;
    }

    @Override
    public AbstractGene getGene(String name) throws IllegalArgumentException {
        AbstractGene gene = genome.get(name);
        
        if (gene == null) 
            throw new IllegalArgumentException("Gene " + name + " not found");
        
        return gene;
    }

    @Override
    public void setGene(String name, AbstractGene gene) throws IllegalArgumentException {
        if (null == genome.get(name)) 
            throw new IllegalArgumentException("Gene " + name + " not found");
        
        genome.replace(name, gene);
    }

    @Override public Move getMove(DraughtsState s) {
        Move bestMove = null;
        bestValue = 0;
        DraughtsNode node = new DraughtsNode(s);    // the root of the search tree
        transposTable = new HashMap<>();
        bestMoves = new HashMap<>();
        
        int i = 1;
        try {
            for (; i <= maxSearchDepth; i++) {
                // compute bestMove and bestValue in a call to alphabeta
                bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, i);

                // store the bestMove found uptill now
                // NB this is not done in case of an AIStoppedException in alphaBeta()
                bestMove  = node.getBestMove();
            }
        } catch (AIStoppedException ex) {}
        // print the results for debugging reasons
//        System.err.format(
//            "%s: depth= %2d, best move = %5s, value=%d\n", 
//            this.getClass().getSimpleName(),i, bestMove, bestValue
//        );
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
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        DraughtsState state = node.getState();
        StateInfo stateInfo = new StateInfo(state.getPieces(), depth, false); //blackPlayer == minimizing player
        if (state.isEndState()) {
            if (transposTable.containsKey(stateInfo)) {
                return transposTable.get(stateInfo);
            }
            int eval = evaluate(state, true);
            transposTable.put(stateInfo, eval);
            return eval;
        }
        if (depth == 0) {
            if (transposTable.containsKey(stateInfo)) {
                return transposTable.get(stateInfo);
            }
            int eval = evaluate(state, false);
            transposTable.put(stateInfo, eval);
            return eval;
        }
        List<Move> moves = state.getMoves();
        Collections.shuffle(moves);
        if (bestMoves.containsKey(stateInfo)) {
            moves.add(0, bestMoves.get(stateInfo)); // will be tried first in subsequent iteration
        }
        for (Move move : moves) {
            state.doMove(move);
            int result = alphaBetaMax(node, alpha, beta, depth - 1, false);
            state.undoMove(move);
            if (result < beta) {
                beta = result;
                if (isRoot) {
                    bestMoves.put(stateInfo, move);
                    node.setBestMove(move);
                }
            }
            if (beta <= alpha) {
                return beta;
            }
        }
        return beta;
     }

    int alphaBetaMax(DraughtsNode node, int alpha, int beta, int depth, boolean isRoot)
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        DraughtsState state = node.getState();
        StateInfo stateInfo = new StateInfo(state.getPieces(), depth, true); // whitePlayer == maximizing player
        if (state.isEndState()) {
            if (transposTable.containsKey(stateInfo)) {
                return transposTable.get(stateInfo);
            }
            int eval = evaluate(state, true);
            transposTable.put(stateInfo, eval);
            return eval;
        }
        if (depth == 0) {
            if (transposTable.containsKey(stateInfo)) {
                return transposTable.get(stateInfo);
            }
            int eval = evaluate(state, false);
            transposTable.put(stateInfo, eval);
            return eval;
        }
        List<Move> moves = state.getMoves();
        Collections.shuffle(moves);
        if (bestMoves.containsKey(stateInfo)) {
            moves.add(0, bestMoves.get(stateInfo)); // will be tried first in subsequent iteration
        }
        for (Move move : moves) {
            state.doMove(move);
            int result = alphaBetaMin(node, alpha, beta, depth - 1, false);
            state.undoMove(move);
            if (result > alpha) {
                alpha = result;
                if (isRoot) {
                    bestMoves.put(stateInfo, move);
                    node.setBestMove(move);
                }
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
    int evaluate(DraughtsState state, boolean endState) {
        float result = 1000000;
        final int KINGVALUE = (int) genome.get("king-value").getValue();
        
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
        
        if (endState) {
            return (int) (result * 10 * (whiteValue - blackValue));
        }
        
        result *= whiteValue / (whiteValue + blackValue);
    // END COUNTING PIECES
        
    // START BALANCED POSITIONS (Adriaan)
        float[] balanceScores = {0, 0};
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
        * Vector: (1 - 10% * balanceScore[white] / #white) * (1 + 20% * balanceScore[black] / # black)
        * Rational: A high balance score implies an unbalanced board for the
        *       corresponding color. Hence a high balance score for white should
        *       negatively impact the score, and a high balance score for black
        *       is positive for white and should therefore increase the score.
        */
        result *= (1 - .05f * ((balanceScores[0] / whiteValue) - (balanceScores[1] / blackValue)));
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
        
        int row = 5; // for explanatory purposes
        
        for (int i = (1 + row); i < 26; i++) { // white's perspective
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
        
        for (int i = 26; i < (pieces.length - row); i++) { // black's perpective
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
        float outpostResult = 1f;
        float outpostFactor = 0.01f;
        int undefendedBlackPieces = blackOutpostPieces - blackDefendedOutpostPieces;
        int undefendedWhitePieces = whiteOutpostPieces - whiteDefendedOutpostPieces;
        
        if (whiteOutpostPieces == 0 && blackOutpostPieces != 0) {
            outpostResult += outpostFactor *(undefendedBlackPieces / blackOutpostPieces);
        } else if (whiteOutpostPieces != 0 && blackOutpostPieces == 0) {
            outpostResult -= outpostFactor *(undefendedWhitePieces / whiteOutpostPieces);
        } else if (whiteOutpostPieces != 0 && blackOutpostPieces != 0) {
            outpostResult += outpostFactor *((undefendedBlackPieces / blackOutpostPieces) - (undefendedWhitePieces / whiteOutpostPieces));
        }
        result *= outpostResult;
        
    // END OUTPOSTS

    // START TEMPI (Abdel)
        /** MOTIVATION and EXPLANATION
         * In general it is important to advance your pieces more than the opponent.
         * This is going to be implemented in the following way:
         * 
         * An 1D-array with incremental multipliers for each advancing row (seen from the baseline of the player)
         * We multiply the positions with the respective multiplier of the row the piece is in.
         * At the end one will have a value representing the advancement of the pieces. 
         * The black player's number is then subtracted from the white player's number and multiplied by a certain factor.
         */
        double [] positionMultipliers = new double []{ 
            1.0, 1.0, 1.0, 1.0, 1.0, // // can be done more egelantly, but this also fullfilss explanatory purposes.
            1.9, 1.9, 1.9, 1.9, 1.9,
            2.7, 2.7, 2.7, 2.7, 2.7,
            3.4, 3.4, 3.4, 3.4, 3.4,
            4.0, 4.0, 4.0, 4.0, 4.0,
            4.5, 4.5, 4.5, 4.5, 4.5,
            4.9, 4.9, 4.9, 4.9, 4.9,
            5.2, 5.2, 5.2, 5.2, 5.2,
            5.4, 5.4, 5.4, 5.4, 5.4,
            5.5, 5.5, 5.5, 5.5, 5.5,
        };

        double whitePlayersTempiScore, blackPlayersTempiScore;
        whitePlayersTempiScore = blackPlayersTempiScore = 0.0;

        for (int i = 1; i < pieces.length; i++) {
            int piece = pieces[i];
            if (piece == DraughtsState.WHITEPIECE || piece == DraughtsState.WHITEKING) {
                whitePlayersTempiScore += positionMultipliers[i - 1];
            }
            if (piece == DraughtsState.BLACKPIECE || piece == DraughtsState.BLACKKING) {
                blackPlayersTempiScore += positionMultipliers[positionMultipliers.length - i - 1];
            }
        }

        float tempiFactor = 0.05f;
        double tempiDifference = whitePlayersTempiScore - blackPlayersTempiScore;
        result *= 1f + tempiFactor * (tempiDifference / (whitePlayersTempiScore + blackPlayersTempiScore));
    // END TEMPI

    // START FORMATIONS (Adriaan)
        /*
         * Remapped all indexes for a more efficient storage, indexes of other
         * (diagonally positioned) cells in the original form can be retrieved
         * in the following matter:
         * Column  :  C = (i mod 4) + 1
         * Row     :  R = floor(i / 4)   
         * Top left     = 5R + C
         * Top right    = 5R + C + 1
         * Bottom left  = 5(R + 2) + C     = 5R + C + 10
         * Bottom right = 5(R + 2) + C + 1 = 5R + C + 11
         * Self         = if R is even : 5(R + 1) + C + 2 = 5R + C + 7
         *                if R is odd  : 5(R + 1) + C + 1 = 5R + C + 6
         *   col  0  1  2  3  4  5  6  7  8  9
         *  row ------------------------------
         *   0  |    0x    0x    0x    0x    0x
         *      |
         *   1  | 0x    00    01    02    03
         *      |
         *   2  |    04    05    06    07    0x
         *      |
         *   3  | 0x    08    09    10    11
         *      |
         *   4  |    12    13    14    15    0x
         *      |
         *   5  | 0x    16    17    18    19
         *      |
         *   6  |    20    21    22    23    0x
         *      |
         *   7  | 0x    24    25    26    27
         *      |
         *   8  |    28    29    30    31    0x
         *      |
         *   9  | 0x    0x    0x    0x    0x
         */
        int whiteGates = 0;
        int blackGates = 0;

        // Store the diagonal index, 1 and -1 resp. denotes a row of 3 white 
        // and black pieces, else 0.
        int[] diagonalIndex1 = new int[32]; // Combines the index to the top left and bottom right.
        int[] diagonalIndex2 = new int[32]; // Combines the index to the top right and bottom left.
        for (int i = 0; i < 32; i++) {
            int c = (i % 4) + 1;
            int r = i / 4;
            int tl = 5*r + c;
            int tr = 5*r + c + 1;
            int bl = 5*r + c + 10;
            int br = 5*r + c + 11;
            int sl; // Self
            if (r % 2 == 0) { // Even
                sl = 5*r + c + 7;
            } else {
                sl = 5*r + c + 6;
            }

            if (    
                   (pieces[tl] == DraughtsState.WHITEKING || pieces[tl] == DraughtsState.WHITEPIECE)
                && (pieces[sl] == DraughtsState.WHITEKING || pieces[sl] == DraughtsState.WHITEPIECE)
                && (pieces[br] == DraughtsState.WHITEKING || pieces[br] == DraughtsState.WHITEPIECE)
            ) {
                diagonalIndex1[i] = 1;
            } else if (
                   (pieces[tl] == DraughtsState.BLACKKING || pieces[tl] == DraughtsState.BLACKPIECE)
                && (pieces[sl] == DraughtsState.BLACKKING || pieces[sl] == DraughtsState.BLACKPIECE)
                && (pieces[br] == DraughtsState.BLACKKING || pieces[br] == DraughtsState.BLACKPIECE)
            ) {
                diagonalIndex1[i] = -1;
            }

            if (    
                   (pieces[tr] == DraughtsState.WHITEKING || pieces[tr] == DraughtsState.WHITEPIECE)
                && (pieces[sl] == DraughtsState.WHITEKING || pieces[sl] == DraughtsState.WHITEPIECE)
                && (pieces[bl] == DraughtsState.WHITEKING || pieces[bl] == DraughtsState.WHITEPIECE)
            ) {
                diagonalIndex2[i] = 1;
            } else if (
                   (pieces[tr] == DraughtsState.BLACKKING || pieces[tr] == DraughtsState.BLACKPIECE)
                && (pieces[sl] == DraughtsState.BLACKKING || pieces[sl] == DraughtsState.BLACKPIECE)
                && (pieces[bl] == DraughtsState.BLACKKING || pieces[bl] == DraughtsState.BLACKPIECE)
            ) {
                diagonalIndex2[i] = -1;
            }

            if (diagonalIndex1[i] != 0 && diagonalIndex1[i] == diagonalIndex2[i]) {
                if (diagonalIndex1[i] == 1) { // White gate
                    whiteGates += 1;
                } else { // Black gate
                    blackGates += 1;
                }
            }
        }
        if (whiteGates + blackGates > 0) {
            result *= 1 + .05f * (whiteGates - blackGates) / (float) (whiteGates + blackGates);
        }
        
        int deltaWhiteSquares = 0; // The amount of squares white posses more than black.
        for (int i = 0; i < 23; i++) {
            if (
                i % 4 != 3 // Skip indices in the last index of each row, since they cannot make squares in combination with elements on the lower right.
                && diagonalIndex2[i] == diagonalIndex2[i+5-(i%8)/4] // Check if the first index if equal to the one on the borrom right
                && diagonalIndex2[i] == diagonalIndex2[i+9] // Check if the first index is equal to the one two positions to the lower right.
            ) {
                deltaWhiteSquares += diagonalIndex2[i];
            }
        }
        result *= 1 + .05f * deltaWhiteSquares;
    // END FORMATIONS 
    
    // BEGIN COMPACTNESS
    
    // END COMPACTNESS
    
        return (int) result;
    }
    
    private class StateInfo {
        int [] pieces;
        int depth;
        boolean whitePlayer;
        
        StateInfo(int[] pieces, int depth, boolean whitePlayer) {
            this.pieces = pieces;
            this.depth = depth;
            this.whitePlayer = whitePlayer;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (this.getClass() != o.getClass()) {
                return false;
            }
            if (this == o){
                return true;
            }
            StateInfo obj = (StateInfo) o;
            return this.depth == obj.depth && Arrays.equals(this.pieces, obj.pieces) &&
                    this.whitePlayer == obj.whitePlayer;
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(this.pieces) + 2 * this.depth + Boolean.compare(this.whitePlayer, false);
        }
    }
    
    @Override
    public String toString() {
        return "["+hashCode()+"]";
    }
}
