package com.takenokoshi.mekaddonlib.upgrade;

import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import net.minecraft.nbt.CompoundTag;

public interface IAdditionalUpgrade {

    default String actualName() {
        return modId() + "$" + path();
    }

    String modId();

    String path();

    ILangEntry langKey();

    ILangEntry descLangKey();

    int maxStack();

    EnumColor color();

    default CompoundTag getTag(int amount) {
        CompoundTag result = new CompoundTag();
        result.putString("type", actualName());
        result.putInt("amount", amount);
        return result;
    }

}
