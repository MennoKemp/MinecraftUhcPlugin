package com.github.mennokemp.minecraft.uhcplugin.domain.world;

import java.util.stream.Stream;

import org.bukkit.block.Biome;

public class ChallengingBiomes
{
	public static Stream<Biome> getChallengingBiomes()
	{
		return Stream.of(
			Biome.BEACH, 
			Biome.DESERT, 
			Biome.FROZEN_PEAKS,
			Biome.FROZEN_RIVER, 
			Biome.GROVE, 
			Biome.ICE_SPIKES, 
			Biome.JAGGED_PEAKS, 
			Biome.SNOWY_BEACH,
			Biome.SNOWY_PLAINS, 
			Biome.SNOWY_SLOPES, 
			Biome.TAIGA, 
			Biome.STONY_PEAKS, 
			Biome.STONY_SHORE);
	}
	
	public static Stream<Biome> getOceanBiomes()
	{
		return Stream.of(
			Biome.COLD_OCEAN, 
			Biome.DEEP_COLD_OCEAN,
			Biome.DEEP_FROZEN_OCEAN, 
			Biome.DEEP_LUKEWARM_OCEAN, 
			Biome.DEEP_OCEAN, 
			Biome.FROZEN_OCEAN,
			Biome.LUKEWARM_OCEAN, 
			Biome.MUSHROOM_FIELDS, 
			Biome.OCEAN, 
			Biome.WARM_OCEAN);
	}
}
