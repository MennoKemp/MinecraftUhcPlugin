package com.github.mennokemp.minecraft.uhcplugin.persistence.abstractions;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameSetting;

public interface ISettingDao extends IScoreboardDao<GameSetting>
{
	Result validateSettings();
	
	void resetSettings();
}
