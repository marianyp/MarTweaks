package dev.mariany.martweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.martweaks.advancement.ModAdvancements;
import dev.mariany.martweaks.engagement.EngagementManager;
import dev.mariany.martweaks.mixin.accessor.AdvancementProgressAccessor;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
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
        int obtained = (int) ((AdvancementProgressAccessor) instance.getProgress(
                advancement)).martweaks$criteriaProgresses().values().stream().filter(CriterionProgress::isObtained)
                .count();

        Identifier id = advancement.id();
        boolean biome = obtained > 1 && ModAdvancements.DISCOVERED_ALL_BIOMES.equals(id);
        boolean structure = ModAdvancements.DISCOVERED_ALL_STRUCTURES.equals(id);

        if (biome || structure) {
            EngagementManager.onDiscover(owner);
        }

        original.call(instance, advancement);
    }
}
