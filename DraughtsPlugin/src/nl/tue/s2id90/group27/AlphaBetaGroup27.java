package nl.tue.s2id90.group27;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
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
        if (state.isEndState()) {
            return evaluate(state, true);
        }
        if (depth == 0) {
            return evaluate(state, false);
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
        if (state.isEndState()) {
            return evaluate(state, true);
        }
        if (depth == 0) {
            return evaluate(state, false);
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
    int evaluate(DraughtsState state, boolean endState) {
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

    // START BREAK THROUGHS (Abdel)
    float breakthroughFactor = 0.01f;
    
    ArrayList<Integer> blackProtectedBaseLineSpots = new ArrayList<>();
    ArrayList<Integer> blackUnprotectedBaseLineSpots = new ArrayList<>();
    ArrayList<Integer> whiteProtectedBaseLineSpots = new ArrayList<>();
    ArrayList<Integer> whiteUnprotectedBaseLineSpots = new ArrayList<>();
    
    for (int i = 1; i <= row; i++) {
        int pos = pieces[i];
        if(pos == DraughtsState.BLACKPIECE || pos == DraughtsState.BLACKKING){
            blackProtectedBaseLineSpots.add(i);
        } else {
            blackUnprotectedBaseLineSpots.add(i);
        }
    }
    
    for (int i = pieces.length - 1; i >= pieces.length - row ; i--) {
        int pos = pieces[i];
        if(pos == DraughtsState.WHITEPIECE || pos == DraughtsState.WHITEKING){
            whiteProtectedBaseLineSpots.add(i);
        } else {
            whiteUnprotectedBaseLineSpots.add(i);
        }
    }
    // result *= (1 + breakthroughFactor * ((blackUnprotectedBaseLineSpots.size() / 5) -  (whiteUnprotectedBaseLineSpots.size() / 5)));
    // END BREAK THROUGHS

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
    int [] blackPlayerMultipliers = new int []{ 
        1, 1, 1, 1, 1, // // can be done more egelantly, but this also fulfilss explanatory purposes.
        2, 2, 2, 2, 2,
        3, 3, 3, 3, 3,
        4, 4, 4, 4, 4,
        5, 5, 5, 5, 5,
        6, 6, 6, 6, 6,
        7, 7, 7, 7, 7,
        8, 8, 8, 8, 8,
        9, 9, 9, 9, 9,
        10, 10, 10, 10, 10
    };
    
    int [] whitePlayerMultipliers = IntStream.rangeClosed(1, blackPlayerMultipliers.length).map(i -> blackPlayerMultipliers[blackPlayerMultipliers.length-i]).toArray();
    
    int whitePlayersTempiScore, blackPlayersTempiScore;
    whitePlayersTempiScore = blackPlayersTempiScore = 0;
    
    for (int i = 1; i < pieces.length; i++) {
        int piece = pieces[i];
        if (piece == DraughtsState.WHITEPIECE || piece == DraughtsState.WHITEKING) {
            whitePlayersTempiScore += whitePlayerMultipliers[i - 1];
        }
        if (piece == DraughtsState.BLACKPIECE || piece == DraughtsState.BLACKKING) {
            blackPlayersTempiScore += blackPlayerMultipliers[i - 1];
        }
    }
    
    float tempiFactor = 0.05f;
    int maxTempi = 170; // has to be refined (maximum tempi difference --> mathematical proof?0
    int tempiDifference = whitePlayersTempiScore - blackPlayersTempiScore;
    float normalizeDifference = 1 - ((tempiDifference) / (maxTempi));
    result *= (1f + (tempiFactor * normalizeDifference));
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
           
            int[] diagonalIndex1 = new int[32];
            int[] diagonalIndex2 = new int[32];
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
    // END FORMATIONS 
    
    // START PROMOTION LINE (Abdel)
    /** 
     * Heuristic takes the following things into account:
     * 1. Aggregated distance of the pawns to promotion line;
     * 2. Number of unoccupied fields on promotion line.
     */
    float promotionLineFactor = .01f;
    
    int aggregatedDistanceWhitePlayer, aggregatedDistanceBlackPlayer;
    aggregatedDistanceWhitePlayer = aggregatedDistanceBlackPlayer = 0;
    
    // code readability purposes
    int [] distancesWhitePlayer = blackPlayerMultipliers;
    int [] distancesBlackPlayer = whitePlayerMultipliers;
    
    // aggregated distances of pawns to the promotion lines
    for (int i = 1; i < pieces.length; i++) {
        int pos = pieces[i];
        if (pos == DraughtsState.WHITEPIECE) {
            aggregatedDistanceWhitePlayer += (10 * distancesWhitePlayer[i - 1] - 1);
        }
        if (pos == DraughtsState.BLACKPIECE) {
            aggregatedDistanceBlackPlayer += (10 * distancesBlackPlayer[i - 1] - 1);
        }
    }
    
    int unoccupiedBaseLineSpotsWhitePlayer, unoccupiedBaseLineSpotsBlackPlayer;
    unoccupiedBaseLineSpotsWhitePlayer = unoccupiedBaseLineSpotsBlackPlayer = 0;
    
    // number of unucoopied fields on the promotion line
    for (int i = 1; i <= row; i++) {
        if (!(pieces[i] == DraughtsState.BLACKPIECE || pieces[i] == DraughtsState.BLACKKING)) {
            unoccupiedBaseLineSpotsBlackPlayer++;
        }
    }
    for (int i = pieces.length - 1; i >= pieces.length - row; i++) {
        if (!(pieces[i] == DraughtsState.WHITEPIECE || pieces[i] == DraughtsState.WHITEKING)) {
            unoccupiedBaseLineSpotsWhitePlayer++;
        }
    }
    // to-do: process aggregated dsitances into the result
    // result *= 1 + (unoccupiedBaseLineSpotsBlackPlayer / 5 - unoccupiedBaseLineSpotsWhitePlayer / 5);
   
    // END PROMOTION LINE

    // START QUIET POSITIONS (Optional)
            // @todo Implement this
            /*
             * Just a thought: when reaching the deepest level (depth == 1) then
             * (recursively) evaluate all moves which involve taking a piece, or
             * just the current situation if no preceding move takes a piece. 
             * This should be easy to detect, since if any move can take a 
             * piece, then it will do so, thus in order to check if any move
             * takes a piece we just evaluate any preceding move.
             * Note: Generating all moves might be computationally intensive,
             * hence we might want to check manually if it is possible to take
             * any piece before computing all moves.
             */
    // END QUIET POSITIONS
        
        return (int) result;
    }
}
