package evolve.Properties;

/**
 * @param <Type>
 */
public abstract class AbstractProperty<Type extends Object> {
    
    abstract public Type getRandom();
    abstract public Type getMutated(Type type, float alike);
    
    private Type type;
    private final String name;

    public AbstractProperty(String name) {
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
