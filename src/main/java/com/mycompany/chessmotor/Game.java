package com.mycompany.chessmotor;

import com.mycompany.chessmotor.piecetypes.Bishop;
import com.mycompany.chessmotor.piecetypes.GenPiece;
import com.mycompany.chessmotor.piecetypes.King;
import com.mycompany.chessmotor.piecetypes.Knight;
import com.mycompany.chessmotor.piecetypes.Pawn;
import com.mycompany.chessmotor.piecetypes.Queen;
import com.mycompany.chessmotor.piecetypes.Rook;
import genmath.GenStepKey;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

// evolution of engine

//  > 2D game table and combinatorical generation of possible steps without step
//    strength indiator (probabilistic) only using available steps with filtering
//    active pieces. Polar coordinate based proximity function for hits, checks,
//    check mates. Use min-max bipartite graph. Usage of alpha-beta prunning 
//    (subgraph edge cuts where the step sequence would cause less probability 
//    of win including multiple loop evaluation in order to use the most efficient steps.
//    
//  > Improved version of the above along width piece type strength weight seeking
//    for maximizing the most n valuable pieces.

public class Game{
    
    // initialization of game is required
    private boolean initialized;
    
    // which player begins with the white pieces
    private boolean allyBegins;
    
    private boolean playGame;
    
    // active in game piece container
    private GenPiece pieces[];
    
    // piece name resolution array
    private ArrayList<String> pieceNames;
    
    // the actual game board to operate with
    private int[][] gameBoard;
    
    // recent status of generated (arbirary incomplete n-ary tree) step sequences 
    // for further step decisions
    StepDecisionTree stepSequences;
    
    private ArrayList<Step> stepHistory;

    public Game(){
    
        initialized = false;
    }
    
    public Game(boolean allyBegins, int stepsToLookAhead, double minConvThreshold, 
            int cumulativeNegativeChangeThreshold) throws Exception{
    
        initialized = true;
        
        this.allyBegins = allyBegins;
        
        
        pieces = new GenPiece[32];
        pieceNames = new ArrayList<String>();
        gameBoard = new int[8][8];
        stepSequences = new StepDecisionTree(allyBegins, pieces, stepHistory, gameBoard, 
            stepsToLookAhead, cumulativeNegativeChangeThreshold, minConvThreshold);
        stepHistory = new ArrayList<Step>();
        
        // initializing ally pieces
        
        for(int i = 0; i < 8; ++i){
        
            pieces[i] = new Pawn(-1.0, 1, i);
            gameBoard[1][i] = i;
            pieceNames.add("" + (i + 1) + "pawn");

            pieces[16 + i] = new Pawn(1.0, 6, i);
            gameBoard[6][i] = 16 + i;
        }

        pieces[8] = new Rook(-14.0, 0, 0);
        pieceNames.add("lrook");
        pieces[9] = new Knight( -8.0, 0, 1);
        pieceNames.add("lknight");
        pieces[10] = new Bishop(-14.0, 0, 2);
        pieceNames.add("lbishop");
        pieces[11] = new King(-8.0, 0, 3);
        pieceNames.add("king");
        pieces[12] = new Queen(-28.0, 0, 4);
        pieceNames.add("queen");
        pieces[13] = new Bishop(-14.0, 0, 5);
        pieceNames.add("rbishop");
        pieces[14] = new Knight(-8.0, 0, 6);
        pieceNames.add("rknight");
        pieces[15] = new Rook(-14.0, 0, 7);
        pieceNames.add("rrook");
        
        gameBoard[0][0] = 8;
        gameBoard[0][1] = 9;
        gameBoard[0][2] = 10;
        gameBoard[0][3] = 11;
        gameBoard[0][4] = 12;
        gameBoard[0][5] = 13;
        gameBoard[0][6] = 14;
        gameBoard[0][7] = 15;

        // initializing opponent pieces
        
        pieces[16 + 8] = new Rook(14.0, 7, 0);
        pieces[16 + 9] = new Knight(8.0, 7, 1);
        pieces[16 + 10] = new Bishop(14.0, 7, 2);
        pieces[16 + 11] = new King(8.0, 7, 3);
        pieces[16 + 12] = new Queen(28.0, 7, 4);
        pieces[16 + 13] = new Bishop(14.0, 7, 5);
        pieces[16 + 14] = new Knight(8.0, 7, 6);
        pieces[16 + 15] = new Rook(14.0, 7, 7);

        gameBoard[7][0] = 16 + 8;
        gameBoard[7][1] = 16 + 9;
        gameBoard[7][2] = 16 + 10;
        gameBoard[7][3] = 16 + 11;
        gameBoard[7][4] = 16 + 12;
        gameBoard[7][5] = 16 + 13;
        gameBoard[7][6] = 16 + 14;
        gameBoard[7][7] = 16 + 15;
        
        // filling empty squares
        for(int rankInd = 2; rankInd < 6; ++rankInd){
        
            for(int fileInd = 0; fileInd < 8; ++fileInd){
            
                gameBoard[fileInd][rankInd] = -1;
            }
        }   
    }
    
    public void runGame() throws Exception{
    
        // TODO get to know the number of concurrent theads
        // https://stackoverflow.com/questions/4759570/finding-number-of-cores-in-java
        
        // TODO create step sequence tree builder operator class as a Runnable 
        // to thread pooling. This relies on heap pass by reference.
    
        if(!initialized) throw new Exception("Uninitialized game.");
        // TODO
    
        int gameStatus = 0;
        playGame = true;
        
        if(allyBegins){
        
            // build step decision tree for the first time
            stepSequences.buildStepSequences(true, 0);
            selectNextStep();
            
            // waiting for player action
            requestPlayerAction();
            
            stepSequences.buildStepSequences(false, 0);
            selectNextStep();
        }
        else{
        
            // waiting for player action
            requestPlayerAction();
            
            // build step decision tree for the first time
            stepSequences.buildStepSequences(true, 0);
            selectNextStep();
        }
        
        while(playGame){
        
            requestPlayerAction();
            stepSequences.buildStepSequences(false, 0);
            selectNextStep();
        }

        if(gameStatus < 0){
        
            System.out.println("Ally won the game.");
            
            // print more information (especially score progressions)
        }
        else if(gameStatus > 0){
        
            System.out.println("Opponent won the game.");
            
            // print more information (especially score progressions)
        }
        else{
        
            System.out.println("Game has ended with draw.");
            
            // TODO print more information (especially score related informations)
            //  the draw does not mean that the scores are equal, identic
            //  It only means the the recent status (step based) is equal with each other
        }
    }
    
    public void setDepth(int depth) throws Exception{
    
        stepSequences.setDepth(depth);
    }
    
    public void requestPlayerAction() throws Exception{
    
        // TODO listen for player action within a determined timeout, validate 
        //      action and return control to machine player
        
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in)); 
        
        String action;
        action = inputReader.readLine();
        
        if(action.isEmpty()){
        
            throw new Exception("Provided input is not acceptable.");
        }
        
        String[] params = action.split("|");
        
        if(params[0].isEmpty()){
        
            throw new Exception("Piece has not been provided.");
        }
        
        if(!pieceNames.contains(params[0])){
        
            throw new Exception("Piece name resolution is unsuccessful.");
        }
        
        int selectedFile = 0;
        
        if(params[1].charAt(0) < 'a' || params[1].charAt(0) > 'h'){
        
            throw new Exception("File is out of range.");
        }
        
        selectedFile = (int)params[1].charAt(0);
        int selectedRank = 0;
        
        if(params[1].charAt(1) < 1 || params[1].charAt(1) > 8){
        
            throw new Exception("Rank is out of range.");
        }
        
        selectedRank = (int)params[1].charAt(1);
        
        GenPiece selectedPiece = pieces[pieceNames.indexOf(params[0])];
        
        if(!(selectedPiece.generateSteps(gameBoard).contains(
                new Pair(selectedFile, selectedRank)))){
        
            throw new Exception("Illegal selected step by chosen piece.");
        }
        
        if(gameBoard[selectedFile][selectedRank] != -1){
            
            // hit occurs
            pieceNames.set(gameBoard[selectedFile][selectedRank], "");
        }
        else{
        
            selectedPiece.setFile(selectedFile);
            selectedPiece.setRank(selectedRank);
            gameBoard[selectedFile][selectedRank] = pieceNames.indexOf(params[0]);
        }
        
        Step selectedStep;
        
        if(stepSequences.size() > 0){
        
            // finding step node with given position
            ArrayList<GenStepKey> levelKeys = stepSequences.getLeafLevelKeys();

            int sizeOfLevelKeys = levelKeys.size();

            int i = 0;

            for(; i < sizeOfLevelKeys; ++i){

                selectedStep = stepSequences.getByKey(levelKeys.get(i));

                if(selectedStep.getFile() == selectedFile 
                        && selectedStep.getRank() == selectedRank){

                    break;
                }
            }
            
            // TASK) shift tree with one level, throw root away (root displacement)
            stepSequences.setNewRootByKey(levelKeys.get(i));
            
            stepHistory.add(stepSequences.getByKey(levelKeys.get(i)));
            
            // TASK) TODO rename step node keys/identifiers (cyclic renaming)
            //       in order to limit the key length (comparison optimization)
            stepSequences.trimKeys();
            
        }
        else{
            
            selectedStep = new Step(gameBoard[selectedFile][selectedRank], 
                    selectedFile, selectedRank, selectedPiece.getValue(), 
                    0, selectedPiece.getValue());
            
            stepSequences.addOne(new GenStepKey("a"), new GenStepKey("a"), selectedStep);
            
            stepHistory.add(selectedStep);
        }
    }
    
    // TODO it could be integrated in step decision tree builder
    private void selectNextStep() throws Exception{
    
        // TASK) select the best option - max search, and apply to the game using 
        //       posteriori update(perform update after execution of further subroutines 
        //       of this method)
        
        ArrayList<GenStepKey> levelKeys = stepSequences.getLeafLevelKeys();
        
        int sizeOfLeafSteps = levelKeys.size();
        int maxI = 0;
        double currCumulativeValue = 0.0;
        double maxCumulativeValue = stepSequences.getByKey(
                levelKeys.get(maxI)).getCumulativeValue();
        
        for(int i = 1; i < sizeOfLeafSteps; ++i){
            
            currCumulativeValue = stepSequences.getByKey(
                levelKeys.get(i)).getCumulativeValue();
            
            if(maxCumulativeValue < currCumulativeValue){
            
                maxCumulativeValue = currCumulativeValue;
                maxI = i;
            }
        }
        
        // TASK) shift tree with one level, throw root away (root displacement)
        stepSequences.setNewRootByKey(levelKeys.get(maxI));
        
        stepHistory.add(stepSequences.getByKey(levelKeys.get(maxI)));
        
        // TASK) TODO rename step node keys/identifiers (cyclic renaming)
        //       in order to limit the key length (comparison optimization)
        stepSequences.trimKeys();
        
        
        // TASK) TODO yield control to opponent player (asynchronous tasks)
    }
}
