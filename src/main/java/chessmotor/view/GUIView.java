package chessmotor.view;


import chessmotor.enginecontroller.GameController;
import java.awt.Container;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.locks.Condition;
import javax.swing.JFrame;

public class GUIView implements IGameUI{
    
    private GameController gameCtl;
    private int windowX;
    private int windowY;
    private int windowWidth;
    private int windowHeight;
    private double boardRatio;
    
    private JFrame mainWindow;
    private Container elementContainer;
    
    // GAME BOARD MANAGEMENT
    private boolean machineBegins;
    private GameBoard board;
    private Condition playerBoardWaitCond;
    private Condition playerBoardActionCond;
    
    // PLAYER CLOCK MANAGEMENT
    private PlayerClock playerClocks;
    private ExecutorService playerClocksExecutor;
    
    // GAME STATUS MANAGEMENT
    private GameStatusMgr gameStatusMgr;
    
    public GUIView(GameController gameCtl, int windowX, int windowY, 
            int windowWidth, int windowHeight) throws Exception{
    
        if(gameCtl == null){
        
            throw new Exception("Game controller object is null.");
        }
        
        this.gameCtl = gameCtl;
        
        if(windowWidth < 0 || windowWidth > 1366){
        
            throw new Exception("Window width is out of range.");
        }
        
        this.windowWidth = windowWidth;
        
        if(windowHeight < 0 || windowHeight > 768){
        
            throw new Exception("Window height is out of range.");
        }
        
        this.windowHeight = windowHeight;
        
        if(windowX < 0 || windowX > 1366 - windowWidth){
        
            throw new Exception("Start x position is out of range.");
        }
        
        this.windowX = windowX;
        
        if(windowY < 0 || windowY > 768 - windowHeight){
        
            throw  new Exception("Start y position is out of range.");
        }
        
        this.windowY = windowY;
        
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
        
        String machineColor = "white";
        String oppColor = "black";
        
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
        
        board = new GameBoard(boardX, boardY, boardWidth, boardHeight, 
                playerBoardWaitCond, playerBoardActionCond, machineBegins, boardSquareStatus);
        elementContainer.add(board.getMainPanel());
        
        int playerClocksX = boardWidth + 1;
        int playerClocksY = 0;
        int playerClocksWidth = 300;
        int playerClocksHeight = 150;
        playerClocks = new PlayerClock(playerClocksX, playerClocksY,
                playerClocksWidth, playerClocksHeight);
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
    
    @Override
    public void loadGame(String[][] boardSquareStatus, int whitePlayerTime, 
            int blackPlayerTime, boolean whitePlayerComes, boolean allyBegins, 
            boolean allyComes){
    }

    @Override
    public void run() {
        
        // todo
    }

    @Override
    public void printErr(String errMsg) {
     
        // todo
    }
    
    
    @Override
    public void updateGameStatus(String gameStatus) {
        
        // todo
    }

    @Override
    public void switchPlayerClock() {
        
        playerClocks.switchPlayer();
    }

    @Override
    public String readPlayerAction() throws InterruptedException {
        
        String action = "";
        board.alternateActivePlayer();
        
        while(true){
        
            playerBoardWaitCond.signal();
            playerBoardActionCond.await();
            
            if((machineBegins && board.getPlayerActionSquare().getPlayer() < 0)
                || (!machineBegins && board.getPlayerActionSquare().getPlayer() > 0)){

                System.out.println("Illegal selection, choose another.");
                break;
            }
        }
        
        action += board.getPlayerActionResult();

        while(true){
        
            playerBoardWaitCond.signal();
            playerBoardActionCond.await();
    
            if((machineBegins && board.getPlayerActionSquare().getPlayer() > 0) 
                || (!machineBegins && board.getPlayerActionSquare().getPlayer() < 0)){
            
                System.out.println("Illegal selection, choose another.");
                break;
            }
        }
        
        action += "|";
        action += board.getPlayerActionResult();
        
        return action;
    }
    
    @Override
    public String selectPawnReplacement() throws InterruptedException{
    
    }
    
    @Override
    public void applyGenPlayerAction(String pieceType, int sourceRank, int sourceFile,
            int targetRank, int targetFile) throws Exception{
    
        if(sourceRank < 0 || sourceRank > 7){
        
            throw new Exception("Source rank is out of range.");
        }
        
        if(sourceFile < 0 || sourceFile > 7){
        
            throw new Exception("Source file is out of range.");
        }
        
        if(targetRank < 0 || targetRank > 7){
        
            throw new Exception("Target rank is ou of range.");
        }
    
        if(targetFile < 0 || targetFile > 7){
        
            throw new Exception("Target file is out of range.");
        }
        
        board.setSquare(machineBegins, "empty", sourceRank, sourceFile);
        board.setSquare(machineBegins, pieceType, targetRank, targetFile);
    }
    
}
