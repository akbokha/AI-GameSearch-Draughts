package nl.tue.s2id90.draughts;
import static nl.tue.s2id90.draughts.DraughtsState.BLACKKING;
import static nl.tue.s2id90.draughts.DraughtsState.BLACKPIECE;
import static nl.tue.s2id90.draughts.DraughtsState.WHITEKING;
import static nl.tue.s2id90.draughts.DraughtsState.WHITEPIECE;

/**
 *
 * @author huub
 */
public class Draughts {

    /**
     *
     * @param piece
     * @return whether or not piece is white
     */
    public static boolean isWhite(int piece) {
        return piece== WHITEPIECE || piece==WHITEKING;
    }
        
    /**
     *
     * @param piece
     * @return whether or not piece is black piece
     */
    public static boolean isBlack(int piece) {
        return piece== BLACKPIECE || piece==BLACKKING;
    }

    /**
     *
     * @param piece
     * @return whether or not piece is a king
     */
    public static boolean isKing(int piece) {
        return piece== WHITEKING || piece==BLACKKING;
    }
}
