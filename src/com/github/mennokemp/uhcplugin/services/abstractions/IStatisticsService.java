package com.github.mennokemp.uhcplugin.services.abstractions;

import org.bukkit.entity.Player;

import com.github.mennokemp.uhcplugin.domain.players.PlayerEvent;

public interface IStatisticsService 
{
	public void UpdatePlayerStatistics(Player player);
	
	public void UpdatePlayerStatistics(Player player, PlayerEvent event);
}
