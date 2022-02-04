package com.github.mennokemp.uhcplugin.services.abstractions;

import java.util.Set;

import org.bukkit.entity.Player;

public interface IServerService 
{
	public Set<Player> getPlayers();
	
	public Set<Player> getPlayers(Set<String> playerNames);
	
	public Player getPlayer(String name);
}
