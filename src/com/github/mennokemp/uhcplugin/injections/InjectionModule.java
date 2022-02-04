package com.github.mennokemp.uhcplugin.injections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.uhcplugin.commands.implementations.game.CancelUhcCommand;
import com.github.mennokemp.uhcplugin.commands.implementations.game.StartFloodCommand;
import com.github.mennokemp.uhcplugin.commands.implementations.game.StartUhcCommand;
import com.github.mennokemp.uhcplugin.commands.implementations.game.StopFloodCommand;
import com.github.mennokemp.uhcplugin.commands.implementations.players.HealPlayerCommand;
import com.github.mennokemp.uhcplugin.commands.implementations.players.RevivePlayerCommand;
import com.github.mennokemp.uhcplugin.persistence.abstractions.IGameStateDao;
import com.github.mennokemp.uhcplugin.persistence.abstractions.IPlayerStatisticsDao;
import com.github.mennokemp.uhcplugin.persistence.abstractions.ISettingDao;
import com.github.mennokemp.uhcplugin.persistence.implementations.GameStateDao;
import com.github.mennokemp.uhcplugin.persistence.implementations.PlayerStatisticsDao;
import com.github.mennokemp.uhcplugin.persistence.implementations.SettingDao;
import com.github.mennokemp.uhcplugin.services.abstractions.IGameService;
import com.github.mennokemp.uhcplugin.services.abstractions.ILobbyService;
import com.github.mennokemp.uhcplugin.services.abstractions.IPlayerService;
import com.github.mennokemp.uhcplugin.services.abstractions.IPlayerTrackingService;
import com.github.mennokemp.uhcplugin.services.abstractions.IServerService;
import com.github.mennokemp.uhcplugin.services.abstractions.IStatisticsService;
import com.github.mennokemp.uhcplugin.services.abstractions.IWorldService;
import com.github.mennokemp.uhcplugin.services.implementations.GameService;
import com.github.mennokemp.uhcplugin.services.implementations.LobbyService;
import com.github.mennokemp.uhcplugin.services.implementations.PlayerService;
import com.github.mennokemp.uhcplugin.services.implementations.PlayerTrackingService;
import com.github.mennokemp.uhcplugin.services.implementations.ServerService;
import com.github.mennokemp.uhcplugin.services.implementations.StatisticsService;
import com.github.mennokemp.uhcplugin.services.implementations.WorldService;

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
    	IGameStateDao gameStateDao = register(IGameStateDao.class, new GameStateDao(settingDao, scoreboard));
    	IPlayerStatisticsDao playerStatisticsDao = register(IPlayerStatisticsDao.class, new PlayerStatisticsDao());
    	
    	IStatisticsService statisticsService = register(IStatisticsService.class, new StatisticsService());
    	IServerService serverService = register(IServerService.class, new ServerService());
    	IWorldService worldService = register(IWorldService.class, new WorldService(settingDao, gameStateDao, plugin));
    	ILobbyService lobbyService = register(ILobbyService.class, new LobbyService(worldService, plugin));
    	IPlayerTrackingService playerTrackingService = register(IPlayerTrackingService.class, new PlayerTrackingService());
    	IPlayerService playerService = register(IPlayerService.class, new PlayerService(settingDao, gameStateDao, statisticsService, worldService, lobbyService, plugin, scoreboard));
    	IGameService gameService = register(IGameService.class, new GameService(settingDao, gameStateDao, playerTrackingService, worldService, playerService, plugin));
    	
    	register(StartUhcCommand.class, new StartUhcCommand(gameStateDao, gameService));
    	register(CancelUhcCommand.class, new CancelUhcCommand(gameStateDao, gameService));
    	register(HealPlayerCommand.class, new HealPlayerCommand(gameStateDao, serverService, playerService));
    	register(RevivePlayerCommand.class, new RevivePlayerCommand(gameStateDao,serverService, playerService));
    	register(StartFloodCommand.class, new StartFloodCommand(gameStateDao, worldService));
    	register(StopFloodCommand.class, new StopFloodCommand(gameStateDao, worldService));
    }
    
    private <T> T register(Class<T> abstraction, T implementation)
    {
    	bindings.put(abstraction, implementation);
    	return implementation;
    }
}
