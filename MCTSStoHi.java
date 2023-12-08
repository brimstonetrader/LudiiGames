package random;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import game.Game;
import game.equipment.component.Component;
import game.equipment.container.Container;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import main.collections.FastArrayList;
import other.AI;
import other.RankUtils;
import other.context.Context;
import other.context.InformationContext;
import other.move.Move;
import other.state.container.ContainerState;
import other.trial.Trial;
import utils.AIUtils;

public class MCTSStoHi extends AI
{
	protected int player = -1;

	public MCTSStoHi()
	{
		this.friendlyName = "MCTSStoHi";
	}
	
	//-------------------------------------------------------------------------

	@Override
	public Move selectAction
	(
		final Game game, 
		final Context context, 
		final double maxSeconds,
		final int maxIterations,
		final int maxDepth
	)
	{
	InformationContext ic = new InformationContext (context, player);
	Game ig = context.game();
	
	Random rndm = new Random();
	int numPlayers = game.players().count();
	int numIterations = 0;
	final long stopTime = (maxSeconds > 0.0) ? System.currentTimeMillis() + (long) (maxSeconds * 1000L) : Long.MAX_VALUE;
	
	FastArrayList<Move> legalMoves      = game.moves(context).moves();
	HashMap<Move, Double> scoresPerMove = new HashMap<Move, Double>();
	HashMap<Move,Integer> visitsPerMove = new HashMap<Move, Integer>();
	
	if (legalMoves.size() == 1) { 
		return legalMoves.get(0);
	}
	
	while (System.currentTimeMillis() < stopTime && !wantsInterrupt) {
		
		Move move = uct(ic, legalMoves, scoresPerMove, visitsPerMove, numIterations, numPlayers);
		// System.out.println(context.components().length - 1);
		Context dcEnd = new Context(generateDeterminizedContext(ig,player,context.components().length - 1,ic));
		dcEnd.game().apply(dcEnd,move);
		if (!dcEnd.trial().over())
		{
			dcEnd = new Context(dcEnd);
			while (!dcEnd.trial().over()) {
				dcEnd.game().apply(dcEnd, dcEnd.game().moves(dcEnd).get(rndm.nextInt(0,Math.max(1,dcEnd.game().moves(dcEnd).count()))));
			}
		}
		double[] ranks = dcEnd.trial().ranking(); 
		//      Capped at number of players; .5s exist. 1 is best, 2 is second best, ... 
		// RankUtils.utilities(contextEnd); .trial().ranking(); 
		double myrank = ranks[player];
//	    for (double d : ranks) { System.out.print(d); System.out.print(' '); } System.out.println(',');
		double score = Math.pow((1 + dcEnd.game().players().count() - myrank) / dcEnd.game().players().count(), 2);
		scoresPerMove.put(move, scoresPerMove.getOrDefault(move,0.0) + score);
		visitsPerMove.put(move, visitsPerMove.getOrDefault(move,0) + 1);
		numIterations++;
	}
		
		int max = 0;
		Move maxMove = null;
		for (Move m : scoresPerMove.keySet()) {
	//		for (Move x : visitsPerMove.keySet()) { System.out.println(visitsPerMove.get(x)); }
			if (max < visitsPerMove.get(m)) {
				max = visitsPerMove.get(m);
				maxMove = m;
			}
		}
		return maxMove;
		}
	
	@Override
	public void initAI(final Game game, final int playerID)
	{
		this.player = playerID;
	}
	
	// This method makes feasible assumptions for all hidden things.
	public static InformationContext generateDeterminizedContext(Game game, int player, int numCards, InformationContext iContext) {
	//	System.out.println(numCards);
		if (game.hiddenInformation() && player >= 1 && player <= iContext.game().players().count())
		{
			for (int cid = 0; cid < iContext.state().containerStates().length; cid++)
			{
				final ContainerState cs = iContext.state().containerStates()[cid];
				final Container container = iContext.containers()[cid];
				HashSet<Integer> informationSet = initInformationSet(numCards);
				if (iContext.game().isCellGame())
				{
					for (int cellId = iContext.sitesFrom()[cid]; cellId < iContext.sitesFrom()[cid]
							+ container.topology().cells().size(); cellId++)
					{
						if (!cs.isEmpty(cellId, SiteType.Vertex)) {
							if (!cs.isHiddenWhat(player, cellId, 0, SiteType.Vertex)) {
								informationSet.remove(cs.whatCell(cellId));
//								System.out.println(cs.whatCell(cellId));
							}
						}
					}
					for (int cellId = iContext.sitesFrom()[cid]; cellId < iContext.sitesFrom()[cid]
							+ container.topology().cells().size(); cellId++)
					{
						if (!cs.isEmpty(cellId, SiteType.Vertex)) {
							if (cs.isHiddenWhat(player, cellId, 0, SiteType.Vertex)) {
								Integer[] is = informationSet.toArray(new Integer[informationSet.size()]);
						        Random rndm = new Random();
						        if (is.length > 1) {
						        	int rndmWhat = rndm.nextInt(is.length - 1);
						        	cs.setSite(iContext.state(), cellId, cs.whoCell(cellId), is[rndmWhat], cs.countCell(cellId), cs.stateCell(cellId), cs.rotationCell(cellId), cs.valueCell(cellId), SiteType.Cell);			
						        	informationSet.remove(is[rndmWhat]); 
						        	is = informationSet.toArray(new Integer[informationSet.size()]);
//									System.out.println(cs.whatCell(cellId));
						        }
							} 
						}
					}
				}
			} 
		} return iContext;
	}

	private Move uct(
			 InformationContext ic, 
	         FastArrayList<Move> legalMoves, 
    	     HashMap<Move, Double> scoresPerMove, 
    	     HashMap<Move, Integer> visitsPerMove, 
    	     int numIterations, 
    	     int numPlayers
    	     ) {

		Move maxMove = legalMoves.get(0); double maxValue = -100000000;
		
		for (Move move : legalMoves) {
			
			double w_i = (scoresPerMove.getOrDefault(move, 0.0) / (double) numPlayers);
			// Number of wins per round, generalized for <2 player card games.
			
			double n_i = (double) visitsPerMove.getOrDefault(move, 1);
			// Number of times we've simulated this particular move. Can't be zero because it's a divisor.
			
			double c = Math.pow(2, 0.5);
			// for (Move o : scoresPerMove.keySet()) {	System.out.println(scoresPerMove.get(o)); } System.out.println("--------------------------------")
			
			double moveValue = (w_i / n_i) + c*(Math.sqrt(Math.log(numIterations) / n_i));
			
			if (moveValue > maxValue) {	maxMove = move;	maxValue = moveValue; }

		} return maxMove; 
	}

	
	// This set contains all cards which have an unknown location. 
		public static HashSet<Integer> initInformationSet(int numCards) {
			HashSet<Integer> informationSet = new HashSet<Integer>();		
			for (int i=1;i<1+numCards;i++) {
				informationSet.add(i);
			} return informationSet;
		}
	
	//-------------------------------------------------------------------------

}
