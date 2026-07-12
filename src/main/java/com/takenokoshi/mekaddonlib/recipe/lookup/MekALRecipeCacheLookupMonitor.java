package com.takenokoshi.mekaddonlib.recipe.lookup;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.takenokoshi.mekaddonlib.recipe.cached.ICachedRecipe;

import mekanism.api.IContentsListener;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.CommonWorldTickHandler;
import net.minecraft.world.item.crafting.Recipe;

public class MekALRecipeCacheLookupMonitor<RECIPE extends Recipe<?>>
        implements IMekALCachedRecipeHolder<RECIPE>, IContentsListener {

    private final IMekALRecipeLookupHandler<RECIPE> handler;
    protected final int cacheIndex;
    protected ICachedRecipe<RECIPE> cachedRecipe;
    protected boolean hasNoRecipe;
    protected boolean shouldUnpause;

    public MekALRecipeCacheLookupMonitor(IMekALRecipeLookupHandler<RECIPE> handler) {
        this(handler, 0);
    }

    public MekALRecipeCacheLookupMonitor(IMekALRecipeLookupHandler<RECIPE> handler, int cacheIndex) {
        this.handler = handler;
        this.cacheIndex = cacheIndex;
    }

    protected boolean cachedIndexMatches(int cacheIndex) {
        return this.cacheIndex == cacheIndex;
    }

    public final void onContentsChanged() {
        this.handler.onContentsChanged();
        this.onChange();
    }

    public void onChange() {
        this.hasNoRecipe = false;
        this.unpause();
    }

    public void unpause() {
        this.shouldUnpause = true;
    }

    public long updateAndProcess(IEnergyContainer energyContainer) {
        long prev = energyContainer.getEnergy();
        return this.updateAndProcess() ? Math.max(0L, prev - energyContainer.getEnergy()) : 0L;
    }

    public boolean updateAndProcess() {
        ICachedRecipe<RECIPE> oldCache = this.cachedRecipe;
        this.cachedRecipe = this.getUpdatedCache(this.cacheIndex);
        if (this.cachedRecipe != oldCache) {
            this.handler.onCachedRecipeChanged(this.cachedRecipe, this.cacheIndex);
        }

        if (this.cachedRecipe != null) {
            if (this.shouldUnpause) {
                this.shouldUnpause = false;
                this.cachedRecipe.unpauseErrors();
            }

            this.cachedRecipe.process();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void loadSavedData(@NotNull ICachedRecipe<RECIPE> cached, int cacheIndex) {
        if (cachedIndexMatches(cacheIndex)) {
            IMekALCachedRecipeHolder.super.loadSavedData(cached, cacheIndex);
        }
    }

    @Override
    public int getSavedOperatingTicks(int cacheIndex) {
        return cachedIndexMatches(cacheIndex) ? handler.getSavedOperatingTicks(cacheIndex)
                : IMekALCachedRecipeHolder.super.getSavedOperatingTicks(cacheIndex);
    }

    @Override
    public @Nullable ICachedRecipe<RECIPE> getCachedRecipe(int cacheIndex) {
        return cachedIndexMatches(cacheIndex) ? cachedRecipe : null;
    }

    @Override
    public @Nullable RECIPE getRecipe(int cacheIndex) {
        return cachedIndexMatches(cacheIndex) ? handler.getRecipe(cacheIndex) : null;
    }

    @Override
    public @Nullable ICachedRecipe<RECIPE> createNewCachedRecipe(@NotNull RECIPE recipe, int cacheIndex) {
        return cachedIndexMatches(cacheIndex) ? handler.createNewCachedRecipe(recipe, cacheIndex) : null;
    }

    @Override
    public boolean invalidateCache() {
        return CommonWorldTickHandler.flushTagAndRecipeCaches;
    }

    @Override
    public void setHasNoRecipe(int cacheIndex) {
        if (cachedIndexMatches(cacheIndex)) {
            hasNoRecipe = true;
        }
    }

    @Override
    public boolean hasNoRecipe(int cacheIndex) {
        return cachedIndexMatches(cacheIndex) ? hasNoRecipe : IMekALCachedRecipeHolder.super.hasNoRecipe(cacheIndex);
    }
}
