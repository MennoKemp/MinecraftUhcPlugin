package com.github.mennokemp.uhcplugin.services.implementations;

import java.nio.file.Path;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.plugin.Plugin;

import com.github.mennokemp.uhcplugin.services.abstractions.ILobbyService;
import com.github.mennokemp.uhcplugin.services.abstractions.IWorldService;
import com.github.shynixn.structureblocklib.api.bukkit.StructureBlockLibApi;

public class LobbyService implements ILobbyService
{
	private static Location LobbyLocation = new Location(null, 0, 291, 0);
	private static Location BeaconLocation = new Location(null, 14, 298, 12);
	private static Location JoinLocation = new Location(null, 14.5, 299, 12.5);
	private static Location SignLocation = new Location(null, 22, 301, 22);
	
	private final IWorldService worldService;
	
	private final Plugin plugin;
	
	public LobbyService(IWorldService worldService, Plugin plugin)
	{
		this.worldService = worldService;
		
		this.plugin = plugin;
	}
	
	@Override
	public void setup(boolean firstTime)
	{
		if(firstTime)
		{
			CreateLobby();
			SetLobbyRules();			
		}
	}
	
	@Override
	public Location getJoinLocation()
	{
		return worldService.getLocation(true, JoinLocation);
	}
	
	@Override
	public boolean doesLobbyExist()
	{
		return worldService.getLobby().getBlockAt(worldService.getLocation(true, BeaconLocation)).getType() == Material.BEACON;
	}
	
	private void CreateLobby()
	{
		plugin.getLogger().log(Level.INFO, "Creating lobby...");
		
		Path structurePath = plugin.getDataFolder().toPath().resolve("uhc_lobby.nbt");
			
		StructureBlockLibApi.INSTANCE
			.loadStructure(plugin)
			.at(worldService.getLocation(true, LobbyLocation))
			.includeEntities(true)
			.loadFromPath(structurePath)
			.onException(e -> plugin.getLogger().log(Level.SEVERE, "Failed to load structure 'uhc_lobby'.", e))
			.onResult(e -> plugin.getLogger().log(Level.INFO, ChatColor.GREEN + "Loaded structure 'uhc_lobby'."));
				
		Bukkit.getScheduler().runTaskLater(plugin, () -> WriteSigns(), 20);
		
		plugin.getLogger().log(Level.INFO, "Lobby created.");
	}
	
	private void SetLobbyRules()
	{
		World lobby = worldService.getLobby();
		lobby.setGameRule(GameRule.DO_MOB_SPAWNING, false);
	}
	
	private void WriteSigns()
	{
		WriteSign(0, "", "Welcome to UHC!");
		WriteSign(2, "Can you find", "the following", "blocks?");
		WriteSign(1, "Ancient Debris", "Bone", "Diamond Ore", "Prismarine");
	}
	
	private void WriteSign(int offset, String... lines)
	{
		World lobby = worldService.getLobby();
		Block block = lobby.getBlockAt(worldService.getLocation(true, SignLocation).add(-offset, 0, 0));
		block.setType(Material.OAK_WALL_SIGN);
		Sign sign = (Sign)block.getState();

		for (int l = 0; l < lines.length && l < 4; l++) 
			sign.setLine(l, lines[l]);
		
		sign.update(true);
	}
}
