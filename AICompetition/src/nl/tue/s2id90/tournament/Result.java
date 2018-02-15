package nl.tue.s2id90.tournament;

import java.util.List;
import lombok.Getter;

/**
 *
 * @author huub
 */
//<editor-fold defaultstate="collapsed" desc="Result class">

class Result<P,M> {
    private Result() {}
    @Getter int index;
    @Getter private P p0, p1;
    @Getter private List<M> moves;
    @Getter private int r0, r1; // result for p0 and p1, respectively
    @Getter private boolean maxMoveReached;
    
    public Row white() {
        Row row = new Row();
        row.addWhite(this);
        return row;
    }
    
    public Row black() {
        Row row = new Row();
        row.addBlack(this);
        return row;
    }
    
    public static <P,M> Result<P,M> of(int index,P p0, P p1, List<M> moves, int r0, int r1, boolean maxMoveReached) {
        Result<P,M> r = new Result<>();
        r.index = index;
        r.p0=p0; r.p1=p1; r.r0=r0; r.moves = moves; r.r1=r1; r.maxMoveReached = maxMoveReached;
        return r;
    }
}
//</editor-fold>
