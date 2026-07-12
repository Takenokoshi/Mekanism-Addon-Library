package com.takenokoshi.mekaddonlib.mixin.mekanism;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.common.tile.interfaces.IUpgradeTile;
import mekanism.common.util.MekanismUtils;

@Mixin(value = { MekanismUtils.class }, remap = false)
public class MekanismUtilsMixin {

    @ModifyReturnValue(method = "getTicksD", at = @At("RETURN"))
    private static double mek_addon_lib$modifyTicksD(double original, IUpgradeTile tile, int def) {
        return AdditionalUpgradeUtils.modifyTicks(tile, original);
    }

    @ModifyReturnValue(method = "getEnergyPerTick", at = @At("RETURN"))
    private static long mek_addon_lib$modifyEnergyPerTick(long original, IUpgradeTile tile, long def) {
        return AdditionalUpgradeUtils.modifyUsage(tile, original);
    }

    @ModifyReturnValue(method = "getMaxEnergy", at = @At("RETURN"))
    private static long mek_addon_lib$modifyMaxEnergy(long original, IUpgradeTile tile, long def) {
        return AdditionalUpgradeUtils.modifyStorage(tile, original);
    }
}
