package net.fabricmc.fabric.biome;

public interface BiomeSourceAccess {
    boolean fabric_shouldModifyBiomeEntries();

    void fabric_setModifyBiomeEntries(boolean modifyBiomeEntries);
}
