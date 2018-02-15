package nl.tue.s2id90.draughts;

import java.util.ArrayList;
import java.util.List;
import nl.tue.s2id90.game.GameState;
import org10x10.dam.game.BoardState;
import org10x10.dam.game.Move;
import org10x10.dam.game.MoveGenerator;
import org10x10.dam.game.MoveGeneratorFactory;

/**
 * <blockquote><pre>
 *   col  0  1  2  3  4  5  6  7  8  9
 *  row ------------------------------
 *   0  |    01    02    03    04    05
 *      |
 *   1  | 06    07    08    09    10
 *      |
 *   2  |    11    12    13    14    15
 *      |
 *   3  | 16    17    18    19    20
 *      |
 *   4  |    21    22    23    24    25
 *      |
 *   5  | 26    27    28    29    30
 *      |
 *   6  |    31    32    33    34    35
 *      |
 *   7  | 36    37    38    39    40
 *      |
 *   8  |    41    42    43    44    45
 *      |
 *   9  | 46    47    48    49    50
 * </pre></blockquote>
 * @author huub
 */
public class DraughtsState implements GameState<Move> {
    /**
     * Represents an empty field on a board.
     */
    final public static int EMPTY = 0;

    /**
     * Represents a white piece.
     **/
    final public static int WHITEPIECE = 1;

    /**
     * Represents a black piece.
     **/
    final public static int BLACKPIECE = 2;

    /**
     * Represents a white king.
     **/
    final public static int WHITEKING = 3;

    /**
     * Represents a black king.
     **/
    final public static int BLACKKING = 4;

    /**
     * Represents a white (non-playing) field on a board.
     **/
    final public static int WHITEFIELD = 5;
    
    private final BoardState bs;
    private final MoveGenerator moveGenerator = MoveGeneratorFactory.createMoveGeneratorInternational();
    private List<Move> moves=null;
    
    /**
     * creates an initial  draughts state.
     */
    public DraughtsState() {
        bs = new BoardState(10,10);
        bs.setBegin();
    }
    
    private DraughtsState(DraughtsState ds) {
        this(ds.bs);
    }
    
    /** creates a draughts state with a copy of the given BoardState. **/
    DraughtsState(BoardState bs) {
        this.bs = (BoardState) bs.clone();
    }  
    
    /**
     *
     * @return
     */
    @Override
    public List<Move> getMoves() {
        if (moves==null)
            return moves=new ArrayList(moveGenerator.generateMoves(bs));
        return moves;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isEndState() {
        if (moves==null)
            moves = new ArrayList(moveGenerator.generateMoves(bs));
        return moves.isEmpty();
    }

    /**
     *
     * @param m
     */
    @Override
    public void doMove(Move m) {
        moves = null;      // invalidate moves
        bs.moveForward(m);
    }

    /**
     *
     * @param m
     */
    @Override
    public void undoMove(Move m) {
        moves=null;             // invalidate cached moves
        bs.moveBackward(m);
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return bs.toString()+"      w2m = "+ isWhiteToMove();
    }
    
    /** returns an array a of length 51: 
     * a[0] is unused; a[i] is one of {WHITE,BLACK,WHITE_KING,BLACK_KING,EMPTY}.
     * Note that you can directly change this array to change this DraughtsState;
     * however, you should normally do that using the moveBackward and moveForward methods.
     * @return state of the board as an int array.
     * @see DraughtsState#doMove(Move)
     * @see DraughtsState#undoMove(Move) 
     */
    public int[] getPieces() {
        return bs.getPieces();
    }
    
    /** @return piece at field f 
     * @see DraughtsState#WHITEPIECE
     * @see DraughtsState#BLACKPIECE
     * @see DraughtsState#WHITEKING
     * @see DraughtsState#BLACKKING
     * @see DraughtsState#EMPTY
     **/
    public int getPiece(int f) {
        if (f<1 || f > 50) throw new IllegalArgumentException();
        return bs.getPiece(f);
    }
    
    /** @return one of {WHITE,BLACK,WHITE_PIECE,BLACK_PIECE,EMPTY}
     * @param r row    
     * @param c column
     **/
    public int getPiece(int r, int c) {
        int f = bs.rc2f(r, c);
        if (f==-1) throw new IllegalArgumentException();
        return bs.getPiece(f);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isWhiteToMove() {
        return bs.isWhiteToMove();
    }

    /**
     *
     */
    @Override
    public void reset() {
        moves=null;       // invalidate cached moves
        bs.setBegin();
    }

    @Override
    public DraughtsState clone() {
        return new DraughtsState(this);
    }
}