package com.github.mennokemp.minecraft.uhcplugin.services.abstractions;

import org.bukkit.Location;

public interface ILobbyService
{
	Location getLobbyLocation();
	
	void createLobby();
}
