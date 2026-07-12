package com.takenokoshi.mekaddonlib.mixin.mekanismelements;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fxd927.mekanismelements.common.tile.prefab.MSTileEntityProgressMachine;
import com.takenokoshi.mekaddonlib.mixin.mekanism.tile.TileEntityMekanismMixin;
import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.api.Upgrade;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.MekanismUtils;

@Mixin(value = { MSTileEntityProgressMachine.class }, remap = false)
public class MSTileEntityProgressMachineMixin extends TileEntityMekanismMixin {

    @Shadow(remap = false)
    private int baseTicksRequired;
    @Shadow(remap = false)
    private int ticksRequired;

    @Override
    protected void mek_addon_lib$recalculateAdditionalUpgrades(Upgrade upgrade, CallbackInfo ci) {
        super.mek_addon_lib$recalculateAdditionalUpgrades(upgrade, ci);
        if (AdditionalUpgradeUtils.isSpeedModifier(upgrade)) {
            ticksRequired = MekanismUtils.getTicks(((TileEntityMekanism) (Object) this), baseTicksRequired);
        }
    }
}
