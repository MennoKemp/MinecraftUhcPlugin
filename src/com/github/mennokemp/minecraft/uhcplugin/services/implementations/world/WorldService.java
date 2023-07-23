package com.github.mennokemp.minecraft.uhcplugin.services.implementations.world;

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

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameSetting;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameState;
import com.github.mennokemp.minecraft.uhcplugin.domain.world.Realm;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.IGameStateDao;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.ISettingDao;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.world.IWorldService;

import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;

public class WorldService implements IWorldService
{
	private static Random Random = new Random();

	private static double MaxRangeRadiusRatio = Math.pow(2, 0.5);
	private static double MinimumBorderSpawnDistance = 100;
	private static double SpawnSearchInterval = 100;
	private static int SpawnAttempts = 100;

	private static final String LobbyName = "lobby";
	private static final String OverworldName = "world";
	private static final String NetherName = "world_nether";
	private static final String EndName = "world_the_end";
	
	private static List<Biome> ChallengingBiomes = Arrays.asList(
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
			Biome.STONY_SHORE);

	private static List<Biome> OceanBiomes = Arrays.asList(
			Biome.COLD_OCEAN, 
			Biome.DEEP_COLD_OCEAN,
			Biome.DEEP_FROZEN_OCEAN, 
			Biome.DEEP_LUKEWARM_OCEAN, 
			Biome.DEEP_OCEAN, 
			Biome.FROZEN_OCEAN,
			Biome.LUKEWARM_OCEAN, 
			Biome.MUSHROOM_FIELDS, 
			Biome.OCEAN, 
			Biome.WARM_OCEAN);

	private final ISettingDao settingDao;
	private final IGameStateDao gameStateDao;

	private final Plugin plugin;

	private double shrinkBaseSpeed;
	private int shrinkIncrement;

	private BukkitTask floodTask;

	public WorldService(ISettingDao settingDao, IGameStateDao gameStateDao, Plugin plugin)
	{
		this.settingDao = settingDao;
		this.gameStateDao = gameStateDao;

		this.plugin = plugin;

		createLobbyWorld();
	}

	@Override
	public World getWorld(Realm realm)
	{
		return Bukkit.getWorld(switch(realm)
		{
			case Overworld -> OverworldName;
			case Lobby -> LobbyName;
			case Nether -> NetherName;
			case End -> EndName;
		});
	}

	@Override
	public Location getLocation(Realm worldType, Location location)
	{		
		return new Location(getWorld(worldType), location.getX(), location.getY(), location.getZ());
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

				if (spawnLocation == null)
					break;

				spawnLocations.put(teams.get(g), spawnLocation);
			}

			if (spawnLocations.size() == teams.size())
				return spawnLocations;

			startAngle += 1.0 / SpawnAttempts;
		}

		return null;
	}

	@Override
	public Location getRespawnLocation(Location location)
	{
		World overworld = getWorld(Realm.Overworld);
		int highestY = overworld.getHighestBlockYAt(location);

		Location reviveLocation = null;

		int maxRange = gameStateDao.getValue(GameState.WorldBorderRadius);

		for (int y = location.getBlockY(); y <= highestY + 1; y++)
		{
			reviveLocation = new Location(overworld, location.getBlockX(), y, location.getBlockZ());

			if (IsSpawnLocationValid(reviveLocation, maxRange))
				return reviveLocation;
		}

		return null;
	}

	@Override
	public int getTime(boolean gameTime)
	{
		World overworld = getWorld(Realm.Overworld);
		return (int) (gameTime ? overworld.getGameTime() : overworld.getTime());
	}

	@Override
	public void setTime(int time)
	{
		World overworld = getWorld(Realm.Overworld);
		overworld.setTime(time);
	}

	@Override
	public Result setWorldBorder()
	{
		WorldBorder worldBorder = getWorld(Realm.Overworld).getWorldBorder();

		worldBorder.setCenter(0, 0);
		int radius = settingDao.getValue(GameSetting.WorldBorderInitialRadius);
		double diameter = radius * 2 + 1;

		double shrinkAmount = radius - settingDao.getValue(GameSetting.WorldBorderInitialRadius);
		// double shrinkTime = settingDao.getValue(GameSetting.WorldBorderShrinkEnd)
		//		- settingDao.getValue(GameSetting.WorldBorderShrinkStart);
		// double increments = settingDao.getValue(GameSetting.WorldBorderShrinkIncrements);

		shrinkIncrement = 0;
		//shrinkBaseSpeed = shrinkAmount / (shrinkTime * increments);

		worldBorder.setSize(diameter);

		return Result.success("Set the world border to a radius of " + radius + ".");
	}

	@Override
	public Result startShrink()
	{
		WorldBorder worldBorder = getWorld(Realm.Overworld).getWorldBorder();

		int radius = settingDao.getValue(GameSetting.WorldBorderFinalRadius);
		double diameter = radius * 2 + 1;
		// int time = 60 * (settingDao.getValue(GameSetting.WorldBorderShrinkEnd)
		//		- settingDao.getValue(GameSetting.WorldBorderShrinkStart));
		// worldBorder.setSize(diameter, time);
		//return Result.success("Shrinking the world border to a radius of " + radius + " over " + time + " minutes.");
		return Result.failure("Not implemented");
	}

	@Override
	public Result startFlood()
	{
		long interval = settingDao.getValue(GameSetting.FloodInterval);
		int radius = settingDao.getValue(GameSetting.WorldBorderFinalRadius);
		int height = settingDao.getValue(GameSetting.FloodHeight);
		Material floodType = settingDao.getValue(GameSetting.FloodLava) == 1 ? Material.LAVA : Material.WATER;

		floodTask = new BukkitRunnable()
		{
			@Override
			public void run()
			{
				World overworld = getWorld(Realm.Overworld);
				
				int floodLevel = gameStateDao.getValue(GameState.FloodLevel) + 1;
				gameStateDao.setValue(GameState.FloodLevel, floodLevel);

				for (int z = -radius; z <= radius; z++)
				{
					for (int x = -radius; x <= radius; x++)
					{
						Block block = overworld.getBlockAt(x, floodLevel, z);

						if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR)
							block.setType(floodType);
					}
				}

				if (floodLevel >= height)
					cancel();
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
		WorldCreator worldCreator = new WorldCreator(Realm.Lobby.name());
		worldCreator.environment(Environment.NORMAL);
		worldCreator.type(WorldType.NORMAL);
		worldCreator.createWorld();
	}

	private Location FindValidSpawnLocation(double angle, double maxRange)
	{
		double radius = maxRange;
		Location spawnLocation = GetLocation(angle, radius);

		if (IsSpawnLocationValid(spawnLocation, maxRange))
			return spawnLocation;

		for (radius = MaxRangeRadiusRatio * maxRange; radius > maxRange / 2; radius -= SpawnSearchInterval)
		{
			spawnLocation = GetLocation(angle, radius);

			if (IsSpawnLocationValid(spawnLocation, maxRange))
				return spawnLocation;
		}

		return null;
	}

	private Location GetLocation(double angle, double radius)
	{
		int x = (int) (radius * Math.sin(angle));
		int z = (int) (radius * Math.cos(angle));

		return getWorld(Realm.Overworld).getHighestBlockAt(x, z).getLocation().add(0, 1, 0);
	}

	private boolean IsSpawnLocationValid(Location spawnLocation, double maxRange)
	{
		if (Math.abs(spawnLocation.getBlockX()) >= maxRange || Math.abs(spawnLocation.getBlockZ()) >= maxRange)
			return false;

		if (spawnLocation.getBlockY() < 64 || spawnLocation.getBlockY() > 100)
			return false;

		if (!spawnLocation.subtract(0, 1, 0).getBlock().getType().isOccluding())
			return false;

		if (!spawnLocation.getBlock().getType().isAir())
			return false;

		if (!spawnLocation.add(0, 1, 0).getBlock().getType().isAir())
			return false;

		Biome biome = getWorld(Realm.Overworld).getBiome(spawnLocation);

		if (OceanBiomes.contains(biome))
			return false;

		if (settingDao.getValue(GameSetting.ChallengingBiomes) != 1 && ChallengingBiomes.contains(biome))
			return false;

		return true;
	}

	private void setShrink()
	{
		//int increments = settingDao.getValue(GameSetting.WorldBorderShrinkIncrements);
		//double incrementParts = increments * (increments + 1) / 2;

		//double incrementMultiplier = (shrinkIncrement + 1) / incrementParts;

		//double shrinkSpeed = shrinkBaseSpeed * incrementMultiplier;

		//worldBorder.setSize(diameter, time);
	}

	@Override
	public Result stopShrink()
	{
		// TODO Auto-generated method stub
		return null;
	}
}