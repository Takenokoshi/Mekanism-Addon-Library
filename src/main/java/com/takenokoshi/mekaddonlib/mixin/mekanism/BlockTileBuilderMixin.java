package com.takenokoshi.mekaddonlib.mixin.mekanism;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.api.Upgrade;
import mekanism.common.content.blocktype.BlockTypeTile.BlockTileBuilder;

@Mixin(value = { BlockTileBuilder.class }, remap = false)
public class BlockTileBuilderMixin {

    @ModifyVariable(method = "withSupportedUpgrades", at = @At("HEAD"), argsOnly = true)
    private Upgrade[] mek_addon_lib$supportAdditionalUpgrades(Upgrade[] original) {
        Set<Upgrade> upgrades = new HashSet<>();
        upgrades.addAll(Arrays.asList(original));
        if (upgrades.contains(Upgrade.ENERGY)) {
            upgrades.addAll(AdditionalUpgradeUtils.getAdditionalEnergyUpgrades());
        }
        if (upgrades.contains(Upgrade.SPEED)) {
            upgrades.addAll(AdditionalUpgradeUtils.getAdditionalSpeedUpgrades());
        }
        return upgrades.toArray(Upgrade[]::new);
    }
}
