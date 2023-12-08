package DataStructures;

public class ActionStatistics
{
 /** Visit count (not int because we want to be able to decay) */
 public double visitCount = 0.0;
 
 /** Accumulated score */
 public double accumulatedScore = 0.0;
 
 @Override
 public String toString()
 {
   return "[visits = " + visitCount + ", accum. score = " + accumulatedScore + "]";
 }
}