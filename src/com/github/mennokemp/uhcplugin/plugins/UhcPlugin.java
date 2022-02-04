package com.github.mennokemp.uhcplugin.plugins;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.uhcplugin.commands.abstractions.ICommand;
import com.github.mennokemp.uhcplugin.injections.InjectionModule;
import com.github.mennokemp.uhcplugin.services.abstractions.ILobbyService;
import com.github.mennokemp.uhcplugin.services.abstractions.IPlayerService;
import com.github.mennokemp.uhcplugin.services.abstractions.ISetupService;

public class UhcPlugin extends JavaPlugin implements Listener
{
	private InjectionModule kernel;
	
    @Override
    public void onEnable() 
    {
    	Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    	kernel = new InjectionModule(this, scoreboard);
		
    	RegisterCommands();
    	RegisterListeners();
  
		Setup();		
    }
    
    @Override
    public void onDisable() 
    {
    }
     
    private void RegisterCommands()
    {
    	for(ICommand command : kernel.GetAll(ICommand.class))
    		getCommand(command.GetName()).setExecutor(command);
    }
    
    private void RegisterListeners()
    {
    	getServer().getPluginManager().registerEvents(kernel.Get(IPlayerService.class), this);    	
    }
    
    private boolean Setup()
    {
    	if(kernel.Get(ILobbyService.class).DoesLobbyExist())
    	{
    		getLogger().log(Level.INFO, "UHC has already been set up.");
    		return false;
    	}
		
    	getLogger().log(Level.INFO, "Setting up UHC...");    	
    	
    	for(ISetupService service : kernel.GetAll(ISetupService.class))
    		service.Setup(true);
    	    	
    	getLogger().log(Level.INFO, "Finished setting up UHC.");
    	
    	return true;
    }
}