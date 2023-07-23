package com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game;

import com.github.mennokemp.minecraft.pluginhelpers.Result;

public interface IGameEventListener 
{	
	Result onGameEvent(boolean canCancel);
	
	Result canGameStart();
	
	void onGameStart();
	
	void onGameOver();
}
