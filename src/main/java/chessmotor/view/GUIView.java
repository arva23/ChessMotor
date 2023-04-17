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
    private boolean allyBegins;
    private GameBoard board;
    private Condition playerWaitCond;
    private Condition playerActionCond;
    
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
        int[][] boardSquareStatus = new int[8][8];
        
        for(int i = 0; i < 8; ++i){

            boardSquareStatus[1][i] = i;
            boardSquareStatus[6][i] = 16 + i;
        }

        boardSquareStatus[0][0] = 8;
        boardSquareStatus[0][1] = 9;
        boardSquareStatus[0][2] = 10;
        boardSquareStatus[0][3] = 11;
        boardSquareStatus[0][4] = 12;
        boardSquareStatus[0][5] = 13;
        boardSquareStatus[0][6] = 14;
        boardSquareStatus[0][7] = 15;

        boardSquareStatus[7][0] = 16 + 8;
        boardSquareStatus[7][1] = 16 + 9;
        boardSquareStatus[7][2] = 16 + 10;
        boardSquareStatus[7][3] = 16 + 11;
        boardSquareStatus[7][4] = 16 + 12;
        boardSquareStatus[7][5] = 16 + 13;
        boardSquareStatus[7][6] = 16 + 14;
        boardSquareStatus[7][7] = 16 + 15;
        
        for(int rankInd = 2; rankInd < 5; ++rankInd){
        
            for(int fileInd = 0; fileInd < 8; ++fileInd){
            
                boardSquareStatus[rankInd][fileInd] = -1;
            }
        }
        
        board = new GameBoard(boardX, boardY, boardWidth, boardHeight, 
                playerWaitCond, playerActionCond, allyBegins, boardSquareStatus);
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
    public void updateGameStatus(int gameStatus) {
        
        // todo
    }

    @Override
    public void switchPlayerClock() {
    }

    @Override
    public String readPlayerAction() throws InterruptedException {
        
        String action = "";
        return action;
    }
    
    @Override
    public void applyGenPlayerAction(String pieceType, int sourceRank, int sourceFile,
            int targetRank, int targetFile) throws Exception{
    }
    
}
