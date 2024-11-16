package dev.mariany.martweaks.util;

import dev.mariany.martweaks.MarTweaks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public final class ModUtils {
    public static void sendSoundToClient(PlayerEntity player, SoundEvent soundEvent, SoundCategory category,
                                         float volume, float pitch) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(
                    new PlaySoundFromEntityS2CPacket(RegistryEntry.of(soundEvent), category, serverPlayer, volume,
                            pitch, serverPlayer.getRandom().nextLong()));
        }
    }

    public static boolean canLavaSwim(LivingEntity entity) {
        return entity.hasStatusEffect(StatusEffects.FIRE_RESISTANCE) && MarTweaks.CONFIG.lavaSwimming.enabled();
    }
}
