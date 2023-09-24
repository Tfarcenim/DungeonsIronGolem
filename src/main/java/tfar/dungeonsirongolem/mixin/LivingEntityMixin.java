package tfar.dungeonsirongolem.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.dungeonsirongolem.entity.DungeonsIronGolemEntity;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Shadow
    protected int lastHurtByPlayerTime;

    @Shadow
    @Nullable
    protected Player lastHurtByPlayer;

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;getEntity()Lnet/minecraft/world/entity/Entity;"))
    private void markDamagedByPlayer(DamageSource pSource, float pAmount, CallbackInfoReturnable<Boolean> cir) {
        if (pSource.getEntity() instanceof DungeonsIronGolemEntity dungeonsIronGolemEntity) {
            this.lastHurtByPlayerTime = 100;
            LivingEntity livingentity2 = dungeonsIronGolemEntity.getOwner();
            if (livingentity2 instanceof Player player) {
                this.lastHurtByPlayer = player;
            } else {
                this.lastHurtByPlayer = null;
            }
        }
    }
}
