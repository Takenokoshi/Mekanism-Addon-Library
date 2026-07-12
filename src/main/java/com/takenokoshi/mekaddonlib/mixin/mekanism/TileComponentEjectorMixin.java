package com.takenokoshi.mekaddonlib.mixin.mekanism;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.takenokoshi.mekaddonlib.blockentity.component.IEjectorComponentAccess;

import mekanism.api.chemical.IChemicalTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.DataType;

@Mixin(value = { TileComponentEjector.class }, remap = false)
public class TileComponentEjectorMixin implements IEjectorComponentAccess {

    @Unique
    @Nullable
    private BiPredicate<DataType, IExtendedFluidTank> mekanism_utilities$canFluidTankEject;

    @Unique
    @Nullable
    private BiPredicate<DataType, IChemicalTank> mekanism_utilities$canChemicalTankEject;

    @Unique
    public void mek_addon_lib$setCanFluidTankEject(BiPredicate<DataType, IExtendedFluidTank> v) {
        mekanism_utilities$canFluidTankEject = v;
    }

    @Unique
    public void mek_addon_lib$setCanChemicalTankEject(BiPredicate<DataType, IChemicalTank> v) {
        mekanism_utilities$canChemicalTankEject = v;
        // replace null check
        ((TileComponentEjector) (Object) this)
                .setCanTankEject(t -> mekanism_utilities$canChemicalTankEject.test(DataType.OUTPUT, t));
    }
    /*
     * CHEMICAL
     *
     * before:
     * canTankEject.test(tank)
     *
     * ↓
     *
     * mekanism_utilities$canChemicalTankEject
     */

    @WrapOperation(method = "eject", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
    private boolean mek_addon_lib$replaceChemicalPredicate(
            Predicate<Object> predicate,
            Object obj,
            Operation<Boolean> original,
            @Local(argsOnly = true) TransmissionType type,
            @Local DataType dataType) {
        if (type == TransmissionType.CHEMICAL
                && obj instanceof IChemicalTank tank) {

            if (mekanism_utilities$canChemicalTankEject != null) {
                return mekanism_utilities$canChemicalTankEject.test(dataType, tank);
            }

            return true;
        }

        return original.call(predicate, obj);
    }

    /*
     * FLUID
     *
     * before:
     * if (!tank.isEmpty())
     *
     * ↓
     *
     * if (!tank.isEmpty() && customPredicate)
     */

    @WrapOperation(method = "eject", at = @At(value = "INVOKE", target = "Lmekanism/api/fluid/IExtendedFluidTank;isEmpty()Z"))
    private boolean mek_addon_lib$modifyFluidEmptyCheck(
            IExtendedFluidTank tank,
            Operation<Boolean> original,
            @Local(argsOnly = true) TransmissionType type,
            @Local DataType dataType) {
        boolean empty = original.call(tank);

        if (type == TransmissionType.FLUID
                && !empty
                && mekanism_utilities$canFluidTankEject != null) {

            boolean allow = mekanism_utilities$canFluidTankEject.test(dataType, tank);

            return !allow;
        }

        return empty;
    }
}