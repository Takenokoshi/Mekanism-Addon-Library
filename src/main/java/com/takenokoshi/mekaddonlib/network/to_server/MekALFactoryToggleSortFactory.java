package com.takenokoshi.mekaddonlib.network.to_server;

import com.takenokoshi.mekaddonlib.blockentity.base.BlockEntityMekALRecipeFactory;
import com.takenokoshi.mekaddonlib.core.MekAL;

import io.netty.buffer.ByteBuf;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MekALFactoryToggleSortFactory(BlockPos pos) implements IMekanismPacket {

    public static final CustomPacketPayload.Type<MekALFactoryToggleSortFactory> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(MekAL.MODID, "factory_toggle_sort"));

    public static final StreamCodec<ByteBuf, MekALFactoryToggleSortFactory> STREAM_CODEC = StreamCodec
            .composite(BlockPos.STREAM_CODEC, MekALFactoryToggleSortFactory::pos, MekALFactoryToggleSortFactory::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        if (WorldUtils.getTileEntity(context.player().level(), pos) instanceof BlockEntityMekALRecipeFactory factory) {
            factory.toggleSorting();
        }
    }
}