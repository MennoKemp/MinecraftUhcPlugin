package com.github.mennokemp.uhcplugin.persistence.abstractions;

import org.bukkit.scoreboard.DisplaySlot;

public interface IScoreboardDao<T>
{
	public int getValue(T valueType);
	
	public void setValue(T valueType, int value);
	
	public void clear();
	
	public void setDisplaySlot(DisplaySlot displaySlot);
}
