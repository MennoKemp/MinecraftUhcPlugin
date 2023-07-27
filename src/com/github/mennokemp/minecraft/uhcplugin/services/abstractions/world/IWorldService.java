package com.github.mennokemp.minecraft.uhcplugin.services.abstractions.world;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scoreboard.Team;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.uhcplugin.domain.world.Realm;

public interface IWorldService
{
	World getWorld(Realm realm);
			
	Location getLocation(Realm realm, Location location);
	
	Location getLocation(Realm realm, double x, double y, double z);
	
	Map<Team, Location> getSpawnLocations(List<Team> teams);
	
	Location getRespawnLocation(Location location);
	
	int getTime(boolean gameTime);
	
	void setTime(int time);


	
	
	
	
	Result setWorldBorder();
	
	Result startShrink();
	
	Result stopShrink();
	
	Result startFlood();
	
	Result stopFlood();
}
