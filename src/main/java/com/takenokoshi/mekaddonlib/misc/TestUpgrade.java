package com.takenokoshi.mekaddonlib.misc;

import com.takenokoshi.mekaddonlib.upgrade.IAdditionalUpgrade;

import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;

public enum TestUpgrade implements IAdditionalUpgrade {
    LIBRARY
    ;

    @Override
    public String modId() {
        return "mek_addon_lib";
    }

    @Override
    public String path() {
        return "test";
    }

    @Override
    public ILangEntry langKey() {
        return new ILangEntry() {

            @Override
            public String getTranslationKey() {
                return "aaaaa";
            }
            
        };
    }

    @Override
    public ILangEntry descLangKey() {
        return new ILangEntry() {

            @Override
            public String getTranslationKey() {
                return "bbbbb";
            }
            
        };
    }

    @Override
    public int maxStack() {
        return 8;
    }

    @Override
    public EnumColor color() {
        return EnumColor.RED;
    }
    
}
