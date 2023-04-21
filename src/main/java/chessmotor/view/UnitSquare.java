package chessmotor.view;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

// it is a passive square element with image of chess board
// due to massive amount of action listeners for each squares (64 x number of events)
//  position based "pooling" is implemented, this means that the squares do no own
//  events, only setters and getters that are available for board handler
public class UnitSquare extends JPanel{
    
    private String pieceTypeName;
    private int player;
    
    private JPanel squareBase;
    // through reference (black or white background or else with two significantly 
    //  differentiable colors or textures)
    private ImageIcon baseTexture;
    private JLabel baseLabel;

    private JPanel squarePiece;
    // locally stored due to different statuses of squares
    private ImageIcon pieceTexture;
    private JLabel pieceLabel;
    
    public UnitSquare(int player, String pieceTypeName, int x, int y,
            int width, int height, ImageIcon base, ImageIcon piece){
    
        this.player = player;
        this.pieceTypeName = pieceTypeName;
        
        squareBase = new JPanel();
        squareBase.setBounds(x, y, width, height);
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
    
    // if cursor is over field, set square highlighted
    public void setHighlighted(){
        
        // todo
    }
    
    public void removeHighlighted(){
    
        // todo
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
