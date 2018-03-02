package evolve.Properties;

/**
 * @param <Type>
 */
public abstract class AbstractGene<Type extends Object> {
    
    abstract public AbstractGene getRandom();
    abstract public AbstractGene getMutated(Type value, float alike);
    
    private Type value;

    public Type getValue() {
        return value;
    }

    public AbstractGene setValue(Type value) {
        this.value = value;
        return this;
    }

}
