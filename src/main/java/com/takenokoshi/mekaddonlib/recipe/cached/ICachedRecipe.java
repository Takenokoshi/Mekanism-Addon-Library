package com.takenokoshi.mekaddonlib.recipe.cached;

import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import net.minecraft.world.item.crafting.Recipe;

public interface ICachedRecipe<RECIPE extends Recipe<?>> {

    void unpauseErrors();

    void loadSavedOperatingTicks(int operatingTicks);

    void process();

    boolean isInputValid();

    RECIPE getRecipe();

    @SuppressWarnings("unchecked")
    static <R extends MekanismRecipe<?>> IMekanismCachedRecipe<R> fromMekanism(CachedRecipe<R> cachedRecipe) {
        if (!(cachedRecipe instanceof IMekanismCachedRecipe<?> recipe)) {
            throw new IllegalStateException("CachedRecipe mixin missing");
        }
        return (IMekanismCachedRecipe<R>) recipe;
    }

    public static interface IMekanismCachedRecipe<RECIPE extends MekanismRecipe<?>> extends ICachedRecipe<RECIPE> {
        @Override
        RECIPE getRecipe();
    }
}