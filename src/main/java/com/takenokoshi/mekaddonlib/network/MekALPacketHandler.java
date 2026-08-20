package com.takenokoshi.mekaddonlib.network;

import com.takenokoshi.mekaddonlib.network.to_server.MekALFactoryToggleSortFactory;

import mekanism.common.lib.Version;
import mekanism.common.network.BasePacketHandler;
import net.neoforged.bus.api.IEventBus;

public class MekALPacketHandler extends BasePacketHandler {

    public MekALPacketHandler(IEventBus modEventBus, Version version) {
        super(modEventBus, version);
    }

    @Override
    protected void registerClientToServer(PacketRegistrar registrar) {
        registrar.play(MekALFactoryToggleSortFactory.TYPE, MekALFactoryToggleSortFactory.STREAM_CODEC);
    }

    @Override
    protected void registerServerToClient(PacketRegistrar registrar) {
    }
    
}
