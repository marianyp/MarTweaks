package dev.mariany.martweaks.mixin;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {
    @Inject(method = "getSubmersionType", at = @At(value = "RETURN"), cancellable = true)
    public void injectGetSubmersionType(CallbackInfoReturnable<CameraSubmersionType> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        CameraSubmersionType type = cir.getReturnValue();

        if (player != null) {
            boolean fireResistant = player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE);
            if (fireResistant && type.equals(CameraSubmersionType.LAVA)) {
                cir.setReturnValue(CameraSubmersionType.NONE);
            }
        }
    }
}
