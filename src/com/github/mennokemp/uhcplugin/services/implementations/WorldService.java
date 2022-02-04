package com.github.mennokemp.uhcplugin.services.implementations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import com.github.mennokemp.uhcplugin.domain.game.GameSetting;
import com.github.mennokemp.uhcplugin.domain.game.GameState;
import com.github.mennokemp.uhcplugin.helpers.Result;
import com.github.mennokemp.uhcplugin.persistence.abstractions.IGameStateDao;
import com.github.mennokemp.uhcplugin.persistence.abstractions.ISettingDao;
import com.github.mennokemp.uhcplugin.services.abstractions.IWorldService;

import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;

public class WorldService implements IWorldService
{
	private static String OverworldName = "world";
	private static String LobbyName = "world_lobby";
	private static String NetherName = "world_nether";
	private static String EndName = "world_the_end";
	
	private static Random Random = new Random();
	
	private static double MaxRangeRadiusRatio = Math.pow(2, 0.5);
	private static double MinimumBorderSpawnDistance = 100; 
	private static double SpawnSearchInterval = 100;
	private static int SpawnAttempts = 100;
	
	private static List<Biome> ChallengingBiomes = Arrays.asList
	(
		Biome.BEACH,
		Biome.DESERT,
		Biome.FROZEN_PEAKS,
		Biome.FROZEN_RIVER,
		Biome.GROVE,
		Biome.ICE_SPIKES,
		Biome.JAGGED_PEAKS,
		Biome.SNOWY_BEACH,
		Biome.SNOWY_PLAINS,
		Biome.SNOWY_SLOPES,
		Biome.TAIGA,
		Biome.STONY_PEAKS,
		Biome.STONY_SHORE
	);
	
	private static List<Biome> OceanBiomes = Arrays.asList
	(
		Biome.COLD_OCEAN,
		Biome.DEEP_COLD_OCEAN,
		Biome.DEEP_FROZEN_OCEAN,
		Biome.DEEP_LUKEWARM_OCEAN,
		Biome.DEEP_OCEAN,
		Biome.FROZEN_OCEAN,
		Biome.LUKEWARM_OCEAN,
		Biome.MUSHROOM_FIELDS,
		Biome.OCEAN,
		Biome.WARM_OCEAN
	);
	
	private final ISettingDao settingDao;
	private final IGameStateDao gameStateDao;
	
	private final Plugin plugin;
	
	private final World overworld;
	private final World nether;
	private final World end;
	
	private World lobby;
	
	private BukkitTask floodTask;
	
	public WorldService(ISettingDao settingDao, IGameStateDao gameStateDao, Plugin plugin)
	{		
		this.settingDao = settingDao;
		this.gameStateDao = gameStateDao;
		
		this.plugin = plugin;
		
		overworld = Bukkit.getWorld(OverworldName);
		nether = Bukkit.getWorld(NetherName);
		end = Bukkit.getWorld(EndName);
		lobby = Bukkit.getWorld(LobbyName);
	}
		
	@Override
	public void setup(boolean firstTime)
	{
		if(firstTime)
			createLobbyWorld();
	}
	
	@Override
	public World getOverworld() 
	{
		return overworld;
	}

	@Override
	public World getNether()
	{
		return nether;
	}
	
	@Override
	public World getEnd()
	{
		return end;
	}
	
	@Override
	public World getLobby() 
	{
		return lobby;
	}
		
	@Override
	public Location getLocation(boolean lobbyLocation, Location location)
	{
		if(lobbyLocation)
			location.setWorld(lobby);
		else
			location.setWorld(overworld);
		
		return location;
	}
	
	@Override
	public Map<Team, Location> getSpawnLocations(List<Team> teams)
	{
		Collections.shuffle(teams, Random);
		
		double startAngle = Random.nextDouble();
		double maxRange = settingDao.getValue(GameSetting.WorldBorderInitialRadius) - MinimumBorderSpawnDistance;
		
		double teamCount = teams.size();
		
		Map<Team, Location> spawnLocations = new HashMap<Team, Location>();
		
		for (int s = 0; s < SpawnAttempts; s++) 
		{
			for (int g = 0; g < teamCount; g++)
			{
				double angle = 2 * Math.PI * (startAngle + g / teamCount);
				
				Location spawnLocation = FindValidSpawnLocation(angle, maxRange);
				
				if(spawnLocation == null)
					break;
				
				spawnLocations.put(teams.get(g), spawnLocation);
			}
			
			if(spawnLocations.size() == teams.size())
				return spawnLocations;
			
			startAngle += 1.0 / SpawnAttempts;
		}
		
		return null;
	}
	
	@Override
	public Location GetRespawnLocation(Location location)
	{
		int highestY = overworld.getHighestBlockYAt(location);
		
		Location reviveLocation = null;
		
		int maxRange = gameStateDao.getValue(GameState.WorldBorderRadius);
		
		for(int y = location.getBlockY(); y <= highestY + 1 ; y++)
		{
			reviveLocation = new Location(overworld, location.getBlockX(), y, location.getBlockZ());
			
			if(IsSpawnLocationValid(reviveLocation, maxRange))
				return reviveLocation;			
		}
		
		return null;
	}
	
	@Override
	public Result setWorldBorder() 
	{	
		WorldBorder worldBorder = overworld.getWorldBorder(); 
		
		worldBorder.setCenter(0, 0);
		int radius = settingDao.getValue(GameSetting.WorldBorderInitialRadius);
		double diameter = radius * 2 + 1;
		worldBorder.setSize(diameter);
		return Result.success("Set the world border to a radius of " + radius + ".");
	}
	
	@Override
	public Result shrinkWorldBorder() 
	{	
		WorldBorder worldBorder = overworld.getWorldBorder(); 
		
		int radius = settingDao.getValue(GameSetting.WorldBorderFinalRadius);
		double diameter = radius * 2 + 1;
		int time = 60 * (settingDao.getValue(GameSetting.WorldBorderShrinkEnd) - settingDao.getValue(GameSetting.WorldBorderShrinkStart));
		worldBorder.setSize(diameter, time);
		return Result.success("Shrinking the world border to a radius of " + radius + " over " + time + " minutes.");
	}
	
	@Override
	public Result startFlood()
	{
		long interval = settingDao.getValue(GameSetting.FloodInterval);		
		int radius = settingDao.getValue(GameSetting.WorldBorderFinalRadius);
		int height = settingDao.getValue(GameSetting.FloodHeight);
		Material floodType = settingDao.getValue(GameSetting.FloodLava) == 1
				? Material.LAVA
				: Material.WATER;
		
		floodTask = new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				int floodLevel = gameStateDao.getValue(GameState.FloodLevel) + 1;
				gameStateDao.setValue(GameState.FloodLevel, floodLevel);
				
				for(int z = -radius; z <= radius; z++)
				{
					for(int x = -radius; x <= radius; x++)
					{
						Block block = overworld.getBlockAt(x, floodLevel, z);
						
						if(block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR)
							block.setType(floodType);
					}	
				}
					
				if(floodLevel >= height)
					floodTask.cancel();
			}
		}.runTaskTimer(plugin, 0l, interval * 20);
		
		return Result.success("Started the flood");
	}
	
	@Override
	public Result stopFlood()
	{
		floodTask.cancel();
		return Result.success("Stopped the flood.");
	}
	
	private void createLobbyWorld()
	{
		WorldCreator worldCreator = new WorldCreator(LobbyName);
		worldCreator.environment(Environment.NORMAL);
		worldCreator.type(WorldType.NORMAL);
		lobby = worldCreator.createWorld();
	}
	
	private Location FindValidSpawnLocation(double angle, double maxRange)
    {
    	double radius = maxRange;
    	Location spawnLocation = GetLocation(angle, radius);

    	if(IsSpawnLocationValid(spawnLocation, maxRange))
    		return spawnLocation;
    	
    	for (radius = MaxRangeRadiusRatio * maxRange; radius > maxRange / 2; radius -= SpawnSearchInterval) 
    	{
        	spawnLocation = GetLocation(angle, radius);
        	
        	if(IsSpawnLocationValid(spawnLocation, maxRange))
        		return spawnLocation;
		}
   	
    	return null;
    }
	
    private Location GetLocation(double angle, double radius)
    {
    	int x = (int)(radius * Math.sin(angle));
		int z = (int)(radius * Math.cos(angle));
		    	
    	return overworld.getHighestBlockAt(x, z).getLocation().add(0, 1, 0);
    }
	
    private boolean IsSpawnLocationValid(Location spawnLocation, double maxRange)
    {   	
		if(Math.abs(spawnLocation.getBlockX()) >= maxRange || Math.abs(spawnLocation.getBlockZ()) >= maxRange)
			return false;
    	
		if(spawnLocation.getBlockY() < 64 || spawnLocation.getBlockY() > 100)
			return false;
							
		if(!spawnLocation.subtract(0, 1, 0).getBlock().getType().isOccluding())
			return false;
		
		if(!spawnLocation.getBlock().getType().isAir())
			return false;
		
		if(!spawnLocation.add(0, 1, 0).getBlock().getType().isAir())
			return false;
		
		Biome biome = overworld.getBiome(spawnLocation);
		
		if(OceanBiomes.contains(biome))
			return false;
		
		if(settingDao.getValue(GameSetting.ChallengingBiomes) != 1 && ChallengingBiomes.contains(biome))
			return false;
		
		return true;
    }
}