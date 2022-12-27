package net.fabricmc.fabric.biome.modification;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.yeoxuhang.biomeapireforged.fabric.api.biome.BiomeSelectionContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
public class BiomeSelectionContextImpl implements BiomeSelectionContext {
    private final RegistryAccess dynamicRegistries;
    private final PrimaryLevelData levelProperties;
    private final ResourceKey<Biome> key;
    private final Biome biome;
    private final Holder<Biome> entry;

    public BiomeSelectionContextImpl(RegistryAccess dynamicRegistries, PrimaryLevelData levelProperties, ResourceKey<Biome> key, Biome biome) {
        this.dynamicRegistries = dynamicRegistries;
        this.levelProperties = levelProperties;
        this.key = key;
        this.biome = biome;
        this.entry = dynamicRegistries.registryOrThrow(Registry.BIOME_REGISTRY).getHolder(this.key).orElseThrow();
    }

    @Override
    public ResourceKey<Biome> getBiomeKey() {
        return key;
    }

    @Override
    public Biome getBiome() {
        return biome;
    }

    @Override
    public Holder<Biome> getBiomeRegistryEntry() {
        return entry;
    }

    @Override
    public Optional<ResourceKey<ConfiguredFeature<?, ?>>> getFeatureKey(ConfiguredFeature<?, ?> configuredFeature) {
        Registry<ConfiguredFeature<?, ?>> registry = dynamicRegistries.registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);
        return registry.getResourceKey(configuredFeature);
    }

    @Override
    public Optional<ResourceKey<PlacedFeature>> getPlacedFeatureKey(PlacedFeature placedFeature) {
        Registry<PlacedFeature> registry = dynamicRegistries.registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);
        return registry.getResourceKey(placedFeature);
    }

    @Override
    public boolean validForStructure(ResourceKey<Structure> key) {
        Structure instance = dynamicRegistries.registryOrThrow(Registry.STRUCTURE_REGISTRY).get(key);

        if (instance == null) {
            return false;
        }

        return instance.biomes().contains(getBiomeRegistryEntry());
    }

    @Override
    public Optional<ResourceKey<Structure>> getStructureKey(Structure structure) {
        Registry<Structure> registry = dynamicRegistries.registryOrThrow(Registry.STRUCTURE_REGISTRY);
        return registry.getResourceKey(structure);
    }

    @Override
    public boolean canGenerateIn(ResourceKey<LevelStem> dimensionKey) {
        LevelStem dimension = levelProperties.worldGenSettings().dimensions().get(dimensionKey);

        if (dimension == null) {
            return false;
        }

        return dimension.generator().getBiomeSource().possibleBiomes().stream().anyMatch(entry -> entry.value() == biome);
    }

    @Override
    public boolean hasTag(TagKey<Biome> tag) {
        Registry<Biome> biomeRegistry = dynamicRegistries.registryOrThrow(Registry.BIOME_REGISTRY);
        return biomeRegistry.getHolderOrThrow(getBiomeKey()).is(tag);
    }
}
