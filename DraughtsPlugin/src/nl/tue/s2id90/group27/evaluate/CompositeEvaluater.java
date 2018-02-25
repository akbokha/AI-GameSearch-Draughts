/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.s2id90.group27.evaluate;

import java.util.ArrayList;
import nl.tue.s2id90.draughts.DraughtsState;

public class CompositeEvaluater {
    
    private final ArrayList<CompositeEvaluater> evaluaters;

    public CompositeEvaluater() {
        this.evaluaters = new ArrayList<>();
    }
    
    public CompositeEvaluater add(CompositeEvaluater e) {
        evaluaters.add(e);
        return this;
    }
    
    public float evaluate(DraughtsState state) {
        float result = 1f;
        for (CompositeEvaluater e : evaluaters) {
            result *= e.evaluate(state);
        }
        
        return result;
    }
}
