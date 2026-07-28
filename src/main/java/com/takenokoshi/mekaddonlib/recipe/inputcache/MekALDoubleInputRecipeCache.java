package com.takenokoshi.mekaddonlib.recipe.inputcache;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.takenokoshi.mekaddonlib.recipe.type.MekALRecipeType;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ItemStackChemicalToItemStackRecipe;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.recipe.lookup.cache.DoubleInputRecipeCache.CheckRecipeType;
import mekanism.common.recipe.lookup.cache.type.ChemicalInputCache;
import mekanism.common.recipe.lookup.cache.type.IInputCache;
import mekanism.common.recipe.lookup.cache.type.ItemInputCache;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public class MekALDoubleInputRecipeCache<INPUT_A, INGREDIENT_A extends InputIngredient<INPUT_A>, INPUT_B, INGREDIENT_B extends InputIngredient<INPUT_B>, RECIPE extends MekanismRecipe<?> & BiPredicate<INPUT_A, INPUT_B>, CACHE_A extends IInputCache<INPUT_A, INGREDIENT_A, RECIPE>, CACHE_B extends IInputCache<INPUT_B, INGREDIENT_B, RECIPE>>
        extends MekALAbstractInputRecipeCache<RECIPE> {

    private final Set<RECIPE> complexIngredientA = new HashSet<>();
    private final Set<RECIPE> complexIngredientB = new HashSet<>();
    private final Set<RECIPE> complexRecipes = new HashSet<>();
    private final Function<RECIPE, INGREDIENT_A> inputAExtractor;
    private final Function<RECIPE, INGREDIENT_B> inputBExtractor;
    private final CACHE_A cacheA;
    private final CACHE_B cacheB;

    protected MekALDoubleInputRecipeCache(MekALRecipeType<?, RECIPE, ?> recipeType,
            Function<RECIPE, INGREDIENT_A> inputAExtractor, CACHE_A cacheA,
            Function<RECIPE, INGREDIENT_B> inputBExtractor, CACHE_B cacheB) {
        super(recipeType);
        this.inputAExtractor = inputAExtractor;
        this.inputBExtractor = inputBExtractor;
        this.cacheA = cacheA;
        this.cacheB = cacheB;
    }

    @Override
    public void clear() {
        super.clear();
        cacheA.clear();
        cacheB.clear();
        complexIngredientA.clear();
        complexIngredientB.clear();
        complexRecipes.clear();
    }

    public boolean containsInputA(@Nullable Level world, INPUT_A input) {
        return containsInput(world, input, inputAExtractor, cacheA, complexIngredientA);
    }

    public boolean containsInputB(@Nullable Level world, INPUT_B input) {
        return containsInput(world, input, inputBExtractor, cacheB, complexIngredientB);
    }

    public boolean containsInputAB(@Nullable Level world, INPUT_A inputA, INPUT_B inputB) {
        return containsPairing(world, inputA, inputAExtractor, cacheA, complexIngredientA, inputB, inputBExtractor,
                cacheB, complexIngredientB);
    }

    public boolean containsInputBA(@Nullable Level world, INPUT_A inputA, INPUT_B inputB) {
        return containsPairing(world, inputB, inputBExtractor, cacheB, complexIngredientB, inputA, inputAExtractor,
                cacheA, complexIngredientA);
    }

    @Nullable
    public RECIPE findFirstRecipe(@Nullable Level world, INPUT_A inputA, INPUT_B inputB) {
        return findFirstRecipe(world, inputA, inputB, true);
    }

    @Nullable
    public RECIPE findFirstRecipe(@Nullable Level world, INPUT_A inputA, INPUT_B inputB, boolean useCacheA) {
        if (cacheA.isEmpty(inputA) || cacheB.isEmpty(inputB)) {
            // Don't allow empty inputs
            return null;
        }
        initCacheIfNeeded(world);
        // Lookup a recipe from the specified input map
        RECIPE recipe;
        if (useCacheA) {
            recipe = findFirstRecipe(inputA, inputB, cacheA.getRecipes(inputA));
        } else {
            recipe = findFirstRecipe(inputA, inputB, cacheB.getRecipes(inputB));
        }
        // if there is no recipe, then check if any of our complex recipes (either a or
        // b being complex) match
        return recipe == null ? findFirstRecipe(inputA, inputB, complexRecipes) : recipe;
    }

    @Nullable
    private RECIPE findFirstRecipe(INPUT_A inputA, INPUT_B inputB, Iterable<RECIPE> recipes) {
        for (RECIPE recipe : recipes) {
            if (recipe.test(inputA, inputB)) {
                return recipe;
            }
        }
        return null;
    }

    @Nullable
    public <DATA> RECIPE findTypeBasedRecipe(@Nullable Level world, INPUT_A inputA, INPUT_B inputB, DATA data,
            CheckRecipeType<INPUT_A, INPUT_B, RECIPE, DATA> matchCriteria) {
        if (cacheA.isEmpty(inputA)) {
            // Don't allow empty primary inputs
            return null;
        }
        initCacheIfNeeded(world);
        if (cacheB.isEmpty(inputB)) {
            // If b is empty, lookup by A and our match criteria
            for (RECIPE recipe : cacheA.getRecipes(inputA)) {
                if (matchCriteria.testType(recipe, inputA, inputB, data)) {
                    return recipe;
                }
            }
            for (RECIPE complexRecipe : complexRecipes) {
                if (inputAExtractor.apply(complexRecipe).testType(inputA)
                        && matchCriteria.testType(complexRecipe, inputA, inputB, data)) {
                    return complexRecipe;
                }
            }
        } else {
            for (RECIPE recipe : cacheA.getRecipes(inputA)) {
                if (inputBExtractor.apply(recipe).testType(inputB)
                        && matchCriteria.testType(recipe, inputA, inputB, data)) {
                    return recipe;
                }
            }
            for (RECIPE complexRecipe : complexRecipes) {
                if (inputAExtractor.apply(complexRecipe).testType(inputA)) {
                    if (inputBExtractor.apply(complexRecipe).testType(inputB)
                            && matchCriteria.testType(complexRecipe, inputA, inputB, data)) {
                        return complexRecipe;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void initCache(List<RecipeHolder<RECIPE>> recipes) {
        for (RecipeHolder<RECIPE> recipeHolder : recipes) {
            RECIPE recipe = recipeHolder.value();
            boolean complexA = cacheA.mapInputs(recipe, inputAExtractor.apply(recipe));
            boolean complexB = cacheB.mapInputs(recipe, inputBExtractor.apply(recipe));
            if (complexA) {
                complexIngredientA.add(recipe);
            }
            if (complexB) {
                complexIngredientB.add(recipe);
            }
            if (complexA || complexB) {
                complexRecipes.add(recipe);
            }
        }
    }

    protected <INPUT, INGREDIENT extends InputIngredient<INPUT>, CACHE extends IInputCache<INPUT, INGREDIENT, RECIPE>> boolean containsInput(
            @Nullable Level world, INPUT input, Function<RECIPE, INGREDIENT> inputExtractor, CACHE cache,
            Set<RECIPE> complexRecipes) {
        if (cache.isEmpty(input)) {
            return false;
        } else {
            this.initCacheIfNeeded(world);
            if (cache.contains(input)) {
                return true;
            } else {
                for (RECIPE recipe : complexRecipes) {
                    if (inputExtractor.apply(recipe).testType(input)) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    protected <INPUT_1, INGREDIENT_1 extends InputIngredient<INPUT_1>, CACHE_1 extends IInputCache<INPUT_1, INGREDIENT_1, RECIPE>, INPUT_2, INGREDIENT_2 extends InputIngredient<INPUT_2>, CACHE_2 extends IInputCache<INPUT_2, INGREDIENT_2, RECIPE>> boolean containsPairing(
            @Nullable Level world, INPUT_1 input1, Function<RECIPE, INGREDIENT_1> input1Extractor, CACHE_1 cache1,
            Set<RECIPE> complexIngredients1, INPUT_2 input2, Function<RECIPE, INGREDIENT_2> input2Extractor,
            CACHE_2 cache2, Set<RECIPE> complexIngredients2) {
        if (cache1.isEmpty(input1)) {
            return this.containsInput(world, input2, input2Extractor, cache2, complexIngredients2);
        } else if (cache2.isEmpty(input2)) {
            return true;
        } else {
            this.initCacheIfNeeded(world);

            for (RECIPE recipe : cache1.getRecipes(input1)) {
                if (input2Extractor.apply(recipe).testType(input2)) {
                    return true;
                }
            }

            for (RECIPE recipe : complexIngredients1) {
                if (input1Extractor.apply(recipe).testType(input1) && input2Extractor.apply(recipe).testType(input2)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static class MekALItemChemical<RECIPE extends MekanismRecipe<?> & BiPredicate<ItemStack, ChemicalStack>>
            extends
            MekALDoubleInputRecipeCache<ItemStack, ItemStackIngredient, ChemicalStack, ChemicalStackIngredient, RECIPE, ItemInputCache<RECIPE>, ChemicalInputCache<RECIPE>> {

        protected MekALItemChemical(MekALRecipeType<?, RECIPE, ?> recipeType,
                Function<RECIPE, ItemStackIngredient> inputAExtractor,
                Function<RECIPE, ChemicalStackIngredient> inputBExtractor) {
            super(recipeType, inputAExtractor, new ItemInputCache<>(), inputBExtractor, new ChemicalInputCache<>());
        }

        public static MekALItemChemical<ItemStackChemicalToItemStackRecipe> toItem(
                MekALRecipeType<?, ItemStackChemicalToItemStackRecipe, ?> recipeType) {
            return new MekALItemChemical<>(recipeType,
                    ItemStackChemicalToItemStackRecipe::getItemInput,
                    ItemStackChemicalToItemStackRecipe::getChemicalInput);
        }

    }

}