package com.github.mennokemp.minecraft.uhcplugin.domain.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum GamePhase
{
	Lobby(0),
	Preparation(1),
	InProcess(2),
	PostGame(3);

	private int value;
	private static Map<Integer, GamePhase> map = new HashMap<Integer, GamePhase>();

	private GamePhase(int value)
	{
		this.value = value;
	}

	static
	{
		Arrays.stream(GamePhase.values()).forEach(p -> map.put(p.value, p));
	}

	public static GamePhase fromInt(int gamePhase)
	{
		return (GamePhase)map.get(gamePhase);
	}

	public int getValue()
	{
		return value;
	}
}