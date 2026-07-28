package com.takenokoshi.mekaddonlib.registration;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.takenokoshi.mekaddonlib.blockentity.interfaces.IHasGuiSizeOffset;
import com.takenokoshi.mekaddonlib.inventory.container.MekALDynamicSizedContainer;

import mekanism.api.text.ILangEntry;
import mekanism.common.attachments.component.AttachedEjector;
import mekanism.common.attachments.component.AttachedSideConfig;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.content.blocktype.Machine.MachineBuilder;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister.BlockEntityTypeBuilder;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tile.base.TileEntityMekanism;

public class GuiSizedMachineRegistryObject<BE extends TileEntityMekanism & IHasGuiSizeOffset> extends
        MachineRegistryObject<BE, BlockTileModel<BE, Machine<BE>>, MekALDynamicSizedContainer<BE>, ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>> {

    public GuiSizedMachineRegistryObject(String name, BlockDeferredRegister blockRegister,
            TileEntityTypeDeferredRegister tileRegister, ContainerTypeDeferredRegister containerRegister,
            Function<Machine<BE>, BlockTileModel<BE, Machine<BE>>> blockCreator,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor,
            UnaryOperator<BlockEntityTypeBuilder<BE>> beOperator, Class<BE> beClass,
            ILangEntry entry,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        super(name, blockRegister, tileRegister, containerRegister, blockCreator,
                (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                        .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                        .component(MekanismDataComponents.SIDE_CONFIG, attachedSideConfig)),
                holder, beConstructor, beOperator,
                beClass,
                MekALDynamicSizedContainer::new, entry, operator);
    }

    public GuiSizedMachineRegistryObject(String modid, String name, BlockDeferredRegister blockRegister,
            TileEntityTypeDeferredRegister tileRegister, ContainerTypeDeferredRegister containerRegister,
            Function<Machine<BE>, BlockTileModel<BE, Machine<BE>>> blockCreator,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor,
            UnaryOperator<BlockEntityTypeBuilder<BE>> beOperator, Class<BE> beClass,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        super(modid, name, blockRegister, tileRegister, containerRegister, blockCreator,
                (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                        .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                        .component(MekanismDataComponents.SIDE_CONFIG, attachedSideConfig)),
                holder, beConstructor, beOperator,
                beClass,
                MekALDynamicSizedContainer::new, operator);
    }

    public GuiSizedMachineRegistryObject(String name, BlockDeferredRegister blockRegister,
            TileEntityTypeDeferredRegister tileRegister, ContainerTypeDeferredRegister containerRegister,
            Function<Machine<BE>, BlockTileModel<BE, Machine<BE>>> blockCreator,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor, Class<BE> beClass,
            ILangEntry entry,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        super(name, blockRegister, tileRegister, containerRegister, blockCreator,
                (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                        .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                        .component(MekanismDataComponents.SIDE_CONFIG, attachedSideConfig)),
                holder, beConstructor,
                beClass,
                MekALDynamicSizedContainer::new, entry, operator);
    }

    public GuiSizedMachineRegistryObject(String modid, String name, BlockDeferredRegister blockRegister,
            TileEntityTypeDeferredRegister tileRegister, ContainerTypeDeferredRegister containerRegister,
            Function<Machine<BE>, BlockTileModel<BE, Machine<BE>>> blockCreator,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor, Class<BE> beClass,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        super(modid, name, blockRegister, tileRegister, containerRegister, blockCreator,
                (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                        .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                        .component(MekanismDataComponents.SIDE_CONFIG, attachedSideConfig)),
                holder, beConstructor,
                beClass,
                MekALDynamicSizedContainer::new, operator);
    }
}