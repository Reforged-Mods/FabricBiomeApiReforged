package net.fabricmc.fabric.api;

import com.google.common.base.Preconditions;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.function.Predicate;

/**
 * Provides an API to modify Biomes after they have been loaded and before they are used in the World.
 *
 * <p>Any modifications made to biomes will not be available for use in server.properties (as of 1.16.1),
 * or the demo level.
 *
 * <p><b>Experimental feature</b>, may be removed or changed without further notice.
 */
public final class BiomeModifications {
    private BiomeModifications() {
    }

    /**
     * Convenience method to add a feature to one or more biomes.
     *
     * @see BiomeSelectors
     */
    public static void addFeature(Predicate<BiomeSelectionContext> biomeSelector, GenerationStep.Decoration step, ResourceKey<PlacedFeature> placedFeatureRegistryKey) {
        create(placedFeatureRegistryKey.location()).add(ModificationPhase.ADDITIONS, biomeSelector, context -> {
            context.getGenerationSettings().addFeature(step, placedFeatureRegistryKey);
        });
    }

    /**
     * Convenience method to add a carver to one or more biomes.
     *
     * @see BiomeSelectors
     */
    public static void addCarver(Predicate<BiomeSelectionContext> biomeSelector, GenerationStep.Carving step, ResourceKey<ConfiguredWorldCarver<?>> configuredCarverKey) {
        create(configuredCarverKey.location()).add(ModificationPhase.ADDITIONS, biomeSelector, context -> {
            context.getGenerationSettings().addCarver(step, configuredCarverKey);
        });
    }

    /**
     * Convenience method to add an entity spawn to one or more biomes.
     *
     * @see BiomeSelectors
     * @see net.minecraft.world.level.biome.MobSpawnSettings.Builder#addSpawn(MobCategory, MobSpawnSettings.SpawnerData)
     */
    public static void addSpawn(Predicate<BiomeSelectionContext> biomeSelector,
                                MobCategory spawnGroup, EntityType<?> entityType,
                                int weight, int minGroupSize, int maxGroupSize) {
        // See constructor of SpawnSettings.SpawnEntry for context
        Preconditions.checkArgument(entityType.getCategory() != MobCategory.MISC,
                "Cannot add spawns for entities with spawnGroup=MISC since they'd be replaced by pigs.");

        // We need the entity type to be registered, or we cannot deduce an ID otherwise
        ResourceLocation id = Registry.ENTITY_TYPE.getKey(entityType);
        Preconditions.checkState(id != Registry.ENTITY_TYPE.getDefaultKey(), "Unregistered entity type: %s", entityType);

        create(id).add(ModificationPhase.ADDITIONS, biomeSelector, context -> {
            context.getSpawnSettings().addSpawn(spawnGroup, new MobSpawnSettings.SpawnerData(entityType, weight, minGroupSize, maxGroupSize));
        });
    }

    /**
     * Create a new biome modification which will be applied whenever biomes are loaded from data packs.
     *
     * @param id An identifier for the new set of biome modifications that is returned. Is used for
     *           guaranteeing consistent ordering between the biome modifications added by different mods
     *           (assuming they otherwise have the same phase).
     */
    public static BiomeModification create(ResourceLocation id) {
        return new BiomeModification(id);
    }
}
