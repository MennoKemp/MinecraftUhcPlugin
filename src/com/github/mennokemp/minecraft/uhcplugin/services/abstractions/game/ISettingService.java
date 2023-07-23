package com.github.mennokemp.minecraft.uhcplugin.services.abstractions.game;

import com.github.mennokemp.minecraft.uhcplugin.domain.game.GameSetting;

public interface ISettingService
{
	int getSetting(GameSetting gameSetting);
	
	void setSetting(GameSetting gameSetting, int value);
}
