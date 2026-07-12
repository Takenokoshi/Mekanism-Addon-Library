package com.takenokoshi.mekaddonlib.upgrade;

import mekanism.common.config.MekanismConfig;

public interface EnergyModifierUpgradeData {

    default double modifyUsage(double original, int amount) {
        return original * Math.pow(MekanismConfig.general.maxUpgradeMultiplier.getOrDefault(), -amount / 8.0d);
    }

    default double modifyStorage(double original, int amount) {
        return original * Math.pow(MekanismConfig.general.maxUpgradeMultiplier.getOrDefault(), amount / 8.0d);
    }

    /**
     * @apiNote will be applied in ascending order of value.
     *          Lower values are applied first.
     */
    default int priority() {
        return 0;
    }
}
