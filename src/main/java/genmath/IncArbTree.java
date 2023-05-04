package genmath;

import java.util.ArrayList;

// linearized tree Vs nested object oriented tree
//  nested version (reference):
//    + Low cost of insertion of new node [no previous element shift, arbitrary 
//      (system defined memory alloction] simple allocation and reference creation)
//    + Logarithmic key lookup requires less command (no index offsets)
//      (time complexity is the same with the other version)
//    + Root displacement is cheap (simple reference dislocation)
//    + Node removal is cheap (just detach the certain object from reference 
//      likage chain)
//    - Node obtain by direct, specialized order based sort index is logarithmic 
//      instead of constant
//    - Whole level obtain requires entire traversal of tree (including leaf level)
//    ? Leaf level direct access could be available in case of a <recent leaf 
//      node> container if at least one traversal has performed at arbitrary 
//      method that contains traversal, This would cause slower add method (
//    - Insertion cost is high due to ad-hoc memory allocation
//
//  linearized version (ArrayList):
//    - High cost of insertion of new node [previous element shift occurs and 
//      allocation of new array in case of run out of enough memory] advanced 
//      allocation and reference creation)
//    - Logarithmmic key lookup requires more command (index offsets)
//      (time complexity is the same with the other version)
//    - Root displacement is expensive (array modification occurs as at the insertion)
//    - Node removal is expensive (detachment of node requires offset indexing shifts)
//    + Node obtain by direct, specialized order based sort index is constant instead 
//      of logarithmic or else
//    + Whole level obtain requires forward index offset jump traversal on array
//    + Leaf level obtain requires backward traversal on container array 
//      (the more the node number and equated [homogeneous] leaf level[s], the 
//      less the level obtainment)
//    + Option for preallocation in order to save allocation cost at insertion


// The required implementation depends on purpose
//  insertion time (overwhelmingly modification of tree)
//  ability to faster conversion to other formats (e.g. graph matrix)
//  subgraph operation speeds, etc..

public class IncArbTree<K extends ComparableKey<K>, V> {
    
    // pair auxiliary type
    public static class Pair<K extends ComparableKey<K>, V> implements Comparable<Pair<K, V>> {

        public Pair() {

            this.key = null;
            this.value = null;
        }

        public Pair(K key, V value) throws Exception {

            if (key == null) {
                throw new Exception("Key is null.");
            }

            this.key = key;

            if (value == null) {
                throw new Exception("Value is null.");
            }
            this.value = value;
        }

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
    
    
    public int addOne(K key, Pair<K, V> values) throws Exception{
    
        return addOne(key, values.key, values.value);
    }
    
    /* !@brief adds single element to one node (arbitrary n-ary key-value pair on one node) */
    public int addOne(K key, K nKey, V value) throws Exception{
    
        if(nKey == null || value == null){
        
            throw new Exception("Key-value parameter is null.");
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
            int j = 0;
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
                
                    throw new Exception("No parent key has been found and key" 
                            + "length differs from terminated level key length.");
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
            
                throw new Exception("Key duplication is not allowed.");
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
            int cumulativeInd = 0;
            
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
    public int add(K key, ArrayList<Pair<K, V>> values) throws Exception{
    
        if(values.isEmpty()){
            
            throw new Exception("Values parameter is empty.");
        }
        
        int sizeOfValues = values.size() - 1;
        
        for(int i = 0; i < sizeOfValues; ++i){
        
            if(values.get(i).key.compareTo(values.get(i + 1).key) > 0){
            
                throw new Exception("Values are not in order.");
            }
        }
        
        // Only partially ordered keys are placed into nodes. The keys are ordered according to one 
        //  level upper. Multilevel upleveled checks are not available.
        
        int cmpRes = 0;
        
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
                
                    throw new Exception("No parent key has been found and key " 
                            + "length differs from terminated level key length.");
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
            int cumulativeInd = 0;
            
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
    public int mergeToNode(IncArbTree chunk) throws Exception{
    
        if(chunk.size == 0){
        
            throw new Exception("Provided chunk is empty.");
        }
        
        if(size == 0){
        
            cumulativeChildNodeOffsetRegistry = chunk.cumulativeChildNodeOffsetRegistry;
            nodeSizeChildRegistry = chunk.nodeSizeChildRegistry;
            container = chunk.container;
            
            size = chunk.size;
        }
        else{
        
            if(!(chunk.nodeSizeChildRegistry.get(0).equals(1))){
            
                throw new Exception("Multiple roots have found.");
            }
            
            boolean found = false;
            K rootKey = (K)chunk.getKeyByOrdInd(0);
            int i = 0;
            
            for(; i < size && !found; ++i){
            
                found = rootKey.equals(container.get(i));
            }
            
            if(!found){
            
                throw new Exception("The provided root node can not be found in the current tree.");
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
    
    public V getByLevelOrdInd(int i) throws Exception{
        
        if (i >= size || i < 0) {

            throw new Exception("Index out of bounds.");
        }

        return container.get(i).value;
    }
    
    public V getByInd(){
    
        // todo, recursive procedure, computation heavy
        return null;
    }
    
    // warning: it causes inconsistency, reordering is needed, todo
    public void setKeyByInd(int i, K key){
    
        Pair<K, V> modPair = container.get(i);
        modPair.key = key;
        container.set(i, modPair);
    }
    
    public void setValByLevelOrdInd(int i, V value) throws Exception{
    
        if (i >= size || i < 0) {

            throw new Exception("Index out of bounds.");
        }

        Pair<K, V> modPair = container.get(i);
        modPair.value = value;
        container.set(i, modPair);
    }
    
    public void setValByInd(int i, V value){
    
        // todo, recursive procedure, computation heavy
    }
    
    public V getByKey(K key) throws Exception{
    
        int insertionInd = 0;
        int prevShift = 1;
        int i = 0;
        int j = 0;
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
        
        throw new Exception("Given key has not been found.");
    }
    
    public int getOrdIndByKey(K key) throws Exception{
    
        int insertionInd = 0;
        int prevShift = 1;
        int i = 0;
        int j = 0;
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
        
        throw new Exception("Given key has not been found.");
    }
    
    public int getIndByKey(K key){
    
        // todo, recursive procedure, computation heavy
        return 0;
    }
    
    public K getKeyByOrdInd(int ind) throws Exception{
    
        if (ind >= size || ind < 0) {
            throw new Exception("Index out of bounds.");
        }

        return container.get(ind).key;
    }
    
    public K getKeyByInd(int ind){
    
        // todo, recursive procedure, computation heavy
        return null;
    }
    
    public K getKeyByVal(V val) throws Exception{
    
        if(size == 0){
        
            throw new Exception("Container is empty.");
        }
        
        // O(n) complexity
        for(int i = 0; i < size; ++i){
        
            if(container.get(i).value == val){

                return container.get(i).key;
            }
        }
        
        throw new Exception("No key has been found with given value.");
    }
    
    public K lowerKey(K key){
    
        
        // todo
        return null;
    }
    
    public K upperKey(K key){
    
        // todo
        return null;
    }
    
    public int getLevelByKey(K key){
    
        // todo
        return 0;
    }
    
    public int size(){
    
        return size;
    }
    
    public void reserve(int resSize) throws Exception{
    
        if(size >= resSize){
        
            throw new Exception("New reserved size is less than current size of container.");
        }
        
        container.ensureCapacity(resSize);
    }
    
    public boolean isEmpty(){
    
        return size == 0;
    }
    
    // suboptimal, optimization is needed
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
    
    public ArrayList<Pair<K, V>> getContainer(){
    
        return container;
    }
    
    public void removeAll(){
    
        cumulativeChildNodeOffsetRegistry.clear();
        nodeSizeChildRegistry.clear();
        container.clear();
    }
    
    // DFS methods
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
    
    public boolean hasNextDFS(){
    
        return !nodeIndHist.isEmpty();
    }
    
    public boolean wasRecentLeaf(){
    
        return wasStepBack;
    }
    
    public int getNumOfRecentStepBacks(){
        
        return numOfCumulatedStepBacks;
    }

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
