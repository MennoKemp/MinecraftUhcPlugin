package com.github.mennokemp.uhcplugin.services.abstractions;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scoreboard.Team;

import com.github.mennokemp.uhcplugin.helpers.Result;

public interface IWorldService extends ISetupService
{
	public World getOverworld();
	
	public World getLobby();
	
	public World getNether();
	
	public World getEnd();
		
	public Location getLocation(boolean lobbyLocation, Location location);
	
	public Map<Team, Location> getSpawnLocations(List<Team> teams);
	
	public Location GetRespawnLocation(Location location);
	
	public Result setWorldBorder();
	
	public Result shrinkWorldBorder();
	
	public Result startFlood();
	
	public Result stopFlood();
}
