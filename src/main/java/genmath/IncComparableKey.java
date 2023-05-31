package genmath;

public abstract class IncComparableKey<K> extends ComparableKey<K> {

    @Override
    public abstract K less(K key);
    
    @Override
    public abstract K greater(K key);
    
    @Override
    public abstract int sgn();
    
    @Override
    public abstract int compareTo(K key);
    
    @Override
    public abstract int len();
    
    @Override
    public abstract int at(int pos);
    
    @Override
    public abstract K add(K key);
    
    @Override
    public abstract K subtract(K key);
    
    @Override
    public abstract K maxVal();

    public abstract boolean isPlaceholder();
}