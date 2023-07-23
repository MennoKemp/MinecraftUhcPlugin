package com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions;

import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameInfo;

public interface IGameInfoDao extends IScoreboardDao<GameInfo>
{
	public void updateGameInfo();
}
