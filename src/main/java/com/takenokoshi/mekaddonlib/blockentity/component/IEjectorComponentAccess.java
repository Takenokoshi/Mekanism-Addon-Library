package com.takenokoshi.mekaddonlib.blockentity.component;

import java.util.function.BiPredicate;

import mekanism.api.chemical.IChemicalTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.tile.component.config.DataType;

public interface IEjectorComponentAccess {

    void mek_addon_lib$setCanFluidTankEject(BiPredicate<DataType, IExtendedFluidTank> v);

    void mek_addon_lib$setCanChemicalTankEject(BiPredicate<DataType, IChemicalTank> v);
}