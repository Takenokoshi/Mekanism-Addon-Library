package com.takenokoshi.mekaddonlib.misc;

import java.util.List;

import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradePlugin;
import com.takenokoshi.mekaddonlib.upgrade.IAdditiionalUpgradePlugin;
import com.takenokoshi.mekaddonlib.upgrade.IAdditionalUpgrade;

@AdditionalUpgradePlugin
public class TestUpgradePlugin implements IAdditiionalUpgradePlugin {

    @Override
    public List<? extends IAdditionalUpgrade> getAdditionalUpgrades() {
        return List.of(TestUpgrade.values());
    }

}
