package net.fabricmc.fabric.biome.modification;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.yeoxuhang.biomeapireforged.fabric.api.biome.BiomeModificationContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class BiomeModificationContextImpl implements BiomeModificationContext {
    private final RegistryAccess registries;
    private final ResourceKey<Biome> biomeKey;
    private final Biome biome;
    private final WeatherContext weather;
    private final EffectsContext effects;
    private final GenerationSettingsContextImpl generationSettings;
    private final SpawnSettingsContextImpl spawnSettings;

    public BiomeModificationContextImpl(RegistryAccess registries, ResourceKey<Biome> biomeKey, Biome biome) {
        this.registries = registries;
        this.biomeKey = biomeKey;
        this.biome = biome;
        this.weather = new WeatherContextImpl();
        this.effects = new EffectsContextImpl();
        this.generationSettings = new GenerationSettingsContextImpl();
        this.spawnSettings = new SpawnSettingsContextImpl();
    }

    
    public WeatherContext getWeather() {
        return weather;
    }

    
    public EffectsContext getEffects() {
        return effects;
    }

    
    public GenerationSettingsContext getGenerationSettings() {
        return generationSettings;
    }

    
    public SpawnSettingsContext getSpawnSettings() {
        return spawnSettings;
    }

    /**
     * Re-freeze any immutable lists and perform general post-modification cleanup.
     */
    void freeze() {
        generationSettings.freeze();
        spawnSettings.freeze();
    }

    private class WeatherContextImpl implements WeatherContext {
        private final Biome.ClimateSettings weather = biome.climateSettings;

        
        public void setPrecipitation(Biome.Precipitation precipitation) {
            weather.precipitation = Objects.requireNonNull(precipitation);
        }

        
        public void setTemperature(float temperature) {
            weather.temperature = temperature;
        }

        
        public void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier) {
            weather.temperatureModifier = Objects.requireNonNull(temperatureModifier);
        }

        
        public void setDownfall(float downfall) {
            weather.downfall = downfall;
        }
    }

    private class EffectsContextImpl implements EffectsContext {
        private final BiomeSpecialEffects effects = biome.getSpecialEffects();

        
        public void setFogColor(int color) {
            effects.fogColor = color;
        }

        
        public void setWaterColor(int color) {
            effects.waterColor = color;
        }

        
        public void setWaterFogColor(int color) {
            effects.waterFogColor = color;
        }

        
        public void setSkyColor(int color) {
            effects.skyColor = color;
        }

        
        public void setFoliageColor(Optional<Integer> color) {
            effects.foliageColorOverride = Objects.requireNonNull(color);
        }

        
        public void setGrassColor(Optional<Integer> color) {
            effects.grassColorOverride = Objects.requireNonNull(color);
        }

        
        public void setGrassColorModifier(@NotNull BiomeSpecialEffects.GrassColorModifier colorModifier) {
            effects.grassColorModifier = Objects.requireNonNull(colorModifier);
        }

        
        public void setParticleConfig(Optional<AmbientParticleSettings> particleConfig) {
            effects.ambientParticleSettings = Objects.requireNonNull(particleConfig);
        }

        
        public void setAmbientSound(Optional<SoundEvent> sound) {
            effects.ambientLoopSoundEvent = Objects.requireNonNull(sound);
        }

        
        public void setMoodSound(Optional<AmbientMoodSettings> sound) {
            effects.ambientMoodSettings = Objects.requireNonNull(sound);
        }

        
        public void setAdditionsSound(Optional<AmbientAdditionsSettings> sound) {
            effects.ambientAdditionsSettings = Objects.requireNonNull(sound);
        }

        
        public void setMusic(Optional<Music> sound) {
            effects.backgroundMusic = Objects.requireNonNull(sound);
        }
    }

    private class GenerationSettingsContextImpl implements GenerationSettingsContext {
        private final Registry<ConfiguredWorldCarver<?>> carvers = registries.registryOrThrow(Registry.CONFIGURED_CARVER_REGISTRY);
        private final Registry<PlacedFeature> features = registries.registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);
        private final BiomeGenerationSettings generationSettings = biome.getGenerationSettings();

        private boolean rebuildFlowerFeatures;

        /**
         * Unfreeze the immutable lists found in the generation settings, and make sure they're filled up to every
         * possible step if they're dense lists.
         */
        GenerationSettingsContextImpl() {
            unfreezeCarvers();
            unfreezeFeatures();

            rebuildFlowerFeatures = false;
        }

        private void unfreezeCarvers() {
            Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> carversByStep = new EnumMap<>(GenerationStep.Carving.class);
            carversByStep.putAll(generationSettings.carvers);

            generationSettings.carvers = carversByStep;
        }

        private void unfreezeFeatures() {
            generationSettings.features = new ArrayList<>(generationSettings.features);
        }

        /**
         * Re-freeze the lists in the generation settings to immutable variants, also fixes the flower features.
         */
        public void freeze() {
            freezeCarvers();
            freezeFeatures();

            if (rebuildFlowerFeatures) {
                rebuildFlowerFeatures();
            }
        }

        private void freezeCarvers() {
            generationSettings.carvers = ImmutableMap.copyOf(generationSettings.carvers);
        }

        private void freezeFeatures() {
            generationSettings.features = ImmutableList.copyOf(generationSettings.features);
            // Replace the supplier to force a rebuild next time its called.
            generationSettings.featureSet = Suppliers.memoize(() -> {
                return generationSettings.features.stream().flatMap(HolderSet::stream).map(Holder::value).collect(Collectors.toSet());
            });
        }

        private void rebuildFlowerFeatures() {
            // Replace the supplier to force a rebuild next time its called.
            generationSettings.flowerFeatures = Suppliers.memoize(() -> {
                return generationSettings.features.stream().flatMap(HolderSet::stream).map(Holder::value).flatMap(PlacedFeature::getFeatures).filter((configuredFeature) -> {
                    return configuredFeature.feature() == Feature.FLOWER;
                }).collect(ImmutableList.toImmutableList());
            });
        }

        
        public boolean removeFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> placedFeatureKey) {
            PlacedFeature configuredFeature = features.getOrThrow(placedFeatureKey);

            int stepIndex = step.ordinal();
            List<HolderSet<PlacedFeature>> featureSteps = generationSettings.features;

            if (stepIndex >= featureSteps.size()) {
                return false; // The step was not populated with any features yet
            }

            HolderSet<PlacedFeature> featuresInStep = featureSteps.get(stepIndex);
            List<Holder<PlacedFeature>> features = new ArrayList<>(featuresInStep.stream().toList());

            if (features.removeIf(feature -> feature.value() == configuredFeature)) {
                featureSteps.set(stepIndex, HolderSet.direct(features));
                rebuildFlowerFeatures = true;

                return true;
            }

            return false;
        }

        
        public void addFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> entry) {
            List<HolderSet<PlacedFeature>> featureSteps = generationSettings.features;
            int index = step.ordinal();

            // Add new empty lists for the generation steps that have no features yet
            while (index >= featureSteps.size()) {
                featureSteps.add(HolderSet.direct(Collections.emptyList()));
            }

            featureSteps.set(index, plus(featureSteps.get(index), features.getHolder(entry).orElseThrow()));

            // Ensure the list of flower features is up to date
            rebuildFlowerFeatures = true;
        }

        
        public void addCarver(GenerationStep.Carving step, ResourceKey<ConfiguredWorldCarver<?>> entry) {
            // We do not need to delay evaluation of this since the registries are already fully built
            generationSettings.carvers.put(step, plus(generationSettings.carvers.get(step), carvers.getHolder(entry).orElseThrow()));
        }

        
        public boolean removeCarver(GenerationStep.Carving step, ResourceKey<ConfiguredWorldCarver<?>> configuredCarverKey) {
            ConfiguredWorldCarver<?> carver = carvers.getOrThrow(configuredCarverKey);
            List<Holder<ConfiguredWorldCarver<?>>> genCarvers = new ArrayList<>(generationSettings.carvers.get(step).stream().toList());

            if (genCarvers.removeIf(entry -> entry.value() == carver)) {
                generationSettings.carvers.put(step, HolderSet.direct(genCarvers));
                return true;
            }

            return false;
        }

        private <T> HolderSet<T> plus(HolderSet<T> values, Holder<T> entry) {
            List<Holder<T>> list = new ArrayList<>(values.stream().toList());
            list.add(entry);
            return HolderSet.direct(list);
        }
    }

    private class SpawnSettingsContextImpl implements SpawnSettingsContext {
        private final MobSpawnSettings spawnSettings = biome.getMobSettings();
        private final EnumMap<MobCategory, List<MobSpawnSettings.SpawnerData>> fabricSpawners = new EnumMap<>(MobCategory.class);

        SpawnSettingsContextImpl() {
            unfreezeSpawners();
            unfreezeSpawnCost();
        }

        private void unfreezeSpawners() {
            fabricSpawners.clear();

            for (MobCategory spawnGroup : MobCategory.values()) {
                WeightedRandomList<MobSpawnSettings.SpawnerData> entries = spawnSettings.spawners.get(spawnGroup);

                if (entries != null) {
                    fabricSpawners.put(spawnGroup, new ArrayList<>(entries.unwrap()));
                } else {
                    fabricSpawners.put(spawnGroup, new ArrayList<>());
                }
            }
        }

        private void unfreezeSpawnCost() {
            spawnSettings.mobSpawnCosts = new HashMap<>(spawnSettings.mobSpawnCosts);
        }

        public void freeze() {
            freezeSpawners();
            freezeSpawnCosts();
        }

        private void freezeSpawners() {
            Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> spawners = new HashMap<>(spawnSettings.spawners);

            for (Map.Entry<MobCategory, List<MobSpawnSettings.SpawnerData>> entry : fabricSpawners.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    spawners.put(entry.getKey(), WeightedRandomList.create());
                } else {
                    spawners.put(entry.getKey(), WeightedRandomList.create(entry.getValue()));
                }
            }

            spawnSettings.spawners = ImmutableMap.copyOf(spawners);
        }

        private void freezeSpawnCosts() {
            spawnSettings.mobSpawnCosts = ImmutableMap.copyOf(spawnSettings.mobSpawnCosts);
        }

        
        public void setCreatureSpawnProbability(float probability) {
            spawnSettings.creatureGenerationProbability = probability;
        }

        
        public void addSpawn(MobCategory spawnGroup, MobSpawnSettings.SpawnerData spawnEntry) {
            Objects.requireNonNull(spawnGroup);
            Objects.requireNonNull(spawnEntry);

            fabricSpawners.get(spawnGroup).add(spawnEntry);
        }

        
        public boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> predicate) {
            boolean anyRemoved = false;

            for (MobCategory group : MobCategory.values()) {
                if (fabricSpawners.get(group).removeIf(entry -> predicate.test(group, entry))) {
                    anyRemoved = true;
                }
            }

            return anyRemoved;
        }

        
        public void setSpawnCost(EntityType<?> entityType, double mass, double gravityLimit) {
            Objects.requireNonNull(entityType);
            spawnSettings.mobSpawnCosts.put(entityType, new MobSpawnSettings.MobSpawnCost(gravityLimit, mass));
        }

        
        public void clearSpawnCost(EntityType<?> entityType) {
            spawnSettings.mobSpawnCosts.remove(entityType);
        }
    }
}
