package evolve.Properties;

public abstract class AbstractNumberGene<T extends Number> extends AbstractGene<T> {
    
    private T min;
    private T max;

    public T getMin() {
        return min;
    }

    public AbstractNumberGene setMin(T min) {
        this.min = min;
        return this;
    }

    public T getMax() {
        return max;
    }

    public AbstractNumberGene setMax(T max) {
        this.max = max;
        return this;
    }

}
