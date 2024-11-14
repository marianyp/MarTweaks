package dev.mariany.martweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.martweaks.advancement.ModAdvancements;
import dev.mariany.martweaks.engagement.EngagementManager;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {
    @Shadow
    private ServerPlayerEntity owner;

    @WrapOperation(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/PlayerAdvancementTracker;endTrackingCompleted(Lnet/minecraft/advancement/AdvancementEntry;)V"))
    public void wrapGrantCriterion(PlayerAdvancementTracker instance, AdvancementEntry advancement,
                                   Operation<Void> original) {
        if (ModAdvancements.DISCOVERY_ADVANCEMENTS.contains(advancement.id())) {
            EngagementManager.onDiscover(owner);
        }
        original.call(instance, advancement);
    }
}
