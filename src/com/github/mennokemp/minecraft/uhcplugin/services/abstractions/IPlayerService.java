package com.github.mennokemp.minecraft.uhcplugin.services.abstractions;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game.IGameOverListener;

public interface IPlayerService extends Listener
{
	void setPlayerHealthVisibility(boolean visible);
	
	void registerGameOverListener(IGameOverListener listener);
	
	Result preparePlayers();
	
	void BroadcastMessage(String message);
	
	void showCountdown(int timeLeft);
	
	void spawnPlayers();
	
	void healPlayer(Player player, double healAmount);
	
	Result revivePlayer(Player player);
}
