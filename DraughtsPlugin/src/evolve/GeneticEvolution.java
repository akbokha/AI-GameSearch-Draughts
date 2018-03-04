package evolve;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.tue.s2id90.contest.util.SearchTask;
import nl.tue.s2id90.contest.util.TimedSearchTask;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.game.GameState;
import nl.tue.s2id90.game.Player;
import nl.tue.s2id90.group27.AlphaBetaGroup27;
import nl.tue.s2id90.tournament.OfflineTournament;
import org10x10.dam.game.Move;


/**
 * Adaptation from nl.tue.s2id90.tournament.OfflineTournament
 * @param <M>
 */
public class GeneticEvolution<M extends Move>  {
    
    private static final int POOL_SIZE = 5;
    private static final int TIME_LIMIT = 200;
    private static final int MOVES = 150;
    private static final int ITERATIONS = 100;
    
    public static void main(String[] args) {
        GeneticEvolution a = new GeneticEvolution();
        a.go(args, DraughtsState::new, .3f, 0f); // Replace the retain chance later of when running with more genes.
        System.exit(0);
    }
    
    protected void go(String[] args, Supplier<GameState<M>> initialState, float alike, float retain) {
        System.out.println("Starting the genetic evolution contest, settings:");
        System.out.println(" - Pool size            " + POOL_SIZE + " players");
        System.out.println(" - Number of matches    " + ITERATIONS);
        System.out.println(" - Time limit per move  " + TIME_LIMIT + "ms");
        System.out.println(" - Moves limit per game " + MOVES + " moves");
        System.out.println(" - Mutation alikeness   " + (int) (alike * 100) + "%");
        System.out.println(" - Gene retain chance   " + (int) (retain * 100) + "%");
        System.out.println(" - Starting bord \n" + initialState.get());
        System.out.println("");
        
        Map<EvolvableDraughtsPlayer, Integer> solutionCandidates = new HashMap<>(POOL_SIZE);
        EvolvableDraughtsPlayer[] pool = new EvolvableDraughtsPlayer[POOL_SIZE];
        
        // Fill the list of candidate solutions.
        for (int i = solutionCandidates.size(); i < POOL_SIZE; i++) {
            pool[i] = new AlphaBetaGroup27(10);
            solutionCandidates.put(pool[i], 0);
        }
        
        printPool(solutionCandidates);
        
        Random r = new Random();
        for (int i = 1; i <= ITERATIONS; i++) {
            final int indexOfP1 = r.nextInt(POOL_SIZE);
            int indexOfP2;
            do {
                indexOfP2 = r.nextInt(POOL_SIZE);
            } while (indexOfP2 == indexOfP1);
            
            EvolvableDraughtsPlayer p1 = pool[indexOfP1];
            EvolvableDraughtsPlayer p2 = pool[indexOfP2];
            
            System.out.println("Start match " + i + "/" + ITERATIONS + " between " + p1 + " and " + p2);
            
            // Play the first match
            int result = playMatch(p1, p2, MOVES, TIME_LIMIT, initialState);
            if (result == 0) { // If it is a draw, then play a rematch
                System.out.println("First match was a draw, playing a rematch");
                result = -playMatch(p2, p1, MOVES, TIME_LIMIT, initialState); // Negating to take the inversion of roles into account.
                if (result == 0) { // If the rematch also was a draw, select winner at random.
                    System.out.println("Rematch also drawed, picking winner at random");
                    result = r.nextBoolean() ? 1 : -1;
                }
            }
            
            EvolvableDraughtsPlayer winner;
            EvolvableDraughtsPlayer loser;
            int indexOfWinner;
            int indexofLoser;
            if (result > 0) { // p1 wins
                winner = p1;
                loser = p2;
                indexOfWinner = indexOfP1;
                indexofLoser  = indexOfP2;
            } else { // p2 wins
                winner = p2;
                loser = p1;
                indexOfWinner = indexOfP2;
                indexofLoser  = indexOfP1;
            }
            
            System.out.println("Winner: " + winner + "\tLoser: " + loser);
            
            solutionCandidates.compute(winner, (EvolvableDraughtsPlayer t, Integer u) -> {
                return u + 1;
            });
            int newLoserScore = solutionCandidates.compute(loser, (EvolvableDraughtsPlayer t, Integer u) -> {
                return u - 2;
            });
            if (newLoserScore < 0) {
                solutionCandidates.remove(loser);
                EvolvableDraughtsPlayer replacementPlayer = winner.generateOffspring(alike, retain);
                pool[indexofLoser] = replacementPlayer;
                solutionCandidates.put(replacementPlayer, 0);
                
                System.out.println("Replacing loser with " + replacementPlayer);
            }
            
            printPool(solutionCandidates);
        }
    }
    
    private void printPool(Map<EvolvableDraughtsPlayer, Integer> pool) {
        System.out.println("Current pool: ");
        for (EvolvableDraughtsPlayer player : pool.keySet()) {
            System.out.println(" - " + pool.get(player) + "\t" + player);
        }
        System.out.println("\n");
    }
    
    /**
     * Plays a game between the players p0 and p1.
     * @param p0   white player
     * @param p1   black player
     * @param maxMove   maximum number of allowed moves
     * @param maxTimeinMS  maximum time in milliseconds allowed per move
     * @param initialState
     * @return 1 if white won, 0 for a tie, -1 if black won
     */
    int playMatch(EvolvableDraughtsPlayer p0, EvolvableDraughtsPlayer p1, int maxMove, int maxTimeinMS, Supplier<GameState<M>> initialState) {
        GameState<M> state = initialState.get();
        int moveCount=0;
        while (moveCount<maxMove && !state.isEndState()) {
            // check for illegal moves
            EvolvableDraughtsPlayer player = (state.isWhiteToMove()?p0:p1);
            M move = getComputerMove(player, state, maxTimeinMS);
            if (move==null||!state.getMoves().contains(move)) { // illegal move
                // player who tried to make the illegal move, looses the game
                return state.isWhiteToMove() ? -1 : 1;
            }
            
            // do the move if it is legal.
            state.doMove(move);
            moveCount++;
        }
        
        if (state.isEndState()) { // player who is to move, looses the game
            System.out.println("Reached end-state");
            return state.isWhiteToMove() ? -1 : 1;
        } else {
            return 0;
        }
    }
    
    protected M getComputerMove(final Player player, final GameState<M> gs, final int maxTime) {
        Semaphore flag = new Semaphore(0);
        SearchTask<M, Long, GameState<M>> searchTask;
        Object[] moves = new Object[1];
        searchTask = new TimedSearchTask<M, Long, GameState<M>>(player, gs, maxTime) {
            @Override
            public void done(M m) {
                moves[0]=m;
                flag.release();
            }
        };
        searchTask.execute();
        try {
            flag.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(OfflineTournament.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (M) moves[0];
    }
}
