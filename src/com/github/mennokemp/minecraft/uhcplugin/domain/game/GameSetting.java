package com.github.mennokemp.minecraft.uhcplugin.domain.game;

public enum GameSetting 
{
	//	Set to 1 to show advancements to other players.
	Advancements,
	//	Set to 1 to allow players to spawn in biomes defined in the challenging biomes list.
	ChallengingBiomes,
	//	Set the time in minutes when eternal day must start.
	//	Use a negative number to never enable eternal day.
	EternalDayStart,
	FloodHeight,
	FloodInitialLevel,
	FloodInterval,
	FloodStart,
	FloodLava,
	//	Set to 1 to enable freezing damage in powered snow.
	Freezing,
	// 
	FriendlyFire,
	MaxHealth,
	NaturalRegeneration,
	//	0 - No collision.
	//	1 - Collide with enemies.
	//	2 - Collide with friendlies.
	PlayerCollision,
	PortalRatio,
	ShowHealth,
	ShowNameTags,
	//	World border damage in hearts / (blocks * minute). 
	ShrinkDeathBoost,
	ShrinkIncrements,
	ShrinkStart,
	ShrinkEnd,
	ReviveHealth,
	Teams,
	Weather,
	WorldBorderBuffer,
	WorldBorderFinalRadius,
	WorldBorderInitialRadius
}
