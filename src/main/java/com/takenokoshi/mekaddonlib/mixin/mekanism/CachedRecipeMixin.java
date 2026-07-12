package com.takenokoshi.mekaddonlib.mixin.mekanism;

import org.spongepowered.asm.mixin.Mixin;

import com.takenokoshi.mekaddonlib.recipe.cached.ICachedRecipe.IMekanismCachedRecipe;

import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;

@Mixin(value = { CachedRecipe.class }, remap = false)
public abstract class CachedRecipeMixin<RECIPE extends MekanismRecipe<?>> implements IMekanismCachedRecipe<RECIPE> {
}
