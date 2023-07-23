package com.github.mennokemp.minecraft.uhcplugin.commands.implementations.game;

import java.util.stream.Stream;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.uhcplugin.commands.implementations.Command;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game.IGameStateService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.world.IWorldService;

public class StopFloodCommand extends Command
{
	private final IWorldService worldService;
	
	public StopFloodCommand(IGameStateService gamePhaseService, IWorldService worldService) 
	{
		super(gamePhaseService);
		
		this.worldService = worldService;
	}
	
	@Override
	protected Result onCommand(String[] args) 
	{
		return worldService.stopFlood();
	}

	@Override
	protected Result checkConditions(String[] args)
	{
		return Result.failure("Not implemented");
	}

	@Override
	protected Stream<GamePhase> getValidGamePhases()
	{
		return Stream.of(GamePhase.Lobby);
	}
}