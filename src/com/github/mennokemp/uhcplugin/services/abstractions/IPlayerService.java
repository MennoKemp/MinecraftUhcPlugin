package com.github.mennokemp.uhcplugin.services.abstractions;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.github.mennokemp.uhcplugin.helpers.Result;

public interface IPlayerService extends ISetupService, Listener
{
	public void setPlayerHealthVisibility(boolean visible);
	
	public void registerGameOverListener(IGameOverListener listener);
	
	public Result preparePlayers();
	
	public void showCountdown(int timeLeft);
	
	public void spawnPlayers();
	
	public void healPlayer(Player player, double healAmount);
	
	public Result revivePlayer(Player player);
}
