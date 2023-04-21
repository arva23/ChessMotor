package chessmotor.view;

// this class represents the game board that is consisted of squared

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EventListener;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

// the squares are managed by pooling the events, only one event is by event 
//  types to avoid overwhelmingly loaded listener cases
public class GameBoard implements IGameBoard {

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
    
    public GameBoard(int x, int y, int width, int height, 
            Condition playerBoardWaitCond, Condition playerBoardActionCond,
            boolean machineComes, String[][] boardSquareStatus) throws Exception{
    
        try {
            
            // loading square backgrounds textures
            squareBgs = new ImageIcon[3];
            squareBgs[0] = new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "blackSquareBg.png").toString())));
            squareBgs[1] = new ImageIcon(ImageIO.read(
                    new File(Paths.get("board", "whiteSquareBg.png").toString())));
            squareBgs[2] = squareBgs[0];
        
            // loading piece textures
            pieceTypes = new HashMap<String, ImageIcon>();
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
            
            System.out.println("Error at loading gameboard textures.");
        }    
        
        if(width < 0 || width > 1366){
        
            throw new Exception("Width parameter is out range.");
        }
        
        this.boardWidth = width;
        
        if(height < 0 || height > 768){
        
            throw new Exception("Height parameter is out of range.");
        }
        
        this.boardHeight = height;
        
        if(x < 0 || x > 1366 - width){
        
            throw new Exception("X coordinate is out of range.");
        }
        
        this.boardX = x;
        
        if(y < 0 || y > 768 - height){
        
            throw new Exception("Y coordinate is out of range.");
        }
        
        this.boardY = y;
        
        eventHandlerPanel = new JPanel();
        eventHandlerPanel.setBounds(x, y, width, height);

        this.playerBoardWaitCond = playerBoardWaitCond;
        this.playerBoardActionCond = playerBoardActionCond;
        
        this.machineComes = machineComes;
        this.machineBegins = machineComes;
        
        if(!machineComes){
        
            squareBgs[0] = squareBgs[1];
            squareBgs[1] = squareBgs[2];
            squareBgs[2] = squareBgs[0];
        }
        
        // default initialization of game board
        board = new UnitSquare[8][8];
        int squareTypeId = 0;
        
        for(int rankInd = 0; rankInd < 8; ++rankInd){
        
            for(int fileInd = 0; fileInd < 8; ++fileInd){
            
                squareTypeId = fileInd % 2 + rankInd % 2;
                
                board[rankInd][fileInd] = new UnitSquare(
                        rankInd * squareHeight, fileInd * squareWidth, 
                        squareWidth, squareHeight, squareBgs[squareTypeId],
                        pieceTypes.get(boardSquareStatus[rankInd][fileInd]));
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
                        
                        System.out.println("Player action requirest has been failed.");
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
    
    @Override
    public void setGameBoard(String[][] boardSquareStatus, boolean machineBegins,
            boolean machineComes){
    
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
    
    @Override
    public void setSquare(boolean isAlly, String pieceType, int rank, int file) throws Exception{
    
        if(rank < 0 || rank > 7){
        
            throw new Exception("Rank is out of range.");
        }
        
        if(file < 0 || file > 7){
        
            throw new Exception("File is out of range.");
        }
                    
        if(!pieceTypes.containsKey(pieceType)){
        
            throw new Exception("No such piece type exists.");
        }
        
        board[rank][file].setNewPiece((isAlly ? -1 : 1), 
                pieceTypes.get(pieceType));
    }
    
    public boolean pieceEquals(String pos, String pieceTypeName){
    
        return board[pos.charAt(0)][pos.charAt(1)].getPieceTypeName()
                .equals(pieceTypeName);
    }
}
