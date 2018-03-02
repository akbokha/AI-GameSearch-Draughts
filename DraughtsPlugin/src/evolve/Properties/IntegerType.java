package evolve.Properties;

import java.util.Random;


public class IntegerType extends AbstractNumber<Integer> {

    Random r;
    
    public IntegerType(String name) {
        super(name);
        r = new Random();
    }

    @Override
    public Integer getRandom() {
        return r.nextInt(getMax() - getMin()) + getMin();
    }

    @Override
    public Integer getMutated(Integer type, float alike) {
        return (int) (type * alike + getRandom() * (1f - alike));
    }
    
}