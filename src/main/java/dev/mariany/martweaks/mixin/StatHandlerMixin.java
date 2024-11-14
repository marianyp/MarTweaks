package dev.mariany.martweaks.mixin;

import dev.mariany.martweaks.engagement.EngagementManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatHandler.class)
public class StatHandlerMixin {
    @Inject(method = "increaseStat", at = @At(value = "HEAD"))
    public void injectIncreaseStatBefore(PlayerEntity player, Stat<?> stat, int value, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            EngagementManager.onStatIncrement(serverPlayer, stat, true);
        }
    }

    @Inject(method = "increaseStat", at = @At(value = "TAIL"))
    public void injectIncreaseStatAfter(PlayerEntity player, Stat<?> stat, int value, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            EngagementManager.onStatIncrement(serverPlayer, stat, false);
        }
    }
}
