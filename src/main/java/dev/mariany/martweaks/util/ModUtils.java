package dev.mariany.martweaks.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public final class ModUtils {
    public static void sendSoundToClient(PlayerEntity player, RegistryEntry.Reference<SoundEvent> soundEvent,
                                         SoundCategory category, float volume, float pitch) {
        sendSoundToClient(player, soundEvent.value(), category, volume, pitch);
    }

    public static void sendSoundToClient(PlayerEntity player, SoundEvent soundEvent, SoundCategory category,
                                         float volume, float pitch) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(
                    new PlaySoundFromEntityS2CPacket(RegistryEntry.of(soundEvent), category, serverPlayer, volume,
                            pitch, serverPlayer.getRandom().nextLong()));
        }
    }
}
