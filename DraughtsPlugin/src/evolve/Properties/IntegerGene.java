package evolve.Properties;

import java.util.Random;


public class IntegerGene extends AbstractNumberGene<Integer> {

    Random r;
    
    public IntegerGene(String name) {
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