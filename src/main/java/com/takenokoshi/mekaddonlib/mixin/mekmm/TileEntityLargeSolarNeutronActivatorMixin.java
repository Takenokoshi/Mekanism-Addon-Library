package com.takenokoshi.mekaddonlib.mixin.mekmm;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.SoftOverride;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jerry.meklm.common.tile.machine.TileEntityLargeSolarNeutronActivator;
import com.takenokoshi.mekaddonlib.mixin.mekanism.tile.TileEntityConfigurableMachineMixin;
import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.api.Upgrade;
import mekanism.common.tile.base.TileEntityMekanism;

@Mixin(value = { TileEntityLargeSolarNeutronActivator.class }, remap = false)
public class TileEntityLargeSolarNeutronActivatorMixin extends TileEntityConfigurableMachineMixin {

    @Shadow(remap = false)
    private int baselineMaxOperations;

    @SoftOverride
    @Override
    protected void mek_addon_lib$recalculateAdditionalUpgrades(Upgrade upgrade, CallbackInfo ci) {
        super.mek_addon_lib$recalculateAdditionalUpgrades(upgrade, ci);
        if (upgrade == Upgrade.SPEED
                || upgrade.name().equals("EMPOWERED_SPEED") // Mekanism Empowered Compability
                || AdditionalUpgradeUtils.isSpeedModifier(upgrade)) {
            baselineMaxOperations = AdditionalUpgradeUtils.modifyOperations((TileEntityMekanism) (Object) this,
                    baselineMaxOperations);
        }
    }
}
