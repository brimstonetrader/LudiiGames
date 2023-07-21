package mcts;

import game.Game;
import other.AI;
import other.context.Context;
import other.context.InformationContext;
import other.move.Move;

import search.mcts.MCTS;

//  Thank you to Victor Putrich, author, also known as Github user schererl.

public class MCTSStoHI extends AI
{
	
	protected int player = -1;
	private MCTS UCT_Ludii;
	public MCTSStoHI()
	{
		this.friendlyName = "MCTSStoHI";
	}
	
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
		Move selectedMove=UCT_Ludii.selectAction(game, new InformationContext(context,player), maxSeconds, maxIterations, maxDepth);
		return selectedMove;
	}
	
	@Override
	public void initAI(final Game game, final int playerID)
	{
		this.player = playerID;
		UCT_Ludii = MCTS.createUCT();
		UCT_Ludii.initAI(game, playerID);
	}
	
	@Override
	public boolean supportsGame(final Game game)
	{
		if (!game.isAlternatingMoveGame())
			return false;
		
		return true;
	}
}

