package com.github.mennokemp.uhcplugin.commands.implementations.game;

import java.util.Arrays;
import java.util.List;

import com.github.mennokemp.uhcplugin.commands.implementations.CommandBase;
import com.github.mennokemp.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.uhcplugin.helpers.Result;
import com.github.mennokemp.uhcplugin.persistence.abstractions.IGameStateDao;
import com.github.mennokemp.uhcplugin.services.abstractions.IGameService;

public class StartUhcCommand extends CommandBase 
{
	private static String Name = "StartUhc";
	private static final List<GamePhase> ValidGamePhases = Arrays.asList(GamePhase.Lobby);
	
	private final IGameService gameService;
	
	public StartUhcCommand(IGameStateDao gameStateDao, IGameService gameService) 
	{
		super(gameStateDao);
		
		this.gameService = gameService;
	}
	
	@Override
	public String getName() 
	{
		return Name;
	}
	
	@Override
	protected Result onCommand(String[] args) 
	{
		return gameService.StartUhc();
	}
	
	@Override
	protected List<GamePhase> getValidGamePhases() 
	{
		return ValidGamePhases;
	}
}
