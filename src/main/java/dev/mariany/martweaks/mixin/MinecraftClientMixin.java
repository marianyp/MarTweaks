package dev.mariany.martweaks.mixin;

import dev.mariany.martweaks.item.custom.BrokenArrowItem;
import dev.mariany.martweaks.packet.serverbound.RemoveArrowPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "doItemUse", at = @At(value = "HEAD"))
    private void injectDoItemUse(CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient) (Object) this;

        if (client.interactionManager == null || client.interactionManager.isBreakingBlock()) {
            return;
        }

        if (client.player == null || !client.player.isSneaking()) {
            return;
        }

        if (client.player.getStuckArrowCount() <= 0) {
            return;
        }

        for (Hand hand : Hand.values()) {
            ItemStack stack = client.player.getStackInHand(hand);

            if (!stack.isEmpty() && !(stack.getItem() instanceof BrokenArrowItem)) {
                return;
            }
        }

        client.player.swingHand(Hand.MAIN_HAND);
        ClientPlayNetworking.send(new RemoveArrowPayload());
    }
}
