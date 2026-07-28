package com.takenokoshi.mekaddonlib.registration;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import mekanism.api.text.ILangEntry;
import mekanism.common.attachments.component.AttachedEjector;
import mekanism.common.attachments.component.AttachedSideConfig;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.content.blocktype.Machine.MachineBuilder;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister.BlockEntityTypeBuilder;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tile.base.TileEntityMekanism;

public class SimpleMachineRegistryObject<BE extends TileEntityMekanism> extends
        MachineRegistryObject<BE, BlockTileModel<BE, Machine<BE>>, MekanismTileContainer<BE>, ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>> {

    public SimpleMachineRegistryObject(String name, BlockDeferredRegister blockRegister,
            TileEntityTypeDeferredRegister tileRegister, ContainerTypeDeferredRegister containerRegister,
            Function<Machine<BE>, BlockTileModel<BE, Machine<BE>>> blockCreator,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor,
            UnaryOperator<BlockEntityTypeBuilder<BE>> beOperator,
            Class<BE> beClass, ILangEntry entry,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        super(name, blockRegister, tileRegister, containerRegister, blockCreator,
                (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                        .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                        .component(MekanismDataComponents.SIDE_CONFIG, attachedSideConfig)),
                holder, beConstructor, beOperator, beClass,
                MekanismTileContainer::new, entry, operator);
    }

    public SimpleMachineRegistryObject(String modid, String name, BlockDeferredRegister blockRegister,
            TileEntityTypeDeferredRegister tileRegister, ContainerTypeDeferredRegister containerRegister,
            Function<Machine<BE>, BlockTileModel<BE, Machine<BE>>> blockCreator,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor,
            UnaryOperator<BlockEntityTypeBuilder<BE>> beOperator,
            Class<BE> beClass,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        super(modid, name, blockRegister, tileRegister, containerRegister, blockCreator,
                (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                        .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                        .component(MekanismDataComponents.SIDE_CONFIG, attachedSideConfig)),
                holder, beConstructor, beOperator, beClass,
                MekanismTileContainer::new, operator);
    }

    public SimpleMachineRegistryObject(String name, BlockDeferredRegister blockRegister,
            TileEntityTypeDeferredRegister tileRegister, ContainerTypeDeferredRegister containerRegister,
            Function<Machine<BE>, BlockTileModel<BE, Machine<BE>>> blockCreator,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor,
            Class<BE> beClass, ILangEntry entry,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        super(name, blockRegister, tileRegister, containerRegister, blockCreator,
                (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                        .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                        .component(MekanismDataComponents.SIDE_CONFIG, attachedSideConfig)),
                holder, beConstructor, beClass,
                MekanismTileContainer::new, entry, operator);
    }

    public SimpleMachineRegistryObject(String modid, String name, BlockDeferredRegister blockRegister,
            TileEntityTypeDeferredRegister tileRegister, ContainerTypeDeferredRegister containerRegister,
            Function<Machine<BE>, BlockTileModel<BE, Machine<BE>>> blockCreator,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor,
            Class<BE> beClass,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        super(modid, name, blockRegister, tileRegister, containerRegister, blockCreator,
                (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                        .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                        .component(MekanismDataComponents.SIDE_CONFIG, attachedSideConfig)),
                holder, beConstructor, beClass,
                MekanismTileContainer::new, operator);
    }
}