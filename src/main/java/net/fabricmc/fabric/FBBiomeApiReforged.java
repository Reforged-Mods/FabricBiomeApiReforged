package net.fabricmc.fabric;

import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.fabricmc.fabric.impl.biome.InternalBiomeData;
import net.fabricmc.fabric.impl.biome.InternalBiomeUtils;
import net.fabricmc.fabric.impl.biome.WeightedBiomePicker;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

@Mod("fabric-biome-api")
public class FBBiomeApiReforged {
    public FBBiomeApiReforged(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(InternalBiomeUtils::addForgeAddedBiomesToList);
    }
}
