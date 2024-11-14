package dev.mariany.martweaks.attachment;

import com.mojang.serialization.Codec;
import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.util.ModConstants;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Uuids;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModAttachmentTypes {
    public static final AttachmentType<List<UUID>> PARTICIPANTS = AttachmentRegistry.<List<UUID>>builder()
            .persistent(Uuids.CODEC.listOf()).initializer(ArrayList::new)
            .buildAndRegister(MarTweaks.id("participants"));
    public static final AttachmentType<Boolean> ELDER_REWARD = AttachmentRegistry.<Boolean>builder()
            .persistent(Codec.BOOL).initializer(() -> false).buildAndRegister(MarTweaks.id("elder_reward"));

    public static final AttachmentType<List<RegistryEntry<Item>>> BUILDING_CACHE = createCache("building_cache");
    public static final AttachmentType<List<RegistryEntry<Item>>> MINING_CACHE = createCache("mining_cache");

    public static final AttachmentType<Integer> ENGAGEMENT_RATE = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT).initializer(() -> ModConstants.DEFAULT_ENGAGEMENT_RATE)
            .buildAndRegister(MarTweaks.id("engagement_rate"));

    private static AttachmentType<List<RegistryEntry<Item>>> createCache(String name) {
        return AttachmentRegistry.<List<RegistryEntry<Item>>>builder().persistent(ItemStack.ITEM_CODEC.listOf())
                .initializer(ArrayList::new).copyOnDeath().buildAndRegister(MarTweaks.id(name));
    }
}
