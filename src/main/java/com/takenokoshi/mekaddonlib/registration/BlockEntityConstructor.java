package com.takenokoshi.mekaddonlib.registration;

import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface BlockEntityConstructor<BE extends TileEntityMekanism, BLOCKTYPE extends BlockTypeTile<BE>, BLOCK extends BlockTileModel<BE, BLOCKTYPE>> {
    public BE create(
            BlockRegistryObject<BLOCK, ? extends BlockItem> registryObject,
            BlockPos pos, BlockState state);
}
