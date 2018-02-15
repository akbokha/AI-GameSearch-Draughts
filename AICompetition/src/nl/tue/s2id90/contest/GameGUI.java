package nl.tue.s2id90.contest;

import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import nl.tue.s2id90.game.GameState;
import nl.tue.s2id90.game.Player;

/**
 *
 * @author huub
 * @param <State>
 * @param <P>
 * @param <Move>
 */
public interface GameGUI<State extends GameState<Move>,P extends Player<Move,State>,Move> 
                 extends CompetitionListener<Move> {
    /** get a board that shows the game state and maybe allows interactions for
     * a human player.
     * @return a panel containing such a board widget
     */
    JPanel getBoardPanel();
    
    /** return a list of additional gui panels to be added to the tabbed pane of
     * the competition gui. 
     * @return 
     */
    List<? extends JComponent> getPanels();
    
    /** update the panels to show information of game state gs.
     * @param gs **/
    void show(State gs);
    
    /** returns the current game state.
     * @return  **/
    State getCurrentGameState();
}
