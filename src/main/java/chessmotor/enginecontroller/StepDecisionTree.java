package chessmotor.enginecontroller;

import chessmotor.enginecontroller.piecetypes.GenPiece;
import genmath.GenStepKey;
import genmath.GenTmpStepKey;
import genmath.IncArbTree;
import genmath.LinTreeMultiMap;
import java.util.ArrayList;
import java.util.Stack;

public class StepDecisionTree implements Runnable{
    
    private IncArbTree<GenStepKey, Step> stepDecisionTree;
    
    private boolean machineBegins;
    private GenPiece[] piecesRef;
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
    
    private ArrayList<ArrayList<Integer>> gameBoardHistoryContinuation;
    
    private int fracs;
    private int no;
    public StepDecisionTree(boolean machineBegins, GenPiece[] pieces, 
            Stack<Step> stepHistory, Integer[][] gameBoard, int depth, 
            int cumulativeNegativeChangeThreshold, double minConvThreshold, int fracs, 
            int no, long memLimit) throws Exception{
    
        super();
        
        stepDecisionTree = new IncArbTree<GenStepKey, Step>();
        this.machineBegins = machineBegins;
        
        if(pieces == null){
        
            throw new Exception("Piece storage is null.");
        }
        
        this.piecesRef = pieces;
        
        if(stepHistory == null){
        
            throw new Exception("Step history is null.");
        }
        
        this.stepHistoryRef = stepHistory;
        
        if(gameBoard == null){
        
            throw new Exception("Game board is null.");
        }
        
        this.gameBoardRef = gameBoard;
        
        if(minConvThreshold < 0.0)
            throw new Exception("Human score increase slope must be positive.");
        
        this.minConvThreshold = minConvThreshold;
        
        
        if(depth < 1){
        
            throw new Exception("Provided depth is not enough.");
        }
        
        // +1 for 0th start step, initiate board positions
        this.depth = depth;
        
        // TODO limit maximum depth according to available resources
        //      (mostly memory and response time by computation speed)
        
        if(cumulativeNegativeChangeThreshold < 1){
        
            throw new Exception("Consequent cumulative negative change limit is under 1.");
        }
        
        this.cumulativeNegativeChangeThreshold = cumulativeNegativeChangeThreshold;
    
        if(fracs < 0){
        
            throw new Exception("Negative fractional value.");
        }
        
        this.fracs = fracs;
        
        if(no >= fracs){
        
            throw new Exception("Number of fractions are less than the"
                    + " provided fractional identifier.");
        }
    
        this.no = no;
        
        stepHistoryStack = new ArrayList<Step>();
        keyHistoryStack = new ArrayList<String>();
        
        leafMachineSteps = new ArrayList<Step>();
        leafMachineKeys = new ArrayList<String>();
        leafHumanSteps = new ArrayList<Step>();
        leafHumanKeys = new ArrayList<String>();
        
        gameBoardHistoryContinuation = new ArrayList<ArrayList<Integer>>();
        
    }
    
    public void StepDecisionTree(StepDecisionTree orig){
    
    public StepDecisionTree(StepDecisionTree orig) throws Exception{
    
        if(orig == null){
        
            throw new Exception("Original object is null.");
        }
        
        this.stepDecisionTree = orig.stepDecisionTree;
        this.machineBegins = orig.machineBegins;
        this.piecesRef = orig.piecesRef;
        this.stepHistoryRef = orig.stepHistoryRef;
        this.gameBoardRef = orig.gameBoardRef;
        this.depth = orig.depth;
        this.cumulativeNegativeChangeThreshold = orig.cumulativeNegativeChangeThreshold;
        this.minConvThreshold = orig.minConvThreshold;
        
        this.stepHistoryStack = orig.stepHistoryStack;
        this.keyHistoryStack = orig.keyHistoryStack;
        this.leafSteps = orig.leafSteps;
        this.leafKeys = orig.leafKeys;
        this.gameBoardHistoryContinuation = orig.gameBoardHistoryContinuation;
        
        this.fracs = orig.fracs;
        this.no = orig.no;
    }
    
    
    public int size(){
    
        return stepDecisionTree.size();
    }
    
    
    public ArrayList<GenStepKey> getLevelKeys(int levelId){

        return stepDecisionTree.getLevelKeys(levelId);
    }

    
    public ArrayList<GenStepKey> getLeafLevelKeys(){
    
        return stepDecisionTree.getLeafLevelKeys();
    }
    
    
    public Step getByKey(GenStepKey key) throws Exception{
    
        return stepDecisionTree.getByKey(key);
    }
    
    
    public void setNewRootByKey(GenStepKey key) throws Exception{
    
        stepDecisionTree.setNewRootByKey(key);
    }
    
    
    public void setFracNo(int newNo) throws Exception{
    
        if(newNo < 0 || newNo >= fracs){
    
            throw new Exception("Fraction identifier is out of range.");
        }
        
        this.no = newNo;
    }
    
    
    public int getDepth(){
    
        return depth;
    }
    
    
    public IncArbTree<GenStepKey, Step> getContainer(){
    
        return stepDecisionTree;
    }
    
    
    // improvement: dynamic depth variation according to recent game status scores
    public void setDepth(int depth) throws Exception{
    
        if(depth < 1) throw new Exception("Step sequence depth is less than 1.");
        
        this.depth = depth;
    }
    
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
    
    
    public void unite(StepDecisionTree chunk) throws Exception{
    
        // expand container using upper estimation of number of nodes respect 
        //  to potentially reserved (in future) memory
        stepDecisionTree.reserve(stepDecisionTree.size() + chunk.stepDecisionTree.size());
        
        stepDecisionTree.mergeToNode(chunk.stepDecisionTree);
    }
    
    
    public void addOne(GenStepKey whereKey, GenStepKey key, Step step) throws Exception{
    
        stepDecisionTree.addOne(whereKey, key, step);
    }
    
    
    public void addToHistoryStack(String key, Step step){
    
        stepHistoryStack.add(step);
        keyHistoryStack.add(key);
    }
    
    
    public void reserveMem(int resMemSize) throws Exception{
    
        stepDecisionTree.reserve(resMemSize);
    }
    
    
    public void generateFirstMachineStep() throws Exception{

        LinTreeMultiMap<GenTmpStepKey, Step> sortedGeneratedSteps = 
            new LinTreeMultiMap<GenTmpStepKey, Step>();

        Step step;

        for(int i = 0; i < 16; ++i){

            if(piecesRef[i].generateSteps(gameBoardRef).size() > 0){

                step = new Step(i, 1 - (int)Math.floor(i / 8), i % 8,
                piecesRef[i].getValue(), 0,
                piecesRef[i].getValue());

                sortedGeneratedSteps.add(
                        new GenTmpStepKey(piecesRef[i].getValue()), step);
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
    
    
    // TODO count check status too
    @Override
    public void run(){
        
        // TODO: optimize, refactor Game.GenStep, Pair, LinTreeMap.Pair
        
        // TASK Generate these treebuilding utilizing the available concurrent 
        //      threads using mutexes
        
        // generating steps for levels
        
        LinTreeMultiMap<GenTmpStepKey, Step> sortedGeneratedSteps =
                new LinTreeMultiMap<GenTmpStepKey, Step>();
        
        Step step;
        
        // TASK) iterate through available further lookAhead(depth) steps according to
        //       collision states collect available steps
        
        // alternate piece strength scores by machine-human oscillating scheme
        boolean humanSide = !machineBegins;
        
        ArrayList<ArrayList<Step> > generatedLevelNodeSteps =
            new ArrayList<ArrayList<Step> >();
        
        ArrayList<Integer> gameBoardHistory = new ArrayList<Integer>();
        
        boolean wasStepBack = false;
        
        int lvl = 1;
        int lvlLimit = depth;
        
        double cumulativeValue = 0.0;
        int cumulativeNegativeChange = 0;
        String key;
        char incKey = 'a';
        Step selectedStep;
        
        boolean divInit = true;
        
        while(stepHistoryStack.size() > 0){
            
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
            
            // preconditional evaluation due to avoidance of unneccesary 
            // generations
            if(lvl < lvlLimit){
                
                if(!wasStepBack){

                    // TASK) sort generated steps

                    generatedLevelNodeSteps.add(new ArrayList<Step>());

                    ArrayList<Pair> generatedSteps = 
                            piecesRef[step.getPieceId()].generateSteps(gameBoardRef);
                    
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
                    
                    Step allocatedGeneratedStep;
                    
                    // special step case: castling option
                    if(piecesRef[step.getPieceId()].getTypeName().equals("king")
                            || piecesRef[step.getPieceId()].getTypeName().equals("rook")){
                    
                        int playerIndOffset = humanSide ? 16 : 0;
                        int playerPosRank = humanSide ? 7 : 0;
                        boolean emptyInterFiles = true;
                        
                        
                        if(piecesRef[playerIndOffset + 8].getRank() == playerPosRank 
                                && piecesRef[playerIndOffset + 8].getFile() == 0
                                && piecesRef[playerIndOffset + 11].getRank() == playerPosRank 
                                && piecesRef[playerIndOffset + 11].getFile() == 4){
                        
                            
                            for(int fileInd = 5; fileInd < 7 && emptyInterFiles; ++fileInd){
                            
                                emptyInterFiles = gameBoardRef.get(playerPosRank, fileInd) == -1;
                            }
                            
                            if(emptyInterFiles){
                            
                                try{
                                
                                    allocatedGeneratedStep = new DualStep(
                                            11, 0, playerPosRank, 
                                            4, playerPosRank, 7, 
                                            piecesRef[11].getValue(), 0, 
                                            step.getCumulativeValue());
                                    
                                    generatedLevelNodeSteps.get(lvl).add(allocatedGeneratedStep);
                                }
                                catch(Exception e){
                                
                                    System.out.println("Cold not add dual step (" + e.getMessage() + ")");
                                }
                            }
                        }
                        else if(piecesRef[playerIndOffset + 15].getRank() == playerPosRank 
                                && piecesRef[playerIndOffset + 16].getFile() == 7
                                && piecesRef[playerIndOffset + 11].getRank() == playerPosRank
                                && piecesRef[playerIndOffset + 11].getFile() == 4){
                        
                            for(int fileInd = 0; fileInd < 5; ++fileInd){
                            
                                emptyInterFiles = gameBoardRef.get(playerPosRank, fileInd) == -1;
                            }
                            
                            if(emptyInterFiles){
                            
                                try{
                                
                                    allocatedGeneratedStep = new DualStep(
                                            11, 8, playerPosRank, 4, 
                                            playerPosRank, 7,
                                            piecesRef[11].getValue(), 0,
                                            step.getCumulativeValue());
                                    
                                    generatedLevelNodeSteps.get(lvl).add(allocatedGeneratedStep);
                                }
                                catch(Exception e){
                                
                                    System.out.println("Could not add dual step (" + e.getMessage() + ")");
                                }
                            }
                        }
                    }
                    
                    int pieceInd;
                    Pair generatedStep;
                    double value;
                    cumulativeNegativeChange = step.getCumulativeChangeCount();
                    cumulativeValue = step.getCumulativeValue();

                    // TASK) iterate through available further lookAhead(1) steps according to 
                    //    collision states collect available steps
                    // TASK) sort these possible steps by a penalty function (heuristics)
                    for(int stepI = 0; stepI < sizeOfGeneratedSteps; ++stepI){

                        generatedStep = generatedSteps.get(stepI);
                        pieceInd = gameBoardRef.get(generatedStep.rank, generatedStep.file);

                        // ordered insertion is quasi nlogn

                        // machine piecesRef are filtered out at step generation
                        if(pieceInd != -1){

                            // human score addition comes
                            if(humanSide &&
                                (-1.0) * step.getValue() + minConvThreshold < piecesRef[pieceInd].getValue()){

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
                                    step.getPieceId(), generatedStep.rank, 
                                    generatedStep.file, piecesRef[pieceInd].getValue(),
                                    cumulativeNegativeChange,  
                                    cumulativeValue + piecesRef[pieceInd].getValue());

                                    // 1000 - vlaue due to reversed order (decreasing values)
                                    value = 1000.0 - piecesRef[pieceInd].getValue();
                                    sortedGeneratedSteps.add(new GenTmpStepKey(value), 
                                        allocatedGeneratedStep);
                                }
                                catch(Exception e){

                                    System.out.println("Could not insert step (" + e.getMessage() + ")");
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
                                step.getPieceId(), generatedStep.rank,
                                generatedStep.file, 0, 
                                cumulativeNegativeChange, 
                                cumulativeValue + 0.0);

                                sortedGeneratedSteps.add(new GenTmpStepKey(1000.0), 
                                allocatedGeneratedStep);
                            }
                            catch(Exception e){

                                System.out.println("Could not insert step (" + e.getMessage() + ")");
                            }
                        }
                    }

                    // converting ordered step list into decision tree favored form
                    //  inserting steps into buffer array
                    int sizeOfSortedGeneratedSteps = sortedGeneratedSteps.size();

                    // step identifier/key conversion
                    for(int sortedI = 0; sortedI < sizeOfSortedGeneratedSteps; ++sortedI){

                        try{

                            generatedLevelNodeSteps.get(lvl).add(
                            sortedGeneratedSteps.getByInd(sortedI));
                        }
                        catch(Exception e){

                            System.out.println("Could not obtain node (" + e.getMessage() + ")");
                        }
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
                        
                        leafHumanSteps.add(generatedLevelNodeSteps.get(lvl).get(0));
                        leafHumanKeys.add(key);
                    }
                    else{
                        
                        leafMachineSteps.add(generatedLevelNodeSteps.get(lvl).get(0));
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
                
                    stepDecisionTree.addOne(new GenStepKey(key), 
                        new GenStepKey(key + (++incKey)), selectedStep);
                }
                catch(Exception e){
                
                    System.out.println("Could not add generated step to step sequences (" 
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
    public void continueMachineStepSequences() throws Exception{
    
        if(stepDecisionTree.size() < 1){
        
            throw new Exception("Decision tree has not been generated.");
        }
        
        // TASK place leaf level generation after root node offset displacement
        
        // continuing generation by generator
        
        // false due to restrictio nof decision tree generation with human leaf level
        boolean humanSide = false;
        
        LinTreeMultiMap<GenTmpStepKey, Step> sortedGeneratedSteps =
            new LinTreeMultiMap<GenTmpStepKey, Step>();
        
        Step step;
        
        ArrayList<Step> generatedLevelNodeSteps = new ArrayList<Step>();
        ArrayList<Integer> gameBoardHistory = new ArrayList<Integer>();
        
        boolean wasStepBack = false;
        
        int lvl = depth - 2;// -1 for machine and human
        int lvlLimit = depth;
        
        double cumulativeValue = 0.0;
        int cumulativeNegativeChange = 0;
        String key = new String();
        char incKey = 'a';
        Step selectedStep = new Step();
        
        // no validation is needed for zero number of elements (upper level evaluation)
        
        int sizeOfLeafLevel = leafMachineSteps.size();
        
        ArrayList<Step> recentLeafMachineSteps = new ArrayList<Step>();
        ArrayList<String> recentLeafMachineKeys = new ArrayList<String>();
        ArrayList<Step> recentLeafHumanSteps = new ArrayList<Step>();
        ArrayList<String> recentLeafHumanKeys = new ArrayList<String>();
        ArrayList<ArrayList<Integer>> recentGameBoardHistoryContinuation =
            new ArrayList<ArrayList<Integer>>();
        
        //if(gameBoardHistoryContinuation.size() > 0)
        for(; lvl < lvlLimit; ++lvl){
            
            for(int i = 0; i < sizeOfLeafLevel; ++i){

                if(humanSide){
                    
                    step = leafHumanSteps.get(i);
                    key = leafHumanKeys.get(i);
                }
                else{
                    
                    step = leafMachineSteps.get(i);
                    key = leafMachineKeys.get(i);
                }
                
                GameBoardData gameBoardCopy = gameBoardRef;
                int sizeOfTakenSteps = gameBoardHistoryContinuation.get(i).size();

                // conditioning game table according to selected step sequence
                for(int j = 0; j < sizeOfTakenSteps; ++j){

                    gameBoardCopy.set(step.getRank(), step.getFile(),  
                        gameBoardHistoryContinuation.get(i).get(j));
                }

                ArrayList<Pair> generatedSteps =
                        piecesRef[step.getPieceId()].generateSteps(gameBoardCopy);

                sortedGeneratedSteps.removeAll();

                int pieceInd;
                int sizeOfGeneratedSteps = generatedSteps.size();

                Pair generatedStep;
                Step allocatedGeneratedStep;
                double value;
                cumulativeNegativeChange = step.getCumulativeChangeCount();
                cumulativeValue = step.getCumulativeValue();

                for(int stepI = 0; stepI < sizeOfGeneratedSteps; ++stepI){

                    generatedStep = generatedSteps.get(stepI);
                    pieceInd = gameBoardCopy.get(generatedStep.rank, generatedStep.file);

                    if(pieceInd != -1){

                        if(humanSide &&
                            (-1.0) * step.getValue() + minConvThreshold < piecesRef[pieceInd].getValue()){

                            ++cumulativeNegativeChange;
                        }

                        if(cumulativeNegativeChange <= cumulativeNegativeChangeThreshold){

                            try{

                                allocatedGeneratedStep = new Step(
                                step.getPieceId(), generatedStep.rank, 
                                generatedStep.file, piecesRef[pieceInd].getValue(),
                                cumulativeNegativeChange,  
                                cumulativeValue + piecesRef[pieceInd].getValue());

                                value = 1000.0 - piecesRef[pieceInd].getValue();
                                sortedGeneratedSteps.add(new GenTmpStepKey(value), 
                                    allocatedGeneratedStep);
                            }
                            catch(Exception e){

                                System.out.println("Could not insert step (" + e.getMessage() + ")");
                            }
                        }
                    }
                    else{

                        try{

                            allocatedGeneratedStep = new Step(
                            step.getPieceId(), generatedStep.rank,
                            generatedStep.file, piecesRef[pieceInd].getValue(), 
                            cumulativeNegativeChange, 
                            cumulativeValue + 0.0);

                            sortedGeneratedSteps.add(new GenTmpStepKey(1000.0), 
                            allocatedGeneratedStep);
                        }
                        catch(Exception e){

                            System.out.println("Could not insert step (" + e.getMessage() + ")");
                        }
                    }
                }

                int sizeOfSortedGeneratedSteps = sortedGeneratedSteps.size();

                for(int sortedI = 0; sortedI < sizeOfSortedGeneratedSteps; ++sortedI){

                    try{

                        generatedLevelNodeSteps.add(sortedGeneratedSteps.getByInd(sortedI));
                    }
                    catch(Exception e){

                        System.out.println("Could not obtain node (" + e.getMessage() + ")");
                    }
                }

                int sizeOfGeneratedLevelNodeSteps = generatedLevelNodeSteps.size();

                try{

                    String  newKey = new String();
                    
                    for(int j  = 0; j < sizeOfGeneratedLevelNodeSteps; ++j){

                        newKey = key + (++incKey);
                        
                        stepDecisionTree.addOne(new GenStepKey(key), 
                            new GenStepKey(newKey), generatedLevelNodeSteps.get(j));
                        
                        if(humanSide){
                            
                            recentLeafHumanSteps.add(generatedLevelNodeSteps.get(j));
                            recentLeafHumanKeys.add(newKey);
                        }
                        else{
                        
                            recentLeafMachineSteps.add(generatedLevelNodeSteps.get(j));
                            recentLeafMachineKeys.add(newKey);
                        }
                        
                        recentGameBoardHistoryContinuation.add(
                        gameBoardHistoryContinuation.get(i));
                        recentGameBoardHistoryContinuation.get(i).add(
                            gameBoardCopy.get(selectedStep.getRank(), selectedStep.getFile()));
                    }
                }
                catch(Exception e){

                    System.out.println("Could not add generated step to step sequences (" 
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
