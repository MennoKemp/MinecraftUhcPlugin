package com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions;

import com.github.mennokemp.minecraft.uhcplugin.domain.players.PlayerStatistics;

public interface IPlayerStatisticsDao 
{
	public void saveStatistics(PlayerStatistics playerStatistics);
}