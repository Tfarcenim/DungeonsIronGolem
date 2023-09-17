package tfar.dungeonsirongolem.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import tfar.dungeonsirongolem.entity.DungeonsIronGolemEntity;

public class CustomWalkNodeEvaluator extends WalkNodeEvaluator {


    public BlockPathTypes getBlockPathType(BlockGetter pLevel, int pX, int pY, int pZ) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pX,pY,pZ);

        if (DungeonsIronGolemEntity.shouldNoClip(pLevel.getBlockState(mutable))) {
            return BlockPathTypes.OPEN;
        }

        return getBlockPathTypeStatic(pLevel, mutable);
    }
}
