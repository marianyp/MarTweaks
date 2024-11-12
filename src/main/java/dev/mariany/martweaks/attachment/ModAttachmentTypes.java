package dev.mariany.martweaks.attachment;

import com.mojang.serialization.Codec;
import dev.mariany.martweaks.MarTweaks;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
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
}
