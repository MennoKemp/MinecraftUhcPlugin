package com.github.mennokemp.minecraft.uhcplugin.commands.implementations.players;

import java.util.stream.Stream;

import org.bukkit.entity.Player;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.uhcplugin.commands.implementations.Command;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.IPlayerService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.IServerService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game.IGameStateService;

public class RevivePlayerCommand extends Command
{
	private final IServerService serverService;
	private final IPlayerService playerService;
	
	public RevivePlayerCommand(IGameStateService gamePhaseService, IServerService serverService, IPlayerService playerService)
	{
		super(gamePhaseService);
		
		this.serverService = serverService;
		this.playerService = playerService;
	}
	
	@Override
	public Result onCommand(String[] args)
	{
		if(args.length != 1)
			return Result.failure("Incorrect format. Use: " + command.getUsage());
		
		Player player = serverService.getPlayer(args[0]);
		
		if(player == null)
			return Result.failure("Cannot find player " + args[0]);
				
		return playerService.revivePlayer(player);
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