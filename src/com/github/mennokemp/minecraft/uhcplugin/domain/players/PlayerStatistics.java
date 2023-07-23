package com.github.mennokemp.minecraft.uhcplugin.domain.players;

import java.util.Date;

import org.bukkit.entity.Player;

public class PlayerStatistics 
{
	public final Date timestamp = new Date();
	public final Player player;
	public final PlayerEvent playerEvent;
	public final String eventData;
	
	public PlayerStatistics(Player player)
	{
		this.player = player;
		this.playerEvent = PlayerEvent.None;
		this.eventData = "";
	}
	
	public PlayerStatistics(Player player, PlayerEvent playerEvent) 
	{
		this.player = player;
		this.playerEvent = playerEvent;
		this.eventData = "";
	}
	
	public PlayerStatistics(Player player, PlayerEvent playerEvent, String eventData) 
	{
		this.player = player;
		this.playerEvent = playerEvent;
		this.eventData = eventData;
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
	
	public String getEventData()
	{
		return eventData;
	}
}