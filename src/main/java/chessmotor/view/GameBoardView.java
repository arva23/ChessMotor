package chessmotor.view;

// this class represents the game board that is consisted of squared

import chessmotor.enginecontroller.ComplexGameStatus;
import genmath.genmathexceptions.NoObjectFoundException;
import genmath.genmathexceptions.ValueOutOfRangeException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

// the squares are managed by pooling the events, only one event is by event 
//  types to avoid overwhelmingly loaded listener cases
public class GameBoardView implements IGameBoardView {

    private IConsoleUI consoleUI;
    
    // square type
    private ImageIcon[] squareBgs;// 0 white, 1 black

    // piece types
    private HashMap<String, ImageIcon> pieceTypes;
    
    private int squareWidth;
    private int squareHeight;
    private int boardX;
    private int boardY;
    private int boardWidth;
    private int boardHeight;
    
    private String[][] boardSquareStatus;
    private UnitSquare[][] board;// strictly 8 x 8 board
    // boardStatus is assigned at game controller object temporarily
     
    private Condition playerBoardWaitCond;
    private Condition playerBoardActionCond;
    
    private boolean machineComes;
    private boolean machineBegins;
    private JPanel eventHandlerPanel;
    
    private int recentRank;
    private int recentFile;
    private UnitSquare recentSquare;
    
    /**
     * Parameterized constructor of visual game board object
     * @param x Upper left corner x coordinate component
     * @param y Upper left corner y coordinate component
     * @param width Width of the game table
     * @param height Height of the game table
     * @param machineComes Whether machine is the next player or not
     * @param boardSquareStatus A 2D array that stores all the field/square 
     *        status containment
     * @throws Exception 
     *         x coordinate range violation
     *         y coordinate range violation
     *         width range violation
     *         height range violation
     */
    public GameBoardView(IConsoleUI consoleUI, int x, int y, int width, int height, 
            boolean machineComes, String[][] boardSquareStatus) throws Exception{
    
        this.consoleUI = consoleUI;
        
        try {
            
            // loading square backgrounds textures
            squareBgs = new ImageIcon[3];
            squareBgs[0] = new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "blackSquareBg.png").toString())));
            squareBgs[1] = new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "whiteSquareBg.png").toString())));
            squareBgs[2] = squareBgs[0];
        
            // loading piece textures
            pieceTypes = new HashMap<>();
            pieceTypes.put("empty", new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "emptyPiece.png").toString()))));
            pieceTypes.put("whitepawn", new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "whitepawn.png").toString()))));
            pieceTypes.put("whiterook", new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "whiterook.png").toString()))));
            pieceTypes.put("whiteknight", new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "whiteknight.png").toString()))));
            pieceTypes.put("whitebishop", new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "whitebishop.png").toString()))));
            pieceTypes.put("whiteking", new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "whiteking.png").toString()))));
            pieceTypes.put("whitequeen", new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "whitequeen.png").toString()))));
            pieceTypes.put("blackpawn", new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "blackpawn.png").toString()))));
            pieceTypes.put("blackrook", new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "blackrook.png").toString()))));
            pieceTypes.put("blackknight", new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "blackknight.png").toString()))));
            pieceTypes.put("blackbishop", new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "blackbishop.png").toString()))));
            pieceTypes.put("blackking", new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "blackking.png").toString()))));
            pieceTypes.put("blackqueen", new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "blackqueen.png").toString()))));
        } 
        catch (IOException ex) {
            
            consoleUI.println("Error at loading gameboard textures.");
        }    
        
        if(width < 0 || width > 1366){
        
            throw new ValueOutOfRangeException("Width parameter is out range.");
        }
        
        this.boardWidth = width;
        
        if(height < 0 || height > 768){
        
            throw new ValueOutOfRangeException("Height parameter is out of range.");
        }
        
        this.boardHeight = height;
        
        if(x < 0 || x > 1366 - width){
        
            throw new ValueOutOfRangeException("X coordinate is out of range.");
        }
        
        this.boardX = x;
        
        if(y < 0 || y > 768 - height){
        
            throw new ValueOutOfRangeException("Y coordinate is out of range.");
        }
        
        this.boardY = y;
        
        eventHandlerPanel = new JPanel();
        eventHandlerPanel.setBounds(x, y, width, height);

        this.machineComes = machineComes;
        this.machineBegins = machineComes;
        
        if(!machineComes){
        
            squareBgs[0] = squareBgs[1];
            squareBgs[1] = squareBgs[2];
            squareBgs[2] = squareBgs[0];
        }
        
        // default initialization of game board
        board = new UnitSquare[8][8];
        int squareTypeId;
        
        this.boardSquareStatus = boardSquareStatus;
        
        for(int rankInd = 0; rankInd < 2; ++rankInd){
        
            for(int fileInd = 0; fileInd < 8; ++fileInd){
            
                squareTypeId = fileInd % 2 + rankInd + 2;
                board[rankInd][fileInd] = new UnitSquare(
                        machineBegins ? 1 : 0,
                        this.boardSquareStatus[rankInd][fileInd],
                        fileInd * squareWidth, rankInd * squareHeight,
                        squareWidth, squareHeight, squareBgs[squareTypeId],
                        pieceTypes.get(this.boardSquareStatus[rankInd][fileInd]));
            }
        }
        
        for(int rankInd = 2; rankInd < 6; ++rankInd){
        
            for(int fileInd = 0; fileInd < 8; ++fileInd){
                
                squareTypeId = fileInd % 2 + rankInd % 2;
                board[rankInd][fileInd] = new UnitSquare(0, 
                        this.boardSquareStatus[rankInd][fileInd],
                        fileInd * squareWidth, rankInd * squareHeight, 
                        squareWidth, squareHeight, squareBgs[squareTypeId],
                        pieceTypes.get(this.boardSquareStatus[rankInd][fileInd]));
            }
        }
        
        for(int rankInd = 6; rankInd < 8; ++rankInd){
        
            for(int fileInd = 0; fileInd < 8; ++fileInd){
            
                squareTypeId = fileInd % 2 + rankInd + 2;
                board[rankInd][fileInd] = new UnitSquare(
                        machineBegins ? 0 : 1, this.boardSquareStatus[rankInd][fileInd],
                        fileInd * squareWidth, rankInd * squareHeight,
                        squareWidth, squareHeight, squareBgs[squareTypeId],
                        pieceTypes.get(this.boardSquareStatus[rankInd][fileInd]));
            }
        }
        
        eventHandlerPanel.addMouseListener(new MouseAdapter(){
        
            @Override
            public void mouseClicked(MouseEvent e){
            
                if(!machineComes && e.getButton() == MouseEvent.BUTTON1){
                
                    try {
                        playerBoardWaitCond.await();
                        
                        recentSquare = board[recentRank][recentFile];
                        
                        playerBoardActionCond.signal();
                        
                    }
                    catch (InterruptedException ex) {
                        
                        consoleUI.println("Player action requirest has been failed.");
                    }
                }
            }
            
            @Override
            public void mouseMoved(MouseEvent e){
            
                recentRank =
                        (int)Math.ceil((double)(e.getY()) / (8.0 * squareHeight));                    
                recentFile =
                        (int)Math.ceil((double)(e.getX()) / (8.0 * squareWidth));
            }
        });
    }
    
    /**
     * It sets new game status for visual game board
     * @param gameStatus previously saved game status that stores visual game 
     *        board information
     */
    @Override
    public void setGameBoard(ComplexGameStatus gameStatus){
    
        // recent values are preserved but not used due to immediate modification 
        //  by new values
        this.machineComes = gameStatus.getMachineComes();
        this.machineBegins = gameStatus.getMachineBegins();
        
        this.boardSquareStatus = gameStatus.getBoardSquareStatus();
        
        int squareTypeId;
        char machineCmp = machineBegins ? 'w' : 'b';
        char humanCmp = !machineBegins ? 'b' : 'w';
        int player;
        String typeName;
        
        for(int rankInd = 0; rankInd < 8; ++rankInd){
        
            for(int fileInd = 0; fileInd < 8; ++fileInd){
            
                squareTypeId = fileInd % 2 + rankInd + 2;
                typeName = this.boardSquareStatus[rankInd][fileInd];
                
                if(typeName.charAt(0) == machineCmp){
                
                    player = -1;
                }
                else if(typeName.charAt(0) == humanCmp){
                
                    player = 1;
                }
                else{
                    // neutral piece (empty square))
                    player = 0;
                }
                
                board[rankInd][fileInd] = new UnitSquare(
                        player, typeName, fileInd * squareWidth, 
                        rankInd * squareHeight, squareWidth, 
                        squareHeight, squareBgs[squareTypeId],
                        pieceTypes.get(typeName));
            }
        }
    }
    
    public void setBoardSquareStatus(String[][] newBoardSquareStatus){
    
        this.boardSquareStatus = newBoardSquareStatus;
        
        char machineCmp = machineBegins ? 'w' : 'b';
        char humanCmp = !machineBegins ? 'b' : 'w';
        int player;
        String pieceType;
        
        for(int rankInd = 0; rankInd < 8; ++rankInd){
        
            for(int fileInd = 0; fileInd < 8; ++fileInd){
            
                pieceType = this.boardSquareStatus[rankInd][fileInd];
                
                if(pieceType.charAt(0) == machineCmp){
                
                    player = -1;
                }
                else if(pieceType.charAt(0) == humanCmp){
                
                    player = 1;
                }
                else{
                
                    // neutral piece (empty square)
                    player = 0;
                }
                
                board[rankInd][fileInd].setNewPiece(player, 
                        pieceTypes.get(pieceType));
            }
        }
    }
    
    public String[][] getBoardSquareStatus(){
    
        return boardSquareStatus;
    }
    
    @Override
    public JPanel getMainPanel(){
    
        return eventHandlerPanel;
    }
    
    @Override
    public void alternateActivePlayer(){
    
        machineComes = !machineComes;
    }
    
    @Override
    public String getPlayerActionResult(){
    
        return "" + recentRank + recentFile;
    }
    
    @Override
    public UnitSquare getPlayerActionSquare(){
    
        return recentSquare;
    }
    
    /**
     * It updates the square status by piece change
     * @param isAlly Whether the current piece belongs to ally or not
     * @param pieceType General piece type identifier to locate specific visual 
     *        piece image
     * @param rank Position rank
     * @param file Position file
     * @throws Exception 
     *         Range violation of rank
     *         Range violation of file
     *         No such piece type with the provided identifier
     */
    @Override
    public void setSquare(boolean isAlly, String pieceType, int rank, int file) throws Exception{
    
        if(rank < 0 || rank > 7){
        
            throw new ValueOutOfRangeException("Rank is out of range.");
        }
        
        if(file < 0 || file > 7){
        
            throw new ValueOutOfRangeException("File is out of range.");
        }
                    
        if(!pieceTypes.containsKey(pieceType)){
        
            throw new NoObjectFoundException("No such piece type exists.");
        }
        
        board[rank][file].setNewPiece((isAlly ? -1 : 1), 
                pieceTypes.get(pieceType));
    }
    
    /**
     * It compares the type identifier of a given pieces with an other piece type 
     * identifier
     * @param pos Piece position in literal form
     * @param pieceTypeName The second piece type name identifier to be compared 
     *        with the actual one
     * @return It returns the comparison result (equality, inequality) 
     */
    public boolean pieceEquals(String pos, String pieceTypeName){
    
        return board[pos.charAt(0)][pos.charAt(1)].getPieceTypeName()
                .equals(pieceTypeName);
    }
    
    @Override
    public void waitForBoard() throws InterruptedException{
    
        playerBoardWaitCond.await();
    }
    
    @Override
    public void signalForBoard() throws InterruptedException{
    
        playerBoardWaitCond.signal();
    }
    
    @Override
    public void waitForAction() throws InterruptedException{
    
        playerBoardActionCond.await();
    }
    
    @Override
    public void signalForAction() throws InterruptedException{
    
        playerBoardActionCond.signal();
    }
}
