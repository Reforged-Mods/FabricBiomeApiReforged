package net.fabricmc.fabric.api;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.yeoxuhang.biomeapireforged.impl.biome.modification.BuiltInRegistryKeys;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiPredicate;


/**
 * Allows {@link Biome} properties to be modified.
 *
 * <p><b>Experimental feature</b>, may be removed or changed without further notice.
 */
public interface BiomeModificationContext {
    /**
     * Returns the modification context for the biomes weather properties.
     */
    WeatherContext getWeather();

    /**
     * Returns the modification context for the biomes effects.
     */
    EffectsContext getEffects();

    /**
     * Returns the modification context for the biomes generation settings.
     */
    GenerationSettingsContext getGenerationSettings();

    /**
     * Returns the modification context for the biomes spawn settings.
     */
    SpawnSettingsContext getSpawnSettings();

    interface WeatherContext {
        /**
         * @see Biome#getPrecipitation()
         * @see Biome.BiomeBuilder#precipitation(Biome.Precipitation)
         */
        void setPrecipitation(Biome.Precipitation precipitation);

        /**
         * @see Biome#getBaseTemperature()
         * @see Biome.BiomeBuilder#temperature(float)
         */
        void setTemperature(float temperature);

        /**
         * @see Biome.BiomeBuilder#temperatureAdjustment(Biome.TemperatureModifier)
         */
        void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier);

        /**
         * @see Biome#getDownfall()
         * @see Biome.BiomeBuilder#downfall(float)
         */
        void setDownfall(float downfall);
    }

    interface EffectsContext {
        /**
         * @see BiomeSpecialEffects#getFogColor()
         * @see BiomeSpecialEffects.Builder#fogColor(int)
         */
        void setFogColor(int color);

        /**
         * @see BiomeSpecialEffects#getWaterColor()
         * @see BiomeSpecialEffects.Builder#waterColor(int)
         */
        void setWaterColor(int color);

        /**
         * @see BiomeSpecialEffects#getWaterFogColor()
         * @see BiomeSpecialEffects.Builder#waterFogColor(int)
         */
        void setWaterFogColor(int color);

        /**
         * @see BiomeSpecialEffects#getSkyColor()
         * @see BiomeSpecialEffects.Builder#skyColor(int)
         */
        void setSkyColor(int color);

        /**
         * @see BiomeSpecialEffects#getFoliageColorOverride()
         * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
         */
        void setFoliageColor(Optional<Integer> color);

        /**
         * @see BiomeSpecialEffects#getFoliageColorOverride()
         * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
         */
        default void setFoliageColor(int color) {
            setFoliageColor(Optional.of(color));
        }

        /**
         * @see BiomeSpecialEffects#getFoliageColorOverride()
         * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
         */
        default void clearFoliageColor() {
            setFoliageColor(Optional.empty());
        }

        /**
         * @see BiomeSpecialEffects#getGrassColorOverride()
         * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
         */
        void setGrassColor(Optional<Integer> color);

        /**
         * @see BiomeSpecialEffects#getGrassColorOverride()
         * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
         */
        default void setGrassColor(int color) {
            setGrassColor(Optional.of(color));
        }

        /**
         * @see BiomeSpecialEffects#getGrassColorOverride()
         * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
         */
        default void clearGrassColor() {
            setGrassColor(Optional.empty());
        }

        /**
         * @see BiomeSpecialEffects#getGrassColorModifier()
         * @see BiomeSpecialEffects.Builder#grassColorModifier(BiomeSpecialEffects.GrassColorModifier)
         */
        void setGrassColorModifier(@NotNull BiomeSpecialEffects.GrassColorModifier colorModifier);

        /**
         * @see BiomeSpecialEffects#getAmbientParticleSettings()
         * @see BiomeSpecialEffects.Builder#ambientParticle(AmbientParticleSettings)
         */
        void setParticleConfig(Optional<AmbientParticleSettings> particleConfig);

        /**
         * @see BiomeSpecialEffects#getAmbientParticleSettings()
         * @see BiomeSpecialEffects.Builder#ambientParticle(AmbientParticleSettings)
         */
        default void setParticleConfig(@NotNull AmbientParticleSettings particleConfig) {
            setParticleConfig(Optional.of(particleConfig));
        }

        /**
         * @see BiomeSpecialEffects#getAmbientParticleSettings()
         * @see BiomeSpecialEffects.Builder#ambientParticle(AmbientParticleSettings)
         */
        default void clearParticleConfig() {
            setParticleConfig(Optional.empty());
        }

        /**
         * @see BiomeSpecialEffects#getAmbientLoopSoundEvent()
         * @see BiomeSpecialEffects.Builder#ambientLoopSound(SoundEvent)
         */
        void setAmbientSound(Optional<SoundEvent> sound);

        /**
         * @see BiomeSpecialEffects#getAmbientLoopSoundEvent()
         * @see BiomeSpecialEffects.Builder#ambientLoopSound(SoundEvent)
         */
        default void setAmbientSound(@NotNull SoundEvent sound) {
            setAmbientSound(Optional.of(sound));
        }

        /**
         * @see BiomeSpecialEffects#getAmbientLoopSoundEvent()
         * @see BiomeSpecialEffects.Builder#ambientLoopSound(SoundEvent)
         */
        default void clearAmbientSound() {
            setAmbientSound(Optional.empty());
        }

        /**
         * @see BiomeSpecialEffects#getAmbientMoodSettings()
         * @see BiomeSpecialEffects.Builder#ambientMoodSound(AmbientMoodSettings)
         */
        void setMoodSound(Optional<AmbientMoodSettings> sound);

        /**
         * @see BiomeSpecialEffects#getAmbientMoodSettings()
         * @see BiomeSpecialEffects.Builder#ambientMoodSound(AmbientMoodSettings)
         */
        default void setMoodSound(@NotNull AmbientMoodSettings sound) {
            setMoodSound(Optional.of(sound));
        }

        /**
         * @see BiomeSpecialEffects#getAmbientMoodSettings()
         * @see BiomeSpecialEffects.Builder#ambientMoodSound(AmbientMoodSettings)
         */
        default void clearMoodSound() {
            setMoodSound(Optional.empty());
        }

        /**
         * @see BiomeSpecialEffects#getAmbientAdditionsSettings()
         * @see BiomeSpecialEffects.Builder#ambientAdditionsSound(AmbientAdditionsSettings)
         */
        void setAdditionsSound(Optional<AmbientAdditionsSettings> sound);

        /**
         * @see BiomeSpecialEffects#getAmbientAdditionsSettings()
         * @see BiomeSpecialEffects.Builder#ambientAdditionsSound(AmbientAdditionsSettings)
         */
        default void setAdditionsSound(@NotNull AmbientAdditionsSettings sound) {
            setAdditionsSound(Optional.of(sound));
        }

        /**
         * @see BiomeSpecialEffects#getAmbientAdditionsSettings()
         * @see BiomeSpecialEffects.Builder#ambientAdditionsSound(AmbientAdditionsSettings)
         */
        default void clearAdditionsSound() {
            setAdditionsSound(Optional.empty());
        }

        /**
         * @see BiomeSpecialEffects#getBackgroundMusic()
         * @see BiomeSpecialEffects.Builder#backgroundMusic(Music)
         */
        void setMusic(Optional<Music> sound);

        /**
         * @see BiomeSpecialEffects#getBackgroundMusic()
         * @see BiomeSpecialEffects.Builder#backgroundMusic(Music)
         */
        default void setMusic(@NotNull Music sound) {
            setMusic(Optional.of(sound));
        }

        /**
         * @see BiomeSpecialEffects#getBackgroundMusic()
         * @see BiomeSpecialEffects.Builder#backgroundMusic(Music)
         */
        default void clearMusic() {
            setMusic(Optional.empty());
        }
    }

    interface GenerationSettingsContext {
        /**
         * Removes a feature from one of this biomes generation steps, and returns if any features were removed.
         */
        boolean removeFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> placedFeatureKey);

        /**
         * Removes a feature from all of this biomes generation steps, and returns if any features were removed.
         */
        default boolean removeFeature(ResourceKey<PlacedFeature> placedFeatureKey) {
            boolean anyFound = false;

            for (GenerationStep.Decoration step : GenerationStep.Decoration.values()) {
                if (removeFeature(step, placedFeatureKey)) {
                    anyFound = true;
                }
            }

            return anyFound;
        }

        /**
         * {@link #removeFeature(ResourceKey)} for built-in features (see {@link #addBuiltInFeature(GenerationStep.Decoration, PlacedFeature)}).
         */
        default boolean removeBuiltInFeature(PlacedFeature placedFeature) {
            return removeFeature(BuiltInRegistryKeys.get(placedFeature));
        }

        /**
         * {@link #removeFeature(GenerationStep.Decoration, ResourceKey)} for built-in features (see {@link #addBuiltInFeature(GenerationStep.Decoration, PlacedFeature)}).
         */
        default boolean removeBuiltInFeature(GenerationStep.Decoration step, PlacedFeature placedFeature) {
            return removeFeature(step, BuiltInRegistryKeys.get(placedFeature));
        }

        /**
         * Adds a feature to one of this biomes generation steps, identified by the placed feature's registry key.
         *
         * @see BuiltinRegistries#PLACED_FEATURE
         */
        void addFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> placedFeatureKey);

        /**
         * Adds a placed feature from {@link BuiltinRegistries#PLACED_FEATURE} to this biome.
         *
         * <p>This method is intended for use with the placed features found in
         * classes such as {@link net.minecraft.data.worldgen.placement.OrePlacements}.
         *
         * <p><b>NOTE:</b> In case the placed feature is overridden in a data pack, the data pack's version
         * will be used.
         */
        default void addBuiltInFeature(GenerationStep.Decoration step, PlacedFeature placedFeature) {
            addFeature(step, BuiltInRegistryKeys.get(placedFeature));
        }

        /**
         * Adds a configured carver to one of this biomes generation steps.
         */
        void addCarver(GenerationStep.Carving step, ResourceKey<ConfiguredWorldCarver<?>> carverKey);

        /**
         * Adds a configured carver from {@link BuiltinRegistries#CONFIGURED_CARVER} to this biome.
         *
         * <p>This method is intended for use with the configured carvers found in {@link net.minecraft.data.worldgen.Carvers}.
         *
         * <p><b>NOTE:</b> In case the configured carver is overridden in a data pack, the data pack's version
         * will be used.
         */
        default void addBuiltInCarver(GenerationStep.Carving step, ConfiguredWorldCarver<?> configuredCarver) {
            addCarver(step, BuiltInRegistryKeys.get(configuredCarver));
        }

        /**
         * Removes all carvers with the given key from one of this biomes generation steps.
         *
         * @return True if any carvers were removed.
         */
        boolean removeCarver(GenerationStep.Carving step, ResourceKey<ConfiguredWorldCarver<?>> configuredCarverKey);

        /**
         * Removes all carvers with the given key from all of this biomes generation steps.
         *
         * @return True if any carvers were removed.
         */
        default boolean removeCarver(ResourceKey<ConfiguredWorldCarver<?>> configuredCarverKey) {
            boolean anyFound = false;

            for (GenerationStep.Carving step : GenerationStep.Carving.values()) {
                if (removeCarver(step, configuredCarverKey)) {
                    anyFound = true;
                }
            }

            return anyFound;
        }

        /**
         * {@link #removeCarver(ResourceKey)} for built-in carvers (see {@link #addBuiltInCarver(GenerationStep.Carving, ConfiguredWorldCarver)}).
         */
        default boolean removeBuiltInCarver(ConfiguredWorldCarver<?> configuredCarver) {
            return removeCarver(BuiltInRegistryKeys.get(configuredCarver));
        }

        /**
         * {@link #removeCarver(GenerationStep.Carving, ResourceKey)} for built-in carvers (see {@link #addBuiltInCarver(GenerationStep.Carving, ConfiguredWorldCarver)}).
         */
        default boolean removeBuiltInCarver(GenerationStep.Carving step, ConfiguredWorldCarver<?> configuredCarver) {
            return removeCarver(step, BuiltInRegistryKeys.get(configuredCarver));
        }
    }

    interface SpawnSettingsContext {
        /**
         * Associated JSON property: <code>creature_spawn_probability</code>.
         *
         * @see MobSpawnSettings#getCreatureProbability()
         * @see MobSpawnSettings.Builder#creatureGenerationProbability(float)
         */
        void setCreatureSpawnProbability(float probability);

        /**
         * Associated JSON property: <code>spawners</code>.
         *
         * @see MobSpawnSettings#getMobs(MobCategory)
         * @see MobSpawnSettings.Builder#addSpawn(MobCategory, MobSpawnSettings.SpawnerData)
         */
        void addSpawn(MobCategory spawnGroup, MobSpawnSettings.SpawnerData spawnEntry);

        /**
         * Removes any spawns matching the given predicate from this biome, and returns true if any matched.
         *
         * <p>Associated JSON property: <code>spawners</code>.
         */
        boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> predicate);

        /**
         * Removes all spawns of the given entity type.
         *
         * <p>Associated JSON property: <code>spawners</code>.
         *
         * @return True if any spawns were removed.
         */
        default boolean removeSpawnsOfEntityType(EntityType<?> entityType) {
            return removeSpawns((spawnGroup, spawnEntry) -> spawnEntry.type == entityType);
        }

        /**
         * Removes all spawns of the given spawn group.
         *
         * <p>Associated JSON property: <code>spawners</code>.
         */
        default void clearSpawns(MobCategory group) {
            removeSpawns((spawnGroup, spawnEntry) -> spawnGroup == group);
        }

        /**
         * Removes all spawns.
         *
         * <p>Associated JSON property: <code>spawners</code>.
         */
        default void clearSpawns() {
            removeSpawns((spawnGroup, spawnEntry) -> true);
        }

        /**
         * Associated JSON property: <code>spawn_costs</code>.
         *
         * @see MobSpawnSettings#getMobSpawnCost(EntityType)
         * @see MobSpawnSettings.Builder#addMobCharge(EntityType, double, double)
         */
        void setSpawnCost(EntityType<?> entityType, double mass, double gravityLimit);

        /**
         * Removes a spawn cost entry for a given entity type.
         *
         * <p>Associated JSON property: <code>spawn_costs</code>.
         */
        void clearSpawnCost(EntityType<?> entityType);
    }
}
