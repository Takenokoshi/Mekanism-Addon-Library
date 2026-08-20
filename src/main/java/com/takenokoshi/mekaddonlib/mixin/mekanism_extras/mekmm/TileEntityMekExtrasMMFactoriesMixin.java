package com.takenokoshi.mekaddonlib.mixin.mekanism_extras.mekmm;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.SoftOverride;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jerry.mekextras.common.integration.mekaf.tile.factory.base.TileEntityExtraAdvancedFactoryBase;
import com.jerry.mekextras.common.integration.mekmm.tile.factory.TileEntityExtraMoreMachineFactory;
import com.takenokoshi.mekaddonlib.mixin.mekanism.tile.TileEntityConfigurableMachineMixin;
import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.api.Upgrade;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.MekanismUtils;

@Mixin(value = {
        TileEntityExtraMoreMachineFactory.class,
        TileEntityExtraAdvancedFactoryBase.class,
}, remap = false)
public class TileEntityMekExtrasMMFactoriesMixin extends TileEntityConfigurableMachineMixin {

    @Shadow(remap = false)
    private int upgradeMaxOperations;
    @Shadow(remap = false)
    private int ticksRequired;
    @Shadow(remap = false)
    private int operationsPerTick;

    @SoftOverride
    @Override
    protected void mek_addon_lib$recalculateAdditionalUpgrades(Upgrade upgrade, CallbackInfo ci) {
        super.mek_addon_lib$recalculateAdditionalUpgrades(upgrade, ci);
        if (AdditionalUpgradeUtils.isSpeedModifier(upgrade)) {
            ticksRequired = MekanismUtils.getTicks(((TileEntityMekanism) (Object) this), 200);
            operationsPerTick = MekanismUtils.getOperationsPerTick(((TileEntityMekanism) (Object) this),
                    200, upgradeMaxOperations);
        } else if (upgrade.name().equals("STACK")) {
            operationsPerTick = MekanismUtils.getOperationsPerTick(((TileEntityMekanism) (Object) this),
                    200, upgradeMaxOperations);
        }
    }
}
