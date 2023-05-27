package net.fabricmc.fabric.biome;

import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.yeoxuhang.biomeapireforged.impl.biome.TheEndBiomeData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.function.Supplier;


@Mixin(TheEndBiomeSource.class)
public class MixinTheEndBiomeSource extends MixinBiomeSource {
    @Unique
    private Supplier<TheEndBiomeData.Overrides> overrides;

    @Unique
    private boolean biomeSetModified = false;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(Registry<Biome> biomeRegistry, CallbackInfo ci) {
        overrides = Suppliers.memoize(() -> TheEndBiomeData.createOverrides(biomeRegistry));
    }

    @Inject(method = "getNoiseBiome", at = @At("RETURN"), cancellable = true)
    private void getWeightedEndBiome(int biomeX, int biomeY, int biomeZ, Climate.Sampler noise, CallbackInfoReturnable<Holder<Biome>> cir) {
        cir.setReturnValue(overrides.get().pick(biomeX, biomeY, biomeZ, noise, cir.getReturnValue()));
    }

    @Override
    protected void fabric_modifyBiomeSet(Set<Holder<Biome>> biomes) {
        if (!biomeSetModified) {
            biomeSetModified = true;
            biomes.addAll(overrides.get().customBiomes);
        }
    }
}
