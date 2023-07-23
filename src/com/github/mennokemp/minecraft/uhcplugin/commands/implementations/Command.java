package com.github.mennokemp.minecraft.uhcplugin.commands.implementations;

import java.util.stream.Stream;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.pluginhelpers.commands.CommandBase;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game.IGameStateService;

public abstract class Command extends CommandBase
{
	private IGameStateService gameStateService;
	
	public Command(IGameStateService gameStateService)
	{
		this.gameStateService = gameStateService;
	}
	
	@Override
	protected Result canExecute(String[] args)
	{
		GamePhase gamePhase = gameStateService.getGamePhase();
		
		if(!getValidGamePhases().anyMatch(p -> p == gamePhase))
			return Result.failure("Command not valid during " + gamePhase);
		
		return checkConditions(args);
	}
	
	protected abstract Stream<GamePhase> getValidGamePhases();
	
	protected abstract Result checkConditions(String[] args);
}
