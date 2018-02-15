package nl.tue.s2id90.contest.util;

import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import nl.tue.s2id90.game.GameState;
import nl.tue.s2id90.game.Player;

/**
 * class
 * @author huub
 * @param <M>  Move
 * @param <U>  Undo
 * @param <S> GameState
 */
public abstract class SearchTask<M,U,S extends GameState<M>>
{  
    private SwingWorker<M,U> worker;
    private final Player<M,S> player;
    private final S state;

    /**
     * @param player for whom to perform a search for the best move in state s
     * @param s game state in which to search for a best move
     */
    public SearchTask(Player<M,S> player, S s) {
        this.state = s;
        this.player = player;
    }
    
    /**
     * starts a background job to determine the best move of this SearchTask's
     * player and calls done() when the job finishes.
     * @see SearchTask#done(Object) 
     * @see SwingWorker
     */
    public void execute() {
        worker = createNewSwingWorker();
        worker.execute();
    }
    
    /** @return the moves of player in this state. **/
    private M search() {
        if (player!=null) {
            try {
                // we clone the state here, so whatever the player does with the
                // state, will not ruin the GUI!
                return player.getMove((S)state.clone());
            } catch(Exception e) {
                e.printStackTrace();
                System.err.println(e);
                return null;
            }
        } else {
            return null;
        }
    }
    
    /**
     * @return swing worker that starts the search() method and calls done() when that 
     *         task finishes.
     * @see SwingWorker
     */
    private SwingWorker<M,U> createNewSwingWorker() {
        return new SwingWorker<M,U>() {
            @Override
            protected M doInBackground() throws Exception {
                    return SearchTask.this.search();
            }

            @Override
            protected void done() {
                try {
                    M m = get(); // gets computed move
                    SearchTask.this.done(m);
                    
                } catch (InterruptedException | ExecutionException ex) { 
                    System.err.println("Exception in search task: " + ex);
                    ex.printStackTrace();
                }
            }            
        };
    }
    
    /**
     * method called just before execute finishes.
     * @param m move found in search
     * @see execute()
     */
    abstract public void done(M m);
    
    /**
     *
     */
    public void stop() {
        // only do this once!
        if (!worker.isDone()) player.stop();
    }
}