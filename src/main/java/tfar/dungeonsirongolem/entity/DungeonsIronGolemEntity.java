package tfar.dungeonsirongolem.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tfar.dungeonsirongolem.DungeonsIronGolem;
import tfar.dungeonsirongolem.IronGolemKitItem;
import tfar.dungeonsirongolem.entity.goal.AttackSlamGoal;
import tfar.dungeonsirongolem.entity.goal.AttackStrikeGoal;
import tfar.dungeonsirongolem.entity.goal.FollowAggresivelyGoal;
import tfar.dungeonsirongolem.entity.goal.FollowSummonerGoal;

import java.util.Optional;
import java.util.UUID;

public class DungeonsIronGolemEntity extends PathfinderMob implements GeoEntity, NeutralMob,OwnableEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> ANIMATION = SynchedEntityData.defineId(DungeonsIronGolemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOULD_ANIMATION_CONTINUE = SynchedEntityData.defineId(DungeonsIronGolemEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(DungeonsIronGolemEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private int timer = 0;//server
    private boolean reset;//client
    public boolean useSlam;//server

    public DungeonsIronGolemEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        moveControl = new GolemMoveControl(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AttackStrikeGoal(this));
        this.goalSelector.addGoal(1, new AttackSlamGoal(this));
        this.goalSelector.addGoal(2, new FollowAggresivelyGoal(this,1.25,true));
        //  this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
      //  this.goalSelector.addGoal(2, new MoveBackToVillageGoal(this, 0.6D, false));
   //     this.goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(this, 0.6D));
      //  this.goalSelector.addGoal(5, new OfferFlowerGoal(this));

        this.goalSelector.addGoal(6, new FollowSummonerGoal(this, 1.0D, 11.0F, 3.0F, false));

        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
       // this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
      //  this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (livingEntity) -> {
            return livingEntity instanceof Enemy && !(livingEntity instanceof Creeper);
        }));

        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));


        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 120.0D).add(Attributes.MOVEMENT_SPEED, 0.30D).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D).add(Attributes.ATTACK_DAMAGE, 18.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ANIMATION,0);
        entityData.define(SHOULD_ANIMATION_CONTINUE,false);
        entityData.define(DATA_OWNERUUID_ID, Optional.empty());
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwnerUUID(@javax.annotation.Nullable UUID pUuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(pUuid));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.getOwnerUUID() != null) {
            pCompound.putUUID("Owner", this.getOwnerUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        UUID uuid;
        if (pCompound.hasUUID("Owner")) {
            uuid = pCompound.getUUID("Owner");
        } else {
            String s = pCompound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
            } catch (Throwable throwable) {
            }
        }
    }

    public enum GolemAnimation {
        NOTHING(Integer.MAX_VALUE), STRIKE(25),SLAM(30);

        private final int duration;

        GolemAnimation(int duration) {

            this.duration = duration;
        }
    }

    public GolemAnimation getAnimation() {
        return GolemAnimation.values()[entityData.get(ANIMATION)];
    }

     public void setAnimation(GolemAnimation animation) {
        entityData.set(ANIMATION,animation.ordinal());
    }

    private boolean shouldAnimationContinue() {
        return entityData.get(SHOULD_ANIMATION_CONTINUE);
    }

    private void setShouldAnimationContinue(boolean shouldAnimationContinue) {
        entityData.set(SHOULD_ANIMATION_CONTINUE,shouldAnimationContinue);
    }

    public boolean isAnimationDone() {
        if (getAnimation() == GolemAnimation.NOTHING) return true;
        GolemAnimation animation = getAnimation();
        return animation == null || timer >= animation.duration;
    }

    public void resetTimer() {
        timer = 0;
    }

    public int getTimer() {
        return timer;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
        controllers.add(swipeAnimation());
    }

    public AnimationController<DungeonsIronGolemEntity> swipeAnimation() {
        return new AnimationController<>(this, "Attack", 3, state -> {

            if (reset) {
                reset = false;
                state.getController().forceAnimationReset();
                return PlayState.STOP;
            }

            if (shouldAnimationContinue()) {
                switch (getAnimation()) {
                    case SLAM -> {
                        return state.setAndContinue(DefaultAnimations.ATTACK_SLAM);
                    }
                    case STRIKE -> {
                        return state.setAndContinue(DefaultAnimations.ATTACK_STRIKE);
                    }
                }
            }
            state.getController().forceAnimationReset();
            return PlayState.STOP;
        });
    }


    @Override
    public void aiStep() {
        super.aiStep();

        if (!level().isClientSide) {
            if (!shouldAnimationContinue()) {
                setShouldAnimationContinue(true);
            }

            GolemAnimation animation = getAnimation();
            updateAnimationTimer(animation);
        } else {
            if (!shouldAnimationContinue()) reset = true;
        }
    }

    private void updateAnimationTimer(GolemAnimation animation) {
        if (animation.duration > 0 && timer < animation.duration) {
            timer++;
            if (timer > animation.duration) setShouldAnimationContinue(false);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return 0;
    }

    @Override
    public void setRemainingPersistentAngerTime(int pRemainingPersistentAngerTime) {

    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return null;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID pPersistentAngerTarget) {

    }

    @Override
    public void startPersistentAngerTimer() {

    }

    @Override
    public void die(DamageSource pDamageSource) {
        super.die(pDamageSource);
        if (!level().isClientSide) {
            DungeonsIronGolem.ironGolemSavedData.removeGolem(uuid);
            IronGolemKitItem.addDeathCooldown(uuid);
        }
    }

    protected PathNavigation createNavigation(Level pLevel) {
        return new GolemPathNavgation(this, pLevel);
    }

    public static class GolemPathNavgation extends GroundPathNavigation {

        public GolemPathNavgation(Mob pMob, Level pLevel) {
            super(pMob, pLevel);
        }

        @Override
        protected PathFinder createPathFinder(int pMaxVisitedNodes) {
            this.nodeEvaluator = new CustomWalkNodeEvaluator();
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, pMaxVisitedNodes);
        }
    }

    public static class GolemMoveControl extends MoveControl {
        public GolemMoveControl(Mob pMob) {
            super(pMob);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.STRAFE) {
                float f = (float)this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
                float f1 = (float)this.speedModifier * f;
                float f2 = this.strafeForwards;
                float f3 = this.strafeRight;
                float f4 = Mth.sqrt(f2 * f2 + f3 * f3);
                if (f4 < 1.0F) {
                    f4 = 1.0F;
                }

                f4 = f1 / f4;
                f2 *= f4;
                f3 *= f4;
                float f5 = Mth.sin(this.mob.getYRot() * ((float)Math.PI / 180F));
                float f6 = Mth.cos(this.mob.getYRot() * ((float)Math.PI / 180F));
                float f7 = f2 * f6 - f3 * f5;
                float f8 = f3 * f6 + f2 * f5;
                if (!this.isWalkable1(f7, f8)) {
                    this.strafeForwards = 1.0F;
                    this.strafeRight = 0.0F;
                }

                this.mob.setSpeed(f1);
                this.mob.setZza(this.strafeForwards);
                this.mob.setXxa(this.strafeRight);
                this.operation = MoveControl.Operation.WAIT;
            } else if (this.operation == MoveControl.Operation.MOVE_TO) {
                this.operation = MoveControl.Operation.WAIT;
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedZ - this.mob.getZ();
                double d2 = this.wantedY - this.mob.getY();
                double d3 = d0 * d0 + d2 * d2 + d1 * d1;
                if (d3 < (double)2.5000003E-7F) {
                    this.mob.setZza(0.0F);
                    return;
                }

                float f9 = (float)(Mth.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 90.0F));
                this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                BlockPos blockpos = this.mob.blockPosition();
                BlockState blockstate = this.mob.level().getBlockState(blockpos);
                VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level(), blockpos, CollisionContext.of(mob));//the only change
                if (d2 > (double)this.mob.getStepHeight() && d0 * d0 + d1 * d1 < (double)Math.max(1.0F, this.mob.getBbWidth()) || !voxelshape.isEmpty() && this.mob.getY() < voxelshape.max(Direction.Axis.Y) + (double)blockpos.getY() && !blockstate.is(BlockTags.DOORS) && !blockstate.is(BlockTags.FENCES)) {
                    this.mob.getJumpControl().jump();
                    this.operation = MoveControl.Operation.JUMPING;
                }
            } else if (this.operation == MoveControl.Operation.JUMPING) {
                this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                if (this.mob.onGround()) {
                    this.operation = MoveControl.Operation.WAIT;
                }
            } else {
                this.mob.setZza(0.0F);
            }
        }

        private boolean isWalkable1(float pRelativeX, float pRelativeZ) {
            PathNavigation pathnavigation = this.mob.getNavigation();
            if (pathnavigation != null) {
                NodeEvaluator nodeevaluator = pathnavigation.getNodeEvaluator();
                return nodeevaluator == null || nodeevaluator.getBlockPathType(this.mob.level(), Mth.floor(this.mob.getX() + (double) pRelativeX), this.mob.getBlockY(), Mth.floor(this.mob.getZ() + (double) pRelativeZ)) == BlockPathTypes.WALKABLE;
            }
            return true;
        }
    }

    public static boolean shouldNoClip(BlockState state) {
        return state.is(BlockTags.LEAVES);
    }
}
