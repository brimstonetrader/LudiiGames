package DataStructures;
import other.move.Move;

/**
 * Object to be used as key for a move in hash tables.
 * 
 * @author Dennis Soemers
 */
public class MoveKey {
  /** The full move object */
  public final Move move;
    
  /** Cached hashCode */
  private final int cachedHashCode;
    
  /**
   * Constructor
   * @param move
   * @param depth Depth at which the move was played. Can be 0 if not known.
   * Only used to distinguish pass/swap moves at different levels of search tree.
   */
  public MoveKey(final Move move, final int depth) {
    this.move = move;
    final int prime = 31;
    int result = 1;
    
    if (move.isPass()) {
      result = prime * result + depth + 1297;
    } else if (move.isSwap()) {
      result = prime * result + depth + 587;
    } else {
      if (!move.isOrientedMove()) {
        result = prime * result + (move.toNonDecision() + move.fromNonDecision());
      } else {
        result = prime * result + move.toNonDecision();
        result = prime * result + move.fromNonDecision();
      }
      
      result = prime * result + move.stateNonDecision();
    }
      
    result = prime * result + move.mover();
    
    cachedHashCode = result;
  }

  @Override
  public int hashCode()
  {
    return cachedHashCode;
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
      return true;

    if (!(obj instanceof MoveKey))
      return false;
    
    final MoveKey other = (MoveKey) obj;
    if (move == null)
      return (other.move == null);
    
    if (move.isOrientedMove() != other.move.isOrientedMove())
      return false;
    
    if (move.isOrientedMove()) 
    {
      if (move.toNonDecision() != other.move.toNonDecision() || move.fromNonDecision() != other.move.fromNonDecision())
        return false;
    }
    else
    {
      boolean fine = false;
      
      if 
      (
        (move.toNonDecision() == other.move.toNonDecision() && move.fromNonDecision() == other.move.fromNonDecision())
        ||
        (move.toNonDecision() == other.move.fromNonDecision() && move.fromNonDecision() == other.move.toNonDecision())
      )
      {
        fine = true;
      }
      
      if (!fine)
        return false;
    }
    
    return (move.mover() == other.move.mover() && move.stateNonDecision() == other.move.stateNonDecision());
  }
  
  @Override
  public String toString()
  {
    return "[Move = " + move + ", Hash = " + cachedHashCode + "]";
  }
}