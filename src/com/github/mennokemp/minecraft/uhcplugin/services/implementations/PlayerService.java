package com.github.mennokemp.minecraft.uhcplugin.services.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameSetting;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameState;
import com.github.mennokemp.minecraft.uhcplugin.domain.players.PlayerEvent;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.IGameStateDao;
import com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions.ISettingDao;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.ILobbyService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.IPlayerService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.IServerService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.IStatisticsService;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game.IGameOverListener;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.world.IWorldService;

public class PlayerService implements IPlayerService
{
	private static String PlayerHealthName = "health";
	
	private static int SpawnProtectionDuration = 10;
	private static int PortalSearchRadius = 5;
	
	private static List<IGameOverListener> gameOverListeners = new ArrayList<IGameOverListener>();
	
	private final ISettingDao settingDao;
	private final IGameStateDao gameStateDao;
	
	private final IServerService serverService;
	private final IStatisticsService statisticsService; 
	private final IWorldService worldService;
	private final ILobbyService lobbyService;

	private final Plugin plugin;
	private final Scoreboard scoreboard;
	
	private final Map<Player, Location> deathLocations = new HashMap<Player, Location>();
	
	private Map<Team, Location> spawnLocations = new HashMap<Team, Location>();
	private Map<Player, Team> players = new HashMap<Player, Team>();
	
	private List<Team> teams = new ArrayList<Team>();
	
	public PlayerService(
			ISettingDao settingDao, 
			IGameStateDao gameStateDao, 
			IServerService serverService,
			IStatisticsService statisticsService, 
			IWorldService worldService, 
			ILobbyService lobbyService, 
			Plugin plugin, 
			Scoreboard scoreboard)
	{
		this.settingDao = settingDao;
		this.gameStateDao = gameStateDao;
		
		this.serverService = serverService;
		this.statisticsService = statisticsService; 
		this.worldService = worldService;
		this.lobbyService = lobbyService;
		
		this.plugin = plugin;
		this.scoreboard = scoreboard;
		
		if(gameStateDao.getGamePhase() == GamePhase.InProcess)
			teams = new ArrayList<Team>(scoreboard.getTeams());
	}
	
//	@Override
//	public void setup(boolean firstTime)
//	{
//		if(firstTime)
//		{
//			scoreboard.registerNewObjective(PlayerHealthName, PlayerHealthName, PlayerHealthName, RenderType.HEARTS).setDisplaySlot(DisplaySlot.PLAYER_LIST);
//			
//			// TODO: does this work?
//			Bukkit.getServer().getOperators().clear();
//			createTeams();
//		}
//		else
//		{
//			for(Player player : serverService.getPlayers())
//				teleportToLobby(player);
//		}
//	}
	
	@Override
	public void setPlayerHealthVisibility(boolean visible)
	{
		Objective playerHealth = scoreboard.getObjective(PlayerHealthName);
		
		if(visible)
			playerHealth.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		else
			playerHealth.setDisplaySlot(null);
	}
	
	@Override
	public void registerGameOverListener(IGameOverListener listener)
	{
		gameOverListeners.add(listener);
	}
	
	@Override
	public Result preparePlayers()
	{
		this.players.clear();
		
    	if(settingDao.getValue(GameSetting.Teams) == 1)
    	{
    		teams = new ArrayList<Team>(scoreboard.getTeams());
        	int teamPlayerCount = 0;
    		    		
    		for(Team team : teams)
    		{
    			for(Player player : serverService.getPlayers(team.getEntries()))
    				this.players.put(player, team);
    			
    			teamPlayerCount += team.getEntries().size();
    		}
    		
    		if(teamPlayerCount != players.size())
    			return Result.failure("Not all players are in a team.");
    	}
    	else
    	{
    		for(Team team : teams)
    		{
    			for(String entry : team.getEntries())
    				team.removeEntry(entry);
    		}
    		
    		for(Player player : serverService.getPlayers())
    		{
    			Team team = scoreboard.getTeam(player.getName());
    			
    			if(team == null)
    				createTeam(player.getName(), ChatColor.WHITE, player.getName());
    			
    			team.addEntry(player.getName());
    			this.players.put(player, team);
    		}
    	}
    	
    	configureTeams();

    	spawnLocations = worldService.getSpawnLocations(teams);
    	
    	if(spawnLocations == null)
    		return Result.failure("Could not set spawn locations.");
    	    	
    	return Result.success("");
	}
	
	@Override
	public void BroadcastMessage(String message)
	{
		for(Player player : serverService.getPlayers())
			player.sendTitle(message, "", 0, 20, 0);
	}
	
	@Override
	public void showCountdown(int timeLeft)
	{
		for(Player player : serverService.getPlayers())
		{
			if(timeLeft == 1)
			{
				player.sendTitle(ChatColor.GREEN + "Starting UHC!", String.valueOf(timeLeft), 0, 20, 0);				
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1.0f, 1.189207f);
			}
			else
			{
				player.sendTitle(ChatColor.RED + "Starting UHC!", String.valueOf(timeLeft), 0, 20, 0);
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1.0f, 0.594604f);
			}
		}
	}
	
	@Override
	public void spawnPlayers() 
	{
		for (Map.Entry<Team, Location> teamSpawn : spawnLocations.entrySet()) 
		{
			for(String playerName : teamSpawn.getKey().getEntries())
			{	
				Player player = serverService.getPlayer(playerName);
				
				ResetPlayer(player, GameMode.SURVIVAL);
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, SpawnProtectionDuration * 20, 6, false, false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, SpawnProtectionDuration * 20, 6, false, false));
				player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(settingDao.getValue(GameSetting.MaxHealth) * 2);   	
				
				player.teleport(teamSpawn.getValue());				
			}
		}
	}
	
	@Override
    public void healPlayer(Player player, double healAmount)
    {
		double currentHealth = player.getHealth();
		double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		double newHealth = Math.min(currentHealth + healAmount, maxHealth);
		
		player.setHealth(newHealth);
    }
	
	@Override
	public Result revivePlayer(Player player)
	{
		ResetPlayer(player, GameMode.SURVIVAL);
		player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, SpawnProtectionDuration * 20, 6, false, false));
		
		double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		double reviveHealth = maxHealth * settingDao.getValue(GameSetting.ReviveHealth) / 100;
		player.setHealth(reviveHealth);
		
		Location deathLocation = deathLocations.remove(player);
		Location reviveLocation = FindClosestTeammateLocation(player, deathLocation);
		
		if(reviveLocation == null)
			reviveLocation = worldService.getRespawnLocation(deathLocation);
		
		player.teleport(reviveLocation);
		statisticsService.UpdatePlayerStatistics(player, PlayerEvent.Revival);
		return Result.success("Revived " + player.getName() + " at death location.");
	}
	
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) 
    {
        Player player = event.getPlayer();
     
        switch(gameStateDao.getGamePhase())
        {
	        case Lobby:
	        {
	        	teleportToLobby(player);
	        	break;
	        }
	        case InProcess:
	        {
	        	player.sendMessage(ChatColor.DARK_GREEN + "Welcome back " + player.getName() + "!");
	        	break;
	        }
	        default:
	        {
	        	break;
	        }
        }
    }
	
	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event)
	{
		plugin.getLogger().log(Level.INFO, "Test");
		
		event.getPlayer().sendPluginMessage(plugin, "uhc:tracking" , new byte[] { 1, 2, 3 });
	}
	
	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event) 
	{
	    if(event.getCause() != TeleportCause.NETHER_PORTAL)
	    	return;
	    
	    Location location = event.getFrom();
	    World destinationWorld = event.getTo().getWorld();
	    
	    double portalRatio = settingDao.getValue(GameSetting.PortalRatio);
	    
	    if(destinationWorld.getEnvironment() == Environment.NETHER)
	    	location.multiply(1.1/portalRatio);
	    else
	    	location.multiply(portalRatio);
	    
	    location.setWorld(destinationWorld);
	    event.setTo(location);
	    event.setSearchRadius(PortalSearchRadius);
	}
		
//    @EventHandler
//    public void PlayerItemConsumeEvent(Player player, ItemStack item)
//    {
//    	switch(item.getType())
//    	{    	
//	    	case GOLDEN_APPLE:
//	    	case ENCHANTED_GOLDEN_APPLE:
//	    	{
//	    		statisticsService.UpdatePlayerStatistics(player, PlayerEvent.GoldenAppleConsumption);
//	    		break;
//	    	}
//	    	default:
//	    	{
//	    		break;
//	    	}
//    	}
//    }
    
//    @EventHandler
//    public void EntityDamageByBlockEvent(Block damager, Entity damagee, DamageCause cause, double damage)
//    {
//    	if(damagee instanceof Player)
//    	{
//    		String damageData = cause + " from " + damager.getType().name() + ": " + damage /2;
//    		statisticsService.UpdatePlayerStatistics((Player)damagee, PlayerEvent.Pve, damageData);
//    	}
//    }
    
//    @EventHandler
//    public void EntityDamageByEntityEvent(Entity damager, Entity damagee, DamageCause cause, double damage)
//    {
//    	if(damagee instanceof Player)
//    	{
//    		String damageData = damager instanceof Player
//				? cause + " from " + ((Player)damager).getName() + ": " + damage /2
//				: cause + " from " + damager.getType().name() + ": " + damage /2;
//    	
//    		statisticsService.UpdatePlayerStatistics((Player)damagee, PlayerEvent.Pvp, damageData);
//    	}
//    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) 
    {
    	if(gameStateDao.getGamePhase() != GamePhase.InProcess)
    		return;
    	
    	Player slainPlayer = e.getEntity();
    	
    	Location deathLocation = slainPlayer.getLocation();
    	deathLocations.put(slainPlayer, deathLocation);
    	statisticsService.UpdatePlayerStatistics(slainPlayer, PlayerEvent.Death);
    	    	
    	for(Player player : serverService.getPlayers())
    		player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 1f, 1f);   	
    	    	
    	Bukkit.getScheduler().runTaskLater(plugin, () -> MakeSpectator(slainPlayer), 20);

    	Team winningTeam = GetWinningTeam(); 
    	
    	if(winningTeam != null)
    	{   		
    		for(Player player : serverService.getPlayers())
    		{
    			if(settingDao.getValue(GameSetting.Teams) == 1)
    			{
    				if(players.get(player) == winningTeam)
    					player.sendTitle(ChatColor.GREEN + "Victory!", ChatColor.GREEN + "Your team won this UHC", 0, 20 * 5, 0);
    				else
    					player.sendTitle(ChatColor.RED + "Defeat!", ChatColor.RED + winningTeam.getDisplayName() + " won this UHC", 0, 20 * 5, 0);    				
    			}
    			else
    			{
    				if(players.get(player) == winningTeam)
    					player.sendTitle(ChatColor.GREEN + "Victory!", ChatColor.GREEN + "You won this UHC", 0, 20 * 5, 0);
    				else
    					player.sendTitle(ChatColor.RED + "Defeat!", ChatColor.RED + winningTeam.getEntries().iterator().next() + " won this UHC", 0, 20 * 5, 0);
    			}
    				
    			player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.MASTER, 0.1f, 1f);   		
    		}
    		
    		gameStateDao.setValue(GameState.Phase, 2);
    		
    		for(IGameOverListener listener : gameOverListeners)
    			listener.onGameOver();
    	}
    }
    
    private void teleportToLobby(Player player) 
	{	
		ResetPlayer(player, GameMode.ADVENTURE);
		player.getInventory().addItem(new ItemStack(Material.SPYGLASS, 1));    	
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 9999999, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 9999999, 1, false, false));
        
        Location lobbyLocation = lobbyService.getLobbyLocation();
        
        player.teleport(lobbyLocation);
    	
    	player.sendMessage(ChatColor.DARK_GREEN + "Welcome to the UHC server " + player.getName() + "!");
    	player.sendMessage(ChatColor.DARK_GREEN + "Wait in the lobby for the game to start.");
	}
    
    private void createTeams()
    {
    	createTeam("black", ChatColor.BLACK, "Black");
    	createTeam("light_blue", ChatColor.BLUE, "Light Blue");
    	createTeam("cyan", ChatColor.DARK_AQUA, "Cyan");
    	createTeam("blue", ChatColor.DARK_BLUE, "Blue");
    	createTeam("gray", ChatColor.DARK_GRAY, "Gray");
    	createTeam("green", ChatColor.DARK_GREEN, "Green");
    	createTeam("purple", ChatColor.DARK_PURPLE, "Purple");
    	createTeam("red", ChatColor.DARK_RED, "Red");
    	createTeam("orange", ChatColor.GOLD, "Orange");
    	createTeam("light_gray", ChatColor.GRAY, "Light Gray");
    	createTeam("lime", ChatColor.GREEN, "Lime");
    	createTeam("magenta", ChatColor.LIGHT_PURPLE, "Magenta");
    	createTeam("pink", ChatColor.RED, "Pink");
    	createTeam("white", ChatColor.WHITE, "White");
    	createTeam("yellow", ChatColor.YELLOW, "Yellow");
    }
    
    private Team createTeam(String name, ChatColor color, String displayName)
    {
    	Team team = scoreboard.registerNewTeam(name);
        team.setColor(color);
        team.setDisplayName(displayName);
        return team;
    }
    
    private void configureTeams()
    {
    	for(Team team : scoreboard.getTeams())
    	{
    		team.setAllowFriendlyFire(settingDao.getValue(GameSetting.FriendlyFire) == 1);
    		    		
    		switch(settingDao.getValue(GameSetting.PlayerCollision))
    		{
	    		case 0:
	    		{
	    			team.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);	    			
	    			break;	
	    		}
	    		case 1:
	    		{
	    			team.setOption(Option.COLLISION_RULE, OptionStatus.FOR_OTHER_TEAMS);
	    			break;	
	    		}
	    		default:
	    		{
	    			team.setOption(Option.COLLISION_RULE, OptionStatus.ALWAYS);
	    			break;
	    		}
    		}
    		
    		if(settingDao.getValue(GameSetting.ShowNameTags) == 1)
    			team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
    		else
    			team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
    	}
    }
        
    private Location FindClosestTeammateLocation(Player player, Location deathLocation)
    {    	
    	for(Team team : spawnLocations.keySet())
		{
			Set<Player> teamMembers = serverService.getPlayers(team.getEntries());
			
			if(teamMembers.isEmpty())
				continue;

			Player closestTeammate = null;
			Location closestTeammateLocation = null;
			double closestTeammateDistance = Double.MAX_VALUE;
			
			for(Player teamMember : teamMembers)
			{
				if(teamMember.getGameMode() != GameMode.SURVIVAL)
					continue;
				
				Location teammateLocation = teamMember.getLocation();
				double teammateDistance = deathLocation.distance(teammateLocation);
				
				if(teammateDistance < closestTeammateDistance)
				{
					closestTeammate = teamMember;
					closestTeammateLocation = teammateLocation;
					closestTeammateDistance = teammateDistance;
				}
			}
			
			if(closestTeammate != null)
			{
				player.teleport(closestTeammateLocation);
				statisticsService.UpdatePlayerStatistics(player, PlayerEvent.Revival);
				return closestTeammateLocation;
			}
		}
    	
    	return null;
    }
    
    private void ResetPlayer(Player player, GameMode gameMode)
    {
    	player.setGameMode(gameMode);
        player.getInventory().clear();
        
        for (PotionEffect effect : player.getActivePotionEffects())
        	player.removePotionEffect(effect.getType());
    }
    
    private Team GetWinningTeam()
    {
    	Team winningTeam = null;
    	
    	for(Team team : spawnLocations.keySet())
    	{
    		for(Player player : serverService.getPlayers(team.getEntries()))
    		{
    			if(deathLocations.containsKey(player))
    				continue;
    			
    			if(winningTeam != null)
    				return null;
    			
    			winningTeam = team;
    			break;
    		}
    	}
    	
    	return winningTeam;
    }
    
    private void MakeSpectator(Player player)
    {
    	player.setGameMode(GameMode.SPECTATOR);
    	player.teleport(deathLocations.get(player));
    }
}
