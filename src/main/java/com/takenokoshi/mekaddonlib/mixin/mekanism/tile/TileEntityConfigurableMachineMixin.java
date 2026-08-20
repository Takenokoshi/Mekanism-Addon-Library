package com.takenokoshi.mekaddonlib.mixin.mekanism.tile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.SoftOverride;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.takenokoshi.mekaddonlib.blockentity.component.EjectorComponentUtils;

import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import net.minecraft.core.Direction;

@Mixin(value = {TileEntityConfigurableMachine.class},remap = false)
public class TileEntityConfigurableMachineMixin extends TileEntityMekanismMixin {
    @Shadow
    private TileComponentEjector ejectorComponent;

    @SoftOverride
    @Override
    protected void mek_addon_lib$onSetFacing(Direction direction, boolean notifyCaps, CallbackInfo ci) {
        super.mek_addon_lib$onSetFacing(direction, notifyCaps, ci);
        EjectorComponentUtils.clearCapabilityCaches(ejectorComponent);
    }
}
