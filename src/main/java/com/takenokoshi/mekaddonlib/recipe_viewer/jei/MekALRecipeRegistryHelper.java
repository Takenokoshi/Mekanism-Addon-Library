package com.takenokoshi.mekaddonlib.recipe_viewer.jei;

import com.takenokoshi.mekaddonlib.recipe.type.IMekALRecipeTypeProvider;

import mekanism.client.recipe_viewer.jei.MekanismJEI;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.Recipe;

public class MekALRecipeRegistryHelper {
    public static <RECIPE extends Recipe<?>> void register(
            IRecipeRegistration registry,
            IRecipeViewerRecipeType<RECIPE> recipeType,
            IMekALRecipeTypeProvider<?, RECIPE, ?> type) {
        registry.addRecipes(MekanismJEI.holderRecipeType(recipeType), type.getRecipes(Minecraft.getInstance().level));
    }
}
