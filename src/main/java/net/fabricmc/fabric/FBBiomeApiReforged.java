package net.fabricmc.fabric;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("fabric-biome-api")
public class FBBiomeApiReforged {
    public FBBiomeApiReforged(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(InternalBiomeUtils::addForgeAddedBiomesToList);
    }
}
