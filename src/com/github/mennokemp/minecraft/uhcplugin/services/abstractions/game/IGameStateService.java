package com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameEvent;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameState;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.IGameEventListener;

public interface IGameStateService
{
	void registerGameEventListener(IGameEventListener listener);
		
	int getGameState(GameState gameState);
	
	void setGameState(GameState gameState, int value);

	void onGameEvent(GameEvent gameEvent);
	
	GamePhase getGamePhase();
	
	Result startUhc();
	
	Result cancelUhc();
	
	Result stopUhc();
}
