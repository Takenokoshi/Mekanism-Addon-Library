package com.takenokoshi.mekaddonlib.recipe.inputcache;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.takenokoshi.mekaddonlib.recipe.type.MekALRecipeType;

import mekanism.common.recipe.lookup.cache.IInputRecipeCache;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public abstract class MekALAbstractInputRecipeCache<RECIPE extends Recipe<?>> implements IInputRecipeCache {
    protected final MekALRecipeType<?, RECIPE, ?> recipeType;
    protected boolean initialized;

    protected MekALAbstractInputRecipeCache(MekALRecipeType<?, RECIPE, ?> recipeType) {
        this.recipeType = recipeType;
    }

    protected void initCacheIfNeeded(@Nullable Level world) {
        if (!initialized) {
            initialized = true;
            initCache(recipeType.getRecipes(world));
        }
    }

    @Override
    public void clear() {
        initialized = false;
    }

    protected abstract void initCache(List<RecipeHolder<RECIPE>> recipes);
}