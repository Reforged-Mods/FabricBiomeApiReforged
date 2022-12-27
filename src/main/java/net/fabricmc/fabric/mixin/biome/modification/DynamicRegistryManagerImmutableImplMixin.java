package net.fabricmc.fabric.mixin.biome.modification;

import net.fabricmc.fabric.biome.modification.BiomeModificationImpl;
import net.fabricmc.fabric.biome.modification.BiomeModificationMarker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * This Mixin allows us to keep backup copies of biomes for
 * {@link BiomeModificationImpl} on a per-DynamicRegistryManager basis.
 */
@Mixin(RegistryAccess.ImmutableRegistryAccess.class)
public class DynamicRegistryManagerImmutableImplMixin implements BiomeModificationMarker {
    @Unique
    private boolean modified;

    @Override
    public void fabric_markModified() {
        if (modified) {
            throw new IllegalStateException("This dynamic registries instance has already been modified");
        }

        modified = true;
    }
}
