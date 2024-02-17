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

/**
 * The squares are managed by event pooling, only on event is by event types to 
 * avoid overwhelmingly loaded listener cases
 * @author arva
 */
public class GameBoardView implements IGameBoardView {

    private IConsoleUI consoleUI;
    
    // square type
    private ImageIcon[] squareBgs;// 0 white, 1 black

    // piece types
    private HashMap<String, ImageIcon> pieceTypes;
    
    private int squareWidth;
    private int squareHeight;
    
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
     * @param consoleUI Manages error messages toward console
     * @param x Upper left corner x coordinate component
     * @param y Upper left corner y coordinate component
     * @param width Width of the game table
     * @param height Height of the game table
     * @param displayWidth horizontal resolution of display
     * @param displayHeight vertical resolution of display
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
            int displayWidth, int displayHeight, boolean machineComes, 
            String[][] boardSquareStatus) throws Exception{
    
        this.consoleUI = consoleUI;
        
        try {
            
            /**
             * Source of images: https://www.pngegg.com/en/png-bsylj
             */
            
            String srcPath = "gui_src";
            
            // loading square backgrounds textures
            squareBgs = new ImageIcon[4];
            squareBgs[0] = new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "blackSquareBg.png").toString())));
            squareBgs[1] = new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "whiteSquareBg.png").toString())));
            squareBgs[2] = new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "blackHighlightedSquareBg.png").toString())));
            squareBgs[3] = new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "whiteHighlightedSquareBg.png").toString())));
        
            // loading piece textures
            pieceTypes = new HashMap<>();
            pieceTypes.put("empty", new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "emptyPiece.png").toString()))));
            pieceTypes.put("whitepawn", new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "whitePawn.png").toString()))));
            pieceTypes.put("whiterook", new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "whiteRook.png").toString()))));
            pieceTypes.put("whiteknight", new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "whiteKnight.png").toString()))));
            pieceTypes.put("whitebishop", new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "whiteBishop.png").toString()))));
            pieceTypes.put("whiteking", new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "whiteKing.png").toString()))));
            pieceTypes.put("whitequeen", new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "whiteQueen.png").toString()))));
            pieceTypes.put("blackpawn", new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "blackPawn.png").toString()))));
            pieceTypes.put("blackrook", new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "blackRook.png").toString()))));
            pieceTypes.put("blackknight", new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "blackKnight.png").toString()))));
            pieceTypes.put("blackbishop", new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "blackBishop.png").toString()))));
            pieceTypes.put("blackking", new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "blackKing.png").toString()))));
            pieceTypes.put("blackqueen", new ImageIcon(ImageIO.read(
                    new File(Paths.get(srcPath, "blackQueen.png").toString()))));
        } 
        catch (IOException ex) {
            
            consoleUI.println("Error at loading gameboard textures.");
        }    
        
        if(width < 0 || width > displayWidth){
        
            throw new ValueOutOfRangeException("Width parameter is out range.");
        }
        
        if(height < 0 || height > displayHeight){
        
            throw new ValueOutOfRangeException("Height parameter is out of range.");
        }
        
        if(x < 0 || x > displayWidth - width){
        
            throw new ValueOutOfRangeException("X coordinate is out of range.");
        }
        
        
        if(y < 0 || y > displayHeight - height){
        
            throw new ValueOutOfRangeException("Y coordinate is out of range.");
        }
        
        eventHandlerPanel = new JPanel();
        eventHandlerPanel.setBounds(x, y, width, height);

        this.machineComes = machineComes;
        this.machineBegins = machineComes;
        
        
        if(!machineComes){
        
            ImageIcon tmp = squareBgs[0];
            squareBgs[0] = squareBgs[1];
            squareBgs[1] = tmp;
            
            tmp = squareBgs[2];
            squareBgs[2] = squareBgs[3];
            squareBgs[3] = tmp;
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
                        squareWidth, squareHeight, 
                        squareBgs[squareTypeId], squareBgs[squareTypeId + 2],
                        pieceTypes.get(this.boardSquareStatus[rankInd][fileInd]));
            }
        }
        
        for(int rankInd = 2; rankInd < 6; ++rankInd){
        
            for(int fileInd = 0; fileInd < 8; ++fileInd){
                
                squareTypeId = fileInd % 2 + rankInd % 2;
                board[rankInd][fileInd] = new UnitSquare(0, 
                        this.boardSquareStatus[rankInd][fileInd],
                        fileInd * squareWidth, rankInd * squareHeight, 
                        squareWidth, squareHeight, 
                        squareBgs[squareTypeId], squareBgs[squareTypeId + 2],
                        pieceTypes.get(this.boardSquareStatus[rankInd][fileInd]));
            }
        }
        
        for(int rankInd = 6; rankInd < 8; ++rankInd){
        
            for(int fileInd = 0; fileInd < 8; ++fileInd){
            
                squareTypeId = fileInd % 2 + rankInd + 2;
                board[rankInd][fileInd] = new UnitSquare(
                        machineBegins ? 0 : 1, this.boardSquareStatus[rankInd][fileInd],
                        fileInd * squareWidth, rankInd * squareHeight,
                        squareWidth, squareHeight, 
                        squareBgs[squareTypeId], squareBgs[squareTypeId + 2],
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
                        squareBgs[squareTypeId + 2],
                        pieceTypes.get(typeName));
            }
        }
    }
    
    /**
     * It sets the new status of board square by literal type of piece identifiers
     * @param newBoardSquareStatus New board status
     */
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
    
    /**
     * Function return board game status
     * @return Board game status in matrix literal form
     */
    public String[][] getBoardSquareStatus(){
    
        return boardSquareStatus;
    }
    
    /**
     * Function returns main panel of game board view object
     * @return Main panel
     */
    @Override
    public JPanel getMainPanel(){
    
        return eventHandlerPanel;
    }
    
    /**
     * Function switches current player to other player for providing alternating 
     * play
     */
    @Override
    public void alternateActivePlayer(){
    
        machineComes = !machineComes;
    }
    
    /**
     * Function returns result of player action
     * @return Returns the executed, commanded coordinate with the given piece
     */
    @Override
    public String getPlayerActionResult(){
    
        return "" + recentRank + recentFile;
    }
    
    /**
     * Function returns the player action square for further processing
     * @return Player action square
     */
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
     * It updates square background according to its selection status. If the 
     * square can be found on the given coordinates, the function updates its 
     * background to its visual highlighted status.
     * @param rank Rank of selected square
     * @param file File of selected square
     * @throws Exception ValueOutOfRangeException
     */
    public void addSquareHighlight(int rank, int file) throws Exception{
    
        if(rank < 0 || rank > 7){
        
            throw new ValueOutOfRangeException("Rank is out of range.");
        }
        
        if(file < 0 || file > 7){
        
            throw new ValueOutOfRangeException("File is out of range.");
        }
        
        board[rank][file].setHighlighted();
    }
    
    /**
     * It updates removes highlighted square background according to its selection 
     * status. If the square can be found on the given coordinates, the function 
     * removes its visual highlight status.
     * @param rank Rank of selected square
     * @param file File of selected square
     * @throws Exception ValueOutOfRangeException
     */
    public void removeSquareHighlight(int rank, int file) throws Exception{
    
        if(rank < 0 || rank > 7){
        
            throw new ValueOutOfRangeException("Rank is out of range.");
        }
        
        if(file < 0 || file > 7){
        
            throw new ValueOutOfRangeException("File is out of range.");
        }
        
        board[rank][file].removeHighlighted();
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
    
    /**
     * Triggers a waiting for board action request
     * @throws InterruptedException Inherited condition variable exceptions
     */
    @Override
    public void waitForBoard() throws InterruptedException{
    
        playerBoardWaitCond.await();
    }
    
    /**
     * Terminates waiting for board action request
     * @throws InterruptedException Inherited condition variable exceptions
     */
    @Override
    public void signalForBoard() throws InterruptedException{
    
        playerBoardWaitCond.signal();
    }
    
    /**
     * Triggers a waiting for performed board actions
     * @throws InterruptedException Inherited condition variable exceptions
     */
    @Override
    public void waitForAction() throws InterruptedException{
    
        playerBoardActionCond.await();
    }
    
    /**
     * Terminates waiting for performed board actions
     * @throws InterruptedException Inherited condition variable exceptions
     */
    @Override
    public void signalForAction() throws InterruptedException{
    
        playerBoardActionCond.signal();
    }
}
