package net.fabricmc.fabric.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.yeoxuhang.biomeapireforged.impl.biome.TheEndBiomeData;

/**
 * API that exposes some internals of the minecraft default biome source for The End.
 *
 * <p><b>Experimental feature</b>, may be removed or changed without further notice.
 * Because of the volatility of world generation in Minecraft 1.16, this API is marked experimental
 * since it is likely to change in future Minecraft versions.
 */
public final class TheEndBiomes {
    private TheEndBiomes() {
    }

    /**
     * <p>Adds the biome as a main end island biome with the specified weight; note that this includes the main island
     * and some of the land encircling the empty space. Note that adding a biome to this region could potentially mess
     * with the generation of the center island and cause it to generate incorrectly; this method only exists for
     * consistency.</p>
     *
     * @param biome  the biome to be added
     * @param weight the weight of the entry. The weight in this method corresponds to its selection likelihood, with
     *               heavier biomes being more likely to be selected and lighter biomes being selected with less likelihood.
     *               Vanilla biomes have a weight of 1.0
     */
    public static void addMainIslandBiome(ResourceKey<Biome> biome, double weight) {
        TheEndBiomeData.addEndBiomeReplacement(Biomes.THE_END, biome, weight);
    }

    /**
     * <p>Adds the biome as an end highlands biome with the specified weight. End Highlands biomes make up the
     * center region of the large outer islands in The End.</p>
     *
     * @param biome  the biome to be added
     * @param weight the weight of the entry. The weight in this method corresponds to its selection likelihood, with
     *               heavier biomes being more likely to be selected and lighter biomes being selected with less likelihood.
     *               The vanilla biome has a weight of 1.0.
     */
    public static void addHighlandsBiome(ResourceKey<Biome> biome, double weight) {
        TheEndBiomeData.addEndBiomeReplacement(Biomes.END_HIGHLANDS, biome, weight);
    }

    /**
     * <p>Adds a custom biome as a small end islands biome with the specified weight; small end island biomes
     * make up the smaller islands in between the larger islands of the end.</p>
     *
     * @param biome  the biome to be added
     * @param weight the weight of the entry. The weight in this method corresponds to its selection likelihood, with
     *               heavier biomes being more likely to be selected and lighter biomes being selected with less likelihood.
     *               The vanilla biome has a weight of 1.0.
     */
    public static void addSmallIslandsBiome(ResourceKey<Biome> biome, double weight) {
        TheEndBiomeData.addEndBiomeReplacement(Biomes.SMALL_END_ISLANDS, biome, weight);
    }

    /**
     * <p>Adds the biome as an end midlands of the parent end highlands biome. End Midlands make up the area on
     * the large outer islands between the highlands and the barrens and are similar to edge biomes in the
     * overworld. If you don't call this method, the vanilla biome will be used by default.</p>
     *
     * @param highlands The highlands biome to where the midlands biome is added
     * @param midlands  the biome to be added as a midlands biome
     * @param weight    the weight of the entry. The weight in this method corresponds to its selection likelihood, with
     *                  heavier biomes being more likely to be selected and lighter biomes being selected with less likelihood.
     *                  The vanilla biome has a weight of 1.0.
     */
    public static void addMidlandsBiome(ResourceKey<Biome> highlands, ResourceKey<Biome> midlands, double weight) {
        TheEndBiomeData.addEndMidlandsReplacement(highlands, midlands, weight);
    }

    /**
     * <p>Adds the biome as an end barrens of the parent end highlands biome. End Midlands make up the area on
     * the edge of the large outer islands and are similar to edge biomes in the overworld. If you don't call
     * this method, the vanilla biome will be used by default.</p>
     *
     * @param highlands The highlands biome to where the barrens biome is added
     * @param barrens   the biome to be added as a barrens biome
     * @param weight    the weight of the entry. The weight in this method corresponds to its selection likelihood, with
     *                  heavier biomes being more likely to be selected and lighter biomes being selected with less likelihood.
     *                  The vanilla biome has a weight of 1.0.
     */
    public static void addBarrensBiome(ResourceKey<Biome> highlands, ResourceKey<Biome> barrens, double weight) {
        TheEndBiomeData.addEndBarrensReplacement(highlands, barrens, weight);
    }
}
