package dev.mariany.martweaks.mixin.accessor;

import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrackTargetGoal.class)
public interface TrackTargetGoalAccessor {
    @Accessor("mob")
    MobEntity martweaks$mob();
}
