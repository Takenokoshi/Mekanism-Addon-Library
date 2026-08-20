package com.takenokoshi.mekaddonlib.blockentity.component;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface IEjectionTargetModifier {
    BlockPos modifyPosition(BlockPos def, Direction tileFacing);

    Direction modifyDirection(Direction def, Direction tileFacing);

    public static final IEjectionTargetModifier EMPTY_MODIFIER = new EmptyModifier();

    public static class EmptyModifier implements IEjectionTargetModifier {

        private EmptyModifier() {
        }

        @Override
        public BlockPos modifyPosition(BlockPos def, Direction tileFacing) {
            return def;
        }

        @Override
        public Direction modifyDirection(Direction def, Direction tileFacing) {
            return def;
        }

    }
}
