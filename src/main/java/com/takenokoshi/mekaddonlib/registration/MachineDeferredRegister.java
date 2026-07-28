package com.takenokoshi.mekaddonlib.registration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.takenokoshi.mekaddonlib.blockentity.interfaces.IHasGuiSizeOffset;

import mekanism.api.text.ILangEntry;
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
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;

public class MachineDeferredRegister {

    private final String modId;
    public final BlockDeferredRegister blockRegister;
    private final TileEntityTypeDeferredRegister tileRegister;
    private final ContainerTypeDeferredRegister containerRegister;
    private final List<MachineRegistryObject<?, ?, ?, ?>> machines;

    public MachineDeferredRegister(String modId) {
        this.modId = modId;
        this.blockRegister = new BlockDeferredRegister(this.modId);
        this.tileRegister = new TileEntityTypeDeferredRegister(this.modId);
        this.containerRegister = new ContainerTypeDeferredRegister(this.modId);
        this.machines = new ArrayList<>();
    }

    public void register(IEventBus modEventBus) {
        blockRegister.register(modEventBus);
        tileRegister.register(modEventBus);
        containerRegister.register(modEventBus);
    }

    public List<MachineRegistryObject<?, ?, ?, ?>> getMachines() {
        return Collections.unmodifiableList(machines);
    }

    public <BE extends TileEntityMekanism, BLOCK extends BlockTileModel<BE, Machine<BE>>, CONTAINER extends MekanismTileContainer<BE>, ITEM extends BlockItem> MachineRegistryObject<BE, BLOCK, CONTAINER, ITEM> registerFull(
            String name, Function<Machine<BE>, BLOCK> blockCreator,
            BiFunction<BLOCK, Item.Properties, ITEM> itemCreator,
            Consumer<ItemRegistryObject<ITEM>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BLOCK> beConstructor,
            UnaryOperator<BlockEntityTypeBuilder<BE>> beOperator, Class<BE> beClass,
            ContainerConstructor<BE, CONTAINER> contConstructor, ILangEntry entry,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        MachineRegistryObject<BE, BLOCK, CONTAINER, ITEM> result = new MachineRegistryObject<>(name, blockRegister,
                tileRegister, containerRegister, blockCreator,
                itemCreator, holder, beConstructor, beOperator, beClass, contConstructor, entry, operator);
        machines.add(result);
        return result;
    }

    public <BE extends TileEntityMekanism, BLOCK extends BlockTileModel<BE, Machine<BE>>, CONTAINER extends MekanismTileContainer<BE>, ITEM extends BlockItem> MachineRegistryObject<BE, BLOCK, CONTAINER, ITEM> registerFull(
            String name, Function<Machine<BE>, BLOCK> blockCreator,
            BiFunction<BLOCK, Item.Properties, ITEM> itemCreator,
            Consumer<ItemRegistryObject<ITEM>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BLOCK> beConstructor, Class<BE> beClass,
            ContainerConstructor<BE, CONTAINER> contConstructor, ILangEntry entry,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        MachineRegistryObject<BE, BLOCK, CONTAINER, ITEM> result = new MachineRegistryObject<>(name, blockRegister,
                tileRegister, containerRegister, blockCreator,
                itemCreator, holder, beConstructor, beClass, contConstructor, entry, operator);
        machines.add(result);
        return result;
    }

    public <BE extends TileEntityMekanism & IHasGuiSizeOffset> GuiSizedMachineRegistryObject<BE> registerGuiSized(
            String name,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor,
            UnaryOperator<BlockEntityTypeBuilder<BE>> beOperator,
            Class<BE> beClass, ILangEntry entry,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        GuiSizedMachineRegistryObject<BE> result = new GuiSizedMachineRegistryObject<>(name,
                blockRegister, tileRegister, containerRegister,
                bt -> new BlockTileModel<>(bt, p -> p.strength(1.5f, 6.0f)
                        .sound(SoundType.STONE).mapColor(MapColor.STONE)),
                attachedSideConfig,
                holder, beConstructor, beOperator, beClass, entry, operator);
        machines.add(result);
        return result;
    }

    public <BE extends TileEntityMekanism & IHasGuiSizeOffset> GuiSizedMachineRegistryObject<BE> registerGuiSized(
            String name,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor,
            UnaryOperator<BlockEntityTypeBuilder<BE>> beOperator,
            Class<BE> beClass,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        GuiSizedMachineRegistryObject<BE> result = new GuiSizedMachineRegistryObject<>(modId, name,
                blockRegister, tileRegister, containerRegister,
                bt -> new BlockTileModel<>(bt, p -> p.strength(1.5f, 6.0f)
                        .sound(SoundType.STONE).mapColor(MapColor.STONE)),
                attachedSideConfig,
                holder, beConstructor, beOperator, beClass, operator);
        machines.add(result);
        return result;
    }

    public <BE extends TileEntityMekanism & IHasGuiSizeOffset> GuiSizedMachineRegistryObject<BE> registerGuiSized(
            String name,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor,
            Class<BE> beClass, ILangEntry entry,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        GuiSizedMachineRegistryObject<BE> result = new GuiSizedMachineRegistryObject<>(name,
                blockRegister, tileRegister, containerRegister,
                bt -> new BlockTileModel<>(bt, p -> p.strength(1.5f, 6.0f)
                        .sound(SoundType.STONE).mapColor(MapColor.STONE)),
                attachedSideConfig,
                holder, beConstructor, beClass, entry, operator);
        machines.add(result);
        return result;
    }

    public <BE extends TileEntityMekanism & IHasGuiSizeOffset> GuiSizedMachineRegistryObject<BE> registerGuiSized(
            String name,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor,
            Class<BE> beClass,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        GuiSizedMachineRegistryObject<BE> result = new GuiSizedMachineRegistryObject<>(modId, name,
                blockRegister, tileRegister, containerRegister,
                bt -> new BlockTileModel<>(bt, p -> p.strength(1.5f, 6.0f)
                        .sound(SoundType.STONE).mapColor(MapColor.STONE)),
                attachedSideConfig,
                holder, beConstructor, beClass, operator);
        machines.add(result);
        return result;
    }

    public <BE extends TileEntityMekanism> SimpleMachineRegistryObject<BE> registerSimple(String name,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor,
            UnaryOperator<BlockEntityTypeBuilder<BE>> beOperator,
            Class<BE> beClass, ILangEntry entry,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        SimpleMachineRegistryObject<BE> result = new SimpleMachineRegistryObject<>(name,
                blockRegister, tileRegister, containerRegister,
                bt -> new BlockTileModel<>(bt, p -> p.strength(1.5f, 6.0f)
                        .sound(SoundType.STONE).mapColor(MapColor.STONE)),
                attachedSideConfig,
                holder, beConstructor, beOperator, beClass, entry, operator);
        machines.add(result);
        return result;
    }

    public <BE extends TileEntityMekanism> SimpleMachineRegistryObject<BE> registerSimple(String name,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor,
            UnaryOperator<BlockEntityTypeBuilder<BE>> beOperator,
            Class<BE> beClass,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        SimpleMachineRegistryObject<BE> result = new SimpleMachineRegistryObject<>(modId, name,
                blockRegister, tileRegister, containerRegister,
                bt -> new BlockTileModel<>(bt, p -> p.strength(1.5f, 6.0f)
                        .sound(SoundType.STONE).mapColor(MapColor.STONE)),
                attachedSideConfig,
                holder, beConstructor, beOperator, beClass, operator);
        machines.add(result);
        return result;
    }

    public <BE extends TileEntityMekanism> SimpleMachineRegistryObject<BE> registerSimple(String name,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor,
            Class<BE> beClass, ILangEntry entry,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        SimpleMachineRegistryObject<BE> result = new SimpleMachineRegistryObject<>(name,
                blockRegister, tileRegister, containerRegister,
                bt -> new BlockTileModel<>(bt, p -> p.strength(1.5f, 6.0f)
                        .sound(SoundType.STONE).mapColor(MapColor.STONE)),
                attachedSideConfig,
                holder, beConstructor, beClass, entry, operator);
        machines.add(result);
        return result;
    }

    public <BE extends TileEntityMekanism> SimpleMachineRegistryObject<BE> registerSimple(String name,
            AttachedSideConfig attachedSideConfig,
            Consumer<ItemRegistryObject<ItemBlockTooltip<BlockTileModel<BE, Machine<BE>>>>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BlockTileModel<BE, Machine<BE>>> beConstructor,
            Class<BE> beClass,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        SimpleMachineRegistryObject<BE> result = new SimpleMachineRegistryObject<>(modId, name,
                blockRegister, tileRegister, containerRegister,
                bt -> new BlockTileModel<>(bt, p -> p.strength(1.5f, 6.0f)
                        .sound(SoundType.STONE).mapColor(MapColor.STONE)),
                attachedSideConfig,
                holder, beConstructor, beClass, operator);
        machines.add(result);
        return result;
    }
}