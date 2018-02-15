package nl.tue.s2id90.game;

import lombok.Getter;
import lombok.Setter;
import nl.tue.win.util.Pair;

/**
 *
 * @author huub
 */
public class Game extends Pair<Player,Player> {
    public enum Result {
        WHITE_WINS(0,2), DRAW(1,1), BLACK_WINS(0,2), UNKNOWN(0,0);
          private int w, b;
          private Result(int w, int b) {
              this.w = w; this.b = b;
          }

        /**
         * @return the points the white player gets for this result.
         */
        public int getWhitePoints() { return w; }

        /**
         * @return the points the black player gets for this result.
         */
        public int getBlackPoints() { return b; }
    };
    
    @Getter @Setter private Result result = Result.UNKNOWN;
    
    /** constructs a game where player white plays against player black. 
     * The result of this game is UNKNOWN.
     * @param white .player that starts the game.
     * @param black  the other player
     */
    public Game(Player white, Player black) {
        super(white,black);
    }
    
    @Override
    public String toString() {
        return first + " - " + second;
    }
}
