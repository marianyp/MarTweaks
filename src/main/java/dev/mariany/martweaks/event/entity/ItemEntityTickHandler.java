package dev.mariany.martweaks.event.entity;

import dev.mariany.martweaks.attachment.ModAttachmentTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class ItemEntityTickHandler {
    public static void onServerWorldTick(ServerWorld world) {
        for (ItemEntity itemEntity : world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class),
                entity -> !entity.isRemoved())) {
            onItemEntityTick(itemEntity);
        }
    }

    private static void onItemEntityTick(ItemEntity itemEntity) {
        spawnRewardEffects(itemEntity);
    }

    private static void spawnRewardEffects(ItemEntity itemEntity) {
        if (itemEntity.getAttachedOrElse(ModAttachmentTypes.ELDER_REWARD, false)) {
            ServerWorld world = (ServerWorld) itemEntity.getWorld();
            Random random = world.random;

            if (itemEntity.age % 20 == 0) {
                world.spawnParticles(ParticleTypes.FIREWORK, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 8,
                        0, 0, 0, 0.035);
            }

            if (random.nextBoolean() && itemEntity.age % 60 == 0) {
                float volume = MathHelper.nextFloat(random, 0.5F, 1.7F);
                float pitch = MathHelper.nextFloat(random, 0.1F, 1.3F);

                world.playSound(null, itemEntity.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                        SoundCategory.PLAYERS, volume, pitch);
            }
        }
    }
}
