package evolve.Properties;

import java.util.Random;


public class FloatType extends AbstractNumber<Float> {

    Random r;
    
    public FloatType(String name) {
        super(name);
        r = new Random();
    }

    @Override
    public Float getRandom() {
        return r.nextFloat() * (getMax() - getMin()) + getMin();
    }

    @Override
    public Float getMutated(Float type, float alike) {
        return type * alike + getRandom() * (1f - alike);
    }
    
}