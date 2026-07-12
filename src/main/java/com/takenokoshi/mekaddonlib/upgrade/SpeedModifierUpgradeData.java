package com.takenokoshi.mekaddonlib.upgrade;

import mekanism.api.math.MathUtils;
import mekanism.common.config.MekanismConfig;

public interface SpeedModifierUpgradeData {

    /**
     * @apiNote For Multiscaled Machine
     *          (such as Energized Smelter , Chemical Oxidizer etc...)
     *          Mekanism's Speed Upgrade max effect is 10x when Config value is
     *          default.
     * @param amount Installed amount of Upgrade
     * @return Return value should be larger then 0. Can return Smaller then 1.
     */
    default double modifyTicksD(double original, int amount) {
        return original
                * Math.pow(MekanismConfig.general.maxUpgradeMultiplier.getOrDefault(), -amount / 8.0d);
    }

    /**
     * @apiNote For Expscaled Machine
     *          (such as Electrolytic separator, Chemical Infuser etc...)
     *          Mekanism's Speed Upgrade max effect is 256x.
     * @param amount Installed amount of Upgrade
     * @return Return value should be larger then 0.
     */
    default int modifyExpscaledMachineOperations(int original, int amount) {
        return MathUtils.clampToInt(1.0d * original * (1 << amount));
    }

    /**
     * @apiNote For Electric Pump's output rate
     * @param amount Installed amount of Upgrade
     * @return Return value should be larger then 0.
     */
    default int modifyElectricPumpOutputRate(int original, int amount) {
        return original + (amount << 8);
    }

    /**
     * @apiNote will be applied in ascending order of value.
     *          Lower values are applied first.
     */
    default int priority() {
        return 0;
    }
}
