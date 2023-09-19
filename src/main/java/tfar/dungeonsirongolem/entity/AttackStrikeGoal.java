package tfar.dungeonsirongolem.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class AttackStrikeGoal extends Goal {

    DungeonsIronGolemEntity golem;

    private boolean shouldContinue = false;
    public AttackStrikeGoal(DungeonsIronGolemEntity golem) {
        this.golem = golem;
    }

    private BlockPos toAIM;


    int range = 6;

    @Override
    public boolean canUse() {
        if (!golem.isAnimationDone()) return false;
        LivingEntity target = golem.getTarget();
        if (target == null) return false;
        double dist = Math.sqrt(golem.distanceToSqr(target));

        if (dist > range * 1.5f) return false;

        double delX = (target.getX() - golem.getX()) / dist;
        double delZ = (target.getZ() - golem.getZ()) / dist;

        toAIM = golem.blockPosition().offset((int) (delX * range / 1.5f), 0, (int) (delZ * range / 1.5f));
        golem.getLookControl().setLookAt(toAIM.getX() + 0.5f, toAIM.getY(), toAIM.getZ() + 0.5f);

        return true;
    }

    @Override
    public void tick() {
        if (golem.isAnimationDone()) {
            golem.resetTimer();
            shouldContinue = false;
            golem.setAnimation(DungeonsIronGolemEntity.GolemAnimation.NOTHING);
        }

    //    golem.setYRot(rotateFromPos(golem, toAIM.getX() + 0.5d, toAIM.getZ() + 0.5d));

        if (golem.getAnimation() == DungeonsIronGolemEntity.GolemAnimation.STRIKE) {

            //21 tick when the axe hit the entity
            if (golem.getTimer() == 13) {

                golem.doHurtTarget(golem.getTarget());

            /*    AABB box = new AABB(toAIM.getX() - 1f, toAIM.getY(), toAIM.getZ() - 1f, toAIM.getX() + 2f, toAIM.getY() + 3, toAIM.getZ() + 2f);
                List<LivingEntity> entities = golem.level().getEntitiesOfClass(LivingEntity.class, box);
                for (LivingEntity entity : entities) {
                    if (!entity.equals(golem)) setDamage(entity);
                }
                EntityHelper.destroyBlocksInAABB(world, box, false);*/
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    //The entity need to have @RotationBodyController as BodyController
    public static float rotateFromPos(Mob entity, double x, double z) {
        double d0 = x - entity.getX();
        double d2 = z - entity.getZ();
        return (float) ((Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F);
    }

    @Override
    public void start() {
        shouldContinue = true;
        golem.resetTimer();
        golem.setAnimation(DungeonsIronGolemEntity.GolemAnimation.STRIKE);
    }

    @Override
    public boolean canContinueToUse() {
        return shouldContinue;
    }
}
