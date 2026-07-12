package com.takenokoshi.mekaddonlib.recipe.lookup;

import org.jetbrains.annotations.NotNull;

import com.takenokoshi.mekaddonlib.recipe.type.IMekALRecipeTypeProvider;

import mekanism.common.recipe.lookup.cache.IInputRecipeCache;
import net.minecraft.world.item.crafting.Recipe;

public interface IMekALRecipeTypedLookupHandler<RECIPE extends Recipe<?>, INPUT_CACHE extends IInputRecipeCache>
        extends IMekALRecipeLookupHandler<RECIPE> {

    @NotNull
    @Override
    IMekALRecipeTypeProvider<?, RECIPE, INPUT_CACHE> getRecipeType();
}
