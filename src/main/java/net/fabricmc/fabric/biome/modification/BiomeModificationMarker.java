package net.fabricmc.fabric.biome.modification;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface BiomeModificationMarker {
    void fabric_markModified();
}
