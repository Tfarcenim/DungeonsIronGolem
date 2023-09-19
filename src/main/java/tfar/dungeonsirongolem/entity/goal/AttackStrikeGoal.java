package tfar.dungeonsirongolem.entity.goal;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import tfar.dungeonsirongolem.entity.DungeonsIronGolemEntity;

public class AttackStrikeGoal extends Goal {

    DungeonsIronGolemEntity golem;

    private boolean shouldContinue = false;
    public AttackStrikeGoal(DungeonsIronGolemEntity golem) {
        this.golem = golem;
    }

    int range = 3;

    @Override
    public boolean canUse() {
        if (golem.useSlam) return false;
        if (!golem.isAnimationDone()) return false;
        LivingEntity target = golem.getTarget();
        if (target == null) return false;
        double dist = Math.sqrt(golem.distanceToSqr(target));

        return !(dist > range * 1.5f);
    }

    @Override
    public void tick() {
        if (golem.isAnimationDone()) {
            golem.resetTimer();
            shouldContinue = false;
            golem.setAnimation(DungeonsIronGolemEntity.GolemAnimation.NOTHING);
        }

        if (golem.getAnimation() == DungeonsIronGolemEntity.GolemAnimation.STRIKE) {
            //time damage with animation
            if (golem.getTimer() == 13) {
                LivingEntity target = golem.getTarget();
                if (target != null) {
                    golem.doHurtTarget(target);
                    golem.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
                }
                golem.useSlam = true;
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
        golem.setAnimation(DungeonsIronGolemEntity.GolemAnimation.STRIKE);
    }

    @Override
    public boolean canContinueToUse() {
        return shouldContinue;
    }
}
