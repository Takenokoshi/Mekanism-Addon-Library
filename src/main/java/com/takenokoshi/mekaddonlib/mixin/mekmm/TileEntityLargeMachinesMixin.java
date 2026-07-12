package com.takenokoshi.mekaddonlib.mixin.mekmm;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.jerry.meklm.common.tile.machine.TileEntityLargeAntiprotonicNucleosynthesizer;
import com.jerry.meklm.common.tile.machine.TileEntityLargeChemicalInfuser;
import com.jerry.meklm.common.tile.machine.TileEntityLargeElectrolyticSeparator;
import com.jerry.meklm.common.tile.machine.TileEntityLargePigmentMixer;
import com.jerry.meklm.common.tile.machine.TileEntityLargeRotaryCondensentrator;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.takenokoshi.mekaddonlib.mixin.mekanism.tile.TileEntityMekanismMixin;
import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.api.Upgrade;
import mekanism.common.tile.component.TileComponentUpgrade;

@Mixin(value = {
        TileEntityLargeAntiprotonicNucleosynthesizer.class,
        TileEntityLargeChemicalInfuser.class,
        TileEntityLargeElectrolyticSeparator.class,
        TileEntityLargePigmentMixer.class,
        TileEntityLargeRotaryCondensentrator.class,
}, remap = false)
public class TileEntityLargeMachinesMixin extends TileEntityMekanismMixin {

    @WrapOperation(method = "recalculateUpgrades", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/component/TileComponentUpgrade;getUpgrades(Lmekanism/api/Upgrade;)I"))
    private int mek_addon_lib$modifyUpgradeCount(TileComponentUpgrade upgradeComponent,
            Upgrade upgrade, Operation<Integer> original) {
        int upgradeCount = original.call(upgradeComponent, upgrade);
        if (upgrade != Upgrade.SPEED) {
            return upgradeCount;
        }
        for (Upgrade additionalUpgrade : AdditionalUpgradeUtils.getAdditionalSpeedUpgrades()) {
            upgradeCount += upgradeComponent.getUpgrades(additionalUpgrade);
        }
        return upgradeCount;
    }

}
