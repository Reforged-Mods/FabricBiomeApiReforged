package net.fabricmc.fabric.mixin.biome;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.function.Supplier;

@Mixin(BiomeSource.class)
public class MixinBiomeSource {
    @Final
    @Shadow
    private Supplier<Set<Holder<Biome>>> lazyPossibleBiomes;

    @Inject(method = "possibleBiomes", at = @At("HEAD"))
    private void getBiomes(CallbackInfoReturnable<Set<Holder<Biome>>> ci) {
        fabric_modifyBiomeSet(this.lazyPossibleBiomes.get());
    }

    protected void fabric_modifyBiomeSet(Set<Holder<Biome>> biomes) {
    }
}