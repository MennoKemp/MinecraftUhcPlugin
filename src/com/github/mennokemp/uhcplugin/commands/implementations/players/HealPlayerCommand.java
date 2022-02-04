package com.github.mennokemp.uhcplugin.commands.implementations.players;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.mennokemp.uhcplugin.commands.implementations.CommandBase;
import com.github.mennokemp.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.uhcplugin.helpers.Result;
import com.github.mennokemp.uhcplugin.persistence.abstractions.IGameStateDao;
import com.github.mennokemp.uhcplugin.services.abstractions.IPlayerService;
import com.github.mennokemp.uhcplugin.services.abstractions.IServerService;

public class HealPlayerCommand extends CommandBase
{
	private static final String Name = "HealPlayer";
	private static final List<GamePhase> ValidGamePhases = Arrays.asList(GamePhase.InProcess);
	
	private final IServerService serverService;
	private final IPlayerService playerService;
	
	public HealPlayerCommand(IGameStateDao gameStateDao, IServerService serverService, IPlayerService playerService) 
	{
		super(gameStateDao);
		
		this.serverService = serverService;
		this.playerService = playerService;
	}
	
	@Override
	public String getName() 
	{
		return Name;
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
	protected List<GamePhase> getValidGamePhases() 
	{
		return ValidGamePhases;
	}
	
}
