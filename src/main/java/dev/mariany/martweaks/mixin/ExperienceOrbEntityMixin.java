package dev.mariany.martweaks.mixin;

import dev.mariany.martweaks.engagement.EngagementRewardMarkable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin extends Entity implements EngagementRewardMarkable {
    @Unique
    private static final TrackedData<Boolean> ENGAGEMENT_REWARD = DataTracker.registerData(
            ExperienceOrbEntityMixin.class,
            TrackedDataHandlerRegistry.BOOLEAN
    );

    public ExperienceOrbEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public boolean marTweaks$isEngagementReward() {
        return this.dataTracker.get(ENGAGEMENT_REWARD);
    }

    @Override
    public void marTweaks$mark() {
        this.dataTracker.set(ENGAGEMENT_REWARD, true);
    }

    @Inject(method = "initDataTracker", at = @At(value = "TAIL"))
    protected void injectInitDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(ENGAGEMENT_REWARD, false);
    }
}
