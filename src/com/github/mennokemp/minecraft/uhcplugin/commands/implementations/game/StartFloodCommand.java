package com.github.mennokemp.minecraft.uhcplugin.commands.implementations.game;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.uhcplugin.commands.implementations.Command;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game.IGameStateService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.world.IWorldService;

public class StartFloodCommand extends Command
{
	private final IWorldService worldService;
	
	public StartFloodCommand(IGameStateService gameStateService, IWorldService worldService) 
	{
		super(gameStateService);
		
		this.worldService = worldService;
	}
	
	@Override
	protected Result onCommand(String[] args) 
	{
		return worldService.startFlood();
	}

	@Override
	protected Result checkConditions(String[] args)
	{
		return Result.failure("Not implemented");
	}

	@Override
	protected Stream<GamePhase> getValidGamePhases()
	{
		return Stream.of(GamePhase.InProcess);
	}
}