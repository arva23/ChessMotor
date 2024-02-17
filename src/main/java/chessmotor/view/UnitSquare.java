package chessmotor.view;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * It is a passive square element with image of chess board
 * Due to massive amount of action listeners for each squares (64 x number of events)
 * position based "pooling" is implemented, this means that the squares do no own
 * events, only setters and getters that are available for board handler
 * @author arva
 */
public class UnitSquare extends JPanel{
    
    private String pieceTypeName;
    private int player;
    
    private JPanel squareBase;
    // through reference (black or white background or else with two significantly 
    //  differentiable colors or textures)
    private ImageIcon baseTexture;
    // in case of selected, marked square, use this texture
    private ImageIcon highlightedBaseTexture;
    private JLabel baseLabel;

    private JPanel squarePiece;
    // locally stored due to different statuses of squares
    private ImageIcon pieceTexture;
    private JLabel pieceLabel;

    /**
     * Parameterized constructor for non-default initialization
     * @param player Selected player's identifier to be used
     * @param pieceTypeName Typename of piece for resolution
     * @param x Visual position of top left corner x coordinate component
     * @param y Visual position of top left corner y coordinate component
     * @param width Visual width of square
     * @param height Visual height of square
     * @param base Base image of square (black or white)
     * @param highlighted Base highlighted background image
     * @param piece Piece image identifier
     */
    public UnitSquare(int player, String pieceTypeName, int x, int y, int width, 
            int height, ImageIcon base, ImageIcon highlighted, ImageIcon piece){
    
        this.player = player;
        this.pieceTypeName = pieceTypeName;
        
        squareBase = new JPanel();
        squareBase.setBounds(x, y, width, height);
        baseTexture = base;
        highlightedBaseTexture = highlighted;
        baseLabel = new JLabel(baseTexture);
        baseLabel.setBounds(x, y, width, height);
        squareBase.add(baseLabel);
        
        squarePiece = new JPanel();
        squarePiece.setBounds(x, y, width, height);
        pieceLabel = new JLabel(pieceTexture);
        pieceLabel.setBounds(x, y, width, height);
        squarePiece.add(pieceLabel);
        
        squareBase.add(squarePiece);
    }
    
    // if cursor is over field and left clicked, set square highlighted
    /**
     * Function sets square highlighted, therefore the selected piece highlighted
     */
    public void setHighlighted(){
        
        baseLabel.setIcon(highlightedBaseTexture);
    }
    
    /**
     * Function removes highlight from square, if another square action is performed,
     * therefore highlight from selected, piece is removed
     */
    public void removeHighlighted(){
    
        baseLabel.setIcon(baseTexture);
    }
    
    // if cursor is over field and left click has occured
    public void setNewPiece(int player, ImageIcon newPiece){
    
        // all pieces including empty square (by null piece object) are stored 
        //  outside of the square object by reference    
        pieceTexture = newPiece;
    }
    
    public String getPieceTypeName(){
    
        return pieceTypeName;
    }
    
    public int getPlayer(){
    
        return player;
    }
}
