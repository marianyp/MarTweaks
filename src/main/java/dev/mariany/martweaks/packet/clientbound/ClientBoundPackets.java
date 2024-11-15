package dev.mariany.martweaks.packet.clientbound;

import dev.mariany.martweaks.client.MarTweaksClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class ClientBoundPackets {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(EngagedPayload.ID, (payload, context) -> {
            if (MarTweaksClient.CONFIG.enableEngagementXpRewardSounds()) {
                playGainExperienceSound();
            }
        });
    }

    private static void playGainExperienceSound() {
        playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_FALL, 0.5F, 2F);
        playSound(SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.3F, 1.8F);
    }

    private static void playSound(RegistryEntry.Reference<SoundEvent> soundEvent, float volume, float pitch) {
        playSound(soundEvent.value(), volume, pitch);
    }

    private static void playSound(SoundEvent soundEvent, float volume, float pitch) {
        MinecraftClient client = MinecraftClient.getInstance();
        SoundManager soundManager = client.getSoundManager();
        soundManager.play(PositionedSoundInstance.master(soundEvent, pitch, volume));
    }
}
