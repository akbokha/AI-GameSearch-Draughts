package nl.tue.s2id90.contest;

import nl.tue.s2id90.game.Game;

/**
 *
 * @author huub
 */
public interface CompetitionListener<Move> {
    void onStartGame(Game g);
    void onStopGame(Game g);
    void onAIMove(Move m);
}
