package chessmotor.enginecontroller;

import chessmotor.enginecontroller.interfaces.ModularObject;
import chessmotor.view.IConsoleUI;
import genmath.IncArbTree;
import genmath.LinTreeMultiMap;
import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;

/**
 * A step sequence generator class that builds all step chain, sequence along 
 * the available previous step options in aware of restrictions
 * 
 * @author arva
 */
public class StepDecisionTree implements Runnable, ModularObject{
    
    private IConsoleUI consoleUI;
    
    private IncArbTree<GenStepKey, Step> stepDecisionTree;
    
    private boolean machineBegins;
    private PieceContainer piecesRef;
    private Stack<Step> stepHistoryRef;
    private GameBoardData gameBoardRef;
    
    // number of steps to be generated (look ahead with N steps), as an upper 
    // bound of step sequence generation
    private int depth;
    
    private int cumulativeNegativeChangeThreshold;
    
    // Negative tendency threshold in step sequences. If difference of two human 
    // score values are greater than a threshold, drop step sequence.
    private double minConvThreshold;
    
    // in order to restore consistency for peer (by level) steps
    private ArrayList<Step> stepHistoryStack;
    private ArrayList<String> keyHistoryStack;

    private ArrayList<Step> leafMachineSteps;
    private ArrayList<String> leafMachineKeys;
    private ArrayList<Step> leafHumanSteps;
    private ArrayList<String> leafHumanKeys;
    
    private double maxPlayerPieceScore;
    
    private ArrayList<ArrayList<Integer>> gameBoardHistoryContinuation;
    
    private int fracs;
    private int no;
    
    private TreeMap<String, Stack<Integer>> removedHumanPiecesContinuation;
    private TreeMap<String, Stack<Integer>> removedMachinePiecesContinuation;
    
    /**
     * Constructor for initialization of step decision tree object
     * @param consoleUI Console line message manager, especially for error messages
     * @param machineBegins The machine player begins or not
     * @param pieces The container object that stores the available pieces for 
     *        step sequence generation
     * @param stepHistory It contains the taken steps beginning from the starting 
     *        point
     * @param gameBoard The game board storage object
     * @param depth The maximum size of step sequence starting from the actual step
     * @param maxPlayerPieceScore maximum score of player piece that can be achieved 
     *        at a single step
     * @param cumulativeNegativeChangeThreshold Number of consecutive negative 
     *        general player score change
     * @param minConvThreshold Threshold of negative tendency trigger
     * @param fracs The number of parallel builder executor StepDecisionTree objects
     * @param no The identifier of parallel builder executor among all
     * @param memLimit Memory limit for generation in order to confine memory usage
     */
    public StepDecisionTree(IConsoleUI consoleUI, boolean machineBegins, 
            PieceContainer pieces, Stack<Step> stepHistory, GameBoardData gameBoard, 
            int depth, double maxPlayerPieceScore, int cumulativeNegativeChangeThreshold,
            double minConvThreshold, int fracs, int no, long memLimit){
    
        super();
        
        this.consoleUI = consoleUI;
        
        stepDecisionTree = new IncArbTree<>();
        this.machineBegins = machineBegins;
        
        if(pieces == null){
        
            throw new RuntimeException("Piece storage is null.");
        }
        
        this.piecesRef = pieces;
        
        if(stepHistory == null){
        
            throw new RuntimeException("Step history is null.");
        }
        
        this.stepHistoryRef = stepHistory;
        
        if(gameBoard == null){
        
            throw new RuntimeException("Game board is null.");
        }
        
        this.gameBoardRef = gameBoard;
        
        if(minConvThreshold < 0.0){
        
            throw new RuntimeException("Human score increase slope must "
                    + "be positive.");
        }
        
        this.minConvThreshold = minConvThreshold;
        
        
        if(depth < 1){
        
            throw new RuntimeException("Provided depth is not enough.");
        }
        
        // +1 for 0th start step, initiate board positions
        this.depth = depth;
        
        if(maxPlayerPieceScore <= 0){
        
            throw new RuntimeException("Provided maximum score of player "
                    + "piece is less than or equal to 0.");
        }
        
        this.maxPlayerPieceScore = maxPlayerPieceScore;
        
        // TODO limit maximum depth according to available resources
        //      (mostly memory and response time by computation speed)
        
        if(cumulativeNegativeChangeThreshold < 1){
        
            throw new RuntimeException("Consequent cumulative negative "
                    + "change limit is under 1.");
        }
        
        this.cumulativeNegativeChangeThreshold = cumulativeNegativeChangeThreshold;
    
        if(fracs < 0){
        
            throw new RuntimeException("Negative fractional value.");
        }
        
        this.fracs = fracs;
        
        if(no >= fracs){
        
            throw new RuntimeException("Number of fractions are less than the"
                    + " provided fractional identifier.");
        }
    
        this.no = no;
        
        stepHistoryStack = new ArrayList<>();
        keyHistoryStack = new ArrayList<>();
        
        leafMachineSteps = new ArrayList<>();
        leafMachineKeys = new ArrayList<>();
        leafHumanSteps = new ArrayList<>();
        leafHumanKeys = new ArrayList<>();
        
        gameBoardHistoryContinuation = new ArrayList<>();
        
        removedHumanPiecesContinuation = new TreeMap<>();
        removedMachinePiecesContinuation = new TreeMap<>();
        
        if(memLimit <= 0){
        
            throw new RuntimeException("Ill defined memory availability.");
        }
    }
    
    /**
     * Copy constructor for this class
     * @param orig Origin of the current object to be copied from
     */
    public StepDecisionTree(StepDecisionTree orig){
    
        if(orig == null){
        
            throw new RuntimeException("Original object is null.");
        }
        
        this.stepDecisionTree = orig.stepDecisionTree;
        this.machineBegins = orig.machineBegins;
        this.piecesRef = orig.piecesRef;
        this.stepHistoryRef = orig.stepHistoryRef;
        this.gameBoardRef = orig.gameBoardRef;
        this.depth = orig.depth;
        this.maxPlayerPieceScore = orig.maxPlayerPieceScore;
        this.cumulativeNegativeChangeThreshold = orig.cumulativeNegativeChangeThreshold;
        this.minConvThreshold = orig.minConvThreshold;
        
        this.stepHistoryStack = orig.stepHistoryStack;
        this.keyHistoryStack = orig.keyHistoryStack;
        
        this.leafMachineSteps = orig.leafMachineSteps;
        this.leafMachineKeys = orig.leafMachineKeys;
        this.leafHumanSteps = orig.leafHumanSteps;
        this.leafHumanKeys = orig.leafHumanKeys;
        this.gameBoardHistoryContinuation = orig.gameBoardHistoryContinuation;
        
        this.fracs = orig.fracs;
        this.no = orig.no;
        
        this.removedHumanPiecesContinuation = orig.removedHumanPiecesContinuation;
        this.removedMachinePiecesContinuation = orig.removedMachinePiecesContinuation;
    }
    
    
    /**
     * Returns the number of steps
     * @return number of steps
     */
    public int size(){
    
        return stepDecisionTree.size();
    }
    
    /**
     * Get step related keys on specified level
     * @param levelId Level identifier
     * @return Level specific keys
     */
    public ArrayList<GenStepKey> getLevelKeys(int levelId){

        return stepDecisionTree.getLevelKeys(levelId);
    }

    /**
     * Get step related keys on leaf level
     * @return Leaf level keys
     */
    public ArrayList<GenStepKey> getLeafLevelKeys(){
    
        return stepDecisionTree.getLeafLevelKeys();
    }
    
    /**
     * Get certain step by its assigned key identifier
     * @param key Key identifier of step
     * @return Requested step
     * @throws Exception 
     *         No step has been found with the given key, 
     *         No such key exists
     */
    public Step getByKey(GenStepKey key) throws Exception{
    
        return stepDecisionTree.getByKey(key);
    }
    
    /**
     * Set new origin step to the built tree, shift the tree root to the give 
     * step by its specified key
     * @param key New root step related key
     * @throws Exception
     *         No such key exists
     */
    public void setNewRootByKey(GenStepKey key) throws Exception{
    
        stepDecisionTree.setNewRootByKey(key);
    }
    
    /**
     * Assign this object to a certain parallel tree chunk builder executor
     * @param newNo Identifier of the new thread object (this)
     * @throws Exception 
     *         Executor identifier is out of range
     */
    public void setFracNo(int newNo) throws Exception{
    
        if(newNo < 0 || newNo >= fracs){
    
            throw new Exception("Fraction identifier is out of range.");
        }
        
        this.no = newNo;
    }
    
    /**
     * Obtains the preset depth.
     * @return The current depth
     */
    public int getDepth(){
    
        return depth;
    }
    
    /**
     * Obtains the actually altered tree depth involving step sequence 
     * termination induced depth shortages
     * @return Live depth by tree evaluation
     */
    public int getCurrDepth(){
    
        // suboptimal
        int sizeOfStepSequences = stepDecisionTree.size();
        int newDepth = 0;
        int len;
        
        for(int i = sizeOfStepSequences - 1; i >= 0; --i){
            
            len = stepDecisionTree.getKeyByInd(0).val.length();

            if(newDepth < len){
                
                newDepth = len;
            }
        }
        
        return depth = newDepth;
    }
    
    /**
     * Obtains the step container
     * @return Returns an incomplete n-ary arbitrary tree
     */
    public IncArbTree<GenStepKey, Step> getContainer(){
    
        return stepDecisionTree;
    }
    
    /**
     * Sets positive depth
     * Improvement: dynamic depth variation according to recent game status scores
     * @param depth The new depth
     * @throws Exception 
     */
    public void setDepth(int depth) throws Exception{
    
        // todo Define upper bound according to available memory which can be 
        //  used for operations
        if(depth < 1){
        
            throw new RuntimeException("Step sequence depth is less than 1.");
        }
        
        this.depth = depth;
    }
    
    /**
     * Trims keys in order to prevent infinite key length growth
     */
    public void trimKeys(){
    
        // infinite node indexing resolution due to root diplacement 
        //  (root level removal)
        
        // simple prefix trimming
        int sizeOfStepSequences = stepDecisionTree.size();
        int trimEndInd = stepHistoryRef.size() - 1 - 1;
        String prefixTrimmedKeyValue; 
        
        for(int i = 0; i < sizeOfStepSequences; ++i){
        
            prefixTrimmedKeyValue = stepDecisionTree.getKeyByInd(i).val.substring(
                    1, trimEndInd);
            
            stepDecisionTree.setKeyByInd(i, new GenStepKey(prefixTrimmedKeyValue));
        }
    }
    
    /**
     * Unites two StepDecisionTree object to complete parallel tree builder 
     * executor results
     * @param chunk The other chunk to be merged into the current one
     * @throws Exception
     *         Storage reservation underflow, 
     *         Chunk is empty,
     *         Multiple roots have been found, 
     *         Specified root key has not been found
     */
    @Override
    public void unite(ModularObject chunk) throws Exception{
    
        StepDecisionTree convertedChunk = (StepDecisionTree)chunk;
        
        // expand container using upper estimation of number of nodes respect 
        //  to potentially reserved (in future) memory
        stepDecisionTree.reserve(stepDecisionTree.size() + convertedChunk.stepDecisionTree.size());
        
        stepDecisionTree.mergeToNode(convertedChunk.stepDecisionTree);
    }
    
    /**
     * It divides current object into equal chunks by given number of chunks 
     * wanted to be created.
     * @param numOfChunks Number of equally distributed chunks to be created.
     * @return It returns the created chunks.
     * @throws Exception 
     */
    @Override
    public ArrayList<ModularObject> split(int numOfChunks) throws Exception{
    
        ArrayList<ModularObject> result = new ArrayList<>();
        
        // todo
   
        return result;
    }
    
    /**
     * It divides current object into predefined chunks by given ratio values. 
     * Sum of the ratio values must be 1.
     * @param chunkRatios Chunk ratios, the occupied data from the whole by the chunks.
     * @return It returns the created chunks.
     * @throws Exception 
     */
    @Override
    public ArrayList<ModularObject> split(ArrayList<Double> chunkRatios) throws Exception{
    
        
        // todo
    
        return result;
    }
    
    /**
     * Inserts a single key-step pair into the tree
     * @param whereKey The parent key, Key to let the key-step pair be 
     *        inserted from
     * @param key New key related to new step
     * @param step New step related to new key
     * @throws Exception
     *         Key-value parameter is null, 
     *         No parent key has been found,
     *         Provided key to the step has already been existed/inserted
     */
    public void addOne(GenStepKey whereKey, GenStepKey key, Step step) throws Exception{
    
        stepDecisionTree.addOne(whereKey, key, step);
    }
    
    /**
     * Inserts a key-step pair onto the history stacks for further processing
     * @param key Step related key
     * @param step Key related step
     */
    public void addToHistoryStack(String key, Step step){
    
        stepHistoryStack.add(step);
        keyHistoryStack.add(key);
    }
    
    /**
     * Reserves memory in advance in order to prevent suboptimal multiple storage 
     * reallocation during altering operation
     * @param resMemSize The new size of storage by element numbers
     * @throws Exception 
     *         Capacity size under flow comparing to recently used
     */
    public void reserveMem(int resMemSize) throws Exception{
    
        stepDecisionTree.reserve(resMemSize);
    }
    
    /**
     * Generate first machine step in both scenarios of machine begins or not
     * @throws Exception
     *         Rank or file is out of range, 
     *         Addition error (index out of bound),
     *         Index out of bounds at LinTreeMap,
     *         Key-value is null, no parent key has been found, 
     *         Key duplication/redundancy/preexisting key
     */
    public void generateFirstMachineStep() throws Exception {

        LinTreeMultiMap<GenTmpStepKey, Step> sortedGeneratedSteps = 
            new LinTreeMultiMap<>();

        Step step;
        
        for(int i = 0; i < 16; ++i){

            if(!(piecesRef.get(i).generateSteps(gameBoardRef)).isEmpty()){

                step = new Step("standard", i, 1 - (int)Math.floor(i / 8), i % 8,
                piecesRef.get(i).getValue(), 0,
                piecesRef.get(i).getValue());

                sortedGeneratedSteps.add(
                        new GenTmpStepKey(piecesRef.get(i).getValue()), step);
            }
        }

        // suboptimal strategy of initial step
        //  (strategies can be added by weighthening graph later)
        int selectedPieceInd = 
            (int)((double)Math.random() * (double)(sortedGeneratedSteps.size() - 1));

        step = sortedGeneratedSteps.getByInd(selectedPieceInd);

        if(machineBegins){
        
            stepDecisionTree.addOne(new GenStepKey("a"), 
                new GenStepKey("a"), step);
            
            // saving previous level status
            stepHistoryStack.add(step);
            keyHistoryStack.add("a");
        }
        else{
        
            stepDecisionTree.addOne(new GenStepKey("a"), 
                new GenStepKey("aa"), step);
        
            // saving previous level status
            stepHistoryStack.add(step);
            keyHistoryStack.add("aa");
        }
        
        // modify game table status
        gameBoardRef.set(step.getRank(), step.getFile(), step.getPieceId());
    }
    
    /**
     * Special step case evaluator: castling and promotion processing
     * @param key parent key related to parent step
     * @param step parent step related to parent key
     * @param humanSide whether human or machine player comes in this processing 
     *        loop
     * @param currRemovedHumanPieces auxiliary removed pieces container for 
     *        human player
     * @param currRemovedMachinePieces auxiliary removed pieces container for
     *        machine player
     * @param sortedGeneratedSteps temporary container where the potentially passed 
     *        next child steps are inserted into
     */
    private void evaluateSpecialStepCases(String key, Step step, Boolean humanSide,
            Stack<Integer> currRemovedHumanPieces, 
            Stack<Integer> currRemovedMachinePieces, 
            LinTreeMultiMap<GenTmpStepKey, Step> sortedGeneratedSteps){
        
        
        double value;
        Step allocatedGeneratedStep;
    
        // special step case: castling option
        if(piecesRef.get(step.getPieceId()).getTypeName().contains("king")
                || piecesRef.get(step.getPieceId()).getTypeName().contains("rook")){

            int playerIndOffset = humanSide ? 16 : 0;
            int playerPosRank = humanSide ? 7 : 0;
            boolean emptyInterFiles = true;
            
            try{
            
                if(piecesRef.get(playerIndOffset + 8).getRank() == playerPosRank 
                        && piecesRef.get(playerIndOffset + 8).getFile() == 0
                        && piecesRef.get(playerIndOffset + 11).getRank() == playerPosRank 
                        && piecesRef.get(playerIndOffset + 11).getFile() == 4){


                    for(int fileInd = 5; fileInd < 7 && emptyInterFiles; ++fileInd){

                        emptyInterFiles = gameBoardRef.get(playerPosRank, fileInd) == -1;
                    }

                    if(emptyInterFiles){

                        allocatedGeneratedStep = new DualStep(
                                "castling", 11, 
                                0, playerPosRank, 
                                4, playerPosRank, 7, 
                                piecesRef.get(11).getValue(), 
                                0, 
                                step.getCumulativeValue());

                        // maxPlayerPieceScore - value due to reversed order (decreasing values)
                        value = maxPlayerPieceScore - allocatedGeneratedStep.getValue();
                        sortedGeneratedSteps.add(new GenTmpStepKey(value), 
                            allocatedGeneratedStep);
                    }
                }
                else if(piecesRef.get(playerIndOffset + 15).getRank() == playerPosRank 
                        && piecesRef.get(playerIndOffset + 16).getFile() == 7
                        && piecesRef.get(playerIndOffset + 11).getRank() == playerPosRank
                        && piecesRef.get(playerIndOffset + 11).getFile() == 4){

                    for(int fileInd = 0; fileInd < 5; ++fileInd){

                        emptyInterFiles = gameBoardRef.get(playerPosRank, fileInd) == -1;
                    }

                    if(emptyInterFiles){

                        allocatedGeneratedStep = new DualStep(
                                "castling", 11, 
                                8, playerPosRank, 4, 
                                playerPosRank, 7,
                                piecesRef.get(11).getValue(), 0,
                                step.getCumulativeValue());

                        // maxPlayerPieceScore - value due to reversed order (decreasing values)
                        value = maxPlayerPieceScore - allocatedGeneratedStep.getValue();
                        sortedGeneratedSteps.add(new GenTmpStepKey(value), 
                            allocatedGeneratedStep);
                    }
                }
            }
            catch(Exception e){

                consoleUI.println("Could not add dual step (" + e.getMessage() + ")");
            }
        }
        else if(piecesRef.get(step.getPieceId()).getTypeName().contains("pawn")){

            try{
                
                if(humanSide && step.getRank() == 0){

                    currRemovedHumanPieces = removedHumanPiecesContinuation.get(key);

                    int sizeOfCurrRemovedHumanPiecesContinuation = 
                            currRemovedHumanPieces.size();

                    for(int i = 0; i < sizeOfCurrRemovedHumanPiecesContinuation; ++i){

                        allocatedGeneratedStep = new DualStep(
                                "promition", step.getPieceId(), 
                                currRemovedHumanPieces.get(i),
                                step.getRank(), step.getFile(), 
                                step.getRank(), step.getFile(), 
                                piecesRef.get(currRemovedHumanPieces.get(i)).getValue(),
                                step.getCumulativeChangeCount(), 
                                piecesRef.get(currRemovedHumanPieces.get(i)).getValue());

                        // maxPlayerPieceScore - value due to reversed order (decreasing values)
                        value = maxPlayerPieceScore - allocatedGeneratedStep.getValue();
                        sortedGeneratedSteps.add(new GenTmpStepKey(value), 
                            allocatedGeneratedStep);
                    }
                }
                else if(!humanSide && step.getRank() == 7){

                    currRemovedMachinePieces = removedMachinePiecesContinuation.get(key);

                    int sizeOfCurrRemovedMachinePieces = 
                            currRemovedMachinePieces.size();

                    for(int i = 0; i < sizeOfCurrRemovedMachinePieces; ++i){

                        allocatedGeneratedStep = new DualStep(
                                "promition", step.getPieceId(),
                                currRemovedMachinePieces.get(i),
                                step.getRank(), step.getFile(),
                                step.getRank(), step.getFile(),
                                piecesRef.get(currRemovedMachinePieces.get(i)).getValue(),
                                step.getCumulativeChangeCount(),
                                piecesRef.get(currRemovedMachinePieces.get(i)).getValue());

                        // maxPlayerPieceScore - value due to reversed order (decreasing values)
                        value = maxPlayerPieceScore - allocatedGeneratedStep.getValue();
                        sortedGeneratedSteps.add(new GenTmpStepKey(value), 
                            allocatedGeneratedStep);
                    }
                }
            }
            catch(Exception e){

                consoleUI.println("Could not add dual step (" + e.getMessage() + ")");
            }
        }
    }
    
    /**
     * General step case evaluation method
     * @param sizeOfGeneratedSteps number of generated steps
     * @param generatedSteps generally generated steps from parent step (position)
     * @param humanSide whether human or machine player comes in this processing 
     *        loop
     * @param step The current parent step
     * @param currRemovedHumanPieces auxiliary removed pieces container for 
     *        human player
     * @param currRemovedMachinePieces auxiliary removed pieces container for
     *        machine player
     * @param sortedGeneratedSteps temporary container where the potentially passed 
     *        next child steps are inserted into
     */
    public void evaluateGeneralStepCases(Integer sizeOfGeneratedSteps, 
            ArrayList<Pair> generatedSteps, Boolean humanSide, Step step, 
            Stack<Integer> currRemovedHumanPieces, 
            Stack<Integer> currRemovedMachinePieces,
            LinTreeMultiMap<GenTmpStepKey, Step> sortedGeneratedSteps){
    
        int pieceInd;
        Pair generatedStep;
        double value;
        int cumulativeNegativeChange = step.getCumulativeChangeCount();
        double cumulativeValue = step.getCumulativeValue();
        Step allocatedGeneratedStep;
        
        // TASK) iterate through available further lookAhead(1) steps according to 
        //    collision states collect available steps
        // TASK) sort these possible steps by a penalty function (heuristics)
        for(int stepI = 0; stepI < sizeOfGeneratedSteps; ++stepI){

            generatedStep = generatedSteps.get(stepI);
            pieceInd = gameBoardRef.get(
                    generatedStep.getRank(), generatedStep.getFile());

            // ordered insertion is quasi nlogn

            // machine piecesRef are filtered out at step generation
            if(pieceInd != -1){

                // human score addition comes
                if(humanSide &&
                    (-1.0) * step.getValue() + minConvThreshold < piecesRef.get(pieceInd).getValue()){

                    ++cumulativeNegativeChange;
                }

                // TASK) use alpha-beta pruning to throw/cut negative tendency subtrees away
                // suboptimal: this is evaluated multiple times during 
                //  execution until the current parent node is actively 
                //  contribute at node generation (not removed from the tree)
                //  See else case
                if(cumulativeNegativeChange <= cumulativeNegativeChangeThreshold){

                    try{

                        allocatedGeneratedStep = new Step(
                            "hit",
                            step.getPieceId(), generatedStep.getRank(), 
                            generatedStep.getFile(), piecesRef.get(pieceInd).getDynamicValue(),
                            cumulativeNegativeChange,  
                        cumulativeValue + piecesRef.get(pieceInd).getDynamicValue());

                        // maxPlayerPieceScore - value due to reversed order (decreasing values)
                        value = maxPlayerPieceScore - piecesRef.get(pieceInd).getDynamicValue();
                        sortedGeneratedSteps.add(new GenTmpStepKey(value), 
                            allocatedGeneratedStep);

                        if(humanSide){

                            currRemovedHumanPieces.add(pieceInd);
                        }
                        else{

                            currRemovedMachinePieces.add(pieceInd);
                        }
                    }
                    catch(Exception e){

                        consoleUI.println("Could not insert step (" + e.getMessage() + ")");
                    }
                }
                else{

                    // skip step (negative tendency continues after reaching
                    //  tendency threshold)

                    // TODO prevent generation of prunned sequence multiple times
                    //      node with negative tendency is evaluated at each 
                    //       additional step generation in in-game mode
                    //      These cuts are evaluated proportionally in time 
                    //       with the missing number of nodes to fulfill 
                    //       the depth condition
                }
            }
            else{
                
                try{
                    
                    allocatedGeneratedStep = new Step(
                        "standard", step.getPieceId(), 
                        generatedStep.getRank(), generatedStep.getFile(), 
                        0, cumulativeNegativeChange, 
                        cumulativeValue + 0.0);

                    sortedGeneratedSteps.add(new GenTmpStepKey(maxPlayerPieceScore), 
                    allocatedGeneratedStep);
                }
                catch(Exception e){

                    consoleUI.println("Could not insert step (" + e.getMessage() + ")");
                }
            }
        }
    }
    
    /**
     * The main executor chunk tree builder method that construct step tree sub 
     * trees that will be available for unions
     */
    @Override
    public void run(){
        
        // TODO: optimize, refactor Game.GenStep, Pair, LinTreeMap.Pair
        
        // TASK Generate these treebuilding utilizing the available concurrent 
        //      threads using mutexes
        
        // generating steps for levels
        
        LinTreeMultiMap<GenTmpStepKey, Step> sortedGeneratedSteps =
                new LinTreeMultiMap<>();
        
        Step step;
        
        // TASK) iterate through available further lookAhead(depth) steps according to
        //       collision states collect available steps
        
        // alternate piece strength scores by machine-human oscillating scheme
        boolean humanSide = !machineBegins;
        
        ArrayList<ArrayList<Step> > generatedLevelNodeSteps =
            new ArrayList< >();
        
        Stack<Integer> currRemovedHumanPieces = new Stack<>();
        Stack<Integer> currRemovedMachinePieces = new Stack<>();
        
        ArrayList<Integer> gameBoardHistory = new ArrayList<>();
        
        boolean wasStepBack = false;
        
        int lvl = 1;
        int lvlLimit = depth;
        
        String key;
        char incKey = 'a';
        Step selectedStep;
        
        boolean divInit = true;
        
        while(!stepHistoryStack.isEmpty()){
            
            humanSide = !humanSide;
            
            if(generatedLevelNodeSteps.isEmpty() && wasStepBack){
            
                // terminate generation, DFS ended
                break;
            }
            
            // continuous value generation value by second recent value after each 
            //  other in order to fulfill the condition of alternate generation of 
            //  dual player step decisions
            step = stepHistoryStack.get(lvl - 1);
            // continuous key generation key by key after each other
            key = keyHistoryStack.get(lvl);
            
            // changing actual available removed pieces player specifically
            if(humanSide){
            
                currRemovedHumanPieces = removedHumanPiecesContinuation.get(key);
            }
            else{
                
                currRemovedMachinePieces = removedMachinePiecesContinuation.get(key);
            }
            
            // preconditional evaluation due to avoidance of unneccesary 
            // generations
            if(lvl < lvlLimit){
                
                if(!wasStepBack){

                    // TASK) sort generated steps

                    generatedLevelNodeSteps.add(new ArrayList<>());
                    
                    ArrayList<Pair> generatedSteps = new ArrayList<>();
                    
                    try{
                    
                        generatedSteps = piecesRef.get(
                            step.getPieceId()).generateSteps(gameBoardRef);
                    }
                    catch(Exception e){
                    
                        consoleUI.println("An error has occurred during "
                                + "generating next steps of currently selected "
                                + "piece. Skipping piece further step generation.");
                    }
                    
                    sortedGeneratedSteps.removeAll();

                    int sizeOfGeneratedSteps = generatedSteps.size();

                    // selective, chunk decision tree generation case
                    if(divInit && fracs > 1){
                    
                        // suboptimal
                        generatedSteps = (ArrayList<Pair>)generatedSteps.subList(
                                no * sizeOfGeneratedSteps / fracs,
                                (no + 1) * sizeOfGeneratedSteps);
                        divInit = false;
                    }
                    
                    
                    // evaluating special cases
                    evaluateSpecialStepCases(key, step, humanSide, 
                            currRemovedHumanPieces, currRemovedMachinePieces, 
                            sortedGeneratedSteps);

                    // evaluating general cases
                    evaluateGeneralStepCases(sizeOfGeneratedSteps, generatedSteps, 
                            humanSide, step, currRemovedHumanPieces, 
                            currRemovedMachinePieces, sortedGeneratedSteps);
                    
                    // converting ordered step list into decision tree favored form
                    //  inserting steps into buffer array
                    int sizeOfSortedGeneratedSteps = sortedGeneratedSteps.size();

                    // step identifier/key conversion
                    try{

                        for(int sortedI = 0; sortedI < sizeOfSortedGeneratedSteps; ++sortedI){

                                generatedLevelNodeSteps.get(lvl).add(
                                        sortedGeneratedSteps.getByInd(sortedI));
                        }
                    }
                    catch(Exception e){

                        consoleUI.println("Could not obtain node (" + e.getMessage() + ")");
                    }

                    incKey = 'a';
                }
                else{

                    wasStepBack = false;
                }
            }
            
            if(generatedLevelNodeSteps.get(lvl).isEmpty() || lvl >= lvlLimit){
            
                // saving trace for further continuation
                if(!generatedLevelNodeSteps.isEmpty() && lvl >= lvlLimit){
                
                    if(humanSide){
                        
                        leafHumanSteps.add(
                                generatedLevelNodeSteps.get(lvl).get(0));
                        leafHumanKeys.add(key);
                    }
                    else{
                        
                        leafMachineSteps.add(
                                generatedLevelNodeSteps.get(lvl).get(0));
                        leafMachineKeys.add(key);
                    }
                    gameBoardHistoryContinuation.add(gameBoardHistory);
                }
                
                // no further seps to take with currently selected piece, stepback
                stepHistoryStack.remove(lvl);
                keyHistoryStack.remove(lvl);
                
                // suboptimal, simplification is needed
                gameBoardRef.set(stepHistoryStack.get(lvl - 1).getRank(), 
                        stepHistoryStack.get(lvl - 1).getFile(), 
                        gameBoardRef.get(step.getRank(), step.getFile()));
                gameBoardRef.set(step.getRank(), step.getFile(), 
                        gameBoardHistory.get(lvl));
                
                gameBoardHistory.remove(lvl);
                generatedLevelNodeSteps.remove(lvl);
                
                // trace removed pieces on each level (removed pieces stack removal)
                if(humanSide){
                
                    removedHumanPiecesContinuation.remove(key);
                }
                else{
                                    
                    removedMachinePiecesContinuation.remove(key);
                }

                --lvl;
                wasStepBack = true;
            }
            else{
                
                selectedStep = generatedLevelNodeSteps.get(lvl).get(0);
                
                // savign previous level status
                stepHistoryStack.add(selectedStep);
                keyHistoryStack.add(key);
                gameBoardHistory.add(gameBoardRef.get(selectedStep.getRank(), 
                        selectedStep.getFile()));
                
                // TASK) update computation tree
                // insert step into decision tree
                try{
                
                    String newKey = key + (++incKey);
                    stepDecisionTree.addOne(new GenStepKey(key), 
                        new GenStepKey(newKey), selectedStep);
                    
                    if(humanSide){
                    
                        removedHumanPiecesContinuation.put(
                                newKey, currRemovedHumanPieces);
                    }
                    else{
                    
                        removedMachinePiecesContinuation.put(
                                newKey, currRemovedMachinePieces);
                    }
                }
                catch(Exception e){
                
                    consoleUI.println("Could not add generated step to step sequences (" 
                        + e.getMessage() + ")");
                }
                
                // modify game table status
                // in case of piece hit by an human  piece, access of to that 
                //  piece is going to be forbidden by the removal its id from the board
                gameBoardRef.set(selectedStep.getRank(), selectedStep.getFile(), 
                        step.getPieceId());
                // leave previous position free
                gameBoardRef.set(step.getRank(), step.getFile(), -1);
                
                generatedLevelNodeSteps.get(lvl).remove(0);
                
                ++lvl;
            }
        }
    }
    
    // needs to be optimized
    /**
     * It builds the tree with certain steps until the depth limit. Tree depth 
     * specific step tree extender
     * @throws Exception
     *         Initial tree generation has not been performed
     */
    public void continueStepSequences() throws Exception{
    
        if(stepDecisionTree.size() < 1){
        
            throw new Exception("Decision tree has not been generated.");
        }
        
        // TASK place leaf level generation after root node offset displacement
        
        // continuing generation by generator
        
        // false due to restrictio nof decision tree generation with human leaf level
        boolean humanSide = false;
        
        LinTreeMultiMap<GenTmpStepKey, Step> sortedGeneratedSteps =
            new LinTreeMultiMap<>();
        
        Step step;
        
        ArrayList<Step> generatedLevelNodeSteps = new ArrayList<>();
        
        int lvl = depth - 2;// -1 for machine and human
        int lvlLimit = depth;
        
        String key;
        char incKey = 'a';
        Step selectedStep = new Step();
        
        // no validation is needed for zero number of elements (upper level evaluation)
        
        int sizeOfLeafLevel = leafMachineSteps.size();
        
        ArrayList<Step> recentLeafMachineSteps = new ArrayList<>();
        ArrayList<String> recentLeafMachineKeys = new ArrayList<>();
        ArrayList<Step> recentLeafHumanSteps = new ArrayList<>();
        ArrayList<String> recentLeafHumanKeys = new ArrayList<>();
        ArrayList<ArrayList<Integer>> recentGameBoardHistoryContinuation =
            new ArrayList<>();
        
        Stack<Integer> currRemovedHumanPieces = new Stack<>();
        Stack<Integer> currRemovedMachinePieces = new Stack<>();
        
        //if(gameBoardHistoryContinuation.size() > 0)
        for(; lvl < lvlLimit; ++lvl){
            
            for(int i = 0; i < sizeOfLeafLevel; ++i){

                if(humanSide){
                    
                    step = leafHumanSteps.get(i);
                    key = leafHumanKeys.get(i);
                    currRemovedHumanPieces = removedHumanPiecesContinuation.get(key);
                }
                else{
                    
                    step = leafMachineSteps.get(i);
                    key = leafMachineKeys.get(i);
                    currRemovedMachinePieces = removedMachinePiecesContinuation.get(key);
                }
                
                GameBoardData gameBoardCopy = gameBoardRef;
                int sizeOfTakenSteps = gameBoardHistoryContinuation.get(i).size();
                
                // conditioning game table according to selected step sequence
                for(int j = 0; j < sizeOfTakenSteps; ++j){

                    gameBoardCopy.set(step.getRank(), step.getFile(),  
                        gameBoardHistoryContinuation.get(i).get(j));
                }

                ArrayList<Pair> generatedSteps = piecesRef.get(
                        step.getPieceId()).generateSteps(gameBoardCopy);

                sortedGeneratedSteps.removeAll();

                int sizeOfGeneratedSteps = generatedSteps.size();
                
                // evaluating special cases
                evaluateSpecialStepCases(key, step, humanSide, 
                        currRemovedHumanPieces, currRemovedMachinePieces, 
                        sortedGeneratedSteps);
                
                // evaluating general cases
                evaluateGeneralStepCases(sizeOfGeneratedSteps, generatedSteps, 
                        humanSide, step, currRemovedHumanPieces, 
                        currRemovedMachinePieces, sortedGeneratedSteps);

                int sizeOfSortedGeneratedSteps = sortedGeneratedSteps.size();

                try{
                    
                    for(int sortedI = 0; sortedI < sizeOfSortedGeneratedSteps; ++sortedI){

                            generatedLevelNodeSteps.add(sortedGeneratedSteps.getByInd(sortedI));
                    }
                }
                catch(Exception e){

                    consoleUI.println("Could not obtain node (" + e.getMessage() + ")");
                }

                int sizeOfGeneratedLevelNodeSteps = generatedLevelNodeSteps.size();

                try{

                    String  newKey;
                    
                    for(int j  = 0; j < sizeOfGeneratedLevelNodeSteps; ++j){

                        newKey = key + (++incKey);
                        
                        stepDecisionTree.addOne(new GenStepKey(key), 
                            new GenStepKey(newKey), 
                            generatedLevelNodeSteps.get(j));
                        
                        if(humanSide){
                            
                            recentLeafHumanSteps.add(
                                    generatedLevelNodeSteps.get(j));
                            recentLeafHumanKeys.add(newKey);
                            removedHumanPiecesContinuation.put(
                                    newKey, currRemovedHumanPieces);
                        }
                        else{
                        
                            recentLeafMachineSteps.add(
                                    generatedLevelNodeSteps.get(j));
                            recentLeafMachineKeys.add(newKey);
                            removedMachinePiecesContinuation.put(
                                    newKey, currRemovedMachinePieces);
                        }
                        
                        recentGameBoardHistoryContinuation.add(
                        gameBoardHistoryContinuation.get(i));
                        recentGameBoardHistoryContinuation.get(i).add(
                            gameBoardCopy.get(selectedStep.getRank(),
                            selectedStep.getFile()));
                    }
                }
                catch(Exception e){

                    consoleUI.println("Could not add generated step to step sequences (" 
                        + e.getMessage() + ")");
                }

                incKey = 'a';
            }
            
            if(humanSide){
                
                leafHumanSteps = recentLeafHumanSteps;
                leafHumanKeys = recentLeafHumanKeys;
                recentLeafHumanSteps.clear();
                recentLeafHumanKeys.clear();
            }
            else{
            
                leafMachineSteps = recentLeafMachineSteps;
                leafMachineKeys = recentLeafMachineKeys;
                recentLeafMachineSteps.clear();
                recentLeafMachineKeys.clear();
            }
            
            gameBoardHistoryContinuation = recentGameBoardHistoryContinuation;
            recentGameBoardHistoryContinuation.clear();
            
            humanSide = !humanSide;
        }
    }
}
