package nl.tue.s2id90.contest;

/**
 *
 * @author huub
 */
public interface GameGuiListener<S,M> {
    void onHumanMove(M m);
    void onNewGameState(S s);
}
