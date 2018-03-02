package evolve.Properties;

import java.util.Random;


public class IntegerGene extends AbstractNumberGene<Integer> {

    Random r;
    
    public IntegerGene() {
        r = new Random();
    }

    @Override
    public AbstractGene getRandom() {
        return clone().setValue(r.nextInt(getMax() - getMin()) + getMin());
    }

    @Override
    public AbstractGene getMutated(float alike) {
        return clone().setValue((int) ((int) getValue() * alike + (int) getRandom().getValue() * (1f - alike)));
    }
    
    protected IntegerGene clone() {
        IntegerGene clone = new IntegerGene();
        clone
            .setMax(this.getMax())
            .setMin(this.getMin())
            .setValue(this.getValue())
        ;
        
        return clone;
    }
}