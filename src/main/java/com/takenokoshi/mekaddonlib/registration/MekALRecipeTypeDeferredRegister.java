package com.takenokoshi.mekaddonlib.registration;

import java.util.function.Function;

import com.takenokoshi.mekaddonlib.recipe.type.MekALRecipeType;

import mekanism.common.recipe.lookup.cache.IInputRecipeCache;
import mekanism.common.registration.MekanismDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

public class MekALRecipeTypeDeferredRegister extends MekanismDeferredRegister<RecipeType<?>> {

    public MekALRecipeTypeDeferredRegister(String modId) {
        super(Registries.RECIPE_TYPE, modId, MekALRecipeTypeRegistryObject::new);
    }

    @SuppressWarnings("unchecked")
    // Safe: holderCreator always creates MekALRecipeTypeRegistryObject
    public <VANILLA_INPUT extends RecipeInput, RECIPE extends Recipe<VANILLA_INPUT>, INPUT_CACHE extends IInputRecipeCache> MekALRecipeTypeRegistryObject<VANILLA_INPUT, RECIPE, INPUT_CACHE> registerMekAL(
            String name,
            Function<ResourceLocation, ? extends MekALRecipeType<VANILLA_INPUT, RECIPE, INPUT_CACHE>> function) {
        return (MekALRecipeTypeRegistryObject<VANILLA_INPUT, RECIPE, INPUT_CACHE>) super.register(name, function);
    }

}
