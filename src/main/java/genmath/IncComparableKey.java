package genmath;

/**
 * Abstract class takes constraints on incomplete comparable types
 * @author arva
 * @param <K> Key type
 */
public abstract class IncComparableKey<K> extends ComparableKey<K> {

    /**
     * Less than operator
     * @param key Key value to be compared
     * @return Result of comparison in value form
     */
    @Override
    public abstract K less(K key);
    
    /**
     * Greater than operator
     * @param key Key value to be compared
     * @return Result of comparison in value form
     */
    @Override
    public abstract K greater(K key);
    
    /**
     * Signum function
     * @return Result of signum value in form of -1, 0 or 1 by order of signs
     */
    @Override
    public abstract int sgn();
    
    /**
     * Generic compare to function that implements three way comparison in sense 
     * of less, equal or greater comparison value results
     * @param key Key for comparison operation
     * @return -1 less than, 0 equal, 1 greater than
     */
    @Override
    public abstract int compareTo(K key);
    
    /**
     * Length of key in integer form in case of literal countable key
     * (assumed precondition exists for this)
     * @return Length of key
     */
    @Override
    public abstract int len();
    
    /**
     * Operator subscript alias in form of method
     * @param pos Position of the requested value in the literal
     * @return Value if the position is allocated and used
     */
    @Override
    public abstract int at(int pos);
    
    /**
     * Addition operator alias
     * @param key External operand (right operand)
     * @return Added value
     */
    @Override
    public abstract K add(K key);
    
    /**
     * Subtraction operator alias
     * @param key External operand (right operand)
     * @return Subtracted value
     */
    @Override
    public abstract K subtract(K key);
    
    /**
     * Maximum finder using two values (one is externally provided by this function)
     * @return Found maximum value from provided two keys
     */
    @Override
    public abstract K maxVal();

    /**
     * Whether key is a placeholder or not
     * @return True if key is a placeholder
     */
    public abstract boolean isPlaceholder();
}