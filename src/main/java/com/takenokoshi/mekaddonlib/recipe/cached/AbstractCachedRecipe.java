package com.takenokoshi.mekaddonlib.recipe.cached;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

import com.takenokoshi.mekaddonlib.mixin.mekanism.OperationTrackerMixin;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import net.minecraft.world.item.crafting.Recipe;

public abstract class AbstractCachedRecipe<RECIPE extends Recipe<?>> implements ICachedRecipe<RECIPE> {

    protected final RECIPE recipe;
    private Set<RecipeError> errors = Collections.emptySet();
    private final BooleanSupplier recheckAllErrors;
    private boolean pausedForErrors = false;
    private BooleanSupplier canHolderFunction;
    private BooleanConsumer setActive;
    protected IntSupplier requiredTicks;
    private Runnable onFinish;
    private LongSupplier perTickEnergy;
    private LongSupplier storedEnergy;
    private LongConsumer useEnergy;
    private IntSupplier baselineMaxOperations;
    private Consumer<OperationTracker> postProcessOperations;
    private Consumer<Set<RecipeError>> onErrorsChange;
    private int operatingTicks;
    private IntConsumer operatingTicksChanged;

    protected AbstractCachedRecipe(RECIPE recipe, BooleanSupplier recheckAllErrors) {
        this.canHolderFunction = ConstantPredicates.ALWAYS_TRUE;
        this.setActive = (active) -> {
        };
        this.requiredTicks = () -> 1;
        this.onFinish = () -> {
        };
        this.perTickEnergy = ConstantPredicates.ZERO_LONG;
        this.storedEnergy = ConstantPredicates.ZERO_LONG;
        this.useEnergy = (energy) -> {
        };
        this.baselineMaxOperations = () -> 1;
        this.postProcessOperations = (tracker) -> {
        };
        this.onErrorsChange = (errors) -> {
        };
        this.operatingTicksChanged = (ticks) -> {
        };
        this.recipe = (Objects.requireNonNull(recipe, "Recipe cannot be null."));
        this.recheckAllErrors = Objects.requireNonNull(recheckAllErrors,
                "Recheck all errors supplier cannot be null.");
    }

    public AbstractCachedRecipe<RECIPE> setCanHolderFunction(BooleanSupplier canHolderFunction) {
        this.canHolderFunction = (BooleanSupplier) Objects.requireNonNull(canHolderFunction,
                "Can holder function cannot be null.");
        return this;
    }

    public AbstractCachedRecipe<RECIPE> setActive(BooleanConsumer setActive) {
        this.setActive = (BooleanConsumer) Objects.requireNonNull(setActive, "Set active consumer cannot be null.");
        return this;
    }

    public AbstractCachedRecipe<RECIPE> setEnergyRequirements(LongSupplier perTickEnergy,
            IEnergyContainer energyContainer) {
        this.perTickEnergy = (LongSupplier) Objects.requireNonNull(perTickEnergy,
                "The per tick energy cannot be null.");
        Objects.requireNonNull(energyContainer, "Energy container cannot be null.");
        Objects.requireNonNull(energyContainer);
        this.storedEnergy = energyContainer::getEnergy;
        this.useEnergy = (energy) -> energyContainer.extract(energy, Action.EXECUTE, AutomationType.INTERNAL);
        return this;
    }

    public AbstractCachedRecipe<RECIPE> setRequiredTicks(IntSupplier requiredTicks) {
        this.requiredTicks = Objects.requireNonNull(requiredTicks, "Required ticks cannot be null.");
        return this;
    }

    public AbstractCachedRecipe<RECIPE> setOperatingTicksChanged(IntConsumer operatingTicksChanged) {
        this.operatingTicksChanged = Objects.requireNonNull(operatingTicksChanged,
                "Operating ticks changed handler cannot be null.");
        return this;
    }

    public AbstractCachedRecipe<RECIPE> setOnFinish(Runnable onFinish) {
        this.onFinish = (Runnable) Objects.requireNonNull(onFinish, "On finish handling cannot be null.");
        return this;
    }

    public AbstractCachedRecipe<RECIPE> setBaselineMaxOperations(IntSupplier baselineMaxOperations) {
        this.baselineMaxOperations = Objects.requireNonNull(baselineMaxOperations,
                "Baseline max operations cannot be null.");
        return this;
    }

    public AbstractCachedRecipe<RECIPE> setPostProcessOperations(Consumer<OperationTracker> postProcessOperations) {
        this.postProcessOperations = Objects.requireNonNull(postProcessOperations,
                "Post processing of the operation count cannot be null.");
        return this;
    }

    public AbstractCachedRecipe<RECIPE> setErrorsChanged(Consumer<Set<RecipeError>> onErrorsChange) {
        this.onErrorsChange = Objects.requireNonNull(onErrorsChange,
                "On errors change consumer cannot be null.");
        return this;
    }

    private void updateErrors(Set<RecipeError> errors) {
        if (!this.errors.equals(errors)) {
            this.errors = errors;
            if (this.errors.size() > 1) {
                this.pausedForErrors = true;
            } else {
                this.pausedForErrors = !this.errors
                        .contains(RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE);
            }

            this.onErrorsChange.accept(errors);
        }

    }

    public void unpauseErrors() {
        this.pausedForErrors = false;
    }

    public void loadSavedOperatingTicks(int operatingTicks) {
        if (operatingTicks > 0 && operatingTicks < this.requiredTicks.getAsInt()) {
            this.operatingTicks = operatingTicks;
        }

    }

    public void process() {
        if (this.pausedForErrors) {
            this.setActive.accept(false);
        } else {
            int operations;
            if (this.canHolderFunction.getAsBoolean()) {
                this.setupVariableValues();
                OperationTracker tracker = OperationTrackerMixin.mek_addon_lib$invokeInit(this.errors,
                        this.recheckAllErrors.getAsBoolean(),
                        this.baselineMaxOperations.getAsInt());
                this.calculateOperationsThisTick(tracker);
                if (tracker.shouldContinueChecking()) {
                    this.postProcessOperations.accept(tracker);
                    if (tracker.shouldContinueChecking()
                            && ((OperationTrackerMixin) (Object) tracker)
                                    .mek_addon_lib$invokeCapAtMaxForEnergy()) {
                        tracker.addError(RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE);
                    }
                }

                operations = ((OperationTrackerMixin) (Object) tracker).mek_addon_lib$getCurrentMax();
                if (((OperationTrackerMixin) (Object) tracker).mek_addon_lib$invokeHasErrorsToCopy()) {
                    this.updateErrors(((OperationTrackerMixin) (Object) tracker).mek_addon_lib$getErrors());
                }
            } else {
                operations = 0;
                if (!this.errors.isEmpty()) {
                    this.updateErrors(Collections.emptySet());
                }
            }

            if (operations > 0) {
                this.setActive.accept(true);
                this.useEnergy(operations);
                ++this.operatingTicks;
                int ticksRequired = this.requiredTicks.getAsInt();
                if (this.operatingTicks >= ticksRequired) {
                    this.operatingTicks = 0;
                    this.finishProcessing(operations);
                    this.onFinish.run();
                    this.resetCache();
                } else {
                    this.useResources(operations);
                }

                if (ticksRequired > 1) {
                    this.operatingTicksChanged.accept(this.operatingTicks);
                }
            } else {
                this.setActive.accept(false);
                if (operations < 0) {
                    this.operatingTicks = 0;
                    this.operatingTicksChanged.accept(this.operatingTicks);
                    this.resetCache();
                }
            }

        }
    }

    protected void setupVariableValues() {
    }

    protected int getOperatingTicks() {
        return this.operatingTicks;
    }

    protected void useResources(int operations) {
    }

    protected void resetCache() {
    }

    protected void useEnergy(int operations) {
        long energy = this.perTickEnergy.getAsLong();
        if (operations == 1) {
            this.useEnergy.accept(energy);
        } else {
            this.useEnergy.accept(energy * operations);
        }

    }

    protected void calculateOperationsThisTick(OperationTracker tracker) {
        if (tracker.shouldContinueChecking()) {
            long energyPerTick = this.perTickEnergy.getAsLong();
            if (energyPerTick != 0L) {
                int operations = MathUtils.clampToInt(this.storedEnergy.getAsLong() / energyPerTick);
                ((OperationTrackerMixin) (Object) tracker).mek_addon_lib$setMaxForEnergy(operations);
                if (operations == 0) {
                    tracker.updateOperations(operations);
                    tracker.addError(RecipeError.NOT_ENOUGH_ENERGY);
                }
            }
        }

    }

    public abstract boolean isInputValid();

    protected abstract void finishProcessing(int var1);

    public RECIPE getRecipe() {
        return this.recipe;
    }
}