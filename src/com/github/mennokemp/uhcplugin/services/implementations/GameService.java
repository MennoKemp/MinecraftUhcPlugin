package com.github.mennokemp.uhcplugin.services.implementations;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.github.mennokemp.uhcplugin.domain.game.GameSetting;
import com.github.mennokemp.uhcplugin.domain.game.GameState;
import com.github.mennokemp.uhcplugin.persistence.abstractions.IGameStateDao;
import com.github.mennokemp.uhcplugin.persistence.abstractions.ISettingDao;
import com.github.mennokemp.uhcplugin.services.abstractions.IGameService;
import com.github.mennokemp.uhcplugin.services.abstractions.IPlayerService;
import com.github.mennokemp.uhcplugin.services.abstractions.IPlayerTrackingService;
import com.github.mennokemp.uhcplugin.services.abstractions.IWorldService;

public class GameService implements IGameService 
{
	private static int Countdown = 10;
	private static int TrackInterval = 5;
	
	private final ISettingDao settingDao;
	private final IGameStateDao gameStateDao;
	
	private final IPlayerTrackingService playerTrackingService;
	private final IWorldService worldService;
	private final IPlayerService playerService;

	private final Plugin plugin;

	private boolean abortCountdown;

	private BukkitTask updateTask;
	private long uhcStartTime;
	private long trackTime = 0;
	
	private boolean floodStarted;
	private boolean worldBorderShrinkStarted;
	private boolean eternalDayStarted;
	
	public GameService(ISettingDao settingDao, IGameStateDao gameStateDao, IPlayerTrackingService playerTrackingService, IWorldService worldService,IPlayerService playerService, Plugin plugin) 
	{
		this.settingDao = settingDao;
		this.gameStateDao = gameStateDao;
		
		this.playerTrackingService = playerTrackingService;
		this.worldService = worldService;
		this.playerService = playerService;

		this.plugin = plugin;
	}
	
	@Override
	public void StartUhc()
	{
		abortCountdown = false;
		StartCountdown();
	}
	
	@Override
	public void CancelUhc()
	{
		abortCountdown = true;
	}
	
	@Override
	public void StopUhc()
	{
		gameStateDao.ResetGameState(2);
		worldService.SetWorldBorder();
		worldService.StopFlood();
		updateTask.cancel();
	}
	
	@Override
	public void OnGameOver() 
	{
		StopUhc();
	}

	private void StartCountdown()
	{
		new BukkitRunnable() 
		{
			int timer = Countdown;
		
			@Override
			public void run() 
			{
				if(abortCountdown)
				{
					cancel();
					return;					
				}
				
				if (timer == 0) 
			    {
					cancel();
					
					plugin.getLogger().log(Level.INFO, "Starting UHC...");
					
					Bukkit.getLogger().log(Level.INFO, "0");
					
					settingDao.SetDisplaySlot(null);
					Bukkit.getLogger().log(Level.INFO, "1");
					gameStateDao.ResetGameState(1);
					Bukkit.getLogger().log(Level.INFO, "2");
					
					World overworld = worldService.GetOverworld(); 
					overworld.setTime(0);
					worldService.SetWorldBorder();
					
					Bukkit.getLogger().log(Level.INFO, "3");
					
					SetRules();
					Bukkit.getLogger().log(Level.INFO, "4");
					
					uhcStartTime = overworld.getGameTime();
					
					Bukkit.getLogger().log(Level.INFO, "5");
					
					playerService.SpreadPlayers();
					
					Bukkit.getLogger().log(Level.INFO, "6");
					
					updateTask = new BukkitRunnable()
					{
						@Override
						public void run()
						{						
							UpdateState();
						}
					}.runTaskTimer(plugin, 0l, 20l);
					
					plugin.getLogger().log(Level.INFO, "UHC has started.");
					
				    return;
			    }

				playerService.NotifyCountdown(timer);
			    timer--;
			}
		}.runTaskTimer(plugin, 0l, 20l);
	}
	
	private void SetRules()
	{
		World overworld = worldService.GetOverworld();
		
		overworld.setSpawnLocation(0, overworld.getHighestBlockYAt(0, 0) + 1, 0);
		overworld.setGameRule(GameRule.DO_INSOMNIA, false);
		overworld.setGameRule(GameRule.SPAWN_RADIUS, 0);
		overworld.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		
		overworld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, settingDao.GetSetting(GameSetting.Advancements) == 1);
		overworld.setGameRule(GameRule.FREEZE_DAMAGE, settingDao.GetSetting(GameSetting.Freezing) == 1);
		overworld.setGameRule(GameRule.NATURAL_REGENERATION, settingDao.GetSetting(GameSetting.NaturalRegeneration) == 1);
		overworld.setGameRule(GameRule.DO_WEATHER_CYCLE, settingDao.GetSetting(GameSetting.Weather) == 1);
		
		playerService.SetPlayerHealthVisibility(settingDao.GetSetting(GameSetting.ShowHealth) == 1);
	}
	
	private void UpdateState()
	{
		World overworld = worldService.GetOverworld();
		
		long timePassed = (overworld.getGameTime() - uhcStartTime) / 20;
		
		if(!floodStarted)
		{
			int countdown = (int)(settingDao.GetSetting(GameSetting.FloodStart) - timePassed / 60.0);
			
			if(countdown <= 0)
			{
				worldService.StartFlood();
				floodStarted = true;
				gameStateDao.SetGameState(GameState.FloodStart, 0);
			}
			else
			{
				gameStateDao.SetGameState(GameState.FloodStart, countdown);
			}
		}
			
		if(worldBorderShrinkStarted)
		{
			int worldBorderRadius = (int)(overworld.getWorldBorder().getSize() - 1) / 2;
			gameStateDao.SetGameState(GameState.WorldBorderRadius, worldBorderRadius);			
		}
		else
		{
			int countdown = (int)(settingDao.GetSetting(GameSetting.WorldBorderShrinkStart) - timePassed / 60.0);
			
			if(countdown <= 0)
			{
				worldService.ShrinkWorldBorder();
				worldBorderShrinkStarted = true;
				gameStateDao.SetGameState(GameState.WorldBorderStart, 0);
			}
			else
			{
				gameStateDao.SetGameState(GameState.WorldBorderStart, countdown);
			}
		}
		
		if(!eternalDayStarted)
		{
			int countdown = (int)(settingDao.GetSetting(GameSetting.EternalDayStart) - timePassed / 60.0);
			
			if(countdown <= 0)
			{
				overworld.setTime(0);
				overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
				gameStateDao.SetGameState(GameState.EternalDayStart, 0);
				eternalDayStarted = true;
			}
			else
			{
				gameStateDao.SetGameState(GameState.EternalDayStart, countdown);
			}
		}
		
		if(timePassed > trackTime)
		{
			for(Player player : worldService.GetAllPlayers())
			{
				if(playerService.IsPlayerAlive(player))
					playerTrackingService.SaveLocation(player);
			}
			
			trackTime = timePassed + TrackInterval;
		}
	}
}