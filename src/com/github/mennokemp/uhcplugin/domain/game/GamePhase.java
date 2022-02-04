package com.github.mennokemp.uhcplugin.domain.game;

import java.util.HashMap;
import java.util.Map;

public enum GamePhase 
{
	Lobby(0),
	InProcess(1),
	PostGame(2);
	
    private int value;
    private static Map<Integer, GamePhase> map = new HashMap<Integer, GamePhase>();

    private GamePhase(int value) 
    {
        this.value = value;
    }

    static 
    {
        for (GamePhase gamePhase : GamePhase.values()) 
        	map.put(gamePhase.value, gamePhase);
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