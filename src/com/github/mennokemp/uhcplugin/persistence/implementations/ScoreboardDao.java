package com.github.mennokemp.uhcplugin.persistence.implementations;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.uhcplugin.persistence.abstractions.IScoreboardDao;

public abstract class ScoreboardDao<T> implements IScoreboardDao<T>
{
	private final Scoreboard scoreboard;
	
	private Objective objective;
	
	public ScoreboardDao(Scoreboard scoreboard) 
	{
		this.scoreboard = scoreboard;
		
		String objectiveName = getObjectiveName();
		objective = scoreboard.getObjective(objectiveName);
		
		if(objective == null)
			createObjective(objectiveName);
	}
	
	@Override
	public int getValue(T valueType)
	{
		return objective.getScore(valueType.toString()).getScore();
	}
	
	@Override
	public void setValue(T valueType, int value)
	{
		objective.getScore(valueType.toString()).setScore(value);
	}
	
	@Override
	public void clear()
	{
		scoreboard.resetScores(getObjectiveName());
	}
	
	@Override
	public void setDisplaySlot(DisplaySlot displaySlot)
	{
		objective.setDisplaySlot(displaySlot);
	}
	
	protected abstract String getObjectiveName();
	
	protected abstract String getObjectiveDisplayName();
	
	protected Objective getObjective()
	{
		return objective;
	}
	
	private void createObjective(String objectiveName)
	{
		objective = scoreboard.registerNewObjective(objectiveName, "dummy", getObjectiveDisplayName(), RenderType.INTEGER);    	
	}
}
