package com.takenokoshi.mekaddonlib.blockentity.base;

import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;

import org.jetbrains.annotations.NotNull;

import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.api.Upgrade;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.common.util.UpgradeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BEExpScaledProgressFactory<RECIPE extends MekanismRecipe<?>>
        extends BlockEntityMekALProgressFactory<RECIPE> {

    public BEExpScaledProgressFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state,
            List<RecipeError> errorTypes, Set<RecipeError> globalErrorTypes, int baselineMaxOperations,
            ToIntFunction<RECIPE> recipeTicksGetter) {
        super(blockProvider, pos, state, errorTypes, globalErrorTypes, baselineMaxOperations, recipeTicksGetter);
    }

    public @NotNull List<Component> getInfo(@NotNull Upgrade upgrade) {
        return UpgradeUtils.getExpScaledInfo(this, upgrade);
    }

    @Override
    protected void recalculateProcessingSpeed(int cacheIndex) {
        int speedFactor = 1 << upgradeComponent.getUpgrades(Upgrade.SPEED);
        speedFactor = AdditionalUpgradeUtils.modifyOperations(this, speedFactor);
        if (speedFactor > recipeTicksRequired[cacheIndex]) {
            operationsPerTicks[cacheIndex] = MathUtils
                    .clampToInt(1l * speedFactor / recipeTicksRequired[cacheIndex] * baselineMaxOperations);
            ticksRequired[cacheIndex] = 1;
        } else {
            operationsPerTicks[cacheIndex] = baselineMaxOperations;
            ticksRequired[cacheIndex] = MathUtils.clampToInt(1l * recipeTicksRequired[cacheIndex] / speedFactor);
        }
    }

}
