package com.github.mennokemp.uhcplugin.services.abstractions;

import org.bukkit.entity.Player;

public interface IPlayerTrackingService 
{
	public void SaveLocation(Player player);
	
	public void SaveDeathLocation(Player player);
}
