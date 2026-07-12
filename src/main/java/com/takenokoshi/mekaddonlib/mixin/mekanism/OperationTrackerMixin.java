package com.takenokoshi.mekaddonlib.mixin.mekanism;


import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import mekanism.api.recipes.cache.CachedRecipe.OperationTracker;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;

@Mixin(value = { OperationTracker.class }, remap = false)
public interface OperationTrackerMixin {

    @Invoker(value = "<init>")
    static OperationTracker mek_addon_lib$invokeInit(Set<RecipeError> lastErrors, boolean checkAll,
            int startingMax) {
        throw new AssertionError();
    };

    @Invoker("capAtMaxForEnergy")
    boolean mek_addon_lib$invokeCapAtMaxForEnergy();

    @Invoker("hasErrorsToCopy")
    boolean mek_addon_lib$invokeHasErrorsToCopy();

    @Accessor("currentMax")
    int mek_addon_lib$getCurrentMax();

    @Accessor("errors")
    Set<RecipeError> mek_addon_lib$getErrors();

    @Accessor("maxForEnergy")
    void mek_addon_lib$setMaxForEnergy(int value);
}