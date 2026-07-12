package com.takenokoshi.mekaddonlib.core;

import mekanism.api.text.ILangEntry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum MekALLang implements ILangEntry {
    UPGRADE_COBBLESTONE_SUPPLY("uppgrade", "cobblestone_supply"),
    UPGRADE_COBBLESTONE_SUPPLY_DESCRIPTION("uppgrade", "cobblestone_supply.description"),
    UPGRADE_WATER_SUPPLY("uppgrade", "water_supply"),
    UPGRADE_WATER_SUPPLY_DESCRIPTION("uppgrade", "water_supply.description"),
    UPGRADE_HEAVY_WATER_SUPPLY("uppgrade", "heavy_water_supply"),
    UPGRADE_HEAVY_WATER_SUPPLY_DESCRIPTION("uppgrade", "heavy_water_supply.description"),
    UPGRADE_LAVA_SUPPLY("uppgrade", "lava_supply"),
    UPGRADE_LAVA_SUPPLY_DESCRIPTION("uppgrade", "lava_supply.description"),
    ;

    private MekALLang(String type, String path) {
        this(Util.makeDescriptionId(type, ResourceLocation.fromNamespaceAndPath(MekAL.MODID, path)));
    }

    private MekALLang(String key) {
        this.key = key;
    }

    private final String key;

    @Override
    public String getTranslationKey() {
        return key;
    }

}
