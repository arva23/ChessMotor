package chessmotor.view;

// this class represents the game board that is consisted of squared

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

// the squares are managed by pooling the events, only one event is by event 
//  types to avoid overwhelmingly loaded listener cases
public class GameBoard implements IGameBoard {

    // square type
    private ImageIcon[] squareBgs;// 0 white, 1 black

    // piece types
    private HashMap<String, ImageIcon> pieceTypes;
    
    private static int squareWidth;
    private static int squareHeight;
    private static int boardWidth;
    private static int boardHeight;
    
    private UnitSquare[][] board;// strictly 8 x 8 board
    // boardStatus is assigned at game controller object temporarily
     
    private Condition playerWaitCond;
    private Condition playerActionCond;
    
    private boolean allyComes;
    private boolean allyBegins;
    private JPanel eventHandlerPanel;
    
    
    public GameBoard(int x, int y, int widht, int height, 
            Condition playerWaitCond, Condition playerActionCond,
            boolean allyComes, String[][] boardSquareStatus){
    
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
        
        eventHandlerPanel.setBounds(x, y, widht, height);
        
        this.playerWaitCond = playerWaitCond;
        this.playerActionCond = playerActionCond;
        
        this.allyComes = allyComes;
        this.allyBegins = allyComes;
        
        if(!allyComes){
        
            squareBgs[0] = squareBgs[1];
            squareBgs[1] = squareBgs[2];
            squareBgs[2] = squareBgs[0];
        }
        
        board = new UnitSquare[8][8];
        int squareTypeId = 0;
        
        for(int rank = 0; rank < 8; ++rank){
        
            for(int file = 0; file < 8; ++file){
            
                squareTypeId = file % 2 + rank % 2;
                board[rank][file] = new UnitSquare(
                        file * squareHeight, rank * squareWidth, 
                        squareWidth, squareHeight, squareBgs[squareTypeId],
                        pieceTypes.get(boardSquareStatus[rank][file]));
            }
        }
        
        eventHandlerPanel = new JPanel(/*todo position and dimensions*/);
    }
    
    @Override
    public void setGameBoard(String[][] boardSquareStatus, boolean allyBegins,
            boolean allyComes){
    
    }
    
    @Override
    public JPanel getMainPanel(){
    }
    
    @Override
    public void alternateActivePlayer(){
    
    }
    
    @Override
    public String getPlayerActionResult(){
    
    }
    
    @Override
    public UnitSquare getPlayerActionSquare(){
    
    }
    
    @Override
    public void setSquare(boolean isAlly, String pieceType, int rank, int file) throws Exception{
    
    }
}
