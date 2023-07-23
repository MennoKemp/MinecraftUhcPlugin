package com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions;

import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameEvent;

public interface IGameEventListener 
{
	void onGameEvent(GameEvent gameEvent);
}
