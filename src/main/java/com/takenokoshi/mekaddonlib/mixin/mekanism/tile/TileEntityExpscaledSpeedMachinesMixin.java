package com.takenokoshi.mekaddonlib.mixin.mekanism.tile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.SoftOverride;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.api.Upgrade;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.machine.TileEntityChemicalInfuser;
import mekanism.common.tile.machine.TileEntityElectrolyticSeparator;
import mekanism.common.tile.machine.TileEntityIsotopicCentrifuge;
import mekanism.common.tile.machine.TileEntityPigmentMixer;
import mekanism.common.tile.machine.TileEntityRotaryCondensentrator;

@Mixin(value = {
        TileEntityChemicalInfuser.class,
        TileEntityElectrolyticSeparator.class,
        TileEntityIsotopicCentrifuge.class,
        TileEntityPigmentMixer.class,
        TileEntityRotaryCondensentrator.class,
}, remap = false)
public class TileEntityExpscaledSpeedMachinesMixin extends TileEntityMekanismMixin {

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
