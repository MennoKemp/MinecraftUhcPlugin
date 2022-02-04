package com.github.mennokemp.uhcplugin.services.implementations;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.mennokemp.uhcplugin.services.abstractions.IPlayerTrackingService;

public class PlayerTrackingService implements IPlayerTrackingService
{
	private static String TrackingFilePath = "e:\\Minecraft Servers\\Spigot Servers\\UHC\\player paths.txt";
	
	@Override
	public void SaveLocation(Player player) 
	{		
		SaveLocation(player, true);
	}

	@Override
	public void SaveDeathLocation(Player player) 
	{
		SaveLocation(player, false);
	}
	
	private void SaveLocation(Player player, boolean alive)
	{
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter out = null;
		
        try 
        {
			fw = new FileWriter(TrackingFilePath, true);
		    bw = new BufferedWriter(fw);
		    out = new PrintWriter(bw);
		    
		    String timestamp = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());
		    Location location = player.getLocation();
			String position = timestamp + "\t" + player.getName() + "\t" + location.getBlockX() + "\t" + location.getBlockY() + "\t" + location.getBlockZ() + "\t" + String.valueOf(alive); 
		
		    out.println(position);
		    
        }
        catch(IOException ie) 
        {
        	Bukkit.getLogger().log(Level.WARNING, ie.toString());
        }   
        finally
        {
        	if(out != null)
        		out.close();
        }
	}
}
