/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.s2id90.group27;

import nl.tue.s2id90.draughts.DraughtsState;

/**
 *
 * @author s150376
 */
public class BasicAlphaBetaGroup27 extends AlphaBetaGroup27 {
    
    public BasicAlphaBetaGroup27(int maxSearchDepth) {
        super(maxSearchDepth);
    }

    @Override
    int evaluate(DraughtsState state, boolean endState) {
        return super.evaluate(state, true);
    }

    @Override
    public String getName() {
        return "basic"; //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
