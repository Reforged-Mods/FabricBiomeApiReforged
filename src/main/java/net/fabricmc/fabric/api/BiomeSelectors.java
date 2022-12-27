package net.fabricmc.fabric.api;

import com.google.common.collect.ImmutableSet;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;


/**
 * Provides several convenient biome selectors that can be used with {@link BiomeModifications}.
 *
 * <p><b>Experimental feature</b>, may be removed or changed without further notice.
 */
public final class BiomeSelectors {
    private BiomeSelectors() {
    }

    /**
     * Matches all Biomes. Use a more specific selector if possible.
     */
    public static Predicate<BiomeSelectionContext> all() {
        return context -> true;
    }

    /**
     * Matches Biomes that have not been originally defined in a data pack, but that are defined in code.
     */
    public static Predicate<BiomeSelectionContext> builtIn() {
        return context -> BuiltinRegistries.BIOME.containsKey(context.getBiomeKey().location());
    }

    /**
     * Returns a biome selector that will match all biomes from the minecraft namespace.
     */
    public static Predicate<BiomeSelectionContext> vanilla() {
        return context -> {
            // In addition to the namespace, we also check that it doesn't come from a data pack.
            return context.getBiomeKey().location().getNamespace().equals("minecraft")
                    && BuiltinRegistries.BIOME.containsKey(context.getBiomeKey().location());
        };
    }

    /**
     * Returns a biome selector that will match all biomes that would normally spawn in the Overworld,
     * assuming Vanilla's default biome source is used.
     */
    public static Predicate<BiomeSelectionContext> foundInOverworld() {
        return context -> context.canGenerateIn(LevelStem.OVERWORLD);
    }

    /**
     * Returns a biome selector that will match all biomes that would normally spawn in the Nether,
     * assuming Vanilla's default multi noise biome source with the nether preset is used.
     *
     * <p>This selector will also match modded biomes that have been added to the nether using {@link NetherBiomes}.
     */
    public static Predicate<BiomeSelectionContext> foundInTheNether() {
        return context -> context.canGenerateIn(LevelStem.NETHER);
    }

    /**
     * Returns a biome selector that will match all biomes that would normally spawn in the End,
     * assuming Vanilla's default End biome source is used.
     */
    public static Predicate<BiomeSelectionContext> foundInTheEnd() {
        return context -> context.canGenerateIn(LevelStem.END);
    }

    /**
     * Returns a biome selector that will match all biomes in the given tag.
     *
     * @see net.minecraft.tags.BiomeTags
     */
    public static Predicate<BiomeSelectionContext> tag(TagKey<Biome> tag) {
        return context -> context.hasTag(tag);
    }

    /**
     * @see #excludeByKey(Collection)
     */
    @SafeVarargs
    public static Predicate<BiomeSelectionContext> excludeByKey(ResourceKey<Biome>... keys) {
        return excludeByKey(ImmutableSet.copyOf(keys));
    }

    /**
     * Returns a selector that will reject any biome whose key is in the given collection of keys.
     *
     * <p>This is useful for allowing a list of biomes to be defined in the config file, where
     * a certain feature should not spawn.
     */
    public static Predicate<BiomeSelectionContext> excludeByKey(Collection<ResourceKey<Biome>> keys) {
        return context -> !keys.contains(context.getBiomeKey());
    }

    /**
     * @see #includeByKey(Collection)
     */
    @SafeVarargs
    public static Predicate<BiomeSelectionContext> includeByKey(ResourceKey<Biome>... keys) {
        return includeByKey(ImmutableSet.copyOf(keys));
    }

    /**
     * Returns a selector that will accept only biomes whose keys are in the given collection of keys.
     *
     * <p>This is useful for allowing a list of biomes to be defined in the config file, where
     * a certain feature should spawn exclusively.
     */
    public static Predicate<BiomeSelectionContext> includeByKey(Collection<ResourceKey<Biome>> keys) {
        return context -> keys.contains(context.getBiomeKey());
    }

    /**
     * Returns a biome selector that will match biomes in which one of the given entity types can spawn.
     *
     * <p>Matches spawns in all {@link MobCategory spawn groups}.
     */
    public static Predicate<BiomeSelectionContext> spawnsOneOf(EntityType<?>... entityTypes) {
        return spawnsOneOf(ImmutableSet.copyOf(entityTypes));
    }

    /**
     * Returns a biome selector that will match biomes in which one of the given entity types can spawn.
     *
     * <p>Matches spawns in all {@link MobCategory spawn groups}.
     */
    public static Predicate<BiomeSelectionContext> spawnsOneOf(Set<EntityType<?>> entityTypes) {
        return context -> {
            MobSpawnSettings spawnSettings = context.getBiome().getMobSettings();

            for (MobCategory spawnGroup : MobCategory.values()) {
                for (MobSpawnSettings.SpawnerData spawnEntry : spawnSettings.getMobs(spawnGroup).unwrap()) {
                    if (entityTypes.contains(spawnEntry.type)) {
                        return true;
                    }
                }
            }

            return false;
        };
    }
}
