package com.takenokoshi.mekaddonlib.capabilities.energy;

import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.block.attribute.AttributeEnergy;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.tile.base.TileEntityMekanism;

public class VariableUsageMachineEnergyContainer<BE extends TileEntityMekanism> extends MachineEnergyContainer<BE> {

    private long additionalUsage;

    public static <BE extends TileEntityMekanism> VariableUsageMachineEnergyContainer<BE> input(BE be,
            @Nullable IContentsListener listener) {
        AttributeEnergy electricBlock = validateBlock(be);
        return new VariableUsageMachineEnergyContainer<>(electricBlock.getStorage(), electricBlock.getUsage(),
                notExternal, ConstantPredicates.alwaysTrue(), be, listener);
    }

    private VariableUsageMachineEnergyContainer(long maxEnergy, long energyPerTick,
            Predicate<@NotNull AutomationType> canExtract, Predicate<@NotNull AutomationType> canInsert, BE tile,
            @Nullable IContentsListener listener) {
        super(maxEnergy, energyPerTick, canExtract, canInsert, tile, listener);
    }

    public void updateAdditionalUsage(long value) {
        additionalUsage = value;
    }

    @Override
    public long getBaseEnergyPerTick() {
        return super.getBaseEnergyPerTick() + additionalUsage;
    }

    public void track(MekanismContainer container) {
        container.track(SyncableLong.create(() -> additionalUsage, this::updateAdditionalUsage));
    }

}