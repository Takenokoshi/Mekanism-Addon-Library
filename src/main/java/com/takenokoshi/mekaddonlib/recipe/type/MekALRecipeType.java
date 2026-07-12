package com.takenokoshi.mekaddonlib.recipe.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mekanism.client.MekanismClient;
import mekanism.common.recipe.lookup.cache.IInputRecipeCache;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class MekALRecipeType<VANILLA_INPUT extends RecipeInput, RECIPE extends Recipe<VANILLA_INPUT>, INPUT_CACHE extends IInputRecipeCache>
        implements RecipeType<RECIPE>, IMekALRecipeTypeProvider<VANILLA_INPUT, RECIPE, INPUT_CACHE> {

    private static final List<MekALRecipeType<?, ?, ?>> RECIPE_TYPES = new ArrayList<>();

    private List<RecipeHolder<RECIPE>> cachedRecipes = Collections.emptyList();
    private final ResourceLocation registryName;
    private final INPUT_CACHE inputCache;

    public MekALRecipeType(ResourceLocation name,
            Function<MekALRecipeType<VANILLA_INPUT, RECIPE, INPUT_CACHE>, INPUT_CACHE> inputCacheCreator) {
        this.registryName = name;
        this.inputCache = inputCacheCreator.apply(this);
        RECIPE_TYPES.add(this);
    }

    @Override
    public String toString() {
        return this.registryName.toString();
    }

    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

    public MekALRecipeType<VANILLA_INPUT, RECIPE, INPUT_CACHE> getRecipeType() {
        return this;
    }

    protected void clearCaches() {
        this.cachedRecipes = Collections.emptyList();
        this.inputCache.clear();
    }

    public INPUT_CACHE getInputCache() {
        return this.inputCache;
    }

    private static @Nullable RegistryAccess tryGetRegistryAccess() {
        if (FMLEnvironment.dist.isClient()) {
            Level clientWorld = MekanismClient.tryGetClientWorld();
            return clientWorld != null ? clientWorld.registryAccess() : null;
        } else {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            return server == null ? null : server.registryAccess();
        }
    }

    public @NotNull List<RecipeHolder<RECIPE>> getRecipes(@Nullable Level world) {
        RecipeManager recipeManager = null;
        RegistryAccess registryAccess = null;
        if (world == null) {
            if (FMLEnvironment.dist.isClient()) {
                Level clientWorld = MekanismClient.tryGetClientWorld();
                if (clientWorld != null) {
                    recipeManager = clientWorld.getRecipeManager();
                    registryAccess = clientWorld.registryAccess();
                }
            } else {
                MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
                if (currentServer != null) {
                    recipeManager = currentServer.getRecipeManager();
                    registryAccess = currentServer.registryAccess();
                }
            }
        } else {
            recipeManager = world.getRecipeManager();
            registryAccess = world.registryAccess();
        }

        return recipeManager == null ? Collections.emptyList() : this.getRecipes(recipeManager, registryAccess);
    }

    public @NotNull List<RecipeHolder<RECIPE>> getRecipes(RecipeManager recipeManager) {
        return this.getRecipes(recipeManager, tryGetRegistryAccess());
    }

    public @NotNull List<RecipeHolder<RECIPE>> getRecipes(@NotNull RecipeManager recipeManager,
            @Nullable RegistryAccess registryAccess) {
        if (this.cachedRecipes.isEmpty()) {
            List<RecipeHolder<RECIPE>> recipes = this.getRecipesUncached(recipeManager, registryAccess);
            this.cachedRecipes = recipes.stream().filter((recipe) -> isRecipeComplete(recipe.value())).toList();
        }

        return this.cachedRecipes;
    }

    protected @NotNull List<RecipeHolder<RECIPE>> getRecipesUncached(@NotNull RecipeManager recipeManager,
            @Nullable RegistryAccess registryAccess) {
        return recipeManager.getAllRecipesFor(this);
    }

    protected boolean isRecipeComplete(RECIPE recipe) {
        return !recipe.isIncomplete();
    }

    // called from mixin for ReloadListener#onResourceManagerReload
    public static void clearAllCaches() {
        RECIPE_TYPES.forEach(MekALRecipeType::clearCaches);
    }

}
