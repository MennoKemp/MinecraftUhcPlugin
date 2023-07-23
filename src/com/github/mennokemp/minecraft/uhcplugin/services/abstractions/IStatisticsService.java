package com.github.mennokemp.minecraft.uhcplugin.services.abstractions;

import org.bukkit.entity.Player;

import com.github.mennokemp.minecraft.uhcplugin.domain.players.PlayerEvent;

public interface IStatisticsService 
{
	void UpdatePlayerStatistics(Player player);
	
	void UpdatePlayerStatistics(Player player, PlayerEvent event);
	
	void UpdatePlayerStatistics(Player player, PlayerEvent event, String eventData);
}
