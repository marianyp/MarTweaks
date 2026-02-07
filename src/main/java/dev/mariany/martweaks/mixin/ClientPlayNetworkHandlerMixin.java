package dev.mariany.martweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mariany.martweaks.engagement.EngagementRewardMarkable;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @WrapOperation(
            method = "onItemPickupAnimation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/world/ClientWorld;playSoundClient(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"
            )
    )
    public void wrapOnItemPickupAnimation(
            ClientWorld world,
            double x,
            double y,
            double z,
            SoundEvent sound,
            SoundCategory category,
            float volume,
            float pitch,
            boolean useDistance,
            Operation<Void> original,
            @Local(index = 1, argsOnly = true) ItemPickupAnimationS2CPacket packet
    ) {
        Entity entity = world.getEntityById(packet.getEntityId());

        if (entity instanceof EngagementRewardMarkable engagementRewardMarkable) {
            if (engagementRewardMarkable.marTweaks$isEngagementReward()) {
                if (sound.equals(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP)) {
                    pitch = MathHelper.nextFloat(world.random, 1.5F, 2);
                }
            }
        }

        original.call(world, x, y, z, sound, category, volume, pitch, useDistance);
    }
}
