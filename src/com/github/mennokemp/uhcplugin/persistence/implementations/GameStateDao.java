package com.github.mennokemp.uhcplugin.persistence.implementations;

import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.uhcplugin.domain.game.GameSetting;
import com.github.mennokemp.uhcplugin.domain.game.GameState;
import com.github.mennokemp.uhcplugin.persistence.abstractions.IGameStateDao;
import com.github.mennokemp.uhcplugin.persistence.abstractions.ISettingDao;

public class GameStateDao extends ScoreboardDao<GameState> implements IGameStateDao 
{
	private static String ObjectiveName = "uhc_gamestate";
	private static String ObjectiveDisplayName = "UHC";
	
	private final ISettingDao settingDao;
			
	public GameStateDao(ISettingDao settingDao, Scoreboard scoreboard)
	{
		super(scoreboard);
		
		this.settingDao = settingDao;
	}
		
	@Override
	public GamePhase getGamePhase()
	{
		return GamePhase.fromInt(this.getValue(GameState.Phase));
	}

	@Override
	public void setGamePhase(GamePhase gamePhase) 
	{
		this.setValue(GameState.Phase, gamePhase.getValue());
	}
	
	@Override
	public void resetGameState(GamePhase gamePhase)
	{
		setGamePhase(gamePhase);
		
		this.setValue(GameState.EternalDayCountdown, settingDao.getValue(GameSetting.EternalDayStart));
		this.setValue(GameState.FloodLevel, settingDao.getValue(GameSetting.FloodInitialLevel));
		this.setValue(GameState.FloodCountdown, settingDao.getValue(GameSetting.FloodStart));
		this.setValue(GameState.WorldBorderRadius, settingDao.getValue(GameSetting.WorldBorderInitialRadius));
		this.setValue(GameState.WorldBorderCountdown, settingDao.getValue(GameSetting.WorldBorderShrinkStart));
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
}
