package com.takenokoshi.mekaddonlib.blockentity.base;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.takenokoshi.mekaddonlib.blockentity.interfaces.IHasGuiSizeOffset;
import com.takenokoshi.mekaddonlib.recipe.lookup.IMekALRecipeLookupHandler;
import com.takenokoshi.mekaddonlib.recipe.lookup.MekALFactoryRecipeCacheLookupMonitor;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import mekanism.api.IContentsListener;
import mekanism.api.SerializationConstants;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.common.CommonWorldTickHandler;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.util.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockEntityMekALRecipeFactory<RECIPE extends Recipe<?>> extends TileEntityConfigurableMachine
        implements IMekALRecipeLookupHandler<RECIPE>, IHasGuiSizeOffset {

    protected MekALFactoryRecipeCacheLookupMonitor<RECIPE>[] recipeCacheLookupMonitors;
    protected IContentsListener allMonitorsChanged;
    protected final BooleanSupplier[] recheckAllRecipeErrors;
    protected final ErrorTracker errorTracker;
    private final boolean[] activeStates;
    private IContentsListener[] recipeCacheSaveOnlyListeners;
    private IContentsListener[] recipeCacheUnpauseListeners;
    private IContentsListener[] recipeCacheUnpauseSaveOnlyListeners;
    private IContentsListener allRecipeCacheSaveOnlyListener;
    private IContentsListener allRecipeCacheUnpauseListener;
    private IContentsListener allRecipeCacheUnpauseSaveOnlyListener;

    protected long clientEnergyUsed = 0L;
    protected final int[] operationsPerTicks;
    protected final int baselineMaxOperations;
    public FactoryTier tier;
    private boolean sortingNeeded = true;
    protected boolean sorting;

    public BlockEntityMekALRecipeFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state,
            List<RecipeError> errorTypes, Set<RecipeError> globalErrorTypes, int baselineMaxOperations) {
        super(blockProvider, pos, state);
        this.errorTracker = new ErrorTracker(errorTypes, globalErrorTypes, tier.processes);
        this.activeStates = new boolean[tier.processes];
        recheckAllRecipeErrors = new BooleanSupplier[tier.processes];
        for (int i = 0; i < recheckAllRecipeErrors.length; i++) {
            // Note: We store one per slot so that we can recheck the different slots at
            // different times to reduce the
            // load on the server
            recheckAllRecipeErrors[i] = TileEntityRecipeMachine.shouldRecheckAllErrors(this);
        }

        this.baselineMaxOperations = baselineMaxOperations;
        this.operationsPerTicks = new int[tier.processes];
        Arrays.fill(operationsPerTicks, this.baselineMaxOperations);
    }

    // called in super constructor
    @Override
    protected void presetVariables() {
        super.presetVariables();
        tier = Attribute.getTier(getBlockHolder(), FactoryTier.class);
        Runnable setSortingNeeded = () -> sortingNeeded = true;
        recipeCacheLookupMonitors = MekALFactoryRecipeCacheLookupMonitor.createMonitorArray(this, tier.processes,
                setSortingNeeded);
        allMonitorsChanged = () -> {
            for (MekALFactoryRecipeCacheLookupMonitor<RECIPE> monitor : recipeCacheLookupMonitors) {
                monitor.onChange();
            }
        };
    }

    protected IContentsListener[] getRecipeCacheSaveOnlyListeners() {
        if (supportsComparator()) {
            if (recipeCacheSaveOnlyListeners == null) {
                recipeCacheSaveOnlyListeners = new IContentsListener[tier.processes];
                for (int i = 0; i < recipeCacheSaveOnlyListeners.length; i++) {
                    int p = i;
                    recipeCacheSaveOnlyListeners[i] = () -> {
                        this.onContentsChanged();
                        this.recipeCacheLookupMonitors[p].onChange();
                    };
                }
            }
            return recipeCacheSaveOnlyListeners;
        }
        return recipeCacheLookupMonitors;
    }

    protected IContentsListener getAllRecipeCacheSaveOnlyListener() {
        if (supportsComparator()) {
            if (allRecipeCacheSaveOnlyListener == null) {
                allRecipeCacheSaveOnlyListener = () -> {
                    this.onContentsChanged();
                    allMonitorsChanged.onContentsChanged();
                };
            }
            return allRecipeCacheSaveOnlyListener;
        }
        return allMonitorsChanged;
    }

    protected IContentsListener[] getRecipeCacheUnpauseListeners(@Nullable IContentsListener listener) {
        if (listener == this) {
            if (recipeCacheUnpauseListeners == null) {
                recipeCacheUnpauseListeners = new IContentsListener[tier.processes];
                for (int i = 0; i < recipeCacheUnpauseListeners.length; i++) {
                    int p = i;
                    recipeCacheUnpauseListeners[i] = () -> {
                        this.onContentsChanged();
                        this.recipeCacheLookupMonitors[p].unpause();
                    };
                }
            }
            return recipeCacheUnpauseListeners;
        } else {
            if (recipeCacheUnpauseSaveOnlyListeners == null) {
                recipeCacheUnpauseSaveOnlyListeners = new IContentsListener[tier.processes];
                for (int i = 0; i < recipeCacheUnpauseSaveOnlyListeners.length; i++) {
                    int p = i;
                    recipeCacheUnpauseSaveOnlyListeners[i] = () -> {
                        this.markForSave();
                        this.recipeCacheLookupMonitors[p].unpause();
                    };
                }
            }
            return recipeCacheUnpauseSaveOnlyListeners;
        }
    }

    protected IContentsListener getAllRecipeCacheUnpauseListener(@Nullable IContentsListener listener) {
        if (listener == this) {
            if (allRecipeCacheUnpauseListener == null) {
                allRecipeCacheUnpauseListener = () -> {
                    this.onContentsChanged();
                    for (MekALFactoryRecipeCacheLookupMonitor<RECIPE> monitor : recipeCacheLookupMonitors) {
                        monitor.unpause();
                    }
                };
            }
            return allRecipeCacheUnpauseListener;
        } else {
            if (allRecipeCacheUnpauseSaveOnlyListener == null) {
                allRecipeCacheUnpauseSaveOnlyListener = () -> {
                    this.markForSave();
                    for (MekALFactoryRecipeCacheLookupMonitor<RECIPE> monitor : recipeCacheLookupMonitors) {
                        monitor.unpause();
                    }
                };
            }
            return allRecipeCacheUnpauseSaveOnlyListener;
        }
    }

    public BooleanSupplier getWarningCheck(RecipeError error, int processIndex) {
        return errorTracker.getWarningCheck(error, processIndex);
    }

    @Override
    public void clearRecipeErrors(int cacheIndex) {
        Arrays.fill(errorTracker.trackedErrors[cacheIndex], false);
    }

    // called in super constructor after presetVariables
    public final @Nullable IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        return this.getInitialChemicalTanks(listener,
                listener == this
                        ? this.recipeCacheLookupMonitors
                        : this.getRecipeCacheSaveOnlyListeners(),
                listener == this
                        ? this.allMonitorsChanged
                        : this.getAllRecipeCacheSaveOnlyListener(),
                this.getRecipeCacheUnpauseListeners(listener),
                this.getAllRecipeCacheUnpauseListener(listener));
    }

    protected @Nullable IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener,
            IContentsListener[] recipeCacheListeners,
            IContentsListener allRecipeCacheListener,
            IContentsListener[] recipeCacheUnpauseListeners,
            IContentsListener allRecipeCacheUnpauseListeners) {
        return null;
    }

    // called in super constructor after presetVariables
    protected final @Nullable IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        return this.getInitialFluidTanks(listener,
                listener == this
                        ? this.recipeCacheLookupMonitors
                        : this.getRecipeCacheSaveOnlyListeners(),
                listener == this
                        ? this.allMonitorsChanged
                        : this.getAllRecipeCacheSaveOnlyListener(),
                this.getRecipeCacheUnpauseListeners(listener),
                this.getAllRecipeCacheUnpauseListener(listener));
    }

    protected @Nullable IFluidTankHolder getInitialFluidTanks(IContentsListener listener,
            IContentsListener[] recipeCacheListeners,
            IContentsListener allRecipeCacheListener,
            IContentsListener[] recipeCacheUnpauseListeners,
            IContentsListener allRecipeCacheUnpauseListeners) {
        return null;
    }

    // called in super constructor after presetVariables
    protected final @Nullable IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        return this.getInitialEnergyContainers(listener,
                listener == this
                        ? this.recipeCacheLookupMonitors
                        : this.getRecipeCacheSaveOnlyListeners(),
                listener == this
                        ? this.allMonitorsChanged
                        : this.getAllRecipeCacheSaveOnlyListener(),
                this.getRecipeCacheUnpauseListeners(listener),
                this.getAllRecipeCacheUnpauseListener(listener));
    }

    protected @Nullable IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener,
            IContentsListener[] recipeCacheListeners,
            IContentsListener allRecipeCacheListener,
            IContentsListener[] recipeCacheUnpauseListeners,
            IContentsListener allRecipeCacheUnpauseListeners) {
        return null;
    }

    // called in super constructor after presetVariables
    protected final @Nullable IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        return this.getInitialInventory(listener,
                listener == this
                        ? this.recipeCacheLookupMonitors
                        : this.getRecipeCacheSaveOnlyListeners(),
                listener == this
                        ? this.allMonitorsChanged
                        : this.getAllRecipeCacheSaveOnlyListener(),
                this.getRecipeCacheUnpauseListeners(listener),
                this.getAllRecipeCacheUnpauseListener(listener));
    }

    protected @Nullable IInventorySlotHolder getInitialInventory(IContentsListener listener,
            IContentsListener[] recipeCacheListeners,
            IContentsListener allRecipeCacheListener,
            IContentsListener[] recipeCacheUnpauseListeners,
            IContentsListener allRecipeCacheUnpauseListeners) {
        return null;
    }

    // called in super constructor after presetVariables
    protected final @Nullable IHeatCapacitorHolder getInitialHeatCapacitors(IContentsListener listener,
            CachedAmbientTemperature ambientTemperature) {
        return this.getInitialHeatCapacitors(listener,
                listener == this
                        ? this.recipeCacheLookupMonitors
                        : this.getRecipeCacheSaveOnlyListeners(),
                this.getRecipeCacheUnpauseListeners(listener), ambientTemperature);
    }

    protected @Nullable IHeatCapacitorHolder getInitialHeatCapacitors(IContentsListener listener,
            IContentsListener[] recipeCacheListeners, IContentsListener[] recipeCacheUnpauseListeners,
            CachedAmbientTemperature ambientTemperature) {
        return null;
    }

    protected void setActiveState(boolean state, int cacheIndex) {
        activeStates[cacheIndex] = state;
    }

    public boolean getActiveState(int cacheIndex) {
        return activeStates[cacheIndex];
    }

    public double getScaledProgress(int cacheIndex) {
        return getActiveState(cacheIndex) ? 1.0d : 0.0d;
    }

    public boolean isSorting() {
        return sorting;
    }

    protected boolean shouldSort() {
        return sorting && sortingNeeded;
    }

    public void toggleSorting() {
        sorting = !isSorting();
        markForSave();
    }

    protected void updateAndProcess(@Nullable IEnergyContainer energyContainer) {
        if (shouldSort()) {
            runSort();
            sortingNeeded = false;
        } else if (!sortingNeeded && CommonWorldTickHandler.flushTagAndRecipeCaches) {
            sortingNeeded = true;
        }
        if (energyContainer == null) {
            for (MekALFactoryRecipeCacheLookupMonitor<RECIPE> monitor : recipeCacheLookupMonitors) {
                monitor.updateAndProcess();
            }
        } else {
            clientEnergyUsed = 0L;
            for (MekALFactoryRecipeCacheLookupMonitor<RECIPE> monitor : recipeCacheLookupMonitors) {
                clientEnergyUsed += monitor.updateAndProcess(energyContainer);
            }
        }
        updateActive();
    }

    protected void updateActive() {
        boolean isActive = false;
        for (boolean state : activeStates) {
            if (state) {
                isActive = true;
                break;
            }
        }
        setActive(isActive);
    }

    protected void runSort() {
    };

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        errorTracker.track(container);
        container.track(SyncableBoolean.create(this::isSorting, value -> sorting = value));
        container.track(SyncableLong.create(this::getEnergyUsed, value -> clientEnergyUsed = value));
    }

    public int getOperationsPerTick(int cacheIndex) {
        return this.operationsPerTicks[cacheIndex];
    }

    public long getEnergyUsed() {
        return clientEnergyUsed;
    }

    public void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void readSustainedData(Provider provider, CompoundTag data) {
        super.readSustainedData(provider, data);
        NBTUtils.setBooleanIfPresent(data, SerializationConstants.SORTING, v -> sorting = v);
    }

    public void saveAdditional(@NotNull CompoundTag nbtTags, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(nbtTags, provider);
    }

    @Override
    public void writeSustainedData(Provider provider, CompoundTag data) {
        super.writeSustainedData(provider, data);
        data.putBoolean(SerializationConstants.SORTING, isSorting());
    }

    @Override
    protected void collectImplicitComponents(@NotNull DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(MekanismDataComponents.SORTING, isSorting());
    }

    @Override
    protected void applyImplicitComponents(@NotNull BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);
        sorting = input.getOrDefault(MekanismDataComponents.SORTING, sorting);
    }

    protected static class ErrorTracker {

        private final List<RecipeError> errorTypes;
        private final IntSet globalTypes;

        private final boolean[][] trackedErrors;
        private final int processes;

        public ErrorTracker(List<RecipeError> errorTypes, Set<RecipeError> globalErrorTypes, int processes) {
            // Copy the list if it is mutable to ensure it doesn't get changed, otherwise
            // just use the list
            this.errorTypes = List.copyOf(errorTypes);
            globalTypes = new IntArraySet(globalErrorTypes.size());
            for (int i = 0; i < this.errorTypes.size(); i++) {
                RecipeError error = this.errorTypes.get(i);
                if (globalErrorTypes.contains(error)) {
                    globalTypes.add(i);
                }
            }
            this.processes = processes;
            trackedErrors = new boolean[this.processes][];
            int errors = this.errorTypes.size();
            for (int i = 0; i < trackedErrors.length; i++) {
                trackedErrors[i] = new boolean[errors];
            }
        }

        private void track(MekanismContainer container) {
            container.trackArray(trackedErrors);
        }

        public void onErrorsChanged(Set<RecipeError> errors, int processIndex) {
            boolean[] processTrackedErrors = trackedErrors[processIndex];
            for (int i = 0; i < processTrackedErrors.length; i++) {
                processTrackedErrors[i] = errors.contains(errorTypes.get(i));
            }
        }

        private BooleanSupplier getWarningCheck(RecipeError error, int processIndex) {
            if (processIndex >= 0 && processIndex < processes) {
                int errorIndex = errorTypes.indexOf(error);
                if (errorIndex >= 0) {
                    if (globalTypes.contains(errorIndex)) {
                        return () -> {
                            for (boolean[] tracked : trackedErrors) {
                                if (tracked[errorIndex]) {
                                    return true;
                                }
                            }
                            return false;
                        };
                    }
                    return () -> trackedErrors[processIndex][errorIndex];
                }
            }
            // Something went wrong
            return () -> false;
        }
    }

}
