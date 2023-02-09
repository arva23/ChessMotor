package genmath;

import genmath.LinTreeMap.Pair;
import java.util.ArrayList;

public class IncArbTree<K extends ComparableKey<K>, V> {
    
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
        cumulativeChildNodeOffsetRegistry = new ArrayList<Integer>();
        cumulativeChildNodeOffsetRegistry.add(0);
        nodeSizeChildRegistry = new ArrayList<Integer>();
        nodeSizeChildRegistry.add(0);
        container = new ArrayList<Pair<K, V>>();
        
        nodeIndHist = new ArrayList<Integer>();
        childNodeIndHist = new ArrayList<Integer>();
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
            ArrayList<Integer> nodeShiftIndTrace = new ArrayList<Integer>();
            
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
                
                if(key.len() == container.get(prevShift + j).key.at(level)){
                
                    // insert new node onto selected level
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
    
    public V getByLevelOrdInd(int i) throws Exception{
        
        if (i >= size) {

            throw new Exception("Index out of bounds.");
        }

        if (container.get(i).key.isPlaceholder()) {

            throw new Exception("Item is placeholder.");
        }

        return container.get(i).value;
    }
    
    public V getByInd(){
    
        // todo, recursive procedure, computation heavy
        return null;
    }
    
    public void setKeyByInd(int i, K key){
    
        IncBinTree.Pair<K, V> modPair = container.get(i);
        modPair.key = key;
        container.set(i, modPair);
    }
    
    public void setValByLevelOrdInd(int i, V value) throws Exception{
    
        if (i >= size) {

            throw new Exception("Index out of bounds.");
        }

        if (container.get(i).key.isPlaceholder()) {

            throw new Exception("Item is placeholder.");
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
    
        if (ind >= size) {
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
    
    public boolean isEmpty(){
    
        return size == 0;
    }
    
    public void setNewRootByKey(K key){
    
        // todo
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
    
        nodeIndHist = new ArrayList<Integer>();
        childNodeIndHist = new ArrayList<Integer>();

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
        
            return new Pair<K, V>();
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

        ArrayList<K> levelKeys = new ArrayList<K>();

        // todo

        return levelKeys;
    }

    public ArrayList<K> getLeafLevelKeys(){
    
        ArrayList<K> leafLevelKeys = new ArrayList<K>();
        
        // todo
        
        return leafLevelKeys;
    }
}
