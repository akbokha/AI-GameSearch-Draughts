/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolve;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import nl.tue.s2id90.group27.BasicAlphaBetaGroup27;
import nl.tue.s2id90.tournament.OfflineTournament;
import org10x10.dam.game.Move;


/**
 * Adaptation from nl.tue.s2id90.tournament.OfflineTournament
 * @param <M>
 */
public class GeneticEvolution<M extends Move>  {
    
    private static final int POOL_SIZE = 4;
    private static final int TIME_LIMIT = 200;
    private static final int MOVES = 5;
    private static final int ITERATIONS = 20;
    
    public static void main(String[] args) {
        GeneticEvolution a = new GeneticEvolution();
        a.go(args, DraughtsState::new);
        System.exit(0);
    }
    
    protected void go(String[] args, Supplier<GameState<M>> initialState) {
        Map<Player, Integer> solutionCandidates = new HashMap<>(POOL_SIZE);
        Player[] pool = new Player[POOL_SIZE];
        
        // Fill the list of candidate solutions.
        for (int i = solutionCandidates.size(); i < POOL_SIZE; i++) {
            pool[i] = new AlphaBetaGroup27(10);
            solutionCandidates.put(pool[i], 0);
        }
        
        Random r = new Random();
        for (int i = 0; i < ITERATIONS; i++) {
            final int indexOfP1 = r.nextInt(POOL_SIZE);
            int indexOfP2;
            do {
                indexOfP2 = r.nextInt(POOL_SIZE);
            } while (indexOfP2 == indexOfP1);
            
            Player p1 = pool[indexOfP1];
            Player p2 = pool[indexOfP2];
            
            // Play the first match
            int result = playMatch(p1, p2, MOVES, TIME_LIMIT, initialState);
            if (result == 0) { // If it is a draw, then play a rematch
                result = -playMatch(p2, p1, MOVES, TIME_LIMIT, initialState); // Negating to take the inversion of roles into account.
                if (result == 0) { // If the rematch also was a draw, select winner at random.
                    result = r.nextBoolean() ? 1 : -1;
                }
            }
            
            Player winner;
            Player loser;
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
            
            solutionCandidates.compute(winner, (Player t, Integer u) -> {
                return u + 1;
            });
            int newLoserScore = solutionCandidates.compute(loser, (Player t, Integer u) -> {
                return u - 2;
            });
            if (newLoserScore < 0) {
                solutionCandidates.remove(loser);
                Player replacementPlayer = new AlphaBetaGroup27(10);
                pool[indexofLoser] = replacementPlayer;
                solutionCandidates.put(replacementPlayer, 0);
            }
            
            System.out.println(solutionCandidates);
        }
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
    int playMatch(Player p0, Player p1, int maxMove, int maxTimeinMS, Supplier<GameState<M>> initialState) {
        GameState<M> state = initialState.get();
        int moveCount=0;
        while (moveCount<maxMove && !state.isEndState()) {
            // check for illegal moves
            Player player = (state.isWhiteToMove()?p0:p1);
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
