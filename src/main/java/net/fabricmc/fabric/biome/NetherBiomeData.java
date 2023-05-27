package net.fabricmc.fabric.biome;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Internal data for modding Vanilla's {@link MultiNoiseBiomeSource.Preset#NETHER}.
 */
@ApiStatus.Internal
public final class NetherBiomeData {
    // Cached sets of the biomes that would generate from Vanilla's default biome source without consideration
    // for data packs (as those would be distinct biome sources).
    private static final Set<ResourceKey<Biome>> NETHER_BIOMES = new HashSet<>();

    private static final Map<ResourceKey<Biome>, Climate.ParameterPoint> NETHER_BIOME_NOISE_POINTS = new HashMap<>();

    private static final Logger LOGGER = LogUtils.getLogger();

    private NetherBiomeData() {
    }

    public static void addNetherBiome(ResourceKey<Biome> biome, Climate.ParameterPoint spawnNoisePoint) {
        Preconditions.checkArgument(biome != null, "Biome is null");
        Preconditions.checkArgument(spawnNoisePoint != null, "MultiNoiseUtil.NoiseValuePoint is null");
        NETHER_BIOME_NOISE_POINTS.put(biome, spawnNoisePoint);
        clearBiomeSourceCache();
    }

    public static Map<ResourceKey<Biome>, Climate.ParameterPoint> getNetherBiomeNoisePoints() {
        return NETHER_BIOME_NOISE_POINTS;
    }

    public static boolean canGenerateInNether(ResourceKey<Biome> biome) {
        if (NETHER_BIOMES.isEmpty()) {
            MultiNoiseBiomeSource source = MultiNoiseBiomeSource.Preset.NETHER.biomeSource(BuiltinRegistries.BIOME);

            for (Holder<Biome> entry : source.possibleBiomes()) {
                BuiltinRegistries.BIOME.getResourceKey(entry.value()).ifPresent(NETHER_BIOMES::add);
            }
        }

        return NETHER_BIOMES.contains(biome) || NETHER_BIOME_NOISE_POINTS.containsKey(biome);
    }

    private static void clearBiomeSourceCache() {
        NETHER_BIOMES.clear(); // Clear cached biome source data
    }

    private static Climate.ParameterList<Holder<Biome>> withModdedBiomeEntries(Climate.ParameterList<Holder<Biome>> entries, Registry<Biome> biomeRegistry) {
        if (NETHER_BIOME_NOISE_POINTS.isEmpty()) {
            return entries;
        }

        ArrayList<Pair<Climate.ParameterPoint, Holder<Biome>>> entryList = new ArrayList<>(entries.values());

        for (Map.Entry<ResourceKey<Biome>, Climate.ParameterPoint> entry : NETHER_BIOME_NOISE_POINTS.entrySet()) {
            if (biomeRegistry.containsKey(entry.getKey())) {
                entryList.add(Pair.of(entry.getValue(), biomeRegistry.getHolderOrThrow(entry.getKey())));
            } else {
                LOGGER.warn("Nether biome {} not loaded", entry.getKey().location());
            }
        }

        return new Climate.ParameterList<>(entryList);
    }

    public static void modifyBiomeSource(Registry<Biome> biomeRegistry, BiomeSource biomeSource) {
        if (biomeSource instanceof MultiNoiseBiomeSource multiNoiseBiomeSource) {
            if (((BiomeSourceAccess) multiNoiseBiomeSource).fabric_shouldModifyBiomeEntries() && multiNoiseBiomeSource.stable(MultiNoiseBiomeSource.Preset.NETHER)) {
                Climate.ParameterList<Holder<Biome>> parameterList = multiNoiseBiomeSource.parameters;
                parameterList = NetherBiomeData.withModdedBiomeEntries(
                        MultiNoiseBiomeSource.Preset.NETHER.parameterSource.apply(biomeRegistry),
                        biomeRegistry);
                Set<Holder<Biome>> holders = multiNoiseBiomeSource.possibleBiomes();
                holders = multiNoiseBiomeSource.parameters.values().stream().map(Pair::getSecond).collect(Collectors.toSet());
                ((BiomeSourceAccess) multiNoiseBiomeSource).fabric_setModifyBiomeEntries(false);
            }
        }
    }
}