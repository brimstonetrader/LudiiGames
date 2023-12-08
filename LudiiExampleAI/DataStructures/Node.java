package DataStructures;
import java.util.ArrayList;
import java.util.List;

import game.Game;
import main.collections.FastArrayList;
import other.context.Context;
import other.move.Move;

  /**
    * Inner class for nodes used by example UCT
    * 
    * @author Dennis Soemers
    */
  public class Node
  {
    /** Our parent node */
    public final Node parent;
    
    /** The move that led from parent to this node */
    public final Move moveFromParent;
    
    /** This objects contains the game state for this node (this is why we don't support stochastic games) */
    public final Context context;
    
    /** Visit count for this node */
    public int visitCount = 0;
    
    /** For every player, sum of utilities / scores backpropagated through this node */
    public final double[] scoreSums;
    
    /** Child nodes */
    public final List<Node> children = new ArrayList<Node>();
    
    /** List of moves for which we did not yet create a child node */
    public final FastArrayList<Move> unexpandedMoves;
    
    /**
     * Constructor
     * 
     * @param parent
     * @param moveFromParent
     * @param context
     */
    public Node(final Node parent, final Move moveFromParent, final Context context) {
      this.parent = parent;
      this.moveFromParent = moveFromParent;
      this.context = context;
      final Game game = context.game();
      scoreSums = new double[game.players().count() + 1];
      
      // For simplicity, we just take ALL legal moves. 
      // This means we do not support simultaneous-move games.
      unexpandedMoves = new FastArrayList<Move>(game.moves(context).moves());
      
      if (parent != null)
        parent.children.add(this);
    }
  }
