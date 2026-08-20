package com.takenokoshi.mekaddonlib.blockentity.component;

import java.util.function.BiPredicate;

import mekanism.api.RelativeSide;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.DataType;

public class EjectorComponentUtils {
    public static void setCanFluidTankEject(TileComponentEjector ejector, BiPredicate<DataType, IExtendedFluidTank> v) {
        ((IEjectorComponentAccess) ejector).mek_addon_lib$setCanFluidTankEject(v);
    }

    public static void setCanChemicalTankEject(TileComponentEjector ejector, BiPredicate<DataType, IChemicalTank> v) {
        ((IEjectorComponentAccess) ejector).mek_addon_lib$setCanChemicalTankEject(v);
    }

    public static void setEjectionTargetModifier(TileComponentEjector ejector, TransmissionType type, RelativeSide side,
            IEjectionTargetModifier modifier) {
        ((IEjectorComponentAccess) ejector).mek_addon_lib$setEjectionTargetModifier(type, side, modifier);
    }

    public static void clearCapabilityCaches(TileComponentEjector ejector) {
        ((IEjectorComponentAccess) ejector).mek_addon_lib$clearCapabilityCaches();
    }
}
