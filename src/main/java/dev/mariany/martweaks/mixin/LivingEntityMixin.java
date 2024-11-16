package dev.mariany.martweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.martweaks.entity.LavaAwareEntity;
import dev.mariany.martweaks.entity.boss.guardian.ElderGuardianFight;
import dev.mariany.martweaks.util.ModUtils;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "damage", at = @At(value = "HEAD"), cancellable = true)
    public void injectDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (livingEntity instanceof Leashable leashable && leashable.isLeashed()) {
            if (source.isOf(DamageTypes.FALL) || source.isOf(DamageTypes.IN_WALL)) {
                cir.setReturnValue(false);
            }
        }
    }

    @WrapOperation(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onKilledBy(Lnet/minecraft/entity/LivingEntity;)V"))
    public void wrapOnDeath(LivingEntity instance, LivingEntity adversary, Operation<Void> original) {
        if (instance instanceof ElderGuardianEntity elder) {
            ElderGuardianFight.onElderDeath(elder);
        }
        original.call(instance, adversary);
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isTouchingWater()Z"))
    public boolean interceptIsTouchingWater(LivingEntity instance, Operation<Boolean> original) {
        boolean touching = original.call(instance);

        if (!touching) {
            if (instance instanceof PlayerEntity player) {
                return ((LavaAwareEntity) player).marTweaks$isTouchingLava();
            }
        }

        return touching;
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInLava()Z"))
    public boolean interceptIsInLava(LivingEntity entity, Operation<Boolean> original) {
        if (ModUtils.canLavaSwim(entity)) {
            return false;
        }

        return original.call(entity);
    }
}
