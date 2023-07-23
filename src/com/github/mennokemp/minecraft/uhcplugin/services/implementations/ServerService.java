package com.github.mennokemp.minecraft.uhcplugin.services.implementations;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.IServerService;

public class ServerService implements IServerService 
{
	@Override
	public Set<Player> getPlayers() 
	{
		return new HashSet<Player>(Bukkit.getOnlinePlayers());
	}

	@Override
	public Player getPlayer(String name) 
	{
		return Bukkit.getPlayer(name);
	}

	@Override
	public Set<Player> getPlayers(Set<String> playerNames) 
	{
		Set<Player> players = new HashSet<Player>();
		
		for(String playerName : playerNames)
			players.add(getPlayer(playerName));
		
		return players;
	}
}
