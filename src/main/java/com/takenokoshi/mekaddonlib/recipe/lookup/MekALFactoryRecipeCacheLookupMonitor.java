package com.takenokoshi.mekaddonlib.recipe.lookup;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.crafting.Recipe;

public class MekALFactoryRecipeCacheLookupMonitor<RECIPE extends Recipe<?>>
        extends MekALRecipeCacheLookupMonitor<RECIPE> {

    @SuppressWarnings("unchecked")
    public static <RECIPE extends Recipe<?>> MekALFactoryRecipeCacheLookupMonitor<RECIPE>[] createMonitorArray(
            IMekALRecipeLookupHandler<RECIPE> handler, int length,
            Runnable setSortingNeeded) {
        MekALFactoryRecipeCacheLookupMonitor<RECIPE>[] recipeCacheLookupMonitors = new MekALFactoryRecipeCacheLookupMonitor[length];
        for (int i = 0; i < recipeCacheLookupMonitors.length; i++) {
            recipeCacheLookupMonitors[i] = new MekALFactoryRecipeCacheLookupMonitor<>(handler, i, setSortingNeeded);
        }
        return recipeCacheLookupMonitors;
    }

    private final Runnable setSortingNeeded;

    public MekALFactoryRecipeCacheLookupMonitor(IMekALRecipeLookupHandler<RECIPE> handler, int cacheIndex,
            Runnable setSortingNeeded) {
        super(handler, cacheIndex);
        this.setSortingNeeded = setSortingNeeded;
    }

    @Override
    public void onChange() {
        super.onChange();
        // Mark that sorting is needed
        setSortingNeeded.run();
    }

    public void updateCachedRecipe(@NotNull RECIPE recipe) {
        cachedRecipe = createNewCachedRecipe(recipe, cacheIndex);
        // Note: While this is probably not strictly needed we clear our cache of
        // knowing we have no recipe
        // so that we can properly re-enter the lookup cycle if needed
        hasNoRecipe = false;
    }

}
