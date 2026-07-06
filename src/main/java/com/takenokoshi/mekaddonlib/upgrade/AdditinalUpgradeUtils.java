package com.takenokoshi.mekaddonlib.upgrade;

import com.takenokoshi.mekaddonlib.mixin.UpgradeMixin;

import mekanism.api.Upgrade;
import mekanism.common.item.ItemUpgrade;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.registration.impl.ItemRegistryObject;
import net.minecraft.world.item.ItemStack;

public final class AdditinalUpgradeUtils {

    public static Upgrade getUpradeFromAdditional(IAdditionalUpgrade additionalUpgrade) {
        return UpgradeMixin.mek_addon_lib$getUpradeFromAdditional(additionalUpgrade);
    }

    public static IAdditionalUpgrade convertToAdditional(Upgrade upgrade) {
        return UpgradeMixin.mek_addon_lib$convertToAdditional(upgrade);
    }

    public static boolean isAdditional(Upgrade upgrade) {
        return UpgradeMixin.mek_addon_lib$isAdditional(upgrade);
    }

    public static ItemStack getStack(IAdditionalUpgrade additionalUpgrade, int amount) {
        return UpgradeMixin.mek_addon_lib$getStack(additionalUpgrade, amount);
    }

    public static ItemRegistryObject<ItemUpgrade> registerItem(ItemDeferredRegister deferredRegister, String itemName,
            IAdditionalUpgrade additionalUpgrade) {
        ItemRegistryObject<ItemUpgrade> value = deferredRegister.registerItem(itemName,
                props -> new ItemUpgrade(getUpradeFromAdditional(additionalUpgrade), props));
        UpgradeMixin.mek_addon_lib$registerItem(additionalUpgrade, value);
        return value;
    }
}
