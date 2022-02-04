package com.github.mennokemp.uhcplugin.persistence.abstractions;

import com.github.mennokemp.uhcplugin.domain.game.GameSetting;
import com.github.mennokemp.uhcplugin.helpers.Result;

public interface ISettingDao extends IScoreboardDao<GameSetting>
{
	public Result validateSettings();
	
	public void resetSettings();
}
