package com.takenokoshi.mekaddonlib.registration;

import com.takenokoshi.mekaddonlib.recipe.type.IMekALRecipeTypeProvider;
import com.takenokoshi.mekaddonlib.recipe.type.MekALRecipeType;

import mekanism.common.recipe.lookup.cache.IInputRecipeCache;
import mekanism.common.registration.MekanismDeferredHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

public class MekALRecipeTypeRegistryObject<VANILLA_INPUT extends RecipeInput, RECIPE extends Recipe<VANILLA_INPUT>, INPUT_CACHE extends IInputRecipeCache>
        extends MekanismDeferredHolder<RecipeType<?>, MekALRecipeType<VANILLA_INPUT, RECIPE, INPUT_CACHE>>
        implements IMekALRecipeTypeProvider<VANILLA_INPUT, RECIPE, INPUT_CACHE> {

    public MekALRecipeTypeRegistryObject(ResourceKey<RecipeType<?>> key) {
        super(key);
    }

    @Override
    public MekALRecipeType<VANILLA_INPUT, RECIPE, INPUT_CACHE> getRecipeType() {
        return this.value();
    }

}
