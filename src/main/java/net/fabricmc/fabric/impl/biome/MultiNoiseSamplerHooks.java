package net.fabricmc.fabric.impl.biome;

import net.minecraft.util.math.noise.PerlinNoiseSampler;

public interface MultiNoiseSamplerHooks {
    PerlinNoiseSampler fabric_getEndBiomesSampler();

    void fabric_setSeed(long seed);
}
