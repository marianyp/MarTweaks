package dev.mariany.martweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.entity.LavaAwareEntity;
import dev.mariany.martweaks.util.ModUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements LavaAwareEntity {
    @Unique
    boolean touchingLava;
    @Unique
    boolean submergedInLava;

    @Shadow
    protected boolean firstUpdate;

    @Override
    public boolean marTweaks$isTouchingLava() {
        return touchingLava && MarTweaks.CONFIG.lavaSwimming.enabled();
    }

    @Override
    public boolean marTweaks$isSubmergedInLava() {
        return submergedInLava && MarTweaks.CONFIG.lavaSwimming.enabled();
    }

    @WrapOperation(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setSwimming(Z)V"))
    public void wrapUpdateSwimming(Entity instance, boolean swimming, Operation<Void> original) {
        Entity entity = (Entity) (Object) this;

        if (!swimming) {
            if (entity instanceof PlayerEntity player) {
                World world = player.getWorld();
                BlockPos pos = player.getBlockPos();
                FluidState fluidState = world.getFluidState(pos);

                boolean canLavaSwim = ModUtils.canLavaSwim(player);
                boolean isLava = fluidState.isIn(FluidTags.LAVA);
                boolean isSprinting = player.isSprinting();
                boolean noVehicle = !player.hasVehicle();
                boolean notFlying = !player.getAbilities().flying;
                boolean wasSwimming = player.isSwimming();

                if (canLavaSwim && notFlying && isSprinting && noVehicle) {
                    if (wasSwimming) {
                        swimming = touchingLava;
                    } else {
                        swimming = submergedInLava && isLava;
                    }
                }
            }
        }

        original.call(instance, swimming);
    }

    @WrapOperation(method = "updateWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    protected boolean wrapUpdateWaterState(Entity entity, TagKey<Fluid> tag, double speed,
                                           Operation<Boolean> original) {
        boolean fireResistant = entity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(
                StatusEffects.FIRE_RESISTANCE);

        boolean touching = original.call(entity, tag, speed);

        if (tag.equals(FluidTags.LAVA)) {
            if (touching && fireResistant) {
                if (!touchingLava && !firstUpdate) {
                    entity.emitGameEvent(GameEvent.SPLASH);
                }
                entity.onLanding();
            }

            touchingLava = touching;
            submergedInLava = entity.isSubmergedIn(FluidTags.LAVA);
        }

        return touching;
    }

    @WrapOperation(method = "isCrawling", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isTouchingWater()Z"))
    public boolean wrapIsTouchingWater(Entity instance, Operation<Boolean> original) {
        return original.call(instance) || touchingLava;
    }

    @Inject(method = "doesRenderOnFire", at = @At(value = "RETURN"), cancellable = true)
    public void injectDoesRenderOnFire(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                cir.setReturnValue(false);
            }
        }
    }
}
