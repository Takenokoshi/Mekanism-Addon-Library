package com.takenokoshi.mekaddonlib.upgrade;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mekanism.api.Upgrade;
import net.minecraft.world.level.ItemLike;

/**
 * @apiNote IAdditionalUpgrade is interface, so the return value of hashcode()
 *          is unstable.
 */
class AdditionalUpgradeRegistry {

    static final Map<Upgrade, IAdditionalUpgrade> TO_ADDITIONAL_CACHE = new HashMap<>();
    static final Map<String, Upgrade> FROM_ADDITIONAL_CACHE = new HashMap<>();
    static final Map<String, ItemLike> ITEM_CACHE = new HashMap<>();
    static final Map<String, IAdditionalUpgrade> NAME_CACHE = new HashMap<>();

    static final Map<Upgrade, EnergyModifierUpgradeData> ENERGY_MODIFIERS = new HashMap<>();
    static final Map<Upgrade, SpeedModifierUpgradeData> SPEED_MODIFIERS = new HashMap<>();

    static List<UpgradeRecord<EnergyModifierUpgradeData>> ENERGY_RECORDS;
    static List<UpgradeRecord<SpeedModifierUpgradeData>> SPEED_RECORDS;

    static void initCacheIfNeeded() {
        if (ENERGY_RECORDS == null) {
            ENERGY_RECORDS = ENERGY_MODIFIERS.entrySet().stream()
                    .sorted(Comparator.comparing(entry -> entry.getValue().priority()))
                    .map(entry -> new UpgradeRecord<>(entry.getKey(), entry.getValue()))
                    .toList();
            SPEED_RECORDS = SPEED_MODIFIERS.entrySet().stream()
                    .sorted(Comparator.comparing(entry -> entry.getValue().priority()))
                    .map(entry -> new UpgradeRecord<>(entry.getKey(), entry.getValue()))
                    .toList();
        }
    }

    static record UpgradeRecord<D>(Upgrade upgrade, D data) {
    }
}