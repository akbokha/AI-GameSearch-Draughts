package evolve.Properties;

/**
 * @param <Type>
 */
public abstract class AbstractGene<Type extends Object> {
    
    abstract public Type getRandom();
    abstract public Type getMutated(Type type, float alike);
    
    private Type type;
    private final String name;

    public AbstractGene(String name) {
        this.name = name;
    }    

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
