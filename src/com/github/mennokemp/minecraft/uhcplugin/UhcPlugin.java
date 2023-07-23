package com.github.mennokemp.minecraft.uhcplugin;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.minecraft.uhcplugin.commands.abstractions.ICommand;
import com.github.mennokemp.minecraft.uhcplugin.injections.InjectionModule;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.IPlayerService;

public class UhcPlugin extends JavaPlugin implements Listener
{
	private InjectionModule kernel;
	
    @Override
    public void onEnable() 
    {
    	Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    	kernel = new InjectionModule(this, scoreboard);

    	getServer().getMessenger().registerOutgoingPluginChannel(this, "uhc:tracking"); 
    	
    	registerCommands();
    	registerEventListeners();
    }
    
    @Override
    public void onDisable() 
    {
    }
     
    private void registerCommands()
    {
    	for(ICommand command : kernel.getAll(ICommand.class))
    		getCommand(command.getName()).setExecutor(command);
    }
    
    private void registerEventListeners()
    {
		getServer().getPluginManager().registerEvents(kernel.get(IPlayerService.class), this);    	
    }
}