package com.github.mennokemp.minecraft.uhcplugin.services.abstractions.world;

import com.github.mennokemp.minecraft.pluginhelpers.Result;

public interface IWorldBorderService 
{
	Result startShrink();
	
	Result stopShrink();
	
	void setShrinkMultiplier();
	
	double getShrinkMultiplier();
}