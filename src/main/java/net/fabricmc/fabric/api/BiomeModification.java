package net.fabricmc.fabric.api;

import net.minecraft.resources.ResourceLocation;
import net.yeoxuhang.biomeapireforged.impl.biome.modification.BiomeModificationImpl;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * <b>Experimental feature</b>, may be removed or changed without further notice.
 *
 * @see BiomeModifications
 */
public class BiomeModification {
    private final ResourceLocation id;

    @ApiStatus.Internal
    BiomeModification(ResourceLocation id) {
        this.id = id;
    }

    /**
     * Adds a modifier that is not sensitive to the current state of the biome when it is applied, examples
     * for this are modifiers that simply add or remove features unconditionally, or change other values
     * to constants.
     */
    public BiomeModification add(ModificationPhase phase, Predicate<BiomeSelectionContext> selector, Consumer<BiomeModificationContext> modifier) {
        BiomeModificationImpl.INSTANCE.addModifier(id, phase, selector, modifier);
        return this;
    }

    /**
     * Adds a modifier that is sensitive to the current state of the biome when it is applied.
     * Examples for this are modifiers that apply scales to existing values (e.g. half the temperature).
     *
     * <p>For modifiers that should only be applied if a given condition is met for a Biome, please add these
     * conditions to the selector, and use a context-free modifier instead, as this will greatly help
     * with debugging world generation issues.
     */
    public BiomeModification add(ModificationPhase phase, Predicate<BiomeSelectionContext> selector, BiConsumer<BiomeSelectionContext, BiomeModificationContext> modifier) {
        BiomeModificationImpl.INSTANCE.addModifier(id, phase, selector, modifier);
        return this;
    }
}
