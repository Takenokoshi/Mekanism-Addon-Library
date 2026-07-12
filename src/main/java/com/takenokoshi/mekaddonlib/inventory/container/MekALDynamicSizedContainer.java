package com.takenokoshi.mekaddonlib.inventory.container;

import org.jetbrains.annotations.NotNull;

import com.takenokoshi.mekaddonlib.blockentity.interfaces.IHasGuiSizeOffset;

import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.world.entity.player.Inventory;

public class MekALDynamicSizedContainer<BE extends TileEntityMekanism & IHasGuiSizeOffset>
        extends MekanismTileContainer<BE> {

    public MekALDynamicSizedContainer(ContainerTypeRegistryObject<?> type, int id, Inventory inv, @NotNull BE tile) {
        super(type, id, inv, tile);
    }

    @Override
    protected int getInventoryXOffset() {
        return super.getInventoryXOffset() + (tile.getExtraWidth() / 2);
    }

    @Override
    protected int getInventoryYOffset() {
        return super.getInventoryYOffset() + tile.getExtraHeight();
    }

}