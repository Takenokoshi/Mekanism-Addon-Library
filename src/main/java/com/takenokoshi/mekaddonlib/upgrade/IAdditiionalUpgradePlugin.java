package com.takenokoshi.mekaddonlib.upgrade;

import java.util.List;
import java.util.Map;

/**
 * @apiNote MekAL will Register Upgrade via ServiceLoader & Mixin.
 * @apiNote resources/META-INF/services/com.takenokoshi.mekaddonlib.upgrade.IAdditiionalUpgradePlugin
 */
public interface IAdditiionalUpgradePlugin {
    /**
     * @apiNote Will be called from Mixin via ServiceLoader. You can get Registered
     *          Upgrade from
     *          {@link AdditionalUpgradeUtils#getUpgradeFromAdditional(IAdditionalUpgrade)}
     * @apiNote Don't forget Register Item of Upgrade via
     *          {@link AdditionalUpgradeUtils#registerItem(mekanism.common.registration.impl.ItemDeferredRegister, String, IAdditionalUpgrade)}
     */
    List<? extends IAdditionalUpgrade> getAdditionalUpgrades();

    /**
     * @apiNote Will be called from Mixin via ServiceLoader. Mixin register
     *          Modifiers with
     *          {@link AdditionalUpgradeUtils#registerEnergyModifier(IAdditionalUpgrade, EnergyModifierUpgradeData)}
     * @return
     */
    Map<? extends IAdditionalUpgrade, ? extends EnergyModifierUpgradeData> getEnergyModifiers();

    /**
     * @apiNote Will be called from Mixin via ServiceLoader. Mixin register
     *          Modifiers with
     *          {@link AdditionalUpgradeUtils#registerSpeedModifier(IAdditionalUpgrade, SpeedModifierUpgradeData)}
     * @return
     */
    Map<? extends IAdditionalUpgrade, ? extends SpeedModifierUpgradeData> getSpeedModifiers();
}
