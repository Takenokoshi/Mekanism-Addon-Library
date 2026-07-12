package com.takenokoshi.mekaddonlib.recipe.type;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mekanism.common.recipe.lookup.cache.IInputRecipeCache;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

public interface IMekALRecipeTypeProvider<VANILLA_INPUT extends RecipeInput, RECIPE extends Recipe<VANILLA_INPUT>, INPUT_CACHE extends IInputRecipeCache> {

    MekALRecipeType<VANILLA_INPUT, RECIPE, INPUT_CACHE> getRecipeType();

    default ResourceLocation getRegistryName() {
        return this.getRecipeType().getRegistryName();
    }

    default INPUT_CACHE getInputCache() {
        return this.getRecipeType().getInputCache();
    }

    default @NotNull List<RecipeHolder<RECIPE>> getRecipes() {
        return this.getRecipeType().getRecipes((Level) null);
    }

    default @NotNull List<RecipeHolder<RECIPE>> getRecipes(@Nullable Level world) {
        return this.getRecipeType().getRecipes(world);
    }

    default @NotNull List<RecipeHolder<RECIPE>> getRecipes(RecipeManager recipeManager) {
        return this.getRecipeType().getRecipes(recipeManager);
    }

    default @NotNull List<RecipeHolder<RECIPE>> getRecipes(RecipeManager recipeManager, RegistryAccess registryAccess) {
        return this.getRecipeType().getRecipes(recipeManager, registryAccess);
    }

    default Stream<RecipeHolder<RECIPE>> stream(@Nullable Level world) {
        return this.getRecipes(world).stream();
    }

    default @Nullable RECIPE findFirst(@Nullable Level world, Predicate<RECIPE> matchCriteria) {
        for (RecipeHolder<RECIPE> recipeRecipeHolder : this.getRecipes(world)) {
            RECIPE value = recipeRecipeHolder.value();
            if (matchCriteria.test(value)) {
                return value;
            }
        }

        return null;
    }

    default boolean contains(@Nullable Level world, Predicate<RECIPE> matchCriteria) {
        for (RecipeHolder<RECIPE> holder : this.getRecipes(world)) {
            if (matchCriteria.test(holder.value())) {
                return true;
            }
        }

        return false;
    }
}
