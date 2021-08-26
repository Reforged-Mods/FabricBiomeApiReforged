/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.biome;

import com.google.common.base.Preconditions;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Lists and maps for internal use only! Stores data that is used by the various mixins into the world generation
 */
public final class InternalBiomeData {
	private InternalBiomeData() {
	}

	private static final Set<RegistryKey<Biome>> NETHER_BIOMES = new HashSet<>();
	private static final Map<RegistryKey<Biome>, Biome.MixedNoisePoint> NETHER_BIOME_NOISE_POINTS = new HashMap<>();

	private static final Map<RegistryKey<Biome>, WeightedBiomePicker> END_BIOMES_MAP = new IdentityHashMap<>();
	private static final Map<RegistryKey<Biome>, WeightedBiomePicker> END_MIDLANDS_MAP = new IdentityHashMap<>();
	private static final Map<RegistryKey<Biome>, WeightedBiomePicker> END_BARRENS_MAP = new IdentityHashMap<>();

	static {
		END_BIOMES_MAP.computeIfAbsent(BiomeKeys.THE_END, key -> new WeightedBiomePicker()).addBiome(BiomeKeys.THE_END, 1.0);
		END_BIOMES_MAP.computeIfAbsent(BiomeKeys.END_HIGHLANDS, key -> new WeightedBiomePicker()).addBiome(BiomeKeys.END_HIGHLANDS, 1.0);
		END_BIOMES_MAP.computeIfAbsent(BiomeKeys.SMALL_END_ISLANDS, key -> new WeightedBiomePicker()).addBiome(BiomeKeys.SMALL_END_ISLANDS, 1.0);

		END_MIDLANDS_MAP.computeIfAbsent(BiomeKeys.END_HIGHLANDS, key -> new WeightedBiomePicker()).addBiome(BiomeKeys.END_MIDLANDS, 1.0);
		END_BARRENS_MAP.computeIfAbsent(BiomeKeys.END_HIGHLANDS, key -> new WeightedBiomePicker()).addBiome(BiomeKeys.END_BARRENS, 1.0);
	}

	public static void addNetherBiome(RegistryKey<Biome> biome, Biome.MixedNoisePoint spawnNoisePoint) {
		Preconditions.checkArgument(biome != null, "Biome is null");
		Preconditions.checkArgument(spawnNoisePoint != null, "Biome.MixedNoisePoint is null");
		//todo figure this out
		InternalBiomeUtils.ensureIdMapping(biome);
		NETHER_BIOME_NOISE_POINTS.put(biome, spawnNoisePoint);
		NETHER_BIOMES.clear(); // Reset cached overall biome list
	}

	public static void addEndBiomeReplacement(RegistryKey<Biome> replaced, RegistryKey<Biome> variant, double weight) {
		Preconditions.checkNotNull(replaced, "replaced biome is null");
		Preconditions.checkNotNull(variant, "variant biome is null");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (got %s)", weight);
		END_BIOMES_MAP.computeIfAbsent(replaced, key -> new WeightedBiomePicker()).addBiome(variant, weight);
	}

	public static void addEndMidlandsReplacement(RegistryKey<Biome> highlands, RegistryKey<Biome> midlands, double weight) {
		Preconditions.checkNotNull(highlands, "highlands biome is null");
		Preconditions.checkNotNull(midlands, "midlands biome is null");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (got %s)", weight);
		END_MIDLANDS_MAP.computeIfAbsent(highlands, key -> new WeightedBiomePicker()).addBiome(midlands, weight);
	}

	public static void addEndBarrensReplacement(RegistryKey<Biome> highlands, RegistryKey<Biome> barrens, double weight) {
		Preconditions.checkNotNull(highlands, "highlands biome is null");
		Preconditions.checkNotNull(barrens, "midlands biome is null");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (got %s)", weight);
		END_BARRENS_MAP.computeIfAbsent(highlands, key -> new WeightedBiomePicker()).addBiome(barrens, weight);
	}

	public static Map<RegistryKey<Biome>, Biome.MixedNoisePoint> getNetherBiomeNoisePoints() {
		return NETHER_BIOME_NOISE_POINTS;
	}

	public static boolean canGenerateInNether(RegistryKey<Biome> biome) {
		if (NETHER_BIOMES.isEmpty()) {
			MultiNoiseBiomeSource source = MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(BuiltinRegistries.BIOME, 0L);

			for (Biome netherBiome : source.getBiomes()) {
				BuiltinRegistries.BIOME.getKey(netherBiome).ifPresent(NETHER_BIOMES::add);
			}
		}

		return NETHER_BIOMES.contains(biome);
	}

	public static Map<RegistryKey<Biome>, WeightedBiomePicker> getEndBiomesMap() {
		return END_BIOMES_MAP;
	}

	public static Map<RegistryKey<Biome>, WeightedBiomePicker> getEndMidlandsMap() {
		return END_MIDLANDS_MAP;
	}

	public static Map<RegistryKey<Biome>, WeightedBiomePicker> getEndBarrensMap() {
		return END_BARRENS_MAP;
	}
}
