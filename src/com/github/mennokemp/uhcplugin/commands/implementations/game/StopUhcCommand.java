package com.github.mennokemp.uhcplugin.commands.implementations.game;

import java.util.Arrays;
import java.util.List;

import com.github.mennokemp.uhcplugin.commands.implementations.CommandBase;
import com.github.mennokemp.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.uhcplugin.helpers.Result;
import com.github.mennokemp.uhcplugin.persistence.abstractions.IGameStateDao;
import com.github.mennokemp.uhcplugin.services.abstractions.IGameService;

public class StopUhcCommand extends CommandBase 
{
	private static String Name = "StopUhc";
	private static final List<GamePhase> ValidGamePhases = Arrays.asList(GamePhase.InProcess);

	private final IGameService gameService;
	
	public StopUhcCommand(IGameStateDao gameStateDao, IGameService gameService)
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
	protected List<GamePhase> getValidGamePhases() 
	{
		return ValidGamePhases;
	}

	@Override
	protected Result onCommand(String[] args) 
	{
		return gameService.StopUhc();
	}
}
