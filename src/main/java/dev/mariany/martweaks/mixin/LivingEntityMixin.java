package dev.mariany.martweaks.mixin;

import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
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
}
