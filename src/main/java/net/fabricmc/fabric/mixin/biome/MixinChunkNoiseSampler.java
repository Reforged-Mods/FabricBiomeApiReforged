package net.fabricmc.fabric.mixin.biome;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.yeoxuhang.biomeapireforged.impl.biome.MultiNoiseSamplerHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(NoiseChunk.class)
public class MixinChunkNoiseSampler {
    @Unique
    private long seed;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(int horizontalSize, RandomState noiseConfig, int i, int j, NoiseSettings generationShapeConfig, DensityFunctions.BeardifierOrMarker arg, NoiseGeneratorSettings chunkGeneratorSettings, Aquifer.FluidPicker fluidLevelSampler, Blender blender, CallbackInfo ci) {
        seed = noiseConfig.legacyLevelSeed();
    }

    @Inject(method = "cachedClimateSampler", at = @At("RETURN"))
    private void createMultiNoiseSampler(NoiseRouter noiseRouter, List<Climate.ParameterPoint> list, CallbackInfoReturnable<Climate.Sampler> cir) {
        ((MultiNoiseSamplerHooks) (Object) cir.getReturnValue()).fabric_setSeed(seed);
    }
}
