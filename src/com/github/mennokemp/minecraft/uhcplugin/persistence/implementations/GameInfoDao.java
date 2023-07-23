package com.github.mennokemp.minecraft.uhcplugin.persistence.implementations;

import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameState;

import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.minecraft.pluginhelpers.persistence.implementations.ScoreboardDao;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameInfo;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.IGameInfoDao;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.IGameStateDao;

public class GameInfoDao extends ScoreboardDao<GameInfo> implements IGameInfoDao
{
	private static String ObjectiveName = "uhc_gamestatus";	
	private static String DisplayName = "UHC";
	
	private final IGameStateDao gameStateDao;
	
	public GameInfoDao(IGameStateDao gameStateDao, Scoreboard scoreboard)
	{
		super(scoreboard, ObjectiveName, DisplayName);
		
		this.gameStateDao = gameStateDao;
	}
	
	@Override
	public void updateGameInfo() 
	{
		clear();

		updateFloodStatus();
		updateWorldBorderStatus();
		updateEternalDayStatus();
	}
	
	private void updateFloodStatus()
	{
		int countdown = gameStateDao.getValue(GameState.FloodCountdown);
		
		if(countdown > 0)
			this.setValue(GameInfo.FloodCountdown, countdown);
		else
			this.setValue(GameInfo.FloodLevel, gameStateDao.getValue(GameState.FloodLevel));
	}
	
	private void updateWorldBorderStatus()
	{
		//int countdown = gameStateDao.getValue(GameState.WorldBorderCountdown);
		
		//if(countdown > 0)
		//			this.setValue(GameStatus.ShrinkStart, countdown);
		//else
		//			this.setValue(GameStatus.WorldRadius, gameStateDao.getValue(GameState.WorldBorderRadius));
	}
	
	private void updateEternalDayStatus()
	{
		int countdown = gameStateDao.getValue(GameState.EternalDayCountdown);
		
		if(countdown > 0)
			this.setValue(GameInfo.EternalDayCountdown, countdown);
	}
}
