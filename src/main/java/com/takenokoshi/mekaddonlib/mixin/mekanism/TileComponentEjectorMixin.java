package com.takenokoshi.mekaddonlib.mixin.mekanism;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.takenokoshi.mekaddonlib.blockentity.component.IEjectionTargetModifier;
import com.takenokoshi.mekaddonlib.blockentity.component.IEjectorComponentAccess;

import mekanism.api.RelativeSide;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.IMultiTypeCapability;
import mekanism.common.capabilities.MultiTypeCapability;
import mekanism.common.integration.energy.BlockEnergyCapabilityCache;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.DataType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.items.IItemHandler;

@Mixin(value = { TileComponentEjector.class }, remap = false)
public class TileComponentEjectorMixin implements IEjectorComponentAccess {

    @Unique
    @Nullable
    private BiPredicate<DataType, IExtendedFluidTank> mek_addon_lib$canFluidTankEject;

    @Unique
    @Nullable
    private BiPredicate<DataType, IChemicalTank> mek_addon_lib$canChemicalTankEject;

    @Unique
    private Map<TransmissionType, Map<RelativeSide, IEjectionTargetModifier>> mek_addon_lib$ejectionTargetModifiers;

    @Shadow
    private TileEntityMekanism tile;

    @Shadow
    private Map<TransmissionType, Map<Direction, BlockCapabilityCache<?, @Nullable Direction>>> capabilityCaches;

    @Shadow
    private Map<Direction, BlockEnergyCapabilityCache> energyCapabilityCache;

    @Unique
    public void mek_addon_lib$setCanFluidTankEject(BiPredicate<DataType, IExtendedFluidTank> v) {
        mek_addon_lib$canFluidTankEject = v;
    }

    @Unique
    public void mek_addon_lib$setCanChemicalTankEject(BiPredicate<DataType, IChemicalTank> v) {
        mek_addon_lib$canChemicalTankEject = v;
        // replace null check
        ((TileComponentEjector) (Object) this)
                .setCanTankEject(t -> mek_addon_lib$canChemicalTankEject.test(DataType.OUTPUT, t));
    }

    @Unique
    public void mek_addon_lib$setEjectionTargetModifier(TransmissionType type, RelativeSide side,
            IEjectionTargetModifier modifier) {
        mek_addon_lib$ejectionTargetModifiers.get(type).put(side, modifier);
    }

    @Override
    public void mek_addon_lib$clearCapabilityCaches() {
        capabilityCaches.clear();
        energyCapabilityCache.clear();
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

            if (mek_addon_lib$canChemicalTankEject != null) {
                return mek_addon_lib$canChemicalTankEject.test(dataType, tank);
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
                && mek_addon_lib$canFluidTankEject != null) {

            boolean allow = mek_addon_lib$canFluidTankEject.test(dataType, tank);

            return !allow;
        }

        return empty;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mek_addon_lib$initUniqueFields(CallbackInfo ci) {
        mek_addon_lib$ejectionTargetModifiers = new EnumMap<>(TransmissionType.class);
        Map<RelativeSide, IEjectionTargetModifier> map = new EnumMap<>(RelativeSide.class);
        for (RelativeSide side : RelativeSide.values()) {
            map.put(side, IEjectionTargetModifier.EMPTY_MODIFIER);
        }
        for (TransmissionType type : TransmissionType.values()) {
            mek_addon_lib$ejectionTargetModifiers.put(type, new EnumMap<>(map));
        }
    }

    @WrapOperation(//
            method = { "eject" }, //
            at = @At(value = "INVOKE", target = "Lmekanism/common/tile/component/TileComponentEjector;getCapabilityCaches(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Ljava/util/Map;Ljava/util/Set;Lmekanism/common/capabilities/IMultiTypeCapability;)Ljava/util/List;"))
    private <HANDLER> List<BlockCapabilityCache<HANDLER, @Nullable Direction>> mek_addon_lib$wrapCapabilityCaches(
            ServerLevel level, BlockPos pos,
            Map<Direction, BlockCapabilityCache<?, @Nullable Direction>> typeCapabilityCaches, Set<Direction> sides,
            IMultiTypeCapability<HANDLER, ?> capability,
            Operation<List<BlockCapabilityCache<HANDLER, @Nullable Direction>>> original) {
        if (capability == Capabilities.FLUID) {
            return mek_addon_lib$getCapabilityCaches(TransmissionType.FLUID, level, pos, typeCapabilityCaches,
                    sides, capability);
        } else if (capability == Capabilities.CHEMICAL) {
            return mek_addon_lib$getCapabilityCaches(TransmissionType.CHEMICAL, level, pos, typeCapabilityCaches,
                    sides, capability);
        }
        return original.call(level, pos, typeCapabilityCaches, sides, capability);
    }

    @Unique
    @SuppressWarnings("unchecked")
    private <HANDLER> List<BlockCapabilityCache<HANDLER, @Nullable Direction>> mek_addon_lib$getCapabilityCaches(
            TransmissionType type,
            ServerLevel level, BlockPos pos,
            Map<Direction, BlockCapabilityCache<?, @Nullable Direction>> typeCapabilityCaches, Set<Direction> sides,
            IMultiTypeCapability<HANDLER, ?> capability) {
        List<BlockCapabilityCache<HANDLER, @Nullable Direction>> caches = new ArrayList<>(sides.size());
        Direction tileFacing = this.tile.facingSupplier.get();
        for (Direction side : sides) {
            RelativeSide relativeSide = RelativeSide.fromDirections(tileFacing, side);
            IEjectionTargetModifier modifier = this.mek_addon_lib$ejectionTargetModifiers.get(type)
                    .get(relativeSide);
            BlockCapabilityCache<HANDLER, @Nullable Direction> cache = (BlockCapabilityCache<HANDLER, @Nullable Direction>) typeCapabilityCaches
                    .get(side);
            if (cache == null) {
                cache = capability.createCache(level, modifier.modifyPosition(pos.relative(side), tileFacing),
                        modifier.modifyDirection(side.getOpposite(), tileFacing));
                typeCapabilityCaches.put(side, cache);
            }
            caches.add(cache);
        }
        return caches;
    }

    @WrapOperation(method = "outputItems", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/MultiTypeCapability;createCache(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Lnet/neoforged/neoforge/capabilities/BlockCapabilityCache;"))
    private BlockCapabilityCache<IItemHandler, Direction> mek_addon_lib$modifyItemCache(
            MultiTypeCapability<IItemHandler> capability,
            ServerLevel level,
            BlockPos pos,
            Direction direction,
            Operation<BlockCapabilityCache<IItemHandler, Direction>> original) {
        Direction tileFacing = tile.facingSupplier.get();
        RelativeSide side = RelativeSide.fromDirections(tileFacing, direction);

        IEjectionTargetModifier modifier = mek_addon_lib$ejectionTargetModifiers
                .get(TransmissionType.ITEM)
                .get(side);

        return original.call(
                capability,
                level,
                modifier.modifyPosition(pos, tileFacing),
                modifier.modifyDirection(direction, tileFacing));
    }

    @WrapOperation(method = "eject", at = @At(value = "INVOKE", target = "Lmekanism/common/integration/energy/BlockEnergyCapabilityCache;(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Lmekanism/common/integration/energy/BlockEnergyCapabilityCache;"))
    private BlockEnergyCapabilityCache mek_addon_lib$modifyEnergyCache(ServerLevel level, BlockPos pos,
            Direction direction, Operation<BlockEnergyCapabilityCache> original) {
        Direction tileFacing = tile.facingSupplier.get();
        RelativeSide side = RelativeSide.fromDirections(tileFacing, direction);
        IEjectionTargetModifier modifier = mek_addon_lib$ejectionTargetModifiers.get(TransmissionType.ENERGY).get(side);
        return original.call(level, modifier.modifyPosition(pos, tileFacing),
                modifier.modifyDirection(direction, tileFacing));
    }
}