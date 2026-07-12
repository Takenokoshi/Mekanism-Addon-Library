package com.takenokoshi.mekaddonlib.registration;

import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.world.entity.player.Inventory;

@FunctionalInterface
public interface ContainerConstructor<TILE extends TileEntityMekanism, CONTAINER extends MekanismTileContainer<TILE>> {
    CONTAINER create(ContainerTypeRegistryObject<CONTAINER> type, int id, Inventory inv, TILE tile);
}