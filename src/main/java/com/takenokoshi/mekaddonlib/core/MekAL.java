package com.takenokoshi.mekaddonlib.core;

import com.takenokoshi.mekaddonlib.network.MekALPacketHandler;
import com.takenokoshi.mekaddonlib.registries.MekALItems;
import mekanism.common.lib.Version;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@Mod(MekAL.MODID)
public class MekAL {
    public static final String MODID = "mek_addon_lib";

    private static MekAL instance;

    public final Version versionNumber;
    private final MekALPacketHandler packetHandler;

    public MekAL(IEventBus modEventBus, ModContainer modContainer) {
        versionNumber = new Version(modContainer);
        modEventBus.addListener(this::registerRegistries);
        MekALItems.ITEMS.register(modEventBus);
        this.packetHandler = new MekALPacketHandler(modEventBus, versionNumber);
        instance = this;
    }

    public static MekALPacketHandler packetHandler() {
        return instance.packetHandler;
    }

    private void registerRegistries(NewRegistryEvent event) {
    }
}
