package net.fabricmc.fabric.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.yeoxuhang.biomeapireforged.impl.biome.NetherBiomeData;

/**
 * API that exposes the internals of Minecraft's nether biome code.
 *
 * <p><b>Experimental feature</b>, may be removed or changed without further notice.
 */
public final class NetherBiomes {
    private NetherBiomes() {
    }

    /**
     * Adds a biome to the Nether generator.
     *
     * @param biome           The biome to add. Must not be null.
     * @param mixedNoisePoint data about the given {@link Biome}'s spawning information in the nether.
     * @see Climate.TargetPoint
     */
    public static void addNetherBiome(ResourceKey<Biome> biome, Climate.TargetPoint mixedNoisePoint) {
        NetherBiomeData.addNetherBiome(biome, Climate.parameters(
                mixedNoisePoint.temperature(),
                mixedNoisePoint.humidity(),
                mixedNoisePoint.continentalness(),
                mixedNoisePoint.erosion(),
                mixedNoisePoint.depth(),
                mixedNoisePoint.weirdness(),
                0
        ));
    }

    /**
     * Adds a biome to the Nether generator.
     *
     * @param biome           The biome to add. Must not be null.
     * @param mixedNoisePoint data about the given {@link Biome}'s spawning information in the nether.
     * @see Climate.ParameterPoint
     */
    public static void addNetherBiome(ResourceKey<Biome> biome, Climate.ParameterPoint mixedNoisePoint) {
        NetherBiomeData.addNetherBiome(biome, mixedNoisePoint);
    }

    /**
     * Returns true if the given biome can generate in the nether, considering the Vanilla nether biomes,
     * and any biomes added to the Nether by mods.
     */
    public static boolean canGenerateInNether(ResourceKey<Biome> biome) {
        return NetherBiomeData.canGenerateInNether(biome);
    }
}
