package com.github.mennokemp.minecraft.uhcplugin.services.implementations.world;

import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.Plugin;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameEvent;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameSetting;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameState;
import com.github.mennokemp.minecraft.uhcplugin.domain.world.Realm;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.IGameEventListener;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.ISettingDao;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game.IGameStateService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game.ISettingService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.world.IWorldBorderService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.world.IWorldService;

public class WorldBorderService implements IWorldBorderService, IGameEventListener
{
	private final ISettingService settingService;
	private final IGameStateService gameStateService;
	
	private final Plugin plugin;
	
	private final WorldBorder worldBorder;
	
//	private TaskRunner worldBorderTask;
	
	double initialRadius;
	double finalRadius;
	double shrinkStart;
	double shrinkEnd;
	
	private double a;
	private double b;
	private double c;
	
	public WorldBorderService(ISettingService settingService, IGameStateService gameStateService, IWorldService worldService, Plugin plugin)
	{
		this.settingService = settingService;
		this.gameStateService = gameStateService;
		
		this.plugin = plugin;
		
		worldBorder = worldService.getWorld(Realm.Overworld).getWorldBorder();
		
		gameStateService.registerGameEventListener(this);
		
//		if(gameStateService.getShrinkPhase() == ShrinkPhase.Shrinking)
//			setShrinkParameters();
	}
	
	@Override
	public void onGameEvent(GameEvent gameEvent) 
	{
//		switch(gameEvent)
//		{
//			case GameStarting:
//			{
//				double initialRadius = settingService.getSetting(GameSetting.WorldBorderInitialRadius);
//				double finalRadius = settingService.getSetting(GameSetting.WorldBorderFinalRadius);
//				double shrinkStart = settingService.getSetting(GameSetting.ShrinkStart);
//				double shrinkEnd = settingService.getSetting(GameSetting.ShrinkEnd);
//				
//				a = (finalRadius - initialRadius) / Math.pow((shrinkEnd - shrinkStart), 2);
//				b = -shrinkStart;
//				c = initialRadius;
//				
//				worldBorder.setCenter(0, 0);
//				worldBorder.setSize(initialRadius * 2);
//				
//				break;
//			}
//			case GameStarted:
//			{
//				worldBorderTask = new TaskRunner(plugin);
//				worldBorderTask.setDuration(1);
//				worldBorderTask.setTask(() -> worldBorderThread());
//				worldBorderTask.runRepeatedly();
//				
//				break;
//			}
//			default:
//				break;
//		}
	}
	
	private void worldBorderThread()
	{
//		int gameDuration = gameStateService.getGameDuration();
//		
//		if(gameDuration > shrinkStart)
//		{
//			
//		}
//		else if(gameDuration > shrinkEnd)
//		{
//			worldBorderTask.cancel();
//		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	private final ISettingDao settingDao;
//	
//	private final IGameStateService gameStateService;
//	private final IWorldService worldService;
//	
//	private final Plugin plugin;
//	
//	private WorldBorder worldBorder;
//	private double baseShrinkSpeed;		// Decrease in border diameter [blocks/second].
//	private double incrementInterval;	// Interval between shrink increments [seconds].
//	private int finalDiameter;
//	private int currentIncrement;
//		
//	private TaskRunner shrinkUpdateTask;
//	
//	public WorldBorderService(ISettingDao settingDao, IGameStateService gameStateService, IWorldService worldService, Plugin plugin)
//	{
//		this.settingDao = settingDao;
//		
//		this.gameStateService = gameStateService;
//		this.worldService = worldService;
//		
//		this.plugin = plugin;
//		
//		if(gameStateService.getShrinkPhase() == ShrinkPhase.Shrinking)
//			setShrinkParameters();
//	}
	
//	@Override
//	public Result updateWorldBorder() 
//	{
//		switch(gameStateService.getShrinkPhase())
//		{
//			case PreShrink:
//			{
//				int radius = settingDao.getValue(GameSetting.WorldBorderInitialRadius);
//				int diameter = radius * 2 + 1;
//				worldBorder.setCenter(0, 0);
//				worldBorder.setSize(diameter);
//				return Result.success("World border set to a diameter of " + diameter + ".");
//			}
//			case Shrinking:
//			{
//				int shrinkBoostCount = gameStateService.getGameState(GameState.ShrinkBoostCount);
//				double shrinkBoost = settingDao.getValue(GameSetting.ShrinkDeathBoost) * shrinkBoostCount / 100.0;
//				double shrinkSpeed = baseShrinkSpeed * currentIncrement * shrinkBoost;
//				
//				double currentDiameter = worldBorder.getSize();
//				double remainingShrink = currentDiameter - finalDiameter;
//				double shrinkTime = remainingShrink / shrinkSpeed;
//				
//				worldBorder.setSize(finalDiameter, (long)shrinkTime);
//				return Result.success("Updated shrink: " + shrinkSpeed / 2 + " blocks/s.");
//			}
//			default:
//			{
//				return Result.success("No reason to update the world border at this point.");
//			}
//		}
//	}
//
//	@Override
//	public Result startShrink() 
//	{
//		if(gameStateService.getShrinkPhase() == ShrinkPhase.Shrinking)
//			return Result.failure("World border is already shrinking.");
//		
//		setShrinkParameters();
//		
//		Result result = updateWorldBorder();
//		
//		if(!result.isSuccessful())
//			return result;
//		
//		shrinkUpdateTask = new TaskRunner(plugin);
//		shrinkUpdateTask.setDuration(1);
//		shrinkUpdateTask.setTask(() -> updateWorldBorderStatus());
//		shrinkUpdateTask.runRepeatedly();
//		
//		gameStateService.setGameState(GameState.ShrinkStart, worldService.getTime(true));
//		gameStateService.setShrinkPhase(ShrinkPhase.Shrinking);
//		return Result.success("Started shrinking the world border.");
//	}
//
//	@Override
//	public Result stopShrink() 
//	{
//		if(gameStateService.getShrinkPhase() != ShrinkPhase.Shrinking)
//			return Result.failure("World border is not shrinking.");
//		
//		worldBorder.setSize(worldBorder.getSize());
//		shrinkUpdateTask.cancel();
//		
//		gameStateService.setShrinkPhase(ShrinkPhase.PostShrink);
//		return Result.success("Stopped world border shrinking.");
//	}
//
//	@Override
//	public boolean canChangeState(GameState gameState, int value) 
//	{
//		return true;
//	}
//
//
//	
//	private void updateWorldBorderStatus() 
//	{
//		int currentRadius = (int)((worldBorder.getSize() - 1) / 2);
//		gameStateService.setGameState(GameState.WorldBorderRadius, currentRadius);
//		
//		int shrinkStart = gameStateService.getGameState(GameState.ShrinkStart);
//		int currentTime = worldService.getTime(true);
//		int passedTime = (currentTime - shrinkStart) / Constants.TicksPerSecond;
//				
//		worldService.getTime(true);
//	}
//	
//	private void setShrinkParameters() 
//	{
//		worldBorder = worldService.getWorld(Realm.Overworld).getWorldBorder();
//		
//		int initialDiameter = settingDao.getValue(GameSetting.WorldBorderInitialRadius) * 2 + 1;
//		finalDiameter = settingDao.getValue(GameSetting.WorldBorderFinalRadius) * 2 + 1; 
//		double shrinkAmount = finalDiameter - initialDiameter;
//		
//		int incrementCount = settingDao.getValue(GameSetting.ShrinkIncrements);
//		int incrementPartCount = incrementCount * (incrementCount + 1) / 2;
//		
//		int shrinkStart = settingDao.getValue(GameSetting.ShrinkStart);
//		int shrinkEnd = settingDao.getValue(GameSetting.ShrinkEnd);
//		int shrinkTime = (shrinkStart - shrinkEnd) * 60;
//		incrementInterval = (double)shrinkTime / incrementCount;
//		baseShrinkSpeed = shrinkAmount / (incrementPartCount * incrementInterval);
//		
//		int damageBuffer = settingDao.getValue(GameSetting.WorldBorderBuffer);
//		worldBorder.setDamageBuffer(damageBuffer);
//	}

	@Override
	public void setShrinkMultiplier()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getShrinkMultiplier()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Result startShrink()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result stopShrink()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
