package com.github.mennokemp.minecraft.uhcplugin.services.abstractions;

import java.util.Set;

import org.bukkit.entity.Player;

public interface IServerService 
{
	Set<Player> getPlayers();
	
	Set<Player> getPlayers(Set<String> playerNames);
	
	Player getPlayer(String name);
}
