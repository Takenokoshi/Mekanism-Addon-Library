package com.takenokoshi.mekaddonlib.upgrade;

import java.util.Set;

import mekanism.api.Upgrade;
import mekanism.api.math.MathUtils;
import mekanism.common.item.ItemUpgrade;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.common.tile.interfaces.IUpgradeTile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class AdditionalUpgradeUtils {

    public static Upgrade getUpgradeFromAdditional(IAdditionalUpgrade additionalUpgrade) {
        return AdditionalUpgradeRegistry.FROM_ADDITIONAL_CACHE.get(additionalUpgrade.actualName());
    }

    public static IAdditionalUpgrade convertToAdditional(Upgrade upgrade) {
        return AdditionalUpgradeRegistry.TO_ADDITIONAL_CACHE.get(upgrade);
    }

    public static boolean isAdditional(Upgrade upgrade) {
        return AdditionalUpgradeRegistry.TO_ADDITIONAL_CACHE.containsKey(upgrade);
    }

    public static ItemStack getStack(IAdditionalUpgrade additionalUpgrade, int amount) {
        return new ItemStack(
                AdditionalUpgradeRegistry.ITEM_CACHE.getOrDefault(additionalUpgrade.actualName(), Items.REDSTONE),
                amount);
    }

    public static ItemRegistryObject<ItemUpgrade> registerItem(ItemDeferredRegister deferredRegister, String itemName,
            IAdditionalUpgrade additionalUpgrade) {
        Upgrade.ENERGY.getMax();
        ItemRegistryObject<ItemUpgrade> value = deferredRegister.registerItem(itemName,
                props -> new ItemUpgrade(getUpgradeFromAdditional(additionalUpgrade), props));
        AdditionalUpgradeRegistry.ITEM_CACHE.put(additionalUpgrade.actualName(), value);
        return value;
    }

    public static void putCache(IAdditionalUpgrade additionalUpgrade, Upgrade upgrade) {
        AdditionalUpgradeRegistry.TO_ADDITIONAL_CACHE.put(upgrade, additionalUpgrade);
        AdditionalUpgradeRegistry.FROM_ADDITIONAL_CACHE.put(additionalUpgrade.actualName(), upgrade);
        AdditionalUpgradeRegistry.NAME_CACHE.put(additionalUpgrade.actualName(), additionalUpgrade);
    }

    public static IAdditionalUpgrade valueOf(String name) {
        return AdditionalUpgradeRegistry.NAME_CACHE.get(name);
    }

    public static boolean isDuplicate(String name) {
        return AdditionalUpgradeRegistry.FROM_ADDITIONAL_CACHE.containsKey(name);
    }

    /**
     * @apiNote Don't call this. This method will be called from Mixin for return
     *          value of {@link IAdditiionalUpgradePlugin#getEnergyModifiers()}
     */
    public static void registerEnergyModifier(IAdditionalUpgrade additionalUpgrade,
            EnergyModifierUpgradeData upgradeData) {
        AdditionalUpgradeRegistry.ENERGY_MODIFIERS.put(getUpgradeFromAdditional(additionalUpgrade), upgradeData);
    }

    /**
     * @apiNote Don't call this. This method will be called from Mixin for return
     *          value of {@link IAdditiionalUpgradePlugin#getSpeedModifiers()}
     */
    public static void registerSpeedModifier(IAdditionalUpgrade additionalUpgrade,
            SpeedModifierUpgradeData upgradeData) {
        AdditionalUpgradeRegistry.SPEED_MODIFIERS.put(getUpgradeFromAdditional(additionalUpgrade), upgradeData);
    }

    public static boolean isEnergyModifier(IAdditionalUpgrade additionalUpgrade) {
        return AdditionalUpgradeRegistry.ENERGY_MODIFIERS.containsKey(getUpgradeFromAdditional(additionalUpgrade));
    }

    public static boolean isEnergyModifier(Upgrade upgrade) {
        return isAdditional(upgrade) ? isEnergyModifier(convertToAdditional(upgrade)) : false;
    }

    public static boolean isSpeedModifier(IAdditionalUpgrade additionalUpgrade) {
        return AdditionalUpgradeRegistry.SPEED_MODIFIERS.containsKey(getUpgradeFromAdditional(additionalUpgrade));
    }

    public static boolean isSpeedModifier(Upgrade upgrade) {
        return isAdditional(upgrade) ? isSpeedModifier(convertToAdditional(upgrade)) : false;
    }

    public static long modifyUsage(IUpgradeTile tile, double original) {
        AdditionalUpgradeRegistry.initCacheIfNeeded();
        double result = original;
        for (var record : AdditionalUpgradeRegistry.ENERGY_RECORDS) {
            int amount = tile.getComponent().getUpgrades(record.upgrade());
            if (amount > 0) {
                result = record.data().modifyUsage(original, amount);
            }
        }
        return MathUtils.ceilToLong(result);
    }

    public static long modifyStorage(IUpgradeTile tile, double original) {
        AdditionalUpgradeRegistry.initCacheIfNeeded();
        double result = original;
        for (var record : AdditionalUpgradeRegistry.ENERGY_RECORDS) {
            int amount = tile.getComponent().getUpgrades(record.upgrade());
            if (amount > 0) {
                result = record.data().modifyStorage(original, amount);
            }
        }
        return MathUtils.clampToLong(result);
    }

    public static double modifyTicks(IUpgradeTile tile, double original) {
        AdditionalUpgradeRegistry.initCacheIfNeeded();
        double result = original;
        for (var record : AdditionalUpgradeRegistry.SPEED_RECORDS) {
            int amount = tile.getComponent().getUpgrades(record.upgrade());
            if (amount > 0) {
                result = record.data().modifyTicksD(original, amount);
            }
        }
        return result > 0 ? result : 1.0d;
    }

    public static int modifyOperations(IUpgradeTile tile, int original) {
        AdditionalUpgradeRegistry.initCacheIfNeeded();
        int result = original;
        for (var record : AdditionalUpgradeRegistry.SPEED_RECORDS) {
            int amount = tile.getComponent().getUpgrades(record.upgrade());
            if (amount > 0) {
                result = record.data().modifyExpscaledMachineOperations(original, amount);
            }
        }
        return result;
    }

    public static int modifyPumpOutput(IUpgradeTile tile, int original) {
        AdditionalUpgradeRegistry.initCacheIfNeeded();
        int result = original;
        for (var record : AdditionalUpgradeRegistry.SPEED_RECORDS) {
            int amount = tile.getComponent().getUpgrades(record.upgrade());
            if (amount > 0) {
                result = record.data().modifyElectricPumpOutputRate(original, amount);
            }
        }
        return result;
    }

    public static Set<Upgrade> getAdditionalEnergyUpgrades() {
        Upgrade.ENERGY.getMax();
        return AdditionalUpgradeRegistry.ENERGY_MODIFIERS.keySet();
    }

    public static Set<Upgrade> getAdditionalSpeedUpgrades() {
        Upgrade.SPEED.getMax();
        return AdditionalUpgradeRegistry.SPEED_MODIFIERS.keySet();
    }
}
