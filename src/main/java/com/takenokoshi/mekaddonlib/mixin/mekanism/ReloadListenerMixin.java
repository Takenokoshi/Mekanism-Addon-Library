package com.takenokoshi.mekaddonlib.mixin.mekanism;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.takenokoshi.mekaddonlib.recipe.type.MekALRecipeType;

import mekanism.common.ReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

@Mixin(value = { ReloadListener.class }, remap = true)
public class ReloadListenerMixin {
    @Inject(method = { "onResourceManagerReload" }, at = @At("HEAD"))
    void mek_addon_lib$reloadAdditional(ResourceManager resourceManager, CallbackInfo info) {
        MekALRecipeType.clearAllCaches();
    }
}
