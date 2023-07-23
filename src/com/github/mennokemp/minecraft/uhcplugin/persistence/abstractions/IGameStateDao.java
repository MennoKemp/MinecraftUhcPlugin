package com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions;

import com.github.mennokemp.minecraft.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameState;

public interface IGameStateDao extends IScoreboardDao<GameState>
{
	GamePhase getGamePhase();
	
	void setGamePhase(GamePhase gamePhase);
}
