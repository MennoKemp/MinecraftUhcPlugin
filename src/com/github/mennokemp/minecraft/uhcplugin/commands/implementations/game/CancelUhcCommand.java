package com.github.mennokemp.minecraft.uhcplugin.commands.implementations.game;

import java.util.stream.Stream;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.uhcplugin.commands.implementations.Command;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game.IGameService2;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game.IGameStateService;

public class CancelUhcCommand extends Command
{
	private final IGameService2 gameService;
	
	public CancelUhcCommand(IGameStateService gameStateService, IGameService2 gameService)
	{
		super(gameStateService);
		
		this.gameService = gameService;
	}
	
	@Override
	protected Result onCommand(String[] args)
	{
		return gameService.cancelUhc();
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