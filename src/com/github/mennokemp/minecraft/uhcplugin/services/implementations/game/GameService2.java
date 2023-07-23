package com.github.mennokemp.minecraft.uhcplugin.services.implementations.game;

import java.util.logging.Level;

import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameSetting;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameState;
import com.github.mennokemp.minecraft.uhcplugin.domain.players.PlayerEvent;
import com.github.mennokemp.minecraft.uhcplugin.domain.world.Realm;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.IGameStateDao;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.ISettingDao;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.IPlayerService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.IServerService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.IStatisticsService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game.IGameService2;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.world.IWorldService;

import net.md_5.bungee.api.ChatColor;

public class GameService2 implements IGameService2 
{
	private static int CountdownDuration = 10;
	private static int StatisticsInterval = 5;
	
	private final ISettingDao settingDao;
	private final IGameStateDao gameStateDao;
	
	private final IServerService serverService; 
	private final IStatisticsService statisticsService;
	private final IWorldService worldService;
	private final IPlayerService playerService;

	private final Plugin plugin;

	// private TaskRunner countdownRunner;

	// private TaskRunner updateRunner;
	private long uhcStartTime;
	private long statisticsTime = 0;
	
	private boolean floodStarted;
	private boolean worldBorderShrinkStarted;
	private boolean eternalDayStarted;
		
	public GameService2(
			ISettingDao settingDao, 
			IGameStateDao gameStateDao, 
			IServerService serverService, 
			IStatisticsService statisticsService,
			IWorldService worldService,
			IPlayerService playerService, 
			Plugin plugin) 
	{
		this.settingDao = settingDao;
		this.gameStateDao = gameStateDao;
		
		this.serverService = serverService;
		this.statisticsService = statisticsService;
		this.worldService = worldService;
		this.playerService = playerService;

		this.plugin = plugin;
	}
	
	@Override
	public Result startUhc()
	{
//		if(gameStateDao.getGamePhase() != GamePhase.Lobby)
//			return Result.failure("UHC can only be started from the lobby.");
//		
//		countdownRunner = new TaskRunner(plugin);
//		countdownRunner.setTask(() -> playerService.showCountdown(countdownRunner.getRemainingDuration()));
//		countdownRunner.setDuration(CountdownDuration);
//		countdownRunner.setCompletionTask(() ->
//		{
//			plugin.getLogger().log(Level.INFO, "Starting UHC...");
//			
//			gameStateDao.resetGameState(GamePhase.InProcess);
//			
//			worldService.setTime(0);
//			worldService.setWorldBorder();
//			uhcStartTime = worldService.getTime(true);
//			
//			SetRules();
//			
//			playerService.spawnPlayers();
//			
//			updateRunner = new TaskRunner(plugin);
//			updateRunner.setTask(() -> updateState());
//			updateRunner.runRepeatedly();
//			
//			plugin.getLogger().log(Level.INFO, "UHC has started.");
//		});
//		countdownRunner.runRepeatedly();
		
		return Result.success("Started countdown.");
	}
	
	@Override
	public Result cancelUhc()
	{
//		if(!countdownRunner.isRunning())
//			return Result.failure("Countdown is not running");
//			
//		countdownRunner.cancel();
//		playerService.BroadcastMessage(ChatColor.YELLOW + "UHC canceled!");
		return Result.success("Countdown canceled.");
	}
	
	@Override
	public Result stopUhc()
	{
//		if(gameStateDao.getGamePhase() != GamePhase.InProcess)
//			return Result.failure("UHC is not running.");
//		
//		gameStateDao.setGamePhase(GamePhase.PostGame);
//		worldService.stopShrink();
//		worldService.stopFlood();
//		updateRunner.cancel();
		return Result.success("UHC stopped.");
	}
	
	@Override
	public void onGameOver() 
	{
		stopUhc();
	}
	
	private void SetRules()
	{
		World overworld = worldService.getWorld(Realm.Overworld);
		
		overworld.setSpawnLocation(0, overworld.getHighestBlockYAt(0, 0) + 1, 0);
		overworld.setGameRule(GameRule.DO_INSOMNIA, false);
		overworld.setGameRule(GameRule.SPAWN_RADIUS, 0);
		overworld.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		
		overworld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, settingDao.getValue(GameSetting.Advancements) == 1);
		overworld.setGameRule(GameRule.FREEZE_DAMAGE, settingDao.getValue(GameSetting.Freezing) == 1);
		overworld.setGameRule(GameRule.NATURAL_REGENERATION, settingDao.getValue(GameSetting.NaturalRegeneration) == 1);
		overworld.setGameRule(GameRule.DO_WEATHER_CYCLE, settingDao.getValue(GameSetting.Weather) == 1);
		
		playerService.setPlayerHealthVisibility(settingDao.getValue(GameSetting.ShowHealth) == 1);
	}
	
	private void updateState()
	{
		World overworld = worldService.getWorld(Realm.Overworld);
		long timePassed = (worldService.getTime(true) - uhcStartTime) / 20;
		
		if(!floodStarted)
		{
			int countdown = (int)(settingDao.getValue(GameSetting.FloodStart) - timePassed / 60.0);
			
			if(countdown <= 0)
			{
				worldService.startFlood();
				floodStarted = true;
				gameStateDao.setValue(GameState.FloodCountdown, 0);
			}
			else
			{
				gameStateDao.setValue(GameState.FloodCountdown, countdown);
			}
		}
			
		if(worldBorderShrinkStarted)
		{
			int worldBorderRadius = (int)(overworld.getWorldBorder().getSize() - 1) / 2;
			gameStateDao.setValue(GameState.WorldBorderRadius, worldBorderRadius);			
		}
		else
		{
			//int countdown = (int)(settingDao.getValue(GameSetting.WorldBorderShrinkStart) - timePassed / 60.0);
			
			//if(countdown <= 0)
			{
//				worldService.startShrink();
				//worldBorderShrinkStarted = true;
				//gameStateDao.setValue(GameState.WorldBorderCountdown, 0);
			}
			//else
			{
				//gameStateDao.setValue(GameState.WorldBorderCountdown, countdown);
			}
		}
		
		if(!eternalDayStarted)
		{
			int countdown = (int)(settingDao.getValue(GameSetting.EternalDayStart) - timePassed / 60.0);
			
			if(countdown <= 0)
			{
				overworld.setTime(0);
				overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
				gameStateDao.setValue(GameState.EternalDayCountdown, 0);
				eternalDayStarted = true;
			}
			else
			{
				gameStateDao.setValue(GameState.EternalDayCountdown, countdown);
			}
		}
		
		if(timePassed > statisticsTime)
		{
			for(Player player : serverService.getPlayers())
			{
				if(player.getGameMode() == GameMode.SURVIVAL)
					statisticsService.UpdatePlayerStatistics(player, PlayerEvent.None);
			}
			
			statisticsTime = timePassed + StatisticsInterval;
		}
	}

	@Override
	public Result onGameEvent(boolean canCancel)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result canGameStart()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onGameStart()
	{
		// TODO Auto-generated method stub
		
	}
}