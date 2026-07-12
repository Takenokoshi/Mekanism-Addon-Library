package com.takenokoshi.mekaddonlib.core;

import com.takenokoshi.mekaddonlib.registries.MekALItems;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@Mod(MekAL.MODID)
public class MekAL {
    public static final String MODID = "mek_addon_lib";

    public MekAL(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerRegistries);
        MekALItems.ITEMS.register(modEventBus);
    }

    private void registerRegistries(NewRegistryEvent event){
    }
}
