package dev.mariany.martweaks.config;

import blue.endless.jankson.Comment;
import dev.mariany.martweaks.MarTweaks;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;

@SuppressWarnings("unused")
@Config(name = MarTweaks.MOD_ID + "-client", wrapperName = "MarTweaksClientConfig")
@Modmenu(modId = MarTweaks.MOD_ID)
public final class MarTweaksClientConfigModel {
    @Comment("Rather the XP reward sound should play for things like discovering biomes, crafting new items, etc.")
    public boolean enableEngagementXpRewardSounds = true;

    @Nest
    public DurabilityWarning durabilityWarning = new DurabilityWarning();

    @Nest
    public QuickMove quickMove = new QuickMove();

    public static final class DurabilityWarning {
        @Comment("When enabled, will make the durability bar flash yellow when an item is close to breaking and will flash red when there's only 1 use left.")
        public boolean enabled = true;
        @Comment("The max durability to start blinking for.")
        public int warnThreshold = 24;
    }

    public static final class QuickMove {
        @Comment("Allows quick moving items from their inventory to storage blocks by shift + right clicking with an empty hand.")
        public boolean enabled = true;
        @Comment("Require an empty hand when attempting to quick move items.")
        public boolean requireEmptyHand = true;
    }
}
