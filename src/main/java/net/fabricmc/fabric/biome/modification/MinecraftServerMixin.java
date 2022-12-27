package net.fabricmc.fabric.biome.modification;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.yeoxuhang.biomeapireforged.impl.biome.modification.BiomeModificationImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    protected WorldData saveProperties;

    public abstract RegistryAccess.Frozen getRegistryManager();

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void finalizeWorldGen(CallbackInfo ci) {
        if (!(saveProperties instanceof PrimaryLevelData levelProperties)) {
            throw new RuntimeException("Incompatible SaveProperties passed to MinecraftServer: " + saveProperties);
        }

        BiomeModificationImpl.INSTANCE.finalizeWorldGen(getRegistryManager(), levelProperties);
    }
}
