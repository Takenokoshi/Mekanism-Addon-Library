package com.takenokoshi.mekaddonlib.blockentity.interfaces;

public interface IHasGuiSizeOffset {
    default int getExtraWidth() {
        return 0;
    };

    default int getExtraHeight() {
        return 0;
    };
}