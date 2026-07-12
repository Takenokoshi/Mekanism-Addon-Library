package com.takenokoshi.mekaddonlib.mixin.mekanism_extras;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.SoftOverride;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jerry.mekextras.common.tile.machine.TileEntityAdvancedElectricPump;
import com.takenokoshi.mekaddonlib.mixin.mekanism.tile.TileEntityMekanismMixin;
import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.api.Upgrade;
import mekanism.common.tile.machine.TileEntityElectricPump;
import mekanism.common.util.MekanismUtils;

@Mixin(value = TileEntityAdvancedElectricPump.class,remap = false)
public class TileEntityAdvancedElectricPumpMixin extends TileEntityMekanismMixin {
    

    @Shadow(remap = false)
    private int ticksRequired;
    @Shadow(remap = false)
    private int outputRate;

    @SoftOverride
    @Override
    protected void mek_addon_lib$recalculateAdditionalUpgrades(Upgrade upgrade, CallbackInfo ci) {
        super.mek_addon_lib$recalculateAdditionalUpgrades(upgrade, ci);
        if (AdditionalUpgradeUtils.isSpeedModifier(upgrade)) {
            ticksRequired = MekanismUtils.getTicks((TileEntityElectricPump) (Object) this, 19);
            outputRate = AdditionalUpgradeUtils.modifyPumpOutput((TileEntityElectricPump) (Object) this, outputRate);
        } else if (upgrade == Upgrade.SPEED || upgrade.name().equals("EMPOWERED_SPEED")) {
            outputRate = AdditionalUpgradeUtils.modifyPumpOutput((TileEntityElectricPump) (Object) this, outputRate);
        }
    }
}
