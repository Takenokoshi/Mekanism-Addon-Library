package com.takenokoshi.mekaddonlib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.takenokoshi.mekaddonlib.upgrade.AdditinalUpgradeUtils;

import mekanism.api.Upgrade;
import mekanism.common.util.UpgradeUtils;
import net.minecraft.world.item.ItemStack;

@Mixin(value = { UpgradeUtils.class }, remap = false)
public class UpgradeUtilsMixin {
    @Inject(method = "getStack(Lmekanism/api/Upgrade;I)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private static void mek_addon_lib$getStackInject(Upgrade upgrade, int count,
            CallbackInfoReturnable<ItemStack> cir) {
        if (AdditinalUpgradeUtils.isAdditional(upgrade)) {
            cir.setReturnValue(
                    AdditinalUpgradeUtils.getStack(AdditinalUpgradeUtils.convertToAdditional(upgrade), count));
            cir.cancel();
            return;
        }
    }
}
