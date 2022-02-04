package com.github.mennokemp.uhcplugin.persistence.implementations;

import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.uhcplugin.domain.game.GameState;
import com.github.mennokemp.uhcplugin.domain.game.GameStatus;
import com.github.mennokemp.uhcplugin.persistence.abstractions.IGameStateDao;
import com.github.mennokemp.uhcplugin.persistence.abstractions.IGameStatusDao;

public class GameStatusDao extends ScoreboardDao<GameStatus> implements IGameStatusDao 
{
	private static String ObjectiveName = "uhc_gamestatus";	
	private static String ObjectiveDisplayName = "UHC";
	
	private final IGameStateDao gameStateDao;
	
	public GameStatusDao(IGameStateDao gameStateDao, Scoreboard scoreboard) 
	{
		super(scoreboard);
		
		this.gameStateDao = gameStateDao;
	}
	
	@Override
	public void updateStatus() 
	{
		this.clear();

		updateFloodStatus();
		updateWorldBorderStatus();
		updateEternalDayStatus();
	}
	
	@Override
	protected String getObjectiveName() 
	{
		return ObjectiveName;
	}

	@Override
	protected String getObjectiveDisplayName() 
	{
		return ObjectiveDisplayName;
	}

	private void updateEternalDayStatus()
	{
		int countdown = gameStateDao.getValue(GameState.EternalDayCountdown);
		
		if(countdown > 0)
			this.setValue(GameStatus.EternalDay, countdown);
	}
	
	private void updateFloodStatus()
	{
		int countdown = gameStateDao.getValue(GameState.FloodCountdown);
		
		if(countdown > 0)
			this.setValue(GameStatus.FloodStart, countdown);
		else
			this.setValue(GameStatus.FloodLevel, gameStateDao.getValue(GameState.FloodLevel));
	}
	
	private void updateWorldBorderStatus()
	{
		int countdown = gameStateDao.getValue(GameState.WorldBorderCountdown);
		
		if(countdown > 0)
			this.setValue(GameStatus.ShrinkStart, countdown);
		else
			this.setValue(GameStatus.WorldRadius, gameStateDao.getValue(GameState.WorldBorderRadius));
	}
}
