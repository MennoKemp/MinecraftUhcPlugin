package com.github.mennokemp.minecraft.uhcplugin.commands.implementations.players;

import java.util.stream.Stream;

import org.bukkit.entity.Player;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.uhcplugin.commands.implementations.Command;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.IPlayerService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.IServerService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game.IGameStateService;

public class HealPlayerCommand extends Command
{
	private final IServerService serverService;
	private final IPlayerService playerService;
	
	public HealPlayerCommand(IGameStateService gameStateService, IServerService serverService, IPlayerService playerService) 
	{
		super(gameStateService);
		
		this.serverService = serverService;
		this.playerService = playerService;
	}
	
	@Override
	protected Result onCommand(String[] args)
	{
		if(args.length != 2)
			return Result.failure("Incorrect format. Use: " + command.getUsage());
		
		Player player = serverService.getPlayer(args[0]);
				
		if(player == null)
			return Result.failure("Cannot find player " + args[0]);
		
		double healAmount;
		
		try
		{
			healAmount = Double.parseDouble(args[1]);
		}
		catch(Exception exception)
		{
			return Result.failure("Cannot parse heal amount " + args[1]);
		}
		
		double currentHealth = player.getHealth();
		playerService.healPlayer(player, healAmount);
		double newHealth = player.getHealth();
		
		return Result.success("Healed " + player.getName() + " from " + currentHealth + " to " + newHealth);
	}

	@Override
	protected Stream<GamePhase> getValidGamePhases()
	{
		return Stream.of(GamePhase.InProcess);
	}

	@Override
	protected Result checkConditions(String[] args)
	{
		return Result.failure("Not implemented");
	}	
}
