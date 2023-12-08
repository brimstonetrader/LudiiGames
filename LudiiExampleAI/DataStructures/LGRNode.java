package DataStructures;
import other.move.Move;
import java.util.Map;
import java.util.HashMap;

public class LGRNode
{
 /** Visit count (not int because we want to be able to decay) */
  public Move reply = null;

  public final Map<MoveKey, LGRNode> lastGoodReplies;
 

  public LGRNode() {
    lastGoodReplies = new HashMap<MoveKey, LGRNode>();
  }
 
  @Override
  public String toString()
  {
    return " Last Good Reply :" + reply + "]";
  }
}