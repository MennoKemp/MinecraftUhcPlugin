package com.github.mennokemp.minecraft.uhcplugin.commands.abstractions;

import org.bukkit.command.CommandExecutor;

public interface ICommand extends CommandExecutor
{
	public String getName();
}
