package com.takenokoshi.mekaddonlib.blockentity.interfaces;

import java.util.function.BooleanSupplier;

import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;

public interface IWarningSupporter {
    public BooleanSupplier getWarningCheck(RecipeError error);
}