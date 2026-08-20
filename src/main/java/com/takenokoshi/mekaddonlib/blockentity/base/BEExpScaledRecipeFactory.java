package com.takenokoshi.mekaddonlib.blockentity.base;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

public abstract class BEExpScaledRecipeFactory<RECIPE extends Recipe<?>> extends BlockEntityMekALRecipeFactory<RECIPE> {

    public BEExpScaledRecipeFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state,
            List<RecipeError> errorTypes, Set<RecipeError> globalErrorTypes, int baselineMaxOperations) {
        super(blockProvider, pos, state, errorTypes, globalErrorTypes, baselineMaxOperations);
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
        int operationsPerTick = AdditionalUpgradeUtils.modifyOperations(this,
                baseSpeed);
        Arrays.fill(operationsPerTicks, operationsPerTick);
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
