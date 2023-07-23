package com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game;

import com.github.mennokemp.minecraft.pluginhelpers.Result;

public interface IGameService2 extends IGameEventListener
{
	Result startUhc();
	
	Result cancelUhc();
		
	Result stopUhc();
}