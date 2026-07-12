package com.takenokoshi.mekaddonlib.blockentity.component;

import java.util.function.BiPredicate;

import mekanism.api.chemical.IChemicalTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.DataType;

public class EjectorComponentUtils {
    public static void setCanFluidTankEject(TileComponentEjector ejector, BiPredicate<DataType, IExtendedFluidTank> v) {
        ((IEjectorComponentAccess) ejector).mek_addon_lib$setCanFluidTankEject(v);
    }

    public static void setCanChemicalTankEject(TileComponentEjector ejector, BiPredicate<DataType, IChemicalTank> v) {
        ((IEjectorComponentAccess) ejector).mek_addon_lib$setCanChemicalTankEject(v);
    }
}
