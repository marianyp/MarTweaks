package dev.mariany.martweaks.event.entity;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;

public class ItemFrameTickHandler {
    public static void onServerWorldTick(ServerWorld world) {
        if (world.getTime() % 10 == 0) {
            for (ItemFrameEntity itemFrame : world.getEntitiesByType(TypeFilter.instanceOf(ItemFrameEntity.class),
                    entity -> !entity.isRemoved())) {
                onItemFrameTick(itemFrame);
            }
        }
    }

    private static void onItemFrameTick(ItemFrameEntity itemFrame) {
        if (itemFrame.isInvisible() && itemFrame.getHeldItemStack().isEmpty()) {
            itemFrame.setInvisible(false);
        }
    }
}
