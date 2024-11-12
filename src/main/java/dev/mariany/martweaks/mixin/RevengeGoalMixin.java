package dev.mariany.martweaks.mixin;

import dev.mariany.martweaks.mixin.accessor.TrackTargetGoalAccessor;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RevengeGoal.class)
public class RevengeGoalMixin {
    @Inject(method = "canStart", at = @At(value = "HEAD"), cancellable = true)
    public void canStart(CallbackInfoReturnable<Boolean> cir) {
        RevengeGoal goal = (RevengeGoal) (Object) this;
        MobEntity mob = ((TrackTargetGoalAccessor) goal).martweaks$mob();
        if(mob instanceof HostileEntity && mob.getAttacker() instanceof HostileEntity) {
            cir.setReturnValue(false);
        }
    }
}
