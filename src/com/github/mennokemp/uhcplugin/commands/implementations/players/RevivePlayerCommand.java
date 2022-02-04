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

public class RevivePlayerCommand extends CommandBase
{
	private static final String Name = "RevivePlayer";
	private static final List<GamePhase> ValidGamePhases = Arrays.asList(GamePhase.InProcess);
	
	private final IServerService serverService;
	private final IPlayerService playerService;
	
	public RevivePlayerCommand(IGameStateDao gamePhaseDao, IServerService serverService, IPlayerService playerService)
	{
		super(gamePhaseDao);
		
		this.serverService = serverService;
		this.playerService = playerService;
	}
	
	@Override
	public String getName() 
	{
		return Name;
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
	protected List<GamePhase> getValidGamePhases() 
	{
		return ValidGamePhases;
	}
}