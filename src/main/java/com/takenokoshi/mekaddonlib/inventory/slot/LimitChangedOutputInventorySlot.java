package com.takenokoshi.mekaddonlib.inventory.slot;

import org.jetbrains.annotations.Nullable;

import mekanism.api.IContentsListener;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.slot.BasicInventorySlot;
import net.minecraft.world.item.ItemStack;

public class LimitChangedOutputInventorySlot extends BasicInventorySlot {

    private final int changedLimit;

    protected LimitChangedOutputInventorySlot(@Nullable IContentsListener listener, int x, int y, int limit) {
        super(ConstantPredicates.alwaysTrueBi(), ConstantPredicates.internalOnly(), ConstantPredicates.alwaysTrue(),
                listener, x, y);
        this.setSlotType(ContainerSlotType.OUTPUT);
        this.changedLimit = limit;
    }

    @Override
    public int getLimit(ItemStack stack) {
        return changedLimit;
    }

    public static LimitChangedOutputInventorySlot at(@Nullable IContentsListener listener, int x, int y, int limit) {
        return new LimitChangedOutputInventorySlot(listener, x, y, limit);
    }
}
