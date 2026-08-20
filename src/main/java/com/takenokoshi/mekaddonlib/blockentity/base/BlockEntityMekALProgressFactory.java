package com.takenokoshi.mekaddonlib.blockentity.base;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;

import org.jetbrains.annotations.Nullable;

import com.takenokoshi.mekaddonlib.recipe.cached.ICachedRecipe;
import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.api.SerializationConstants;
import mekanism.api.Upgrade;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.common.inventory.container.MekanismContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockEntityMekALProgressFactory<RECIPE extends Recipe<?>>
        extends BlockEntityMekALRecipeFactory<RECIPE> {

    protected final int[] operatingTicks;
    protected final int[] ticksRequired;
    protected final int[] recipeTicksRequired;
    protected final ToIntFunction<RECIPE> recipeTicksGetter;

    public BlockEntityMekALProgressFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state,
            List<RecipeError> errorTypes, Set<RecipeError> globalErrorTypes, int baselineMaxOperations,
            ToIntFunction<RECIPE> recipeTicksGetter) {
        super(blockProvider, pos, state, errorTypes, globalErrorTypes, baselineMaxOperations);
        this.recipeTicksGetter = recipeTicksGetter;
        this.ticksRequired = new int[tier.processes];
        this.operatingTicks = new int[tier.processes];
        this.recipeTicksRequired = new int[tier.processes];
        Arrays.fill(ticksRequired, 200);
        Arrays.fill(recipeTicksRequired, 200);
    }

    public int getProgress(int cacheIndex) {
        return operatingTicks[cacheIndex];
    }

    @Override
    public int getSavedOperatingTicks(int cacheIndex) {
        return getProgress(cacheIndex);
    }

    @Override
    public double getScaledProgress(int cacheIndex) {
        return ((double) getProgress(cacheIndex)) / (double) ticksRequired[cacheIndex];
    }

    @Override
    public void onCachedRecipeChanged(@Nullable ICachedRecipe<RECIPE> cachedRecipe, int cacheIndex) {
        super.onCachedRecipeChanged(cachedRecipe, cacheIndex);
        if (cachedRecipe != null) {
            recipeTicksRequired[cacheIndex] = recipeTicksGetter.applyAsInt(cachedRecipe.getRecipe());
        }
        recalculateProcessingSpeed(cacheIndex);
    }

    protected abstract void recalculateProcessingSpeed(int cacheIndex);

    public void recalculateUpgrades(Upgrade upgrade) {
        super.recalculateUpgrades(upgrade);
        if (upgrade == Upgrade.SPEED
                || upgrade.name().equals("EMPOWERED_SPEED")
                || AdditionalUpgradeUtils.isSpeedModifier(upgrade)) {
            for (int i = 0; i < tier.processes; i++) {
                recalculateProcessingSpeed(i);
            }
        }
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.trackArray(operatingTicks);
        container.trackArray(ticksRequired);
        container.trackArray(recipeTicksRequired);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putIntArray(SerializationConstants.PROGRESS, operatingTicks);
    }

}
