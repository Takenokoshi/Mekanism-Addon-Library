package com.takenokoshi.mekaddonlib.mixin.mekanism.tile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.SoftOverride;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.api.Upgrade;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import mekanism.common.util.MekanismUtils;

@Mixin(value = { TileEntityProgressMachine.class }, remap = false)
public class TileEntityProgressMachineMixin extends TileEntityConfigurableMachineMixin {

    @Shadow(remap = false)
    private int baseTicksRequired;
    @Shadow(remap = false)
    private int ticksRequired;
    @Shadow(remap = false)
    private int operationsPerTick;

    @SoftOverride
    @Override
    protected void mek_addon_lib$recalculateAdditionalUpgrades(Upgrade upgrade, CallbackInfo ci) {
        super.mek_addon_lib$recalculateAdditionalUpgrades(upgrade, ci);
        if (AdditionalUpgradeUtils.isSpeedModifier(upgrade)) {
            ticksRequired = MekanismUtils.getTicks(((TileEntityMekanism) (Object) this), baseTicksRequired);
            operationsPerTick = MekanismUtils.getOperationsPerTick(((TileEntityMekanism) (Object) this),
                    baseTicksRequired, 1);
        }
    }
}
