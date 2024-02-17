package genmath;

/**
 * Abstract class for taking constraints on comparable types
 * @author arva
 * @param <K> Key type
 */
public abstract class ComparableKey<K>{

    public abstract K less(K key);
    public abstract K greater(K key);
    public abstract int sgn();
    public abstract int compareTo(K key);
    public abstract int len();
    public abstract int at(int pos);
    public abstract K add(K key);
    public abstract K subtract(K key);

    public abstract K maxVal();
}