package com.github.mennokemp.uhcplugin.commands.implementations;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.mennokemp.uhcplugin.commands.abstractions.ICommand;
import com.github.mennokemp.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.uhcplugin.helpers.Result;
import com.github.mennokemp.uhcplugin.persistence.abstractions.IGameStateDao;

public abstract class CommandBase implements ICommand 
{
	protected final IGameStateDao gameStateDao;
	
	protected Command command;	
	
	public CommandBase(IGameStateDao gameStateDao)
	{
		this.gameStateDao = gameStateDao;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		command = cmd;
		GamePhase gamePhase = gameStateDao.getGamePhase();
		
		if(!getValidGamePhases().contains(gamePhase))
		{
			sender.sendMessage("Command not valid during " + gamePhase);
			return false;
		}
		
		Result result = onCommand(args);
		
		if(result.isSuccessful())
			return true;
		
		sender.sendMessage(result.getMessage());
		return false;
	}
	
	protected abstract List<GamePhase> getValidGamePhases();
	
	protected abstract Result onCommand(String[] args);
}