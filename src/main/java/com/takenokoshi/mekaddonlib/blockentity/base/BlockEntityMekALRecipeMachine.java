package com.takenokoshi.mekaddonlib.blockentity.base;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.takenokoshi.mekaddonlib.blockentity.interfaces.IWarningSupporter;
import com.takenokoshi.mekaddonlib.recipe.lookup.IMekALRecipeLookupHandler;
import com.takenokoshi.mekaddonlib.recipe.lookup.MekALRecipeCacheLookupMonitor;

import mekanism.api.IContentsListener;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockEntityMekALRecipeMachine<RECIPE extends Recipe<?>> extends TileEntityConfigurableMachine
        implements IMekALRecipeLookupHandler<RECIPE>, IWarningSupporter {

    protected final BooleanSupplier recheckAllRecipeErrors;
    private final List<RecipeError> errorTypes;
    private final boolean[] trackedErrors;
    protected MekALRecipeCacheLookupMonitor<RECIPE> recipeCacheLookupMonitor;
    private @Nullable IContentsListener recipeCacheSaveOnlyListener;
    private @Nullable IContentsListener recipeCacheUnpauseListener;
    private @Nullable IContentsListener recipeCacheUnpauseSaveOnlyListener;

    protected int operationsPerTick;
    protected final int baselineMaxOperations;
    protected long clientEnergyUsed = 0L;

    public BlockEntityMekALRecipeMachine(Holder<Block> blockProvider, BlockPos pos, BlockState state,
            List<RecipeError> errorTypes, int baselineMaxOperations) {
        super(blockProvider, pos, state);
        this.recheckAllRecipeErrors = TileEntityRecipeMachine.shouldRecheckAllErrors(this);
        this.errorTypes = List.copyOf(errorTypes);
        trackedErrors = new boolean[this.errorTypes.size()];
        recipeCacheSaveOnlyListener = null;
        this.baselineMaxOperations = baselineMaxOperations;
        this.operationsPerTick = this.baselineMaxOperations;
    }

    @Override
    protected void presetVariables() {
        super.presetVariables();
        recipeCacheLookupMonitor = createNewCacheMonitor();
    }

    protected MekALRecipeCacheLookupMonitor<RECIPE> createNewCacheMonitor() {
        return new MekALRecipeCacheLookupMonitor<>(this);
    }

    protected IContentsListener getRecipeCacheSaveOnlyListener() {
        if (supportsComparator()) {
            if (recipeCacheSaveOnlyListener == null) {
                recipeCacheSaveOnlyListener = () -> {
                    markForSave();
                    recipeCacheLookupMonitor.onChange();
                };
            }
            return recipeCacheSaveOnlyListener;
        }
        return recipeCacheLookupMonitor;
    }

    protected IContentsListener getRecipeCacheUnpauseListener(@Nullable IContentsListener listener) {
        if (listener == this) {
            if (this.recipeCacheUnpauseListener == null) {
                this.recipeCacheUnpauseListener = () -> {
                    this.onContentsChanged();
                    this.recipeCacheLookupMonitor.unpause();
                };
            }

            return this.recipeCacheUnpauseListener;
        } else {
            if (this.recipeCacheUnpauseSaveOnlyListener == null) {
                this.recipeCacheUnpauseSaveOnlyListener = () -> {
                    this.markForSave();
                    this.recipeCacheLookupMonitor.unpause();
                };
            }

            return this.recipeCacheUnpauseSaveOnlyListener;
        }
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.trackArray(trackedErrors);
        container.track(SyncableLong.create(this::getEnergyUsed, value -> clientEnergyUsed = value));
    }

    @Override
    public void clearRecipeErrors(int cacheIndex) {
        Arrays.fill(trackedErrors, false);
    }

    protected void onErrorsChanged(Set<RecipeError> errors) {
        for (int i = 0; i < trackedErrors.length; i++) {
            trackedErrors[i] = errors.contains(errorTypes.get(i));
        }
    }

    @Override
    public BooleanSupplier getWarningCheck(RecipeError error) {
        int errorIndex = errorTypes.indexOf(error);
        if (errorIndex == -1) {
            return () -> false;
        }
        return () -> trackedErrors[errorIndex];
    }

    public final @Nullable IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        return this.getInitialChemicalTanks(listener,
                (IContentsListener) (listener == this ? this.recipeCacheLookupMonitor
                        : this.getRecipeCacheSaveOnlyListener()),
                this.getRecipeCacheUnpauseListener(listener));
    }

    protected @Nullable IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener,
            IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        return null;
    }

    protected final @Nullable IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        return this.getInitialFluidTanks(listener, (IContentsListener) (listener == this ? this.recipeCacheLookupMonitor
                : this.getRecipeCacheSaveOnlyListener()), this.getRecipeCacheUnpauseListener(listener));
    }

    protected @Nullable IFluidTankHolder getInitialFluidTanks(IContentsListener listener,
            IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        return null;
    }

    protected final @Nullable IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        return this.getInitialEnergyContainers(listener,
                (IContentsListener) (listener == this ? this.recipeCacheLookupMonitor
                        : this.getRecipeCacheSaveOnlyListener()),
                this.getRecipeCacheUnpauseListener(listener));
    }

    protected @Nullable IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener,
            IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        return null;
    }

    protected final @Nullable IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        return this.getInitialInventory(listener, (IContentsListener) (listener == this ? this.recipeCacheLookupMonitor
                : this.getRecipeCacheSaveOnlyListener()), this.getRecipeCacheUnpauseListener(listener));
    }

    protected @Nullable IInventorySlotHolder getInitialInventory(IContentsListener listener,
            IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        return null;
    }

    protected final @Nullable IHeatCapacitorHolder getInitialHeatCapacitors(IContentsListener listener,
            CachedAmbientTemperature ambientTemperature) {
        return this.getInitialHeatCapacitors(listener,
                (IContentsListener) (listener == this ? this.recipeCacheLookupMonitor
                        : this.getRecipeCacheSaveOnlyListener()),
                this.getRecipeCacheUnpauseListener(listener), ambientTemperature);
    }

    protected @Nullable IHeatCapacitorHolder getInitialHeatCapacitors(IContentsListener listener,
            IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener,
            CachedAmbientTemperature ambientTemperature) {
        return null;
    }

    public double getScaledProgress() {
        return getActive() ? 1 : 0;
    }

    public void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(nbt, provider);
    }

    public void saveAdditional(@NotNull CompoundTag nbtTags, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(nbtTags, provider);
    }

    public int getOperationsPerTick() {
        return this.operationsPerTick;
    }

    public long getEnergyUsed() {
        return clientEnergyUsed;
    }

}
