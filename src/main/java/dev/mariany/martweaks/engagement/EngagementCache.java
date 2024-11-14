package dev.mariany.martweaks.engagement;

import dev.mariany.martweaks.attachment.ModAttachmentTypes;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public enum EngagementCache {
    BUILDING, MINING;

    public static final int MAX_CACHE = 4;

    private static AttachmentType<List<RegistryEntry<Item>>> getAttachmentType(EngagementCache cacheType) {
        return switch (cacheType) {
            case BUILDING -> ModAttachmentTypes.BUILDING_CACHE;
            case MINING -> ModAttachmentTypes.MINING_CACHE;
        };
    }

    public static void addToCache(ServerPlayerEntity player, EngagementCache cacheType, Item item) {
        List<Item> cache = new LinkedList<>(getCache(player, cacheType));

        cache.addFirst(item);
        cache = new ArrayList<>(new HashSet<>(cache).stream().toList()); // remove duplicates

        if (cache.size() > MAX_CACHE) {
            cache.removeLast();
        }

        player.setAttached(getAttachmentType(cacheType), unparseCache(cache));
    }

    public static List<Item> getCache(ServerPlayerEntity player, EngagementCache cache) {
        return parseCache(player.getAttachedOrCreate(getAttachmentType(cache)));
    }

    private static List<Item> parseCache(List<RegistryEntry<Item>> cache) {
        return cache.stream().map(RegistryEntry::value).toList();
    }

    private static List<RegistryEntry<Item>> unparseCache(List<Item> cache) {
        return cache.stream().map(Item::getRegistryEntry).map(reference -> (RegistryEntry<Item>) reference).toList();
    }
}
