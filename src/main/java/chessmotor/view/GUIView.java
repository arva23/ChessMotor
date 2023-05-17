package chessmotor.view;


import chessmotor.enginecontroller.ComplexGameStatus;
import chessmotor.enginecontroller.GameController;
import chessmotor.enginecontroller.IGame;
import genmath.genmathexceptions.ValueOutOfRangeException;
import java.awt.Container;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;

/**
 * It is an version of implementation of a graphical user interface based on 
 * interface defined restrictions
 * @author arva
 */
public class GUIView implements IGameUI{
    
    private IConsoleUI consoleUI;
    
    private GameController gameCtl;
    private IGame gameInstance;
    private int windowX;
    private int windowY;
    private int windowWidth;
    private int windowHeight;
    private double boardRatio;
    
    private AtomicBoolean giveUpHumanPlayerVisual;
    
    private String machineColor = "white";
    private String oppColor = "black";
    
    private JFrame mainWindow;
    private Container elementContainer;
    
    // GAME BOARD MANAGEMENT
    private boolean machineBegins;
    private GameBoardView board;

    // PLAYER CLOCK MANAGEMENT
    private PlayerClock playerClocks;
    private ExecutorService playerClocksExecutor;
    
    // GAME STATUS MANAGEMENT
    private GameStatusMgr gameStatusMgr;
   
    /**
     * Parameterized constructor for operation initialization
     * @param consoleUI Console line message manager
     * @param gameCtl Upper high level game controller that manages the application,
     *        singleton pattern
     * @param gameInstance game object interface toward realized current game instance 
     *        controller object
     * @param windowX The top left x coordinate component of corner of application 
     *        window
     * @param windowY The top left y coordinate of component corner of application 
     *        window
     * @param windowWidth The width of window in the screen, the top left x 
     *        coordinate component position is counted at handling
     * @param windowHeight The height of window in the screen, the top left y 
     *        coordinate component position is counted at handling
     * @throws Exception 
     *         Null game controller object
     *         Null current game controller object
     *         Window width is out of range
     *         Window height is out of range
     *         Top left corner x coordinate component is out of range
     *         Top left corner y coordinate component is out of range
     */
    public GUIView(IConsoleUI consoleUI, GameController gameCtl, IGame gameInstance, int windowX,
            int windowY, int windowWidth, int windowHeight) throws Exception{
    
        if(consoleUI == null) {
        
            throw new NullPointerException("Console user interface object is null.");
        }
    
        this.consoleUI = consoleUI;
        
        if(gameCtl == null){
        
            throw new NullPointerException("Game controller object is null.");
        }
        
        this.gameCtl = gameCtl;
        
        if(gameInstance == null){
        
            throw new NullPointerException("Current game controller object is null.");
        }
        
        this.gameInstance = gameInstance;
        
        if(windowWidth < 0 || windowWidth > 1366){
        
            throw new ValueOutOfRangeException("Window width is out of range.");
        }
        
        this.windowWidth = windowWidth;
        
        if(windowHeight < 0 || windowHeight > 768){
        
            throw new ValueOutOfRangeException("Window height is out of range.");
        }
        
        this.windowHeight = windowHeight;
        
        if(windowX < 0 || windowX > 1366 - windowWidth){
        
            throw new ValueOutOfRangeException("Start x position is out of range.");
        }
        
        this.windowX = windowX;
        
        if(windowY < 0 || windowY > 768 - windowHeight){
        
            throw  new ValueOutOfRangeException("Start y position is out of range.");
        }
        
        this.windowY = windowY;
        
        giveUpHumanPlayerVisual.set(false);
        
        mainWindow = new JFrame("ChessMotor - chess engine");
        mainWindow.setBounds(windowX, windowY, windowWidth, windowHeight);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        elementContainer = mainWindow.getContentPane();
    
        // initialization of game board
        
        int boardX = 0;
        int boardY = 0;
        int boardWidth = (int)((double)windowWidth * boardRatio);
        int boardHeight = (int)((double)windowHeight * boardRatio);
        
        // default initialization of game board status
        String[][] boardSquareStatus = new String[8][8];
        
        machineColor = "white";
        oppColor = "black";
        
        // board is rotated at game board
        if(machineBegins){
        
            machineColor = "black";
            oppColor = "white";
        }
        
        for(int i = 0; i < 8; ++i){

            boardSquareStatus[1][i] = machineColor + "pawn";
            boardSquareStatus[6][i] = oppColor + "pawn";
        }

        boardSquareStatus[0][0] = machineColor + "rook";
        boardSquareStatus[0][1] = machineColor + "knight";
        boardSquareStatus[0][2] = machineColor + "bishop";
        boardSquareStatus[0][3] = machineColor + "king";
        boardSquareStatus[0][4] = machineColor + "queen";
        boardSquareStatus[0][5] = machineColor + "bishop";
        boardSquareStatus[0][6] = machineColor + "knight";
        boardSquareStatus[0][7] = machineColor + "rook";

        boardSquareStatus[7][0] = oppColor + "rook";
        boardSquareStatus[7][1] = oppColor + "knight";
        boardSquareStatus[7][2] = oppColor + "bishop";
        boardSquareStatus[7][3] = oppColor + "king";
        boardSquareStatus[7][4] = oppColor + "queen";
        boardSquareStatus[7][5] = oppColor + "bishop";
        boardSquareStatus[7][6] = oppColor + "knight";
        boardSquareStatus[7][7] = oppColor + "rook";
        
        for(int rankInd = 2; rankInd < 5; ++rankInd){
        
            for(int fileInd = 0; fileInd < 8; ++fileInd){
            
                boardSquareStatus[rankInd][fileInd] = "empty";
            }
        }
        
        board = new GameBoardView(consoleUI, boardX, boardY, boardWidth, 
                boardHeight, machineBegins, boardSquareStatus);
        elementContainer.add(board.getMainPanel());
        
        int playerClocksX = boardWidth + 1;
        int playerClocksY = 0;
        int playerClocksWidth = 300;
        int playerClocksHeight = 150;
        playerClocks = new PlayerClock(consoleUI, playerClocksX,
                playerClocksY, playerClocksWidth, 
                playerClocksHeight);
        elementContainer.add(playerClocks.getMainPanel());
        playerClocksExecutor = Executors.newFixedThreadPool(1);
        playerClocksExecutor.execute(playerClocks);
        playerClocks.start();
        
        int statusMgrX = boardWidth + 1;
        int statusMgrY = playerClocksHeight + 1;
        int statusMgrWidth = playerClocksWidth;
        int statusMgrHeight = boardHeight - playerClocksHeight;
        gameStatusMgr = new GameStatusMgr(statusMgrX, statusMgrY,
                statusMgrWidth, statusMgrHeight);
        elementContainer.add(gameStatusMgr.getMainPanel());
    }
    
    /**
     * Game status loader that sets the actual game status to the recently 
     * selected previously saved one
     * @param gameStatus Previously saved game status
     */
    @Override
    public void loadGame(ComplexGameStatus gameStatus){
    
        playerClocks.stop();
        playerClocksExecutor.shutdown();
        board.setGameBoard(gameStatus);
        playerClocks.setPlayersClock(
                gameStatus.getWhitePlayerTime(), 
                gameStatus.getBlackPlayerTime(), 
                gameStatus.getWhitePlayerComes());
        playerClocksExecutor.execute(playerClocks);
        playerClocks.start();
    }

    @Override
    public void run() {
        
        // todo
    }

    @Override
    public void printErr(String errMsg) {
     
        // todo
    }
    
    /**
     * It delegates method for game status loading
     * @param gameStatus Name/identifier of game status
     */
    @Override
    public void updateGameStatus(String gameStatus) {
        
        // todo
    }

    /**
     * Switches player clock at dual clock manager
     */
    @Override
    public void switchPlayerClock() {
        
        playerClocks.switchPlayer();
    }
    
    /**
     * Reads two step after each other, first the source position that contains 
     * an ally piece then secondly the arbitrary second position with the 
     * restriction of non-ally piece presence
     * @return
     * @throws InterruptedException 
     */
    @Override
    public String readPlayerAction() throws InterruptedException, Exception {
        
        String action = "";
        board.alternateActivePlayer();
        boolean castlingAllowed = false;
        
        while(true){
        
            board.signalForBoard();
            board.waitForAction();
            
            if(!giveUpHumanPlayerVisual.get()
                && ((machineBegins && board.getPlayerActionSquare().getPlayer() < 0)
                || (!machineBegins && board.getPlayerActionSquare().getPlayer() > 0 ))){
                
                if(board.pieceEquals(board.getPlayerActionResult(), "king")){
                
                    castlingAllowed = true;
                }
                
                break;
            }
            else{
                throw new Exception("Illegal selection, choose another.");
            }
        }
        
        action += board.getPlayerActionResult();

        while(true){
        
            board.signalForBoard();
            board.waitForAction();
    
            if(!giveUpHumanPlayerVisual.get()
                && ((!machineBegins && board.getPlayerActionSquare().getPlayer() > 0) 
                || (!machineBegins && board.getPlayerActionSquare().getPlayer() < 0
                || (board.pieceEquals(board.getPlayerActionResult(), "rook"))))){
                
                break;
            }
            else{
            
                if(castlingAllowed && ((machineBegins && board.getPlayerActionSquare().getPlayer() < 0)
                    || (!machineBegins && board.getPlayerActionSquare().getPlayer() > 0))){

                    if(board.pieceEquals(board.getPlayerActionResult(), "rook")){

                        castlingAllowed = false;
                        break;
                    }
                }
                
                throw new Exception("Illegal selection, choose another.");
            }
        }
        
        action += "|";
        action += board.getPlayerActionResult();
        
        return action;
    }

    /**
     * Its main purpose is to provide functionality to special step case, promotion
     * 
     * This is a modifier method that directly invokes GAmeBoardView method
     * that updates the gable partially while entering pawn replacement mode
     * then restores the visual game board status after promotion has occurred
     * @return
     * @throws InterruptedException 
     */
    @Override
    public String selectPawnReplacement() throws InterruptedException, Exception{
    
        // function setup
        
        String[][] origBoardStatus = board.getBoardSquareStatus();
        
        String[][] promotionChoiceStatus = new String[8][8];
        
        Stack<String> removedPlayerPieces = gameInstance.machineComes() ? 
                gameInstance.getMachinePromotionTypeNames() 
                : gameInstance.getHumanPromotionTypeNames();
        
        int numOfAvailablePieces = removedPlayerPieces.size();
        
        int endRank = (int)Math.ceil(Math.sqrt(numOfAvailablePieces));
        int startRank = (8 - endRank) / 2;
        
        int endFile = (int)Math.floor(Math.sqrt(numOfAvailablePieces));
        int startFile = (8 - endFile) / 2;
        
        for(int rankInd = 0; rankInd < 8; ++rankInd){
        
            for(int fileInd = 0; fileInd < 8; ++fileInd){
            
                promotionChoiceStatus[rankInd][fileInd] = "empty";
            }
        }
        
        int removedInd = 0;
        
        for(int rankInd = startRank; rankInd < endRank; ++rankInd){
        
            for(int fileInd = startFile; fileInd < endFile; ++fileInd){
            
                promotionChoiceStatus[rankInd][fileInd] = 
                        removedPlayerPieces.get(removedInd);
                
                ++removedInd;
                
                if(removedInd > numOfAvailablePieces){
                
                    break;
                }
            }
        }

        board.setBoardSquareStatus(promotionChoiceStatus);
        
        String action = "";
        
        while(!giveUpHumanPlayerVisual.get()){
        
            board.signalForBoard();
            board.waitForAction();
            
            if(board.pieceEquals(board.getPlayerActionResult(), "empty")){

                throw new Exception("Illegal selection, choose another.");
            }
            else{
            
                break;
            }
        }
        
        action += board.getPlayerActionResult();
        
        board.setBoardSquareStatus(origBoardStatus);
        
        return action;
    }
    
    /**
     * Player action visual persistence provider, it uses game board component
     * @param pieceType The selected piece
     * @param sourceRank Source position rank coordinate
     * @param sourceFile Source position file coordinate
     * @param targetRank Target position rank coordinate
     * @param targetFile Target position file coordinate
     * @throws Exception 
     *         Source rank range violation
     *         Source file range violation
     *         Target rank range violation
     *         GameBoard related exceptions (see further relations)
     */
    @Override
    public void applyGenPlayerAction(String pieceType, int sourceRank, int sourceFile,
            int targetRank, int targetFile) throws Exception{
    
        if(sourceRank < 0 || sourceRank > 7){
        
            throw new ValueOutOfRangeException("Source rank is out of range.");
        }
        
        if(sourceFile < 0 || sourceFile > 7){
        
            throw new ValueOutOfRangeException("Source file is out of range.");
        }
        
        if(targetRank < 0 || targetRank > 7){
        
            throw new ValueOutOfRangeException("Target rank is out of range.");
        }
    
        if(targetFile < 0 || targetFile > 7){
        
            throw new ValueOutOfRangeException("Target file is out of range.");
        }
        
        board.setSquare(machineBegins, "empty", sourceRank, sourceFile);
        board.setSquare(machineBegins, pieceType, targetRank, targetFile);
    }
    
    /**
     * It provides an option for human player give up and make the machine 
     * win the game
     */
    @Override
    public void giveUpHumanPlayer(){
    
        giveUpHumanPlayerVisual.set(true);
        gameCtl.giveUpHumanPlayer();
    }
}
