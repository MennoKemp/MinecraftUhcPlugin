package com.github.mennokemp.uhcplugin.persistence.implementations;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.uhcplugin.domain.game.GameSetting;
import com.github.mennokemp.uhcplugin.helpers.Result;
import com.github.mennokemp.uhcplugin.persistence.abstractions.ISettingDao;

public class SettingDao extends ScoreboardDao<GameSetting> implements ISettingDao
{
	private static String ObjectiveName = "uhc_settings";	
	private static String ObjectiveDisplayName = "UHC Settings";
	private static String UhcSettingsFilePath = "UhcSettings.txt";
		
	public SettingDao(Scoreboard scoreboard)
	{
		super(scoreboard);
	}
		
	@Override
	public Result validateSettings()
	{
        try 
        {
        	for(String setting : getSettingDefaults())
    		{
    			String[] buffer = setting.split("\t");
    			
    			GameSetting gameSetting = Enum.valueOf(GameSetting.class, buffer[0]);
    			int value = getValue(gameSetting);
    			
    			int minimumValue = Integer.valueOf(buffer[2]);

    			if(value < minimumValue)
    				return Result.failure("Setting " + gameSetting + " must be at least " + minimumValue);
    			
    			
    			if(buffer.length == 4)
    			{
    				int maximumValue = Integer.valueOf(buffer[3]);
    				
        			if(value > maximumValue)
        				return Result.failure("Setting " + gameSetting + " cannot be larger than " + maximumValue);
    			}
    		}
			
        	return new Result(true, "All settings are valid.");
		} 
        catch(Exception exception) 
        {
        	return Result.failure(exception.toString());
        }                  		
	}
	
	@Override
	public void resetSettings()
	{
		this.clear();
		
		for(String setting : getSettingDefaults())
		{
			String[] buffer = setting.split("\t");
			
			GameSetting gameSetting = Enum.valueOf(GameSetting.class, buffer[0]);
			int defaultValue = Integer.valueOf(buffer[1]);
			this.setValue(gameSetting, defaultValue);
		}
	}
	
	@Override
	protected String getObjectiveName() 
	{
		return ObjectiveName;
	}

	@Override
	protected String getObjectiveDisplayName() 
	{
		return ObjectiveDisplayName;
	}
	
	private List<String> getSettingDefaults()
	{
		List<String> settingDefaults = new ArrayList<String>();
		
		ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(UhcSettingsFilePath);
        BufferedReader reader = null;
        
        if (inputStream == null) 
        	return settingDefaults;

        try 
        {        	
        	reader = new BufferedReader(new InputStreamReader(inputStream));
        	reader.readLine();
        }
        catch(Exception exception) 
        {
        	Bukkit.getLogger().log(Level.SEVERE, exception.toString());
        }   
        finally
        {
        	if(reader != null)
        	{
        		try 
        		{
        			reader.close();
				} 
        		catch (Exception exception) 
        		{
        			Bukkit.getLogger().log(Level.SEVERE, exception.toString());			
				}
        	}
        }
        
        return settingDefaults;
	}
}
