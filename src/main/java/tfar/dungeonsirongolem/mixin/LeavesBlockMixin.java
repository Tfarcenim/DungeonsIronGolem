package tfar.dungeonsirongolem.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import tfar.dungeonsirongolem.entity.DungeonsIronGolemEntity;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin extends Block {

	public LeavesBlockMixin(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {

		if (pContext instanceof EntityCollisionContext entityCollisionContext) {
			Entity entity = entityCollisionContext.getEntity();
			if (entity instanceof DungeonsIronGolemEntity) {
				return Shapes.empty();
			}
		}
		return super.getCollisionShape(pState, pLevel, pPos, pContext);
	}

	@Override
	public @Nullable BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
		if (mob instanceof DungeonsIronGolemEntity) {
			return BlockPathTypes.OPEN;//same one that air uses
		}
		return super.getBlockPathType(state, level, pos, mob);
	}
}
