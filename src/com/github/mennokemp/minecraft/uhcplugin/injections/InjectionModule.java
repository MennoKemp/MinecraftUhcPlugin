package com.github.mennokemp.minecraft.uhcplugin.injections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.minecraft.uhcplugin.commands.implementations.game.CancelUhcCommand;
import com.github.mennokemp.minecraft.uhcplugin.commands.implementations.game.StartFloodCommand;
import com.github.mennokemp.minecraft.uhcplugin.commands.implementations.game.StartUhcCommand;
import com.github.mennokemp.minecraft.uhcplugin.commands.implementations.game.StopFloodCommand;
import com.github.mennokemp.minecraft.uhcplugin.commands.implementations.players.HealPlayerCommand;
import com.github.mennokemp.minecraft.uhcplugin.commands.implementations.players.RevivePlayerCommand;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.IGameStateDao;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.ISettingDao;
import com.github.mennokemp.minecraft.uhcplugin.persistence.implementations.GameStateDao;
import com.github.mennokemp.minecraft.uhcplugin.persistence.implementations.SettingDao;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.ILobbyService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.IPlayerService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.IServerService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.IStatisticsService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game.IGameService2;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game.IGameStateService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.world.IWorldService;
import com.github.mennokemp.minecraft.uhcplugin.services.implementations.LobbyService;
import com.github.mennokemp.minecraft.uhcplugin.services.implementations.PlayerService;
import com.github.mennokemp.minecraft.uhcplugin.services.implementations.ServerService;
import com.github.mennokemp.minecraft.uhcplugin.services.implementations.StatisticsService;
import com.github.mennokemp.minecraft.uhcplugin.services.implementations.game.GameService2;
import com.github.mennokemp.minecraft.uhcplugin.services.implementations.game.GameStateService;
import com.github.mennokemp.minecraft.uhcplugin.services.implementations.world.WorldService;

public class InjectionModule
{
	private Map<Class<?>, Object> bindings = new LinkedHashMap<>();
	
	private final Plugin plugin;
	private final Scoreboard scoreboard;
	
	public InjectionModule(Plugin plugin, Scoreboard scoreboard)
	{
		this.plugin = plugin;
		this.scoreboard = scoreboard;
		
		registerBindings();
	}
	    
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> abstraction)
	{
    	return (T)bindings.get(abstraction);
	}
    
    @SuppressWarnings("unchecked")
	public <T> Collection<T> getAll(Class<T> abstraction)
	{
    	Collection<T> implementations = new ArrayList<T>(); 
		
    	for(Object service : bindings.values())
    	{   
    		if(abstraction.isInstance(service))
    			implementations.add((T)service);    			
    	}

    	return implementations;
	}
    
    private void registerBindings() 
    {
    	ISettingDao settingDao = register(ISettingDao.class, new SettingDao(scoreboard));
    	IGameStateDao gameStateDao = register(IGameStateDao.class, new GameStateDao(scoreboard));
    	
    	IServerService serverService = register(IServerService.class, new ServerService());
    	IStatisticsService statisticsService = register(IStatisticsService.class, new StatisticsService());
    	IWorldService worldService = register(IWorldService.class, new WorldService(settingDao, gameStateDao, plugin));
    	ILobbyService lobbyService = register(ILobbyService.class, new LobbyService(worldService, plugin));
    	IPlayerService playerService = register(IPlayerService.class, new PlayerService(settingDao, gameStateDao, serverService, statisticsService, worldService, lobbyService, plugin, scoreboard));
    	IGameService2 gameService = register(IGameService2.class, new GameService2(settingDao, gameStateDao, serverService, statisticsService, worldService, playerService, plugin));
    	IGameStateService gameStateService = register(IGameStateService.class, new GameStateService(gameStateDao, worldService, plugin));
    	
    	register(StartUhcCommand.class, new StartUhcCommand(gameStateService, gameService));
    	register(CancelUhcCommand.class, new CancelUhcCommand(gameStateService, gameService));
    	register(HealPlayerCommand.class, new HealPlayerCommand(gameStateService, serverService, playerService));
    	register(RevivePlayerCommand.class, new RevivePlayerCommand(gameStateService, serverService, playerService));
    	register(StartFloodCommand.class, new StartFloodCommand(gameStateService, worldService));
    	register(StopFloodCommand.class, new StopFloodCommand(gameStateService, worldService));
    }
    
    private <T> T register(Class<T> abstraction, T implementation)
    {
    	bindings.put(abstraction, implementation);
    	return implementation;
    }
}
