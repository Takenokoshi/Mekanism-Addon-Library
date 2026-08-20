package com.takenokoshi.mekaddonlib.blockentity.base;

import java.util.List;
import java.util.function.ToIntFunction;

import org.jetbrains.annotations.NotNull;

import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.api.Upgrade;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.common.util.UpgradeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BEExpScaledProgressMachine<RECIPE extends Recipe<?>>
        extends BlockEntityMekALProgressMachine<RECIPE> {

    public BEExpScaledProgressMachine(Holder<Block> blockProvider, BlockPos pos, BlockState state,
            List<RecipeError> errorTypes, int baselineMaxOperations, ToIntFunction<RECIPE> recipeTicksGetter) {
        super(blockProvider, pos, state, errorTypes, baselineMaxOperations, recipeTicksGetter);
    }

    public @NotNull List<Component> getInfo(@NotNull Upgrade upgrade) {
        return UpgradeUtils.getExpScaledInfo(this, upgrade);
    }

    @Override
    protected void recalculateProcessingSpeed() {
        int speedFactor = 1 << upgradeComponent.getUpgrades(Upgrade.SPEED);
        speedFactor = AdditionalUpgradeUtils.modifyOperations(this, speedFactor);
        if (speedFactor > recipeTicksRequired) {
            operationsPerTick = MathUtils.clampToInt(1l * speedFactor / recipeTicksRequired * baselineMaxOperations);
            ticksRequired = 1;
        } else {
            operationsPerTick = baselineMaxOperations;
            ticksRequired = MathUtils.clampToInt(1l * recipeTicksRequired / speedFactor);
        }
    }

}
