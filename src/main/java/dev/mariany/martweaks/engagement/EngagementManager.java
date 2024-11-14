package dev.mariany.martweaks.engagement;

import dev.mariany.martweaks.attachment.ModAttachmentTypes;
import dev.mariany.martweaks.gamerule.ModGamerules;
import dev.mariany.martweaks.util.ModUtils;
import dev.mariany.martweaks.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EngagementManager {
    @FunctionalInterface
    public interface QuadFunction<P1, P2, P3, P4, R> {
        R apply(P1 one, P2 two, P3 three, P4 four);
    }

    public static final Map<StatType<?>, QuadFunction<ServerPlayerEntity, Stat<?>, Integer, Integer, Boolean>> STAT_HANDLERS = Map.of(
            Stats.USED, Building::handle, Stats.CRAFTED, Crafting::handle, Stats.MINED, Mining::handle);

    private static final int MIN_XP_REWARD = 2;
    private static final int MAX_XP_REWARD = 8;
    private static final float DISCOVERY_MULTIPLIER = 2.5F;

    public static void onDiscover(ServerPlayerEntity player) {
        rewardPlayer(player, DISCOVERY_MULTIPLIER);
        engage(player, true);
    }

    public static void onStatIncrement(ServerPlayerEntity player, Stat<?> stat) {
        StatType<?> type = stat.getType();
        if (STAT_HANDLERS.containsKey(type)) {
            int engagementRate = getEngagementRate(player);
            int statCount = getStatCount(player, stat);
            boolean engagementSatisfied = STAT_HANDLERS.get(type).apply(player, stat, statCount, engagementRate);

            if (engagementSatisfied) {
                rewardPlayer(player);
            }
        }
    }

    static void rewardPlayer(ServerPlayerEntity player) {
        rewardPlayer(player, 1);
    }

    static void rewardPlayer(ServerPlayerEntity player, float multiplier) {
        int xpReward = MathHelper.floor(
                multiplier * MathHelper.nextInt(player.getRandom(), MIN_XP_REWARD, MAX_XP_REWARD));
        player.addExperience(xpReward);
        playGainExperienceSound(player);
    }

    static void playGainExperienceSound(ServerPlayerEntity player) {
        ModUtils.sendSoundToClient(player, SoundEvents.BLOCK_AMETHYST_BLOCK_FALL, SoundCategory.PLAYERS, 0.5F, 2F);
        ModUtils.sendSoundToClient(player, SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE, SoundCategory.PLAYERS, 0.3F, 1.8F);
    }

    static int getStatCount(ServerPlayerEntity player, Stat<?> stat) {
        return player.getStatHandler().getStat(stat);
    }

    static int getEngagementRate(ServerPlayerEntity player) {
        return player.getAttachedOrCreate(ModAttachmentTypes.ENGAGEMENT_RATE);
    }

    static void updateEngagementRate(ServerPlayerEntity player) {
        int maxEngagementRate = player.getWorld().getGameRules().get(ModGamerules.ENGAGEMENT_RATE).get();
        int minEngagementRate = maxEngagementRate <= 0 ? 0 : maxEngagementRate / 2;
        int currentEngagementRate = getEngagementRate(player);
        int newEngagementRate = MathHelper.nextInt(player.getRandom(), minEngagementRate, maxEngagementRate);

        if (currentEngagementRate <= minEngagementRate + 1) {
            newEngagementRate = maxEngagementRate;
        }

        player.setAttached(ModAttachmentTypes.ENGAGEMENT_RATE, newEngagementRate);
    }

    static boolean canEngage(ServerPlayerEntity player, Item item) {
        boolean strict = player.getWorld().getGameRules().get(ModGamerules.STRICT_ENGAGEMENT).get();
        if (strict) {
            List<Item> cache = EngagementCache.getMiningCache(player);
            return !cache.contains(item);
        }
        return true;
    }

    static boolean engage(ServerPlayerEntity player, boolean criteria) {
        return engage(player, criteria, Optional.empty());
    }

    static boolean engage(ServerPlayerEntity player, boolean criteria,
                          Optional<Pair<EngagementCache, Item>> optionalCache) {
        if (criteria) {
            if (optionalCache.isPresent()) {
                Pair<EngagementCache, Item> cache = optionalCache.get();
                EngagementCache.addToCache(player, cache.getLeft(), cache.getRight());
            }
            updateEngagementRate(player);
        }
        return criteria;
    }

    static Optional<Pair<EngagementCache, Item>> cache(EngagementCache cache, Item item) {
        return Optional.of(new Pair<>(cache, item));
    }

    static boolean every(int nth, int value) {
        return value == 0 || nth == 0 || value % nth == 0;
    }

    static class Building {
        static boolean handle(ServerPlayerEntity player, Stat<?> stat, int statCount, int engagementRate) {
            if (stat.getValue() instanceof BlockItem blockItem) {
                if (canEngage(player, blockItem)) {
                    return engage(player, every(engagementRate, statCount), cache(EngagementCache.BUILDING, blockItem));
                }
            }
            return false;
        }
    }

    static class Crafting {
        static boolean handle(ServerPlayerEntity player, Stat<?> stat, int statCount, int engagementRate) {
            return engage(player, statCount == 1);
        }
    }

    static class Mining {
        static boolean handle(ServerPlayerEntity player, Stat<?> stat, int statCount, int engagementRate) {
            if (stat.getValue() instanceof Block block) {
                Item item = block.asItem();
                if (canEngage(player, item)) {
                    return engage(player, every(engagementRate, statCount), cache(EngagementCache.MINING, item));
                }
            }

            return false;
        }
    }
}
