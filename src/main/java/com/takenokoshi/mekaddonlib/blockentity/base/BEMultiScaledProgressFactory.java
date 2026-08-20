package com.takenokoshi.mekaddonlib.blockentity.base;

import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;

import mekanism.api.math.MathUtils;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.common.util.MekanismUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BEMultiScaledProgressFactory<RECIPE extends Recipe<?>>
        extends BlockEntityMekALProgressFactory<RECIPE> {

    public BEMultiScaledProgressFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state,
            List<RecipeError> errorTypes, Set<RecipeError> globalErrorTypes, int baselineMaxOperations,
            ToIntFunction<RECIPE> recipeTicksGetter) {
        super(blockProvider, pos, state, errorTypes, globalErrorTypes, baselineMaxOperations, recipeTicksGetter);
    }

    @Override
    protected void recalculateProcessingSpeed(int cacheIndex) {
        double ticksD = MekanismUtils.getTicksD(this, recipeTicksRequired[cacheIndex]);
        if (ticksD < 1) {
            operationsPerTicks[cacheIndex] = MathUtils.clampToInt(baselineMaxOperations / ticksD);
            ticksRequired[cacheIndex] = 1;
        } else {
            operationsPerTicks[cacheIndex] = baselineMaxOperations;
            ticksRequired[cacheIndex] = MathUtils.clampToInt(ticksD);
        }
    }

}
