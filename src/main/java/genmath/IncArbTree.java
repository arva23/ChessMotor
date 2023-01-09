package genmath;

import genmath.LinTreeMap.Pair;
import java.util.ArrayList;

public class IncArbTree<K extends ComparableKey<K>, V> {

    private int prevStep;
    private int leafLevel;
    
    // traversal
    private int travI;
    private double travStep;
    private int travLen;
    private boolean wasStepBack;
    private ArrayList<Boolean> stepDirHist;
    private ArrayList<Double> stepHist;
    private ArrayList<Integer> stepIndHist;
    private int numOfCumulatedStepBacks;

    
    // stores node division quantity, the number hash keys to be stored in a node, these 
    //  quantites/nubmbers can be different from each oter
    ArrayList<Integer> nodeRegistry;
    
    ArrayList<Pair<K, V>> container;// level ordered list, starting from root
    int size;
    
    public IncArbTree(){

        size = 0;
        nodeRegistry = new ArrayList<Integer>();
        nodeRegistry.add(0);
        container = new ArrayList<Pair<K, V>>();
        
        prevStep = 0;
        leafLevel = 0;
        
        travI = 0;
        travStep = 0.0;
        travLen = 0;
        wasStepBack = false;
        stepDirHist = new ArrayList<Boolean>();
        stepHist = new ArrayList<Double>();
        stepIndHist = new ArrayList<Integer>();
        numOfCumulatedStepBacks = 0;
    }
    
    public IncArbTree(IncArbTree<K, V> orig){
    
        this.size = orig.size;
        this.nodeRegistry = orig.nodeRegistry;
        this.container = orig.container;
        
        this.prevStep = orig.prevStep;
        this.leafLevel = orig.leafLevel;

        this.travI = orig.travI;
        this.travStep = orig.travStep;
        this.travLen = orig.travLen;
        this.wasStepBack = orig.wasStepBack;
        this.stepDirHist = orig.stepDirHist;
        this.stepHist = orig.stepHist;
        this.stepIndHist = orig.stepIndHist;
        this.numOfCumulatedStepBacks = orig.numOfCumulatedStepBacks;
    }
    
    
    /* !@brief adds multiple elements to one node (arbitrary n-ary key-value pairs on one node)
    
    */
    public int add(K key, ArrayList<Pair<K, V>> values) throws Exception{
    
        // todo
        return 0;
    }
    
    public int setOrAddByKey(K key, ArrayList<Pair<K, V>> values){
    
        // todo
        return 0;
    }
    
    public V getByOrdInd(){
    
        // todo
        return null;
    }
    
    public V getByInd(){
    
        // todo, recursive procedure, computation heavy
        return null;
    }
    
    public void setValByOrdInd(int i, V value){
    
        // todo
    }
    
    public void setValByInd(int i, V value){
    
        // todo
    }
    
    public V getByKey(K key){
    
        // todo
        return null;
    }
    
    public int getOrdIndByKey(K key){
    
        // todo
        return 0;
    }
    
    public int getIndByKey(K key){
    
        // todo
        return 0;
    }
    
    public K getKeyByOrdInd(int ind){
    
        // todo
        return null;
    }
    
    public K getKeyByInd(int ind){
    
        // todo
        return null;
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
    
        container.clear();
    }
    
    // DFS methods
    public void initDFS(){
    
        // todo
    }
    
    public Pair<K, V> getNextItemDFS(){
    
        // todo
        return null;
    }
    
    public boolean hasNextDFS(){
    
        return travLen > 0 && stepDirHist.get(0);
    }
    
    public boolean wasRecentLeaf(){
    
        return wasStepBack;
    }
    
    public int getNumOfRecentStepBacks(){
    
        return numOfCumulatedStepBacks;
    }
}
