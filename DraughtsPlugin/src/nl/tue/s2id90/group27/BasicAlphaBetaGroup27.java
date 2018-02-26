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
    int evaluate(DraughtsState state) {
        // START COUNTING PIECES
        int [] pieces  = state.getPieces();
        int whiteValue = 0;
        int blackValue = 0;

        for (int i = 1; i < pieces.length; i++) {
            switch(pieces[i]) {
                case DraughtsState.WHITEPIECE:
                    whiteValue++;
                    break;
                case DraughtsState.WHITEKING:
                    whiteValue += 3;
                    break;
                case DraughtsState.BLACKPIECE:
                    blackValue++;
                    break;
                case DraughtsState.BLACKKING:
                    blackValue += 3;
                    break;     
            }
        }
        
        return whiteValue - blackValue;
    }
    
    
}
