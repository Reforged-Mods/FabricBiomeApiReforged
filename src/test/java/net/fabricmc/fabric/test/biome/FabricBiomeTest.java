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

package net.fabricmc.fabric.test.biome;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.biome.v1.NetherBiomes;
import net.fabricmc.fabric.api.biome.v1.TheEndBiomes;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.EndPlacedFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

/**
 * <b>NOTES FOR TESTING:</b>
 * When running with this test-mod, also test this when running a dedicated server since there
 * are significant differences between server + client and how they sync biomes.
 *
 * <p>Ingame, you can use <code>/locatebiome</code> since we use nether- and end-biomes in the overworld,
 * and vice-versa, making them easy to find to verify the injection worked.
 *
 * <p>If you don't find a biome right away, teleport far away (~10000 blocks) from spawn and try again.
 */
@Mod(FabricBiomeTest.MOD_ID)
public class FabricBiomeTest {
	public static final String MOD_ID = "fabric-testmod";

	private static final RegistryKey<Biome> TEST_CRIMSON_FOREST = RegistryKey.of(Registry.BIOME_KEY, new Identifier(MOD_ID, "test_crimson_forest"));
	private static final RegistryKey<Biome> CUSTOM_PLAINS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(MOD_ID, "custom_plains"));
	private static final RegistryKey<Biome> TEST_END_HIGHLANDS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(MOD_ID, "test_end_highlands"));
	private static final RegistryKey<Biome> TEST_END_MIDLANDS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(MOD_ID, "test_end_midlands"));
	private static final RegistryKey<Biome> TEST_END_BARRRENS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(MOD_ID, "test_end_barrens"));

	private static final BlockState STONE = Blocks.STONE.getDefaultState();

	public FabricBiomeTest(){
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	/*@SubscribeEvent
	public void onRegister(final RegistryEvent.Register<?> event){
		if (event.getRegistry() == ForgeRegistries.BIOMES){
			BiomeDictionary.addTypes(TEST_CRIMSON_FOREST, BiomeDictionary.Type.NETHER, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY);
			BiomeDictionary.addTypes(TEST_END_HIGHLANDS, BiomeDictionary.Type.END);
			BiomeDictionary.addTypes(TEST_END_MIDLANDS, BiomeDictionary.Type.END);
			BiomeDictionary.addTypes(TEST_END_BARRRENS, BiomeDictionary.Type.END);
			BiomeDictionary.addTypes(CUSTOM_PLAINS, BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.OVERWORLD);
			((IForgeRegistry)event.getRegistry()).register(createEndHighlands().setRegistryName(TEST_END_HIGHLANDS.getRegistryName()));
			((IForgeRegistry)event.getRegistry()).register(createEndMidlands().setRegistryName(TEST_END_MIDLANDS.getRegistryName()));
			((IForgeRegistry)event.getRegistry()).register(createEndBarrens().setRegistryName(TEST_END_BARRRENS.getRegistryName()));
		}
	}*/


	public void setup(final FMLCommonSetupEvent e) {

		NetherBiomes.addNetherBiome(BiomeKeys.BEACH, MultiNoiseUtil.createNoiseHypercube(00.0F, 0.5F, 0.0F, 0.0F, 0.0f, 0, 0.1F));
		NetherBiomes.addNetherBiome(TEST_CRIMSON_FOREST, MultiNoiseUtil.createNoiseHypercube(0.0F, 0.0F, 0.0f, 0.35F, 0.0f, 0.35F, 0.2F));
		// TESTING HINT: to get to the end:
		// /execute in minecraft:the_end run tp @s 0 90 0
		TheEndBiomes.addHighlandsBiome(TEST_END_HIGHLANDS, 5.0);
		TheEndBiomes.addMidlandsBiome(TEST_END_HIGHLANDS, TEST_END_MIDLANDS, 1.0);
		TheEndBiomes.addBarrensBiome(TEST_END_HIGHLANDS, TEST_END_BARRRENS, 1.0);

		e.enqueueWork(() -> {
			ConfiguredFeature<?, ?> COMMON_DESERT_WELL = new ConfiguredFeature<>(Feature.DESERT_WELL, DefaultFeatureConfig.INSTANCE);
			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(MOD_ID, "fab_desert_well"), COMMON_DESERT_WELL);
			RegistryEntry<ConfiguredFeature<?, ?>> featureEntry = BuiltinRegistries.CONFIGURED_FEATURE.getOrCreateEntry(BuiltinRegistries.CONFIGURED_FEATURE.getKey(COMMON_DESERT_WELL).orElseThrow());

			// The placement config is taken from the vanilla desert well, but no randomness
			PlacedFeature PLACED_COMMON_DESERT_WELL = new PlacedFeature(featureEntry, List.of(SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()));
			Registry.register(BuiltinRegistries.PLACED_FEATURE, new Identifier(MOD_ID, "fab_desert_well"), PLACED_COMMON_DESERT_WELL);

			/*BiomeModifications.create(new Identifier("fabric:test_mod"))
					.add(ModificationPhase.ADDITIONS,
							BiomeSelectors.foundInOverworld(),
							modification -> modification.getWeather().setDownfall(100))
					.add(ModificationPhase.ADDITIONS,
							BiomeSelectors.categories(Biome.Category.DESERT),
							context -> {
								context.getGenerationSettings().addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION,
										BuiltinRegistries.PLACED_FEATURE.getKey(PLACED_COMMON_DESERT_WELL).orElseThrow()
								);
							})
					.add(ModificationPhase.ADDITIONS,
							BiomeSelectors.tag(TagKey.of(Registry.BIOME_KEY, new Identifier(MOD_ID, "tag_selector_test"))),
							context -> context.getEffects().setSkyColor(0x770000));*/
		});
	}

	// These are used for testing the spacing of custom end biomes.
	private static Biome createEndHighlands() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
				.feature(GenerationStep.Feature.SURFACE_STRUCTURES, EndPlacedFeatures.END_GATEWAY_RETURN)
				.feature(GenerationStep.Feature.VEGETAL_DECORATION, EndPlacedFeatures.CHORUS_PLANT);
		return composeEndSpawnSettings(builder);
	}

	public static Biome createEndMidlands() {
		GenerationSettings.Builder builder = (new GenerationSettings.Builder());
		return composeEndSpawnSettings(builder);
	}

	public static Biome createEndBarrens() {
		GenerationSettings.Builder builder = (new GenerationSettings.Builder());
		return composeEndSpawnSettings(builder);
	}

	private static Biome composeEndSpawnSettings(GenerationSettings.Builder builder) {
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addEndMobs(builder2);
		return (new Biome.Builder()).precipitation(Biome.Precipitation.NONE).temperature(0.5F).downfall(0.5F).effects((new BiomeEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(10518688).skyColor(0).moodSound(BiomeMoodSound.CAVE).build()).spawnSettings(builder2.build()).generationSettings(builder.build()).build();
	}
}
