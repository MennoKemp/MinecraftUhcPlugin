package com.github.mennokemp.minecraft.uhcplugin.persistence.implementations;

import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.minecraft.pluginhelpers.persistence.implementations.ScoreboardDao;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameState;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.IGameStateDao;

public class GameStateDao extends ScoreboardDao<GameState> implements IGameStateDao 
{
	private static String ObjectiveName = "uhc_gamestate";
	private static String DisplayName = "Game";
				
	public GameStateDao(Scoreboard scoreboard)
	{
		super(scoreboard, ObjectiveName, DisplayName);
	}
		
	@Override
	public GamePhase getGamePhase()
	{
		return GamePhase.fromInt(getValue(GameState.Phase));
	}

	@Override
	public void setGamePhase(GamePhase gamePhase) 
	{
		this.setValue(GameState.Phase, gamePhase.getValue());
	}
}
