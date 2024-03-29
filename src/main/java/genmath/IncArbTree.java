package genmath;

import genmath.genmathexceptions.NoObjectFoundException;
import genmath.genmathexceptions.UnorderedDataListException;
import genmath.genmathexceptions.AmbiguousObjectException;
import genmath.genmathexceptions.CapacityUnderloadException;
import java.util.ArrayList;

/** 
 * linearized tree Vs nested object oriented tree
 *  nested version (reference):
 *    + Low cost of insertion of new node [no previous element shift, arbitrary 
 *      (system defined memory allocation] simple allocation and reference creation)
 *    + Logarithmic key lookup requires less command (no index offsets)
 *      (time complexity is the same with the other version)
 *    + Root displacement is cheap (simple reference dislocation)
 *    + Node removal is cheap (just detach the certain object from reference 
 *      linkage chain)
 *    - Node obtain by direct, specialized order based sort index is logarithmic 
 *      instead of constant
 *    - Whole level obtain requires entire traversal of tree (including leaf level)
 *    ? Leaf level direct access could be available in case of a <recent leaf 
 *      node> container if at least one traversal has performed at arbitrary 
 *      method that contains traversal, This would cause slower add method (
 *    - Insertion cost is high due to ad-hoc memory allocation
 *
 *  linearized version (ArrayList):
 *    - High cost of insertion of new node [previous element shift occurs and 
 *      allocation of new array in case of run out of enough memory] advanced 
 *      allocation and reference creation)
 *    - Logarithmic key lookup requires more command (index offsets)
 *      (time complexity is the same with the other version)
 *    - Root displacement is expensive (array modification occurs as at the insertion)
 *    - Node removal is expensive (detachment of node requires offset indexing shifts)
 *    + Node obtain by direct, specialized order based sort index is constant instead 
 *      of logarithmic or else
 *    + Whole level obtain requires forward index offset jump traversal on array
 *    + Leaf level obtain requires backward traversal on container array 
 *      (the more the node number and equated [homogeneous] leaf level[s], the 
 *      less the level obtainment)
 *    + Option for pre-allocation in order to save allocation cost at insertion
 */

// The required implementation depends on purpose
//  insertion time (overwhelmingly modification of tree)
//  ability to faster conversion to other formats (e.g. graph matrix)
//  subgraph operation speeds, etc..

/**
 * Class presents an incomplete n-ary tree
 * @author arva
 * @param <K> Key type
 * @param <V> Value type
 */
public class IncArbTree<K extends ComparableKey<K>, V> {
    
    // pair auxiliary type
    public static class Pair<K extends ComparableKey<K>, V> implements Comparable<Pair<K, V>> {

        public Pair() {

            this.key = null;
            this.value = null;
        }

        public Pair(K key, V value) throws Exception {

            if (key == null) {
                throw new NullPointerException("Key is null.");
            }

            this.key = key;

            if (value == null) {
                throw new NullPointerException("Value is null.");
            }
            this.value = value;
        }

        @Override
        public int compareTo(Pair<K, V> item) {

            return key.compareTo(item.key);
        }

        public K key;
        public V value;

    }
    
    // traversal
    private ArrayList<Integer> nodeIndHist;// node identifier for relative child indexing limit
    private ArrayList<Integer> childNodeIndHist;// incremental child node identifier
    private int travI;
    private int travJ;
    private int prevIndShift;
    private int retI;
    private boolean wasStepBack;// recently reached leaf level
    private int numOfCumulatedStepBacks;
    
    
    private ArrayList<Integer> cumulativeChildNodeOffsetRegistry;
    
    // stores node division quantity, the number hash keys to be stored in a node, these 
    //  quantites/nubmbers can be different from each oter
    private ArrayList<Integer> nodeSizeChildRegistry;
    
    private ArrayList<Pair<K, V>> container;// level ordered list, starting from root
    private int size;
    
    /**
     * Default constructor for this class
     */
    public IncArbTree(){

        size = 0;
        cumulativeChildNodeOffsetRegistry = new ArrayList<>();
        cumulativeChildNodeOffsetRegistry.add(0);
        nodeSizeChildRegistry = new ArrayList<>();
        nodeSizeChildRegistry.add(0);
        container = new ArrayList<>();
        
        nodeIndHist = new ArrayList<>();
        childNodeIndHist = new ArrayList<>();
        travI = 0;
        travJ = 0;
        prevIndShift = 1;
        retI = 0;
        wasStepBack = false;
        numOfCumulatedStepBacks = 0;
    }
    
    /**
     * Copy constructor for class
     * @param orig 
     */
    public IncArbTree(IncArbTree<K, V> orig){
    
        this.size = orig.size;
        this.cumulativeChildNodeOffsetRegistry = orig.cumulativeChildNodeOffsetRegistry;
        this.nodeSizeChildRegistry = orig.nodeSizeChildRegistry;
        this.container = orig.container;
        
        this.nodeIndHist = orig.nodeIndHist;
        this.childNodeIndHist = orig.childNodeIndHist;
        this.travI = orig.travI;
        this.travJ = orig.travJ;
        this.prevIndShift = orig.prevIndShift;
        this.retI = orig.retI;
        this.wasStepBack = orig.wasStepBack;
        this.numOfCumulatedStepBacks = orig.numOfCumulatedStepBacks;
    }
    
    /**
     * It provides an insertion method for adding key-value pairs in pair form  
     * along with parent key place specifier
     * @param key Parent key identifier
     * @param values New key-value pair
     * @return It returns the insertion place, its index in the container
     * @throws Exception
     *         see original version of this method with different signature
     */
    public int addOne(K key, Pair<K, V> values) throws Exception{
    
        return addOne(key, values.key, values.value);
    }
    
    /**
     * It provides an insertion method for adding a single key-value pairs separately
     * @param key Parent key identifier
     * @param nKey New key related to new value
     * @param value New value related to new key
     * @return It returns the insertion place, its index in the container
     * @throws Exception
     *         Key-value parameter is null
     *         Pair object creation related exceptions (see further)
     *         Parent key not found (does not exist)
     *         Key duplication (precondition key existence violation)
     */
    public int addOne(K key, K nKey, V value) throws Exception {
    
        if(nKey == null || value == null){
        
            throw new NullPointerException("Key-value parameter is null.");
        }
        
        if(size == 0){
        
            cumulativeChildNodeOffsetRegistry.set(0, 1);
            nodeSizeChildRegistry.set(0, 1);
            
            container.add(new Pair<>(nKey, value));
            cumulativeChildNodeOffsetRegistry.add(0);
            nodeSizeChildRegistry.add(0);
            
            size = 1;
        
            return 0;
        }
        else{
            
            int insertionInd = 0;
            int prevShift = 0;
            int i = 0;
            int j;
            int level = 0;
            ArrayList<Integer> nodeShiftIndTrace = new ArrayList<>();
            
            while(nodeSizeChildRegistry.get(i) > 0){
                
                // finding upper bound split key
                for(j = 0; j < nodeSizeChildRegistry.get(i); ++j){
                
                    // keys are on the same level (hierachical classification)
                    // keeping container ordered by < operator on keys
                    if(key.at(level) < container.get(prevShift + j).key.at(level)){
                    
                        // it also includes case of 0 elements due to 0 offset
                        insertionInd += cumulativeChildNodeOffsetRegistry.get(prevShift + j);
                    }
                    else if(key.at(level) == container.get(prevShift + j).key.at(level)){
                    
                        nodeShiftIndTrace.add(i);// traversal tracking for cumulative offsets
                        i = prevShift + j;
                        ++level;// go down to next level
                        break;
                    }
                    
                    ++insertionInd;
                }
                
                prevShift = insertionInd;
                
                int res = key.compareTo(container.get(prevShift + j).key);
                if(res == 0){
                
                    // parent key match (including middle levels as well)
                    // insert new node onto selected level
                    break;
                }
                else if(res > 0 && key.len() == level + 1){
                
                    throw new NoObjectFoundException("No parent key has been found "
                            + "and key length differs from terminated level key length.");
                }
            }
            
            // insertion new child node
            // order preservation among child
            boolean redundancy = false;
            
            int nodeSizeChildRegistryI = nodeSizeChildRegistry.get(i);
            
            j = cumulativeChildNodeOffsetRegistry.get(i);
            ++level;
            
            for(; j < nodeSizeChildRegistryI; 
                j += (1 + cumulativeChildNodeOffsetRegistry.get(j))){
            
                if(key.at(level) > container.get(j).key.at(level)){
                
                    break;
                }
                else if(key.at(level) == container.get(j).key.at(level)){
                    
                    redundancy = true;
                    break;
                }
            }
            
            if(redundancy){
            
                throw new AmbiguousObjectException("Key duplication is not allowed.");
            }
            
            // increasing capacity of stroage
            // adding new node next to existing ones (not only leaf level)
            container.add(j, new Pair<>(nKey, value));
            nodeSizeChildRegistry.add(j, 0);
            cumulativeChildNodeOffsetRegistry.add(j, 0);
            
            nodeSizeChildRegistry.set(i, 
                nodeSizeChildRegistry.get(i) + 1);
            
            // trace length changes according to traversal depth
            int sizeOfShiftIndTrace = nodeShiftIndTrace.size();
            int cumulativeInd;
            
            // backpropagating index offset change
            for(j = sizeOfShiftIndTrace - 1; j >= 0; --j){
            
                cumulativeInd = nodeShiftIndTrace.get(j);
                cumulativeChildNodeOffsetRegistry.set(cumulativeInd,
                    cumulativeChildNodeOffsetRegistry.get(cumulativeInd) + 1);
            }
            
            size = container.size();
            
            return insertionInd;
        }
    }
    
    /* !@brief adds multiple elements to one node (arbitrary n-ary key-value pairs on one node) */
    /**
     * It adds a list of key-value pairs into the container from a specified 
     * parent key
     * @param key Parent key identifier
     * @param values New key-value pairs
     * @return Returns the insertion index of last inserted item pair
     * @throws Exception
     *         Empty values parameter
     *         Unordered value pair list
     *         Parent key does not exist
     */
    public int add(K key, ArrayList<Pair<K, V>> values) throws Exception {
    
        if(values.isEmpty()){
            
            throw new NoObjectFoundException("Values parameter is empty.");
        }
        
        int sizeOfValues = values.size() - 1;
        
        for(int i = 0; i < sizeOfValues; ++i){
        
            if(values.get(i).key.compareTo(values.get(i + 1).key) > 0){
            
                throw new UnorderedDataListException("Values are not in order.");
            }
        }
        
        // Only partially ordered keys are placed into nodes. The keys are ordered according to one 
        //  level upper. Multilevel upleveled checks are not available.
        
        if(size == 0){
        
            cumulativeChildNodeOffsetRegistry.set(0, sizeOfValues);
            nodeSizeChildRegistry.set(0, sizeOfValues);
            
            for(int j = 0; j < sizeOfValues; ++j){
            
                container.add(values.get(j));
            
                cumulativeChildNodeOffsetRegistry.add(0);
                // initialize key range            
                nodeSizeChildRegistry.add(0);
            }
            
            size = container.size();
            
            return 0;
        } 
        else{

            int insertionInd = 0;
            int prevShift = 1;
            int i = 0;
            int j = 0;
            int level = 0;
            ArrayList<Integer> nodeShiftIndTrace = new ArrayList<>();
            
            while(nodeSizeChildRegistry.get(i) > 0){
            
                nodeShiftIndTrace.add(i);
                // finding parent key
                for(j = 0; j < nodeSizeChildRegistry.get(i); ++j){
                    
                    // keys are on the same level (hierachical classification)
                    // keeping container ordered by < operator on keys
                    if(key.at(level) < container.get(prevShift + j).key.at(level)){
                    
                        // it also includes case of 0 elements due to 0 offset
                        insertionInd += cumulativeChildNodeOffsetRegistry.get(prevShift + j);
                        nodeShiftIndTrace.set(nodeShiftIndTrace.size() - 1, i);
                    }
                    else if(key.at(level) == container.get(prevShift + j).key.at(level)){
                        
                        i = prevShift + j;// priori index assignment
                        ++level;// go down to next level
                        break;
                    }
                    
                    ++insertionInd;
                }
                
                prevShift = insertionInd;
                
                if(key.len() == container.get(i).key.at(level)){
                
                    // insert new node under selected level
                    break;
                }
                else{
                
                    throw new NoObjectFoundException("No parent key has been found "
                            + "and key length differs from terminated level key length.");
                }
            }
            
            // inserting new child nodes

            int sizeOfShiftIndTrace = nodeShiftIndTrace.size();

            // case of insertion of new nodes
            if(i < nodeSizeChildRegistry.size()){
                
                // adding new nodes next to existing ones (not only leaf level)
                cumulativeChildNodeOffsetRegistry.set(i,
                        cumulativeChildNodeOffsetRegistry.get(i) + sizeOfValues);
                
                nodeSizeChildRegistry.set(i, 
                        nodeSizeChildRegistry.get(i) + sizeOfValues);
            }
            else{
                
                // increasing capacity of storage
                // adding new nodes next to existing ones (not only leaf level)
                cumulativeChildNodeOffsetRegistry.add(prevShift + j, sizeOfValues);
                nodeSizeChildRegistry.add(prevShift + j, sizeOfValues);
            }
            
            // trace length is according to traversal depth
            int cumulativeInd;
            
            for(j = 0; j <sizeOfShiftIndTrace; ++j){
            
                cumulativeInd = nodeShiftIndTrace.get(j);
                cumulativeChildNodeOffsetRegistry.set(cumulativeInd, 
                    cumulativeChildNodeOffsetRegistry.get(cumulativeInd) + sizeOfValues);
            }
            
            for(j = 0; j < sizeOfValues; ++j){
                
                container.add(insertionInd + j, values.get(i));
                nodeSizeChildRegistry.add(insertionInd + j, 0);
                cumulativeChildNodeOffsetRegistry.add(insertionInd + j, 0);
            }
            
            size = container.size();
        
            return insertionInd;            
        }
    }
    
    // order preserving subtree insertion
    /**
     * Merges a tree into current one
     * @param chunk The tree to be unified into the actual one
     * @return Returns the new size of the container
     * @throws Exception 
     *         Empty chunk
     *         Multiple roots
     *         Index out of bounds
     *         Build in container Index out of bounds
     */
    public int mergeToNode(IncArbTree chunk) throws Exception{
    
        if(chunk.size == 0){
        
            throw new NoObjectFoundException("Provided chunk is empty.");
        }
        
        if(size == 0){
        
            cumulativeChildNodeOffsetRegistry = chunk.cumulativeChildNodeOffsetRegistry;
            nodeSizeChildRegistry = chunk.nodeSizeChildRegistry;
            container = chunk.container;
            
            size = chunk.size;
        }
        else{
        
            if(!(chunk.nodeSizeChildRegistry.get(0).equals(1))){
            
                throw new AmbiguousObjectException("Multiple roots have found.");
            }
            
            boolean found = false;
            K rootKey = (K)chunk.getKeyByOrdInd(0);
            int i = 0;
            
            for(; i < size && !found; ++i){
            
                found = rootKey.equals(container.get(i));
            }
            
            if(!found){
            
                throw new NoObjectFoundException("The provided root node can not be "
                        + "found in the current tree.");
            }
            
            ArrayList<K> nodeKeyHistory = new ArrayList<>();
            nodeKeyHistory.add((K)chunk.getKeyByOrdInd(0));
            
            Pair<K, V> node;
            
            chunk.initDFS();
            
            while(chunk.hasNextDFS()){
            
                if(chunk.wasRecentLeaf()){
                
                    nodeKeyHistory.remove(nodeKeyHistory.size() - 1);
                }
                
                node = chunk.getNextItemDFS();
                addOne(nodeKeyHistory.get(nodeKeyHistory.size() - 1), node);
                
                nodeKeyHistory.add(node.key);
            }
        }
        
        return size;
    }
    
    /**
     * Obtains an element value at specified index
     * @param i Index where the desired element must be found, otherwise Exception 
     *        will be thrown
     * @return Returns the found element value
     * @throws Exception 
     *         Index out of bounds
     */
    public V getByLevelOrdInd(int i) throws Exception{
        
        if (i >= size || i < 0) {

            throw new IndexOutOfBoundsException("Index out of bounds.");
        }

        return container.get(i).value;
    }
    
    public V getByInd(){
    
        // todo, recursive procedure, computation heavy
        return null;
    }
    
    /**
     * It sets a new key by its index occurrence. It does not reorder the 
     * container in case of key order inconsistency.
     * Warning: it causes inconsistency, reordering is needed, todo
     * @param i The specified index where the key exists
     * @param key The new key value to be used
     */
    public void setKeyByInd(int i, K key){
    
        Pair<K, V> modPair = container.get(i);
        modPair.key = key;
        container.set(i, modPair);
    }
    
    /**
     * Its sets a new value by its index occurrence.
     * @param i The index that shows the position of the key-value pair
     * @param value The value that replaces the old one
     * @throws Exception 
     *         Index out of bounds
     */
    public void setValByLevelOrdInd(int i, V value) throws Exception{
    
        if (i >= size || i < 0) {

            throw new IndexOutOfBoundsException("Index out of bounds.");
        }

        Pair<K, V> modPair = container.get(i);
        modPair.value = value;
        container.set(i, modPair);
    }
    
    /**
     * Function sets value by index of key-value pair according to linearized 
     * container order in memory
     * @param i Index of key-value pair
     * @param value Value to be assigned to a specific key that is located in the 
     *              desired index in the linearized container
     */
    public void setValByInd(int i, V value){
    
        // todo, recursive procedure, computation heavy
    }
    
    /**
     * It obtains value defined by a given key from the container
     * @param key Key identifier that points to the desired value
     * @return Returns the desired key related value
     * @throws Exception 
     *         The given key does not exist
     */
    public V getByKey(K key) throws Exception{
    
        int insertionInd = 0;
        int prevShift = 1;
        int i = 0;
        int j;
        int level = 0;
        
        while(nodeSizeChildRegistry.get(i) > 0){
            
            for(j = 0; j < nodeSizeChildRegistry.get(i); ++j){
            
                if(key.compareTo(container.get(prevShift + j).key) == 0){
                
                    return container.get(prevShift + j).value;
                }
                
                if(key.at(level) < container.get(prevShift + j).key.at(level)){
                
                    insertionInd += cumulativeChildNodeOffsetRegistry.get(prevShift + j);
                }
                else if(key.at(level) == container.get(prevShift + j).key.at(level)){
                
                    i = prevShift + j;
                    ++level;
                    break;
                }
                
                ++insertionInd;
            }
            
            prevShift = insertionInd;
        }
        
        throw new NoObjectFoundException("Given key has not been found.");
    }
    
    /**
     * Obtains the systematically ordered index by key
     * @param key The key that points to the index
     * @return Returns the index of the key
     * @throws Exception 
     *         Key does not exist
     */
    public int getOrdIndByKey(K key) throws Exception{
    
        int insertionInd = 0;
        int prevShift = 1;
        int i = 0;
        int j;
        int level = 0;
        
        while(nodeSizeChildRegistry.get(i) > 0){
            
            for(j = 0; j < nodeSizeChildRegistry.get(i); ++j){
            
                if(key.compareTo(container.get(prevShift + j).key) == 0){
                
                    return prevShift + j;
                }
                
                if(key.at(level) < container.get(prevShift + j).key.at(level)){
                
                    insertionInd += cumulativeChildNodeOffsetRegistry.get(prevShift + j);
                }
                else if(key.at(level) == container.get(prevShift + j).key.at(level)){
                
                    i = prevShift + j;
                    ++level;
                    break;
                }
                
                ++insertionInd;
            }
            
            prevShift = insertionInd;
        }
        
        throw new NoObjectFoundException("Given key has not been found.");
    }
    
    /**
     * Function returns index of key-value pair by key, in accordance with 
     * linearized container traversal
     * @param key Search key
     * @return Index that belongs to the key-value pair
     */
    public int getIndByKey(K key){
    
        // todo, recursive procedure, computation heavy
        return 0;
    }
    
    /**
     * Obtains the key by its ordered index
     * @param ind The index that points to the desired key
     * @return Returns the requested key
     * @throws Exception 
     *         Index out of bounds
     */
    public K getKeyByOrdInd(int ind) throws Exception{
    
        if (ind >= size || ind < 0) {
            
            throw new IndexOutOfBoundsException("Index out of bounds.");
        }

        return container.get(ind).key;
    }
    
    /**
     * Function return key by certain index in the linearized container 
     * (by linear traversal)
     * @param ind
     * @return key by index
     */
    public K getKeyByInd(int ind){
    
        // todo, recursive procedure, computation heavy
        return null;
    }
    
    /**
     * Obtains key by value. It returns the first occurrence value related key.
     * @param val Value to be found and used associated to key
     * @return Returns the desired key
     * @throws Exception 
     *         Container is empty
     *         Key has not been found
     */
    public K getKeyByVal(V val) throws Exception{
    
        if(size == 0){
        
            throw new NoObjectFoundException("Container is empty.");
        }
        
        // O(n) complexity
        for(int i = 0; i < size; ++i){
        
            if(container.get(i).value == val){

                return container.get(i).key;
            }
        }
        
        throw new NoObjectFoundException("No key has been found with given value.");
    }
    
    /**
     * Function returns infimum key of provided key
     * @param key Provided key for infimum search
     * @return Returns infimum key
     */
    public K lowerKey(K key){
    
        
        // todo
        return null;
    }
    
    /**
     * Function returnss supremum key of provided key
     * @param key Provided key for supremum search
     * @return Returns supremum key
     */
    public K upperKey(K key){
    
        // todo
        return null;
    }
    
    /**
     * Function returns specific level that where the provided key belongs
     * @param key Provided key for level search
     * @return Level that where the key is on
     */
    public int getLevelByKey(K key){
    
        // todo
        return 0;
    }
    
    /**
     * Function return the number inserted elements in the linearized container
     * @return Number of inserted elements
     */
    public int size(){
    
        return size;
    }
    
    /**
     * It changes the allocated memory capacity of container.
     * @param resSize New memory size in terms of objects
     * @throws Exception 
     *         Capacity underflow due to under-defined memory size allocation
     */
    public void reserve(int resSize) throws Exception{
    
        if(size >= resSize){
        
            throw new CapacityUnderloadException("New reserved size is less than current "
                    + "size of container.");
        }
        
        container.ensureCapacity(resSize);
    }
    
    public boolean isEmpty(){
    
        return size == 0;
    }
    
    // suboptimal, optimization is needed
    /**
     * It sets new root by a key
     * @param key Key of desired new root node
     * @throws Exception 
     *         Root key does not exist
     */
    public void setNewRootByKey(K key) throws Exception{
    
        // removing prefix subtrees and suffix subtrees
        
        int rootInd = getOrdIndByKey(key);

        // getting new size of container
        int newSize = cumulativeChildNodeOffsetRegistry.get(rootInd);
        
        for(int i = 0; i < newSize; ++i){
        
            container.set(
                    i, container.get(rootInd + i));
            nodeSizeChildRegistry.set(
                    i, nodeSizeChildRegistry.get(rootInd + i));
            cumulativeChildNodeOffsetRegistry.set(
                    i, cumulativeChildNodeOffsetRegistry.get(rootInd + i));
        }
        
        for(int i = newSize + 1; i < size; ++i){
        
            container.remove(i);
            nodeSizeChildRegistry.remove(i);
            cumulativeChildNodeOffsetRegistry.remove(i);
        }
        
        size = newSize;
    }
    
    /**
     * Function returns the raw used underlying container of this advanced data 
     * type class
     * @return Underlying linear container
     */
    public ArrayList<Pair<K, V>> getContainer(){
    
        return container;
    }
    
    /**
     * Function removes all inserted elements from the container
     */
    public void removeAll(){
    
        cumulativeChildNodeOffsetRegistry.clear();
        nodeSizeChildRegistry.clear();
        container.clear();
    }
    
    // DFS methods
    /**
     * Function executes the preprocessing steps of Depth First Search
     */
    public void initDFS(){
    
        nodeIndHist = new ArrayList<>();
        childNodeIndHist = new ArrayList<>();

        if(size > 0){
        
            nodeIndHist.add(0);
            childNodeIndHist.add(0);
        }
        
        travI = 0;
        travJ = 0;
        prevIndShift = 1;
        retI = 0;
        wasStepBack = false;
        numOfCumulatedStepBacks = 0;
    }
    
    /**
     * Function obtains the next element by DFS mechanism if exists, if not, 
     * then returns empty key-value pair
     * @return Next element
     */
    public Pair<K, V> getNextItemDFS(){

        if(size == 0){
        
            return new Pair<>();
        }
        
        retI = travI;
        
        // posteriori node evaluation in aspect of child nodes
        if(nodeSizeChildRegistry.get(travI) > 0){
        
            // priori child selection
            // traversal on child elements
            
            if(!wasStepBack){
            
                travJ = 0;
            }
            
            if(travJ < nodeSizeChildRegistry.get(travI)){
            
                // level ordered linearized node sequence cumulative index shift maintenance
                prevIndShift += cumulativeChildNodeOffsetRegistry.get(prevIndShift + travJ);
                
                // nodes with empty child node list are going to filtered, 
                //  step backs are going to be handled
                // step forward to next child node
                nodeIndHist.add(travI);
                childNodeIndHist.add(travJ);
                travI = prevIndShift + travJ;
                
                wasStepBack = false;
            }
            else{
                
                // all child has been processed, going to next child of 2nd level upper parent child
                //  using step back
                if(nodeIndHist.isEmpty()){
                
                     // returned to root, DFS has been ended
                    return container.get(travI);
                }
                
                travI = nodeIndHist.get(nodeIndHist.size() - 1);
                nodeIndHist.remove(nodeIndHist.size() - 1);
                travJ = childNodeIndHist.get(childNodeIndHist.size() - 1);
                childNodeIndHist.remove(childNodeIndHist.size() - 1);
                prevIndShift -= cumulativeChildNodeOffsetRegistry.get(travJ);
                ++travJ;
                
                if(wasStepBack) ++numOfCumulatedStepBacks;
                else numOfCumulatedStepBacks = 1;
                
                wasStepBack = true;
            }
        }
        else if(nodeSizeChildRegistry.get(travI) == 0){
        
            // leaf level has been reached, step back occurs
            // (termination condition of traversal is at the beginning of this stepping method)
            if(nodeIndHist.isEmpty()) {
            
                // returned to root, DFS has been ended
                return container.get(travI);
            }
            
            travI = nodeIndHist.get(nodeIndHist.size() - 1);
            nodeIndHist.remove(nodeIndHist.size() - 1);
            travJ = childNodeIndHist.get(childNodeIndHist.size() - 1);
            childNodeIndHist.remove(childNodeIndHist.size() - 1);
            prevIndShift -= cumulativeChildNodeOffsetRegistry.get(travJ);
            ++travJ;
            
            if(wasStepBack) ++numOfCumulatedStepBacks;
            else numOfCumulatedStepBacks = 1;
            
            wasStepBack = true;
        }
        
        return container.get(retI);
    }
    
    /**
     * Function returns the whether there is next element or not in the tree
     * @return True if next element exists
     */
    public boolean hasNextDFS(){
    
        return !nodeIndHist.isEmpty();
    }
    
    /**
     * Function obtains the information about whether the recent element that was 
     * provided was on the leaf level, therefore ends the trace, step back is 
     * required or not
     * @return 
     */
    public boolean wasRecentLeaf(){
    
        return wasStepBack;
    }
    
    /**
     * Function returns the number of step back in a row
     * @return Number of step backs
     */
    public int getNumOfRecentStepBacks(){
        
        return numOfCumulatedStepBacks;
    }

    /**
     * Obtains keys on a specified level
     * @param levelId Identifier of the level starting from 0
     * @return Returns the found level keys in list
     */
    public ArrayList<K> getLevelKeys(int levelId){

        ArrayList<K> levelKeys = new ArrayList<>();
        
        if(size <= levelId){
        
            return levelKeys;
        }
        
        ++levelId;
        K itemKey;

        // suboptimal, linear iteration is performed
        
        if(levelId < size / 2){
        
            for(int i = 0; i < size; ++i){

                itemKey = container.get(i).key;

                if(itemKey.len() == levelId){

                    levelKeys.add(itemKey);
                }
            }
        }
        else{
        
            for(int i = size - 1; i >= 0; --i){
        
                itemKey = container.get(i).key;

                if(itemKey.len() == levelId){

                    levelKeys.add(itemKey);
                }
            }
        }

        return levelKeys;
    }

    /**
     * Obtains the leaf level keys
     * @return Returns the leaf level keys, warning: this request does not 
     *         perform a single level leaf node list acquirement
     */
    public ArrayList<K> getLeafLevelKeys(){
    
        ArrayList<K> leafLevelKeys = new ArrayList<>();
        
        // it also includes leaf nodes on different tree levels
        for(int i = size - 1; i >= 0; --i){
        
            if(nodeSizeChildRegistry.get(i) == 0){
            
                leafLevelKeys.add(0, container.get(i).key);
            }
        }
        
        return leafLevelKeys;
    }
    
    /**
     * Function returns the aggregated size of leaf level. Different level of 
     * leafs are also included.
     * @return Size of leaf level
     */
    public int getLeafLevelSize(){
    
        int leafLevelSize = 0;
        
        for(int i = 0; i < size; ++i){
        
            if(nodeSizeChildRegistry.get(i) == 0){
            
                ++leafLevelSize;
            }
        }
        
        return leafLevelSize;
    }
}
