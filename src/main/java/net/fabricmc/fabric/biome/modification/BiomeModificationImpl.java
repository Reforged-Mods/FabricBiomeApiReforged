package net.fabricmc.fabric.biome.modification;

import com.google.common.base.Stopwatch;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.yeoxuhang.biomeapireforged.fabric.api.biome.BiomeModificationContext;
import net.yeoxuhang.biomeapireforged.fabric.api.biome.BiomeSelectionContext;
import net.yeoxuhang.biomeapireforged.fabric.api.biome.ModificationPhase;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

@ApiStatus.Internal
public class BiomeModificationImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(BiomeModificationImpl.class);

    private static final Comparator<ModifierRecord> MODIFIER_ORDER_COMPARATOR = Comparator.<ModifierRecord>comparingInt(r -> r.phase.ordinal()).thenComparingInt(r -> r.order).thenComparing(r -> r.id);

    public static final BiomeModificationImpl INSTANCE = new BiomeModificationImpl();

    private final List<ModifierRecord> modifiers = new ArrayList<>();

    private boolean modifiersUnsorted = true;

    private BiomeModificationImpl() {
    }

    public void addModifier(ResourceLocation id, ModificationPhase phase, Predicate<BiomeSelectionContext> selector, BiConsumer<BiomeSelectionContext, BiomeModificationContext> modifier) {
        Objects.requireNonNull(selector);
        Objects.requireNonNull(modifier);

        modifiers.add(new ModifierRecord(phase, id, selector, modifier));
        modifiersUnsorted = true;
    }

    public void addModifier(ResourceLocation id, ModificationPhase phase, Predicate<BiomeSelectionContext> selector, Consumer<BiomeModificationContext> modifier) {
        Objects.requireNonNull(selector);
        Objects.requireNonNull(modifier);

        modifiers.add(new ModifierRecord(phase, id, selector, modifier));
        modifiersUnsorted = true;
    }

    /**
     * This is currently not publicly exposed but likely useful for modpack support mods.
     */
    void changeOrder(ResourceLocation id, int order) {
        modifiersUnsorted = true;

        for (ModifierRecord modifierRecord : modifiers) {
            if (id.equals(modifierRecord.id)) {
                modifierRecord.setOrder(order);
            }
        }
    }

    @TestOnly
    void clearModifiers() {
        modifiers.clear();
        modifiersUnsorted = true;
    }

    private List<ModifierRecord> getSortedModifiers() {
        if (modifiersUnsorted) {
            // Resort modifiers
            modifiers.sort(MODIFIER_ORDER_COMPARATOR);
            modifiersUnsorted = false;
        }

        return modifiers;
    }

    @SuppressWarnings("ConstantConditions")
    public void finalizeWorldGen(RegistryAccess impl, PrimaryLevelData levelProperties) {
        Stopwatch sw = Stopwatch.createStarted();

        // Now that we apply biome modifications inside the MinecraftServer constructor, we should only ever do
        // this once for a dynamic registry manager. Marking the dynamic registry manager as modified ensures a crash
        // if the precondition is violated.
        BiomeModificationMarker modificationTracker = (BiomeModificationMarker) impl;
        modificationTracker.fabric_markModified();

        Registry<Biome> biomes = impl.registryOrThrow(Registry.BIOME_REGISTRY);

        // Build a list of all biome keys in ascending order of their raw-id to get a consistent result in case
        // someone does something stupid.
        List<ResourceKey<Biome>> keys = biomes.entrySet().stream()
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparingInt(key -> biomes.getId(biomes.getOrThrow(key))))
                .toList();

        List<ModifierRecord> sortedModifiers = getSortedModifiers();

        int biomesChanged = 0;
        int biomesProcessed = 0;
        int modifiersApplied = 0;

        for (ResourceKey<Biome> key : keys) {
            Biome biome = biomes.getOrThrow(key);

            biomesProcessed++;

            // Make a copy of the biome to allow selection contexts to see it unmodified,
            // But do so only once it's known anything wants to modify the biome at all
            BiomeSelectionContext context = new BiomeSelectionContextImpl(impl, levelProperties, key, biome);
            BiomeModificationContextImpl modificationContext = null;

            for (ModifierRecord modifier : sortedModifiers) {
                if (modifier.selector.test(context)) {
                    LOGGER.trace("Applying modifier {} to {}", modifier, key.location());

                    // Create the copy only if at least one modifier applies, since it's pretty costly
                    if (modificationContext == null) {
                        biomesChanged++;
                        modificationContext = new BiomeModificationContextImpl(impl, key, biome);
                    }

                    modifier.apply(context, modificationContext);
                    modifiersApplied++;
                }
            }

            // Re-freeze and apply certain cleanup actions
            if (modificationContext != null) {
                modificationContext.freeze();
            }
        }

        if (biomesProcessed > 0) {
            LOGGER.info("Applied {} biome modifications to {} of {} new biomes in {}", modifiersApplied, biomesChanged,
                    biomesProcessed, sw);
        }
    }

    private static class ModifierRecord {
        private final ModificationPhase phase;

        private final ResourceLocation id;

        private final Predicate<BiomeSelectionContext> selector;

        private final BiConsumer<BiomeSelectionContext, BiomeModificationContext> contextSensitiveModifier;

        private final Consumer<BiomeModificationContext> modifier;

        // Whenever this is modified, the modifiers need to be resorted
        private int order;

        ModifierRecord(ModificationPhase phase, ResourceLocation id, Predicate<BiomeSelectionContext> selector, Consumer<BiomeModificationContext> modifier) {
            this.phase = phase;
            this.id = id;
            this.selector = selector;
            this.modifier = modifier;
            this.contextSensitiveModifier = null;
        }

        ModifierRecord(ModificationPhase phase, ResourceLocation id, Predicate<BiomeSelectionContext> selector, BiConsumer<BiomeSelectionContext, BiomeModificationContext> modifier) {
            this.phase = phase;
            this.id = id;
            this.selector = selector;
            this.contextSensitiveModifier = modifier;
            this.modifier = null;
        }

        @Override
        public String toString() {
            if (modifier != null) {
                return modifier.toString();
            } else {
                return contextSensitiveModifier.toString();
            }
        }

        public void apply(BiomeSelectionContext context, BiomeModificationContextImpl modificationContext) {
            if (contextSensitiveModifier != null) {
                contextSensitiveModifier.accept(context, modificationContext);
            } else {
                modifier.accept(modificationContext);
            }
        }

        public void setOrder(int order) {
            this.order = order;
        }
    }
}
