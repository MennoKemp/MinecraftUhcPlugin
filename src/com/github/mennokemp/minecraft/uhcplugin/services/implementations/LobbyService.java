package com.github.mennokemp.minecraft.uhcplugin.services.implementations;

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

import com.github.mennokemp.minecraft.uhcplugin.domain.world.Realm;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.ILobbyService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.world.IWorldService;
import com.github.shynixn.structureblocklib.api.bukkit.StructureBlockLibApi;

public class LobbyService implements ILobbyService
{
	private static final Location LobbyLocation = new Location(null, 0, 300, 0);
		
	private static final String LobbyStructureName = "uhc_lobby.nbt";
	
	private static final org.bukkit.util.Vector StructureOffset = new org.bukkit.util.Vector(-14, -10, -12);
	private static final org.bukkit.util.Vector BeaconOffset = new org.bukkit.util.Vector(0, -3, 0);
	private static final org.bukkit.util.Vector SignOffset = new org.bukkit.util.Vector(8, 0, 10);
	
	private final IWorldService worldService;
	
	private final Plugin plugin;
	
	private final World lobbyWorld;
	
	public LobbyService(IWorldService worldService, Plugin plugin)
	{
		this.worldService = worldService;
		
		this.plugin = plugin;
		
		lobbyWorld = worldService.getWorld(Realm.Lobby);
		
		if(lobbyWorld.getBlockAt(worldService.getLocation(Realm.Lobby, LobbyLocation).add(BeaconOffset)).getType() != Material.BEACON)
			createLobby();
	}
	
	@Override
	public Location getLobbyLocation()
	{
		return worldService.getLocation(Realm.Lobby, LobbyLocation);
	}
	
	@Override
	public void createLobby()
	{
		plugin.getLogger().log(Level.INFO, "Creating lobby...");
		
		Path structurePath = plugin.getDataFolder().toPath().resolve(LobbyStructureName);

		Location lobbyLocation = worldService.getLocation(Realm.Lobby, LobbyLocation).add(StructureOffset);
		
		StructureBlockLibApi.INSTANCE
			.loadStructure(plugin)
			.at(worldService.getLocation(Realm.Lobby, lobbyLocation))
			.includeEntities(true)
			.loadFromPath(structurePath)
			.onException(e -> plugin.getLogger().log(Level.SEVERE, "Failed to create lobby.", e))
			.onResult(e -> plugin.getLogger().log(Level.INFO, ChatColor.GREEN + "Created lobby."));
				
		Bukkit.getScheduler().runTaskLater(plugin, () -> writeSigns(), 20);
		
		setLobbyRules();
		
		plugin.getLogger().log(Level.INFO, "Lobby created.");
	}
	
	private void setLobbyRules()
	{
		lobbyWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
	}
	
	private void writeSigns()
	{
		WriteSign(0, "", "Welcome to UHC!");
		WriteSign(2, "Can you find", "the following", "blocks?");
		WriteSign(3, "Ancient Debris", "Bone", "Diamond Ore", "Prismarine");
	}
	
	private void WriteSign(int offset, String... lines)
	{
		World lobby = worldService.getWorld(Realm.Lobby);
		Location signLocation = worldService.getLocation(Realm.Lobby, LobbyLocation).add(SignOffset).subtract(offset, 0, 0);
		Block block = lobby.getBlockAt(signLocation);
		block.setType(Material.OAK_WALL_SIGN);
		Sign sign = (Sign)block.getState();

//		for (int l = 0; l < lines.length && l < 4; l++) 
//			sign.setLine(l, lines[l]);
		
		sign.update(true);
	}
}