package com.github.mennokemp.minecraft.uhcplugin.services.implementations.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameEvent;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameState;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.IGameStateDao;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.IGameEventListener;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game.IGameStateService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.world.IWorldService;

public class GameStateService implements IGameStateService 
{
	private static int CountdownDuration = 10;
	
	private final IGameStateDao gameStateDao;
	
	private final IWorldService worldService;
	
	private final Plugin plugin;
	
	public final Collection<IGameEventListener> gameEventListeners = new ArrayList<IGameEventListener>();
	
//	private TaskRunner countdownRunner;
//	private TaskRunner gameStateRunner;
//	
	private int startTime;
	
	public GameStateService(IGameStateDao gameStateDao, IWorldService worldService, Plugin plugin) 
	{
		this.gameStateDao = gameStateDao;
		
		this.worldService = worldService;
		
		this.plugin = plugin; 
	}

	@Override
	public void registerGameEventListener(IGameEventListener listener) 
	{
		gameEventListeners.add(listener);
	}
	
	@Override
	public int getGameState(GameState gameState) 
	{
		return gameStateDao.getValue(gameState);
	}
	
	@Override
	public void setGameState(GameState gameState, int value) 
	{
		gameStateDao.setValue(gameState, value);
	}
	
	@Override
	public void onGameEvent(GameEvent gameEvent)
	{
		gameEventListeners.forEach(l -> l.onGameEvent(gameEvent));
	}
	
	@Override
	public Result startUhc()
	{
//		if(gameStateDao.getGamePhase() != GamePhase.Lobby)
//			return Result.failure("UHC can only be started from the lobby.");
//		
//		onGameEvent(GameEvent.GameStarting);
//		
//		countdownRunner = new TaskRunner(plugin);
//		countdownRunner.setTask(() -> plugin.getLogger().log(Level.INFO, String.valueOf(countdownRunner.getRemainingDuration())));
//		countdownRunner.setDuration(CountdownDuration);
//		countdownRunner.setCompletionTask(() ->
//		{
//			plugin.getLogger().log(Level.INFO, "Starting UHC...");
//			
//			gameStateDao.setGamePhase(GamePhase.InProcess);
//			
//			onGameEvent(GameEvent.GameStarted);
//
//			startTime = worldService.getTime(true);
//			
//			gameStateRunner = new TaskRunner(plugin);
//			gameStateRunner.setTask(() -> updateGameState());
//			gameStateRunner.runRepeatedly();
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
		return Result.success("Countdown canceled.");
	}
	
	@Override
	public Result stopUhc()
	{
//		if(gameStateDao.getGamePhase() != GamePhase.InProcess)
//			return Result.failure("UHC is not running.");
//		
//		gameStateDao.setGamePhase(GamePhase.PostGame);
//		gameStateRunner.cancel();
		return Result.success("UHC stopped.");
	}
	
	private void updateGameState()
	{
		worldService.getTime(true);
	}

	@Override
	public GamePhase getGamePhase()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
