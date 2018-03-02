package evolve.Properties;

import java.util.Random;


public class FloatGene extends AbstractNumberGene<Float> {

    Random r;
    
    public FloatGene() {
        r = new Random();
    }

    
    @Override
    public AbstractGene getRandom() {
        return clone().setValue(r.nextFloat() * (getMax() - getMin()) + getMin());
    }

    @Override
    public AbstractGene getMutated(Float type, float alike) {
        return clone().setValue(type * alike + (float) getRandom().getValue() * (1f - alike));
    }

    @Override
    protected FloatGene clone() {
        FloatGene clone = new FloatGene();
        clone
            .setMax(this.getMax())
            .setMin(this.getMin())
            .setValue(this.getValue())
        ;
        
        return clone;
    }
    
}