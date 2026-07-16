package com.takenokoshi.mekaddonlib.blockentity.base;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;

import mekanism.api.Upgrade;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.common.util.UpgradeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;

public abstract class BEExpScaledRecipeMachine<RECIPE extends Recipe<?>> extends BlockEntityMekALRecipeMachine<RECIPE> {

    public BEExpScaledRecipeMachine(Holder<Block> blockProvider, BlockPos pos, BlockState state,
            List<RecipeError> errorTypes, int baselineMaxOperations) {
        super(blockProvider, pos, state, errorTypes, baselineMaxOperations);
    }

    public @NotNull List<Component> getInfo(@NotNull Upgrade upgrade) {
        return UpgradeUtils.getExpScaledInfo(this, upgrade);
    }

    protected void recaluculateProcessingSpeed() {
        int baseSpeed = 1 << upgradeComponent.getUpgrades(Upgrade.SPEED);
        if (ModList.get().isLoaded("mekanism_empowered")) {
            try {
                int empowered = upgradeComponent.getUpgrades(Upgrade.valueOf("EMPOWERED_SPEED"));
                if (empowered > 0) {
                    baseSpeed += 2 << empowered;
                }
            } catch (Exception e) {
                // if Mekanism:Empowered's Empowered Speed Upgrade's name was changed,
                // Upgrade.valueOf("EMPOWERED_SPEED") throws error
            }
        }
        operationsPerTick = AdditionalUpgradeUtils.modifyOperations(this,
                baseSpeed);
    }

    @Override
    public void recalculateUpgrades(Upgrade upgrade) {
        if (upgrade == Upgrade.SPEED
                || upgrade.name().equals("EMPOWERED_SPEED")
                || AdditionalUpgradeUtils.isSpeedModifier(upgrade)) {
            recaluculateProcessingSpeed();
        }
        super.recalculateUpgrades(upgrade);
    }

}
