package com.takenokoshi.mekaddonlib.blockentity.component;

import mekanism.api.RelativeSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class RelativeEjectionTargetModifier implements IEjectionTargetModifier {

    private final int front;
    private final int left;
    private final int top;
    private final RelativeSide direction;

    public RelativeEjectionTargetModifier(int front, int left, int top, RelativeSide direction) {
        this.front = front;
        this.left = left;
        this.top = top;
        this.direction = direction;
    }

    @Override
    public BlockPos modifyPosition(BlockPos def, Direction tileFacing) {
        return def
                .relative(RelativeSide.FRONT.getDirection(tileFacing), front)
                .relative(RelativeSide.LEFT.getDirection(tileFacing), left)
                .relative(RelativeSide.TOP.getDirection(tileFacing), top);
    }

    @Override
    public Direction modifyDirection(Direction def, Direction tileFacing) {
        return direction.getDirection(tileFacing);
    }

}
