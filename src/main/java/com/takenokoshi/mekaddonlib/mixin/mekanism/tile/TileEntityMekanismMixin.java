package com.takenokoshi.mekaddonlib.mixin.mekanism.tile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.api.Upgrade;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.component.TileComponentUpgrade;

@Mixin(value = { TileEntityMekanism.class }, remap = false)
public class TileEntityMekanismMixin {

    @Shadow(remap = false)
    protected TileComponentUpgrade upgradeComponent;

    @Inject(method = "recalculateUpgrades", at = @At("RETURN"))
    protected void mek_addon_lib$recalculateAdditionalUpgrades(Upgrade upgrade, CallbackInfo ci) {
        if (AdditionalUpgradeUtils.isEnergyModifier(upgrade)) {
            for (IEnergyContainer energyContainer : ((TileEntityMekanism) (Object) this).getEnergyContainers(null)) {
                if (energyContainer instanceof MachineEnergyContainer machineEnergy) {
                    machineEnergy.updateEnergyPerTick();
                    machineEnergy.updateMaxEnergy();
                }
            }
        }
    }

}
