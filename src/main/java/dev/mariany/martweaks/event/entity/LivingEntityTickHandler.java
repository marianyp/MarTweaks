package dev.mariany.martweaks.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

public class LivingEntityTickHandler {
    private static final float MAX_LEASH_DISTANCE = 8.5F;

    public static void onServerWorldTick(ServerWorld world) {
        for (LivingEntity entity : world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class),
                entity -> !entity.isRemoved())) {
            onLivingEntityTick(entity);
        }
    }

    private static void onLivingEntityTick(LivingEntity livingEntity) {
        if (livingEntity instanceof MobEntity mob) {
            onMobTick(mob);
        }
    }

    private static void onMobTick(MobEntity mob) {
        ServerWorld serverWorld = (ServerWorld) mob.getWorld();
        Entity holder = mob.getLeashHolder();

        if (holder != null && holder.getWorld() == mob.getWorld()) {
            if (mob.distanceTo(holder) >= MAX_LEASH_DISTANCE) {
                mob.teleportTo(
                        new TeleportTarget(serverWorld, holder.getPos(), Vec3d.ZERO, mob.getYaw(), mob.getPitch(),
                                TeleportTarget.NO_OP));
                mob.fallDistance = 0;
            }
        }
    }
}
