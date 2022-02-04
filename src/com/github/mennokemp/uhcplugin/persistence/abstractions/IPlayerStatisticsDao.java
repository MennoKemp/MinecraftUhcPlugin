package com.github.mennokemp.uhcplugin.persistence.abstractions;

import com.github.mennokemp.uhcplugin.domain.players.PlayerStatistics;

public interface IPlayerStatisticsDao 
{
	public void saveStatistics(PlayerStatistics playerStatistics);
}