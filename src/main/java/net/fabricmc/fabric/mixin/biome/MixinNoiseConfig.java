package net.fabricmc.fabric.mixin.biome;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.yeoxuhang.biomeapireforged.impl.biome.MultiNoiseSamplerHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomState.class)
public class MixinNoiseConfig {

    @Final
    @Shadow
    private Climate.Sampler sampler;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(NoiseGeneratorSettings chunkGeneratorSettings, Registry<?> noiseRegistry, long seed, CallbackInfo ci) {
        ((MultiNoiseSamplerHooks) (Object) sampler).fabric_setSeed(seed);
    }
}
