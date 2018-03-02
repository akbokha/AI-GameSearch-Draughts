package evolve;

import evolve.Properties.AbstractGene;
import java.util.Map;
import java.util.Random;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import nl.tue.s2id90.group27.AlphaBetaGroup27;

abstract public class EvolvableDraughtsPlayer extends DraughtsPlayer implements Evolvable {

    public EvolvableDraughtsPlayer(String s) {
        super(s);
    }
    
    /**
     * 
     * @param alike The amount of similarities mutated genes will have.
     * @param retain The chance (as a factor) that the gene will be retained.
     * @return 
     */
    public EvolvableDraughtsPlayer generateOffspring(float alike, float retain) {
        Map<String, AbstractGene> genome = getGenome();
        EvolvableDraughtsPlayer result = new AlphaBetaGroup27(30);
        Random r = new Random();
        for (String key : genome.keySet()) {
            if (r.nextFloat() > retain) {
                AbstractGene gene = genome.get(key).getMutated(alike);
                result.setGene(key, gene);
            }
        }
        
        return result;
    }
}
