package dev.mariany.martweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.martweaks.entity.LavaAwareEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @WrapOperation(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
    public boolean interceptIsTouchingWater(ClientPlayerEntity player, Operation<Boolean> original) {
        return original.call(player) || ((LavaAwareEntity) player).marTweaks$isTouchingLava();
    }

    @WrapOperation(method = "shouldStopSwimSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
    public boolean wrapShouldStopSwimSprinting(ClientPlayerEntity player, Operation<Boolean> original) {
        return original.call(player) || ((LavaAwareEntity) player).marTweaks$isTouchingLava();
    }

    @WrapOperation(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
    public boolean wrapCanStartSprintingTouch(ClientPlayerEntity player, Operation<Boolean> original) {
        return original.call(player) || ((LavaAwareEntity) player).marTweaks$isTouchingLava();
    }

    @WrapOperation(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSubmergedInWater()Z"))
    public boolean wrapCanStartSprintingSubmerged(ClientPlayerEntity player, Operation<Boolean> original) {
        return original.call(player) || ((LavaAwareEntity) player).marTweaks$isSubmergedInLava();
    }
}
