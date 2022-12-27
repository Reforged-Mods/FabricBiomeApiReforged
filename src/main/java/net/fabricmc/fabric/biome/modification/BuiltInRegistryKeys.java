package net.fabricmc.fabric.biome.modification;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.ApiStatus;

/**
 * Utility class for getting the registry keys of built-in worldgen objects and throwing proper exceptions if they
 * are not registered.
 */
@ApiStatus.Internal
public final class BuiltInRegistryKeys {
    private BuiltInRegistryKeys() {
    }

    public static ResourceKey<Structure> get(Structure structureFeature) {
        return BuiltinRegistries.STRUCTURES.getResourceKey(structureFeature)
                .orElseThrow(() -> new IllegalArgumentException("Given structure is not built-in: " + structureFeature));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> get(ConfiguredFeature<?, ?> configuredFeature) {
        return BuiltinRegistries.CONFIGURED_FEATURE.getResourceKey(configuredFeature)
                .orElseThrow(() -> new IllegalArgumentException("Given configured feature is not built-in: " + configuredFeature));
    }

    public static ResourceKey<PlacedFeature> get(PlacedFeature placedFeature) {
        return BuiltinRegistries.PLACED_FEATURE.getResourceKey(placedFeature)
                .orElseThrow(() -> new IllegalArgumentException("Given placed feature is not built-in: " + placedFeature));
    }

    public static ResourceKey<ConfiguredWorldCarver<?>> get(ConfiguredWorldCarver<?> configuredCarver) {
        return BuiltinRegistries.CONFIGURED_CARVER.getResourceKey(configuredCarver)
                .orElseThrow(() -> new IllegalArgumentException("Given configured carver is not built-in: " + configuredCarver));
    }
}
