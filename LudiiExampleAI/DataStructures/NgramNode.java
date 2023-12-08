package DataStructures;
import java.util.Map;
import java.util.HashMap;

public class NgramNode {
  /** Visit count (not int because we want to be able to decay) */
  public double visitCount = 0.0;
 
  /** Accumulated score */
  public double accumulatedScore = 0.0;

  /**  */
  public final Map<MoveKey, NgramNode> ngramStats;

  public NgramNode() {
    ngramStats = new HashMap<MoveKey, NgramNode>();
  }
 
  @Override
  public String toString()
  {
    return "[visits = " + visitCount + ", accum. score = " + accumulatedScore + "]";
  }
}
