package com.github.mennokemp.uhcplugin.persistence.abstractions;

import com.github.mennokemp.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.uhcplugin.domain.game.GameState;

public interface IGameStateDao extends IScoreboardDao<GameState>
{
	public GamePhase getGamePhase();
	
	public void setGamePhase(GamePhase gamePhase);
	
	public void resetGameState(GamePhase gamePhase);
}
