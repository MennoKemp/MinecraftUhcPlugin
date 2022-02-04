package com.github.mennokemp.uhcplugin.services.abstractions;

import org.bukkit.Location;

public interface ILobbyService extends ISetupService
{
	public Location getJoinLocation();
	
	public boolean doesLobbyExist();
}
