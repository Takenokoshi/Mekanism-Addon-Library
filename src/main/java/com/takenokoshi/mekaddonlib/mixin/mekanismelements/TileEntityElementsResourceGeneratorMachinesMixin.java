package com.takenokoshi.mekaddonlib.mixin.mekanismelements;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fixdol.mekanismelements.common.tile.machine.TileEntityAirCompressor;
import fixdol.mekanismelements.common.tile.machine.TileEntitySeawaterPump;
import com.takenokoshi.mekaddonlib.mixin.mekanism.tile.TileEntityMekanismMixin;
import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.api.Upgrade;
import mekanism.common.util.MekanismUtils;

@Mixin(value = { TileEntityAirCompressor.class, TileEntitySeawaterPump.class }, remap = false)
public class TileEntityElementsResourceGeneratorMachinesMixin extends TileEntityMekanismMixin {

    @Shadow(remap = false)
    private int ticksRequired;

    @Override
    protected void mek_addon_lib$recalculateAdditionalUpgrades(Upgrade upgrade, CallbackInfo ci) {
        super.mek_addon_lib$recalculateAdditionalUpgrades(upgrade, ci);
        if (AdditionalUpgradeUtils.isSpeedModifier(upgrade)) {
            ticksRequired = MekanismUtils.getTicks((TileEntityAirCompressor) (Object) this, 19);
        }
    }
}
