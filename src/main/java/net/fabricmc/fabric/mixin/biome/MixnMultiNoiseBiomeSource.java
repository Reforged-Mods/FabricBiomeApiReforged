package net.fabricmc.fabric.mixin.biome;

import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.yeoxuhang.biomeapireforged.impl.biome.BiomeSourceAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MultiNoiseBiomeSource.class)
public class MixnMultiNoiseBiomeSource implements BiomeSourceAccess {
    @Unique
    private boolean modifyBiomeEntries = true;

    @Override
    public void fabric_setModifyBiomeEntries(boolean modifyBiomeEntries) {
        this.modifyBiomeEntries = modifyBiomeEntries;
    }

    @Override
    public boolean fabric_shouldModifyBiomeEntries() {
        return this.modifyBiomeEntries;
    }
}
