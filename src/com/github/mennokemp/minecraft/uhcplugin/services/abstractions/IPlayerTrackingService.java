package com.github.mennokemp.minecraft.uhcplugin.services.abstractions;

import org.bukkit.entity.Player;

public interface IPlayerTrackingService 
{
	void SaveLocation(Player player);
	
	void SaveDeathLocation(Player player);
}
