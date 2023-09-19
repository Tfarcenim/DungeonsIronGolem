package tfar.dungeonsirongolem.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class AttackSlamGoal extends Goal {

    DungeonsIronGolemEntity golem;

    private boolean shouldContinue = false;
    public AttackSlamGoal(DungeonsIronGolemEntity golem) {
        this.golem = golem;
    }

    private BlockPos targetPos;
    int range = 5;

    @Override
    public boolean canUse() {
        if (!golem.useSlam) return false;
        if (!golem.isAnimationDone()) return false;
        LivingEntity target = golem.getTarget();
        if (target == null) return false;
        double dist = Math.sqrt(golem.distanceToSqr(target));

        if (dist > range * 1.5f) return false;

        double delX = (target.getX() - golem.getX()) / dist;
        double delZ = (target.getZ() - golem.getZ()) / dist;

        targetPos = golem.blockPosition().offset((int) (delX * range / 1.5f), 0, (int) (delZ * range / 1.5f));

        return true;
    }

    @Override
    public void tick() {
        if (golem.isAnimationDone()) {
            golem.resetTimer();
            shouldContinue = false;
            golem.setAnimation(DungeonsIronGolemEntity.GolemAnimation.NOTHING);
        }

        if (golem.getAnimation() == DungeonsIronGolemEntity.GolemAnimation.SLAM) {
            //time damage with animation
            if (golem.getTimer() == 15) {
                golem.useSlam = false;//swap to strike for next attack
                AABB box = new AABB(targetPos.getX() - 1f, targetPos.getY(), targetPos.getZ() - 1f, targetPos.getX() + 2f, targetPos.getY() + 3, targetPos.getZ() + 2f);
                List<LivingEntity> entities = golem.level().getEntitiesOfClass(LivingEntity.class, box);
                for (LivingEntity entity : entities) {
                    if (!entity.equals(golem)) {
                        golem.doHurtTarget(entity);
                    }
                }
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        shouldContinue = true;
        golem.resetTimer();
        golem.setAnimation(DungeonsIronGolemEntity.GolemAnimation.SLAM);
    }

    @Override
    public boolean canContinueToUse() {
        return shouldContinue;
    }
}
