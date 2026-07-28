package com.takenokoshi.mekaddonlib.registration;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.IHasTranslationKey;
import mekanism.api.text.ILangEntry;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.content.blocktype.Machine.MachineBuilder;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.INamedEntry;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister.BlockEntityTypeBuilder;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public class MachineRegistryObject<BE extends TileEntityMekanism, BLOCK extends BlockTileModel<BE, Machine<BE>>, CONTAINER extends MekanismTileContainer<BE>, ITEM extends BlockItem>
        implements INamedEntry, ItemLike, IHasTextComponent, IHasTranslationKey {

    private final BlockRegistryObject<BLOCK, ITEM> blockRegistryObject;
    private final TileEntityTypeRegistryObject<BE> tileRegistryObject;
    private final ContainerTypeRegistryObject<CONTAINER> containerRegistryObject;
    private final Machine<BE> blockType;
    public final ILangEntry descriptionEntry;

    public MachineRegistryObject(
            String name,
            BlockDeferredRegister blockRegister,
            TileEntityTypeDeferredRegister tileRegister,
            ContainerTypeDeferredRegister containerRegister,
            Function<Machine<BE>, BLOCK> blockCreator,
            BiFunction<BLOCK, Item.Properties, ITEM> itemCreator,
            Consumer<ItemRegistryObject<ITEM>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BLOCK> beConstructor,
            UnaryOperator<BlockEntityTypeBuilder<BE>> beOperator,
            Class<BE> beClass,
            ContainerConstructor<BE, CONTAINER> contConstructor,
            ILangEntry entry,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {

        blockType = operator.apply(MachineBuilder.createMachine(this::getTile, entry))
                .withGui(this::getContainer)
                .build();
        blockRegistryObject = blockRegister.register(name, () -> blockCreator.apply(blockType), itemCreator)
                .forItemHolder(holder);
        tileRegistryObject = beOperator.apply(tileRegister
                .mekBuilder(blockRegistryObject,
                        (p, s) -> beConstructor.create(blockRegistryObject, p, s))
                .clientTicker(BE::tickClient)
                .serverTicker(BE::tickServer))
                .build();
        containerRegistryObject = containerRegister.register(name, beClass,
                (id, inv, be) -> contConstructor.create(getContainer(), id, inv, be));
        this.descriptionEntry = entry;

    }

    public MachineRegistryObject(
            String name,
            BlockDeferredRegister blockRegister,
            TileEntityTypeDeferredRegister tileRegister,
            ContainerTypeDeferredRegister containerRegister,
            Function<Machine<BE>, BLOCK> blockCreator,
            BiFunction<BLOCK, Item.Properties, ITEM> itemCreator,
            Consumer<ItemRegistryObject<ITEM>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BLOCK> beConstructor,
            Class<BE> beClass,
            ContainerConstructor<BE, CONTAINER> contConstructor,
            ILangEntry entry,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        this(name, blockRegister, tileRegister, containerRegister, blockCreator, itemCreator, holder, beConstructor,
                builder -> builder, beClass, contConstructor, entry, operator);
    }

    public MachineRegistryObject(
            String modid,
            String name,
            BlockDeferredRegister blockRegister,
            TileEntityTypeDeferredRegister tileRegister,
            ContainerTypeDeferredRegister containerRegister,
            Function<Machine<BE>, BLOCK> blockCreator,
            BiFunction<BLOCK, Item.Properties, ITEM> itemCreator,
            Consumer<ItemRegistryObject<ITEM>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BLOCK> beConstructor,
            UnaryOperator<BlockEntityTypeBuilder<BE>> beOperator,
            Class<BE> beClass,
            ContainerConstructor<BE, CONTAINER> contConstructor,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        this(name, blockRegister, tileRegister, containerRegister, blockCreator, itemCreator, holder, beConstructor,
                beOperator, beClass, contConstructor, new MachineDescription(modid, name), operator);
    }

    public MachineRegistryObject(
            String modid,
            String name,
            BlockDeferredRegister blockRegister,
            TileEntityTypeDeferredRegister tileRegister,
            ContainerTypeDeferredRegister containerRegister,
            Function<Machine<BE>, BLOCK> blockCreator,
            BiFunction<BLOCK, Item.Properties, ITEM> itemCreator,
            Consumer<ItemRegistryObject<ITEM>> holder,
            BlockEntityConstructor<BE, Machine<BE>, BLOCK> beConstructor,
            Class<BE> beClass,
            ContainerConstructor<BE, CONTAINER> contConstructor,
            UnaryOperator<MachineBuilder<Machine<BE>, BE, ?>> operator) {
        this(name, blockRegister, tileRegister, containerRegister, blockCreator, itemCreator, holder, beConstructor,
                beClass, contConstructor, new MachineDescription(modid, name), operator);
    }

    public BlockRegistryObject<BLOCK, ITEM> getBlock() {
        return blockRegistryObject;
    }

    public TileEntityTypeRegistryObject<BE> getTile() {
        return tileRegistryObject;
    }

    public ContainerTypeRegistryObject<CONTAINER> getContainer() {
        return containerRegistryObject;
    }

    public BlockTypeTile<BE> getBlockType() {
        return blockType;
    }

    @Override
    public ResourceLocation getId() {
        return blockRegistryObject.getId();
    }

    @Override
    public Item asItem() {
        return blockRegistryObject.asItem();
    }

    @Override
    public Component getTextComponent() {
        return blockRegistryObject.getTextComponent();
    }

    @Override
    public String getTranslationKey() {
        return blockRegistryObject.getTranslationKey();
    }

    private static class MachineDescription implements ILangEntry {
        private final String key;

        private MachineDescription(String modid, String machineName) {
            this.key = Util.makeDescriptionId("description", ResourceLocation.fromNamespaceAndPath(modid, machineName));
        }

        @Override
        public String getTranslationKey() {
            return key;
        };

    }
}
