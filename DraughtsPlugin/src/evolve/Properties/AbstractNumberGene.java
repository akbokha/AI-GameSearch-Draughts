package evolve.Properties;

public abstract class AbstractNumberGene<T extends Number> extends AbstractGene<T> {
    
    private T min;
    private T max;

    public AbstractNumberGene(String name) {
        super(name);
    }

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }

}
