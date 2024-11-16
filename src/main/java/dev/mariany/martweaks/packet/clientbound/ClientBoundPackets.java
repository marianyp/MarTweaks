package dev.mariany.martweaks.packet.clientbound;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.client.MarTweaksClient;
import dev.mariany.martweaks.client.quickmove.QuickMoveState;
import dev.mariany.martweaks.packet.serverbound.QuickMoveTicketPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class ClientBoundPackets {
    public static void init() {
        // Engagement XP Reward
        ClientPlayNetworking.registerGlobalReceiver(EngagedPayload.ID, (payload, context) -> {
            if (MarTweaksClient.CONFIG.enableEngagementXpRewardSounds()) {
                playGainExperienceSound();
            }
        });

        // Quick Move Requested
        ClientPlayNetworking.registerGlobalReceiver(RequestQuickMovePayload.ID, (payload, context) -> {
            if (MarTweaksClient.CONFIG.enableQuickMove()) {
                QuickMoveState quickMoveState = MarTweaksClient.QUICK_MOVE_STATE.next(payload.pos());

                BlockPos pos = quickMoveState.getPos();
                boolean useKnownItems = quickMoveState.useKnownItems();
                boolean shouldIncludeHotbar = quickMoveState.shouldIncludeHotbar();

                MarTweaks.LOGGER.info("pos: {} | useKnownItems: {} | shouldIncludeHotbar: {}", pos, useKnownItems,
                        shouldIncludeHotbar);

                if (pos != null) {
                    QuickMoveTicketPayload ticket = new QuickMoveTicketPayload(pos, useKnownItems, shouldIncludeHotbar);
                    context.responseSender().sendPacket(ticket);
                }
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
