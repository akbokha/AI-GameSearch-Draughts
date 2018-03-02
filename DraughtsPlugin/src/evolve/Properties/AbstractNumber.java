package evolve.Properties;

public abstract class AbstractNumber<T extends Number> extends AbstractProperty<T> {
    
    private T min;
    private T max;

    public AbstractNumber(String name) {
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
