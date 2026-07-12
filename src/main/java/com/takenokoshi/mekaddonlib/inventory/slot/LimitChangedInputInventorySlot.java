package com.takenokoshi.mekaddonlib.inventory.slot;

import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mekanism.api.IContentsListener;
import mekanism.common.inventory.slot.InputInventorySlot;
import net.minecraft.world.item.ItemStack;

public class LimitChangedInputInventorySlot extends InputInventorySlot {

    private final int changedLimit;

    protected LimitChangedInputInventorySlot(Predicate<@NotNull ItemStack> insertPredicate,
            Predicate<@NotNull ItemStack> isItemValid, @Nullable IContentsListener listener, int x, int y, int limit) {
        super(insertPredicate, isItemValid, listener, x, y);
        this.changedLimit = limit;
    }

    @Override
    public int getLimit(ItemStack stack) {
        return changedLimit;
    }

    public static LimitChangedInputInventorySlot at(Predicate<@NotNull ItemStack> insertPredicate,
            Predicate<@NotNull ItemStack> isItemValid, @Nullable IContentsListener listener, int x, int y, int limit) {
        return new LimitChangedInputInventorySlot(insertPredicate, isItemValid, listener, x, y, limit);
    }

}
