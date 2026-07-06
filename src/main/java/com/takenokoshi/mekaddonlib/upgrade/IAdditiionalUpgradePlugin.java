package com.takenokoshi.mekaddonlib.upgrade;

import java.util.List;

public interface IAdditiionalUpgradePlugin {
    List<? extends IAdditionalUpgrade> getAdditionalUpgrades();
}
