package com.github.mennokemp.uhcplugin.persistence.abstractions;

import com.github.mennokemp.uhcplugin.domain.game.GameStatus;

public interface IGameStatusDao extends IScoreboardDao<GameStatus>
{
	public void updateStatus();
}
