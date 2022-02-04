package com.github.mennokemp.uhcplugin.commands.abstractions;

import org.bukkit.command.CommandExecutor;

public interface ICommand extends CommandExecutor
{
	public String getName();
}
