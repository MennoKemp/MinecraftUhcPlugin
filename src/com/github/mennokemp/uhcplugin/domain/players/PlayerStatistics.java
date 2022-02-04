package com.github.mennokemp.uhcplugin.domain.players;

import java.util.Date;

import org.bukkit.entity.Player;

public class PlayerStatistics 
{
	public final Date timestamp;
	public final Player player;
	public final PlayerEvent playerEvent;
	
	public PlayerStatistics(Player player)
	{
		timestamp = new Date();
		this.player = player;
		playerEvent = PlayerEvent.None;
	}
	
	public PlayerStatistics(Player player, PlayerEvent playerEvent) 
	{
		timestamp = new Date();
		this.player = player;
		this.playerEvent = playerEvent;
	}
	
	public Date getTimestamp()
	{
		return timestamp;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public PlayerEvent getPlayerEvent()
	{
		return playerEvent;
	}
}