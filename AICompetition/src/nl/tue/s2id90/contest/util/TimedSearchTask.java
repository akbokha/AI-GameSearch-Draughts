package nl.tue.s2id90.contest.util;

import java.util.Timer;
import java.util.TimerTask;
import nl.tue.s2id90.game.GameState;
import nl.tue.s2id90.game.Player;

/**
 * This search task automatically stops after given number of seconds. This is done
 * by calling its stop() method in a TimerTask.
 * @author huub
 * @param <M> Move 
 * @param <U> UndoMove
 * @param <S> GameState<M>
 */
public abstract class TimedSearchTask<M, U, S extends GameState<M>>
    extends SearchTask<M, U, S> {
    static Timer timer = new Timer();  // can schedule multiple tasks, so no need to make a new one
    public TimedSearchTask(Player<M, S> player, S s, int milliSeconds) {
        super(player, s);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                    TimedSearchTask.this.stop();
            }            
        };
        timer.schedule(task, milliSeconds);        
    }    
}
