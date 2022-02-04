package com.github.mennokemp.uhcplugin.persistence.implementations;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringJoiner;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.mennokemp.uhcplugin.domain.players.PlayerEvent;
import com.github.mennokemp.uhcplugin.domain.players.PlayerStatistics;
import com.github.mennokemp.uhcplugin.persistence.abstractions.IPlayerStatisticsDao;

public class PlayerStatisticsDao implements IPlayerStatisticsDao
{
	private static ReentrantLock WriteLock = new ReentrantLock();
	private static String LogFilePath = "player-statistics.txt"; 
	
	@Override
	public void saveStatistics(PlayerStatistics playerStatistics) 
	{
		WriteToLog(playerStatistics);
	}

	private void WriteToLog(PlayerStatistics playerStatistics)
	{
		FileWriter fileWriter = null;
		PrintWriter output = null;
		
        try 
        {
        	WriteLock.lock();
        	
        	fileWriter = new FileWriter(LogFilePath, true);
        	output = new PrintWriter(new BufferedWriter(fileWriter));
		    
        	output.println(formatStatistics(playerStatistics));
        }
        catch(IOException exception) 
        {
        	Bukkit.getLogger().log(Level.SEVERE, exception.toString());
        }   
        finally
        {
        	if(output != null)
        		output.close();

        	WriteLock.unlock();
        }
	}
	
	private String formatStatistics(PlayerStatistics playerStatistics) 
	{
		StringJoiner output = new StringJoiner("\t");

		String timestamp = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());
		addToOutput(output, timestamp);

		Player player = playerStatistics.player;
		addToOutput(output, player.getName());
		
		Location location = player.getLocation();				
		addToOutput(output, location.getBlockX());
		addToOutput(output, location.getBlockY());
		addToOutput(output, location.getBlockZ());
		
		addToOutput(output, player.getHealth());
		
		PlayerInventory inventory = player.getInventory();
		addToOutput(output, getCount(inventory, Material.GOLDEN_APPLE));
		addToOutput(output, getCount(inventory, Material.ENCHANTED_GOLDEN_APPLE));
		
		PlayerEvent playerEvent = playerStatistics.getPlayerEvent();
		
		if(playerEvent != PlayerEvent.None)
			addToOutput(output, playerEvent.toString());
				
		return output.toString();
	}
	
	private void addToOutput(StringJoiner output, Object value)
	{
		output.add(value.toString());
	}
	
	private int getCount(PlayerInventory inventory, Material itemType)
	{
        ItemStack[] stacks = inventory.getContents();
        int count = 0;
        for (ItemStack stack : stacks)
        {
            if ((stack != null) && (stack.getType() == itemType))
            	count += stack.getAmount();
        }
        return count;
	}
}