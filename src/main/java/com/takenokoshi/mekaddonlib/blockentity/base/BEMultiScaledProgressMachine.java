package com.takenokoshi.mekaddonlib.blockentity.base;

import java.util.List;
import java.util.function.ToIntFunction;

import org.jetbrains.annotations.NotNull;

import mekanism.api.Upgrade;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UpgradeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BEMultiScaledProgressMachine<RECIPE extends Recipe<?>>
        extends BlockEntityMekALProgressMachine<RECIPE> {

    public BEMultiScaledProgressMachine(Holder<Block> blockProvider, BlockPos pos, BlockState state,
            List<RecipeError> errorTypes, int baselineMaxOperations, ToIntFunction<RECIPE> recipeTicksGetter) {
        super(blockProvider, pos, state, errorTypes, baselineMaxOperations, recipeTicksGetter);
    }

    public @NotNull List<Component> getInfo(@NotNull Upgrade upgrade) {
        return UpgradeUtils.getMultScaledInfo(this, upgrade);
    }

    protected void recalculateProcessingSpeed() {
        double ticksD = MekanismUtils.getTicksD(this, recipeTicksRequired);
        if (ticksD < 1) {
            operationsPerTick = MathUtils.clampToInt(baselineMaxOperations / ticksD);
            ticksRequired = 1;
        } else {
            operationsPerTick = baselineMaxOperations;
            ticksRequired = MathUtils.clampToInt(ticksD);
        }
    }
}