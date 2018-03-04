package evolve.Properties;

public class ScalarGene extends FloatGene {

    @Override
    public AbstractGene getRandom() {
        return clone().setValue((float) Math.pow(r.nextFloat() * (getMax() - getMin()) + getMin(), 2f));
    }

    @Override
    public AbstractGene getMutated(float alike) {
        return clone().setValue((float) Math.pow(getValue() * alike + (float) getRandom().getValue() * (1f - alike), 2f));
    }

}