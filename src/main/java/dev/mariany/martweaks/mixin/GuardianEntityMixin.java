package dev.mariany.martweaks.mixin;

import dev.mariany.martweaks.entity.boss.guardian.ElderGuardianFight;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuardianEntity.class)
public class GuardianEntityMixin {
    @Inject(method = "damage", at = @At(value = "RETURN"))
    public void damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        GuardianEntity guardian = (GuardianEntity) (Object) this;
        if (!guardian.getWorld().isClient && guardian instanceof ElderGuardianEntity elder) {
            ElderGuardianFight.onElderDamaged(elder, source);
        }
    }
}
