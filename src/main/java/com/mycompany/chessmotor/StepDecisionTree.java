package com.mycompany.chessmotor;

import com.mycompany.chessmotor.piecetypes.GenPiece;
import genmath.GenStepKey;
import genmath.GenTmpStepKey;
import genmath.IncArbTree;
import genmath.LinTreeMultiMap;
import java.util.ArrayList;

public class StepDecisionTree extends Thread{
    
    private IncArbTree<GenStepKey, Step> stepDecisionTree;
    
    private boolean allyBeginsRef;
    private GenPiece[] piecesRef;
    private ArrayList<Step> stepHistoryRef;
    private int[][] gameBoardRef;
    
    // number of steps to be generated (look ahead with N steps), as an upper 
    // bound of step sequence generation
    private int depth;
    
    private int cumulativeNegativeChangeThreshold;
    
    // Negative tendency threshold in step sequences. If difference of two opponent 
    // score values are greater than a threshold, drop step sequence.
    private double minConvThreshold;
    
    StepDecisionTree(boolean allyBegins, GenPiece[] pieces, ArrayList<Step> stepHistory, 
            int[][] gameBoard, int depth, int cumulativeNegativeChangeThreshold, 
            double minConvThreshold) throws Exception{
    
        super();
        stepDecisionTree = new IncArbTree<GenStepKey, Step>();
        this.allyBeginsRef = allyBegins;
        this.piecesRef = pieces;
        this.stepHistoryRef = stepHistory;
        this.gameBoardRef = gameBoard;
        
        if(minConvThreshold < 0)
            throw new Exception("Opponent score increase slope must be positive.");
        
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
    }
    
    public void StepDecisionTree(StepDecisionTree orig){
    
        this.allyBeginsRef = orig.allyBeginsRef;
        this.piecesRef = orig.piecesRef;
        this.stepHistoryRef = orig.stepHistoryRef;
        this.gameBoardRef = orig.gameBoardRef;
        this.depth = orig.depth;
        this.cumulativeNegativeChangeThreshold = orig.cumulativeNegativeChangeThreshold;
        this.minConvThreshold = orig.minConvThreshold;
    }
    
    public int size(){
    
        return stepDecisionTree.size();
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
                    0, trimEndInd);
            
            stepDecisionTree.setKeyByInd(i, new GenStepKey(prefixTrimmedKeyValue));
        }
    }
    
    public void addOne(GenStepKey whereKey, GenStepKey key, Step step) throws Exception{
    
        stepDecisionTree.addOne(whereKey, key, step);
    }
    
    // TODO count check status too
    public void buildStepSequences(boolean initGen, int dataCut) throws Exception{
    
        // TODO: optimize, refactor Game.GenStep, Pair, LinTreeMap.Pair
        
        // It always starts from root node due to root removal at each step 
        //      of ally decision
        
        // TODO Generate these treebuilding utilizing the available concurrent 
        //      threads using mutexes
        
        // generating steps for levels
        
        LinTreeMultiMap<GenTmpStepKey, Step> sortedGeneratedSteps =
                new LinTreeMultiMap<GenTmpStepKey, Step>();
        
        Step step;
        
        // in order to restore consistency for peer (by level) steps
        ArrayList<Step> stepHistoryStack = new ArrayList<Step>();
        
        ArrayList<String> keyHistoryStack = new ArrayList<String>();

        // first step
        if(initGen && allyBeginsRef){
        
            for(int i = 0; i < 16; ++i){
            
                if(piecesRef[i].generateSteps(gameBoardRef).size() > 0){
                
                    step = new Step(i, (int)Math.floor(i / 8), i % 8,
                    piecesRef[i].getValue(), 0,
                    piecesRef[i].getValue());
                
                    sortedGeneratedSteps.add(
                            new GenTmpStepKey(piecesRef[i].getValue()), step);
                }
            }
            
            // suboptimal strategy of initial step
            //  (strategies can be added by weighthening graph later)
            int selectedPieceInd = 
                (int)(Math.random() * (double)(sortedGeneratedSteps.size() - 1));
            
            sortedGeneratedSteps.getByInd(selectedPieceInd);
            
            step = sortedGeneratedSteps.getByInd(selectedPieceInd);
            
            stepDecisionTree.addOne(new GenStepKey("a"), 
                    new GenStepKey("a"), step);
            
            // saving previous level status
            stepHistoryStack.add(step);
            keyHistoryStack.add("a");
            
            // modify game table status
            gameBoardRef[step.getFile()][step.getRank()] = step.getPieceId();
        }
        
        
        // TASK) iterate through available further lookAhead(depth) steps according to
        //       collision states collect available steps
        
        // alternate piece strength scores by ally-opponent oscillating scheme
        boolean opponentSide = true;
        
        ArrayList<ArrayList<Step> > generatedLevelNodeSteps =
            new ArrayList<ArrayList<Step> >();
        
        ArrayList<Integer> gameBoardHistory = new ArrayList<Integer>();
        
        boolean wasStepBack = false;
        
        int lvl = 0;
        int lvlLimit = depth;
        
        if(!initGen){
            
            // continuing generation by generator
            ArrayList<GenStepKey> levelKeys = stepDecisionTree.getLeafLevelKeys();
            
            int sizeOfLevelKeys = levelKeys.size();
            
            generatedLevelNodeSteps.add(new ArrayList<Step>());
            
            for(int i = 0; i < sizeOfLevelKeys; ++i){
            
                generatedLevelNodeSteps.get(0).add(
                        stepDecisionTree.getByKey(levelKeys.get(i)));
            }
            
            lvlLimit = 1;
            wasStepBack = true;
        }
        
        double cumulativeValue = 0.0;
        int cumulativeNegativeChange = 0;
        String key;
        char incKey = 'a';
        Step selectedStep;
        
        while(stepHistoryStack.size() > 1){
            
            opponentSide = !opponentSide;
            
            if(generatedLevelNodeSteps.isEmpty() && wasStepBack){
            
                // terminate generation, DFS ended
                break;
            }
            
            step = stepHistoryStack.get(lvl);
            key = keyHistoryStack.get(lvl);
            
            if(!wasStepBack){
                
                // TASK) sort generated steps
                
                generatedLevelNodeSteps.add(new ArrayList<Step>());
                
                ArrayList<Pair> generatedSteps = 
                        piecesRef[step.getPieceId()].generateSteps(gameBoardRef);
                
                sortedGeneratedSteps.removeAll();
                
                int pieceInd;
                int sizeOfGeneratedSteps = generatedSteps.size();
                
                Pair generatedStep;
                Step allocatedGeneratedStep;
                double value;
                
                // TASK) iterate through available further lookAhead(1) steps according to 
                //    collision states collect available steps
                // TASK) sort these possible steps by a penalty function (heuristics)
                for(int stepI = 0; stepI < sizeOfGeneratedSteps; ++stepI){
                
                    generatedStep = generatedSteps.get(stepI);
                    pieceInd = gameBoardRef[generatedStep.file][generatedStep.rank];

                    // ordered insertion is quasi nlogn

                    // ally piecesRef are filtered out at step generation
                    if(pieceInd != -1){

                        // opponent score addition comes
                        if(opponentSide &&
                            (-1.0) * step.getValue() + minConvThreshold < piecesRef[pieceInd].getValue()){

                            ++cumulativeNegativeChange;
                        }

                        // TASK) use alpha-beta pruning to throw/cut negative tendency subtrees away                        
                        if(cumulativeNegativeChange <= cumulativeNegativeChangeThreshold){

                            allocatedGeneratedStep = new Step(
                                step.getPieceId(), generatedStep.file, 
                                generatedStep.rank, piecesRef[pieceInd].getValue(),
                                cumulativeNegativeChange,  
                                cumulativeValue + piecesRef[pieceInd].getValue());

                            // 1000 - vlaue due to reversed order (decreasing values)
                            value = 1000.0 - piecesRef[pieceInd].getValue();
                            sortedGeneratedSteps.add(new GenTmpStepKey(value), 
                                allocatedGeneratedStep);
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

                        allocatedGeneratedStep = new Step(
                            step.getPieceId(), generatedStep.file,
                            generatedStep.rank, piecesRef[pieceInd].getValue(), 
                            cumulativeNegativeChange, 
                            cumulativeValue + 0.0);

                        sortedGeneratedSteps.add(new GenTmpStepKey(1000.0), 
                            allocatedGeneratedStep);
                    }
                }
                
                // converting ordered step list into decision tree favored form
                //  inserting steps into buffer array
                int sizeOfSortedGeneratedSteps = sortedGeneratedSteps.size();
                
                // step identifier/key conversion
                for(int sortedI = 0; sortedI < sizeOfSortedGeneratedSteps; ++sortedI){
                
                    generatedLevelNodeSteps.get(lvl).add(
                        sortedGeneratedSteps.getByInd(sortedI));
                }
                
                incKey = 'a';
            }
            else{
            
                wasStepBack = false;
            }
            
            if(generatedLevelNodeSteps.get(lvl).isEmpty() || lvl >= lvlLimit){
            
                // no further seps to take with currently selected piece, stepback
                stepHistoryStack.remove(lvl);
                keyHistoryStack.remove(lvl);
                gameBoardRef[step.getFile()][step.getRank()] = gameBoardHistory.get(lvl);
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
                gameBoardHistory.add(gameBoardRef[selectedStep.getFile()][selectedStep.getRank()]);
                
                // TASK) update computation tree
                // insert step into decision tree
                stepDecisionTree.addOne(new GenStepKey(key), 
                        new GenStepKey(key + (++incKey)), selectedStep);
                
                // modify game table status
                // in case of piece hit by an opponent piece, access of to that 
                //  piece is going to be forbidden by the removal its id from the board
                gameBoardRef[selectedStep.getFile()][selectedStep.getRank()] = step.getPieceId();
                
                generatedLevelNodeSteps.get(lvl).remove(0);
                
                ++lvl;
            }
        }
    }
}
