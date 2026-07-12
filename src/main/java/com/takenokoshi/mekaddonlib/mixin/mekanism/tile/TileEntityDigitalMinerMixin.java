package com.takenokoshi.mekaddonlib.mixin.mekanism.tile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.SoftOverride;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.api.Upgrade;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import mekanism.common.util.MekanismUtils;

@Mixin(value = { TileEntityDigitalMiner.class }, remap = false)
public class TileEntityDigitalMinerMixin extends TileEntityMekanismMixin {

    @Shadow(remap = false)
    private int delayLength;

    @SoftOverride
    @Override
    protected void mek_addon_lib$recalculateAdditionalUpgrades(Upgrade upgrade, CallbackInfo ci) {
        super.mek_addon_lib$recalculateAdditionalUpgrades(upgrade, ci);
        if (AdditionalUpgradeUtils.isSpeedModifier(upgrade)) {
            delayLength = MekanismUtils.getTicks((TileEntityDigitalMiner) (Object) this,
                    MekanismConfig.general.minerTicksPerMine.get());
        }
    }
}
