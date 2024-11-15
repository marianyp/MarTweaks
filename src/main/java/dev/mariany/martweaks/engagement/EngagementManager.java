package dev.mariany.martweaks.engagement;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.attachment.ModAttachmentTypes;
import dev.mariany.martweaks.gamerule.ModGamerules;
import dev.mariany.martweaks.packet.clientbound.EngagedPayload;
import dev.mariany.martweaks.util.Pair;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.GameRules;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class EngagementManager {
    @FunctionalInterface
    public interface QuadFunction<P1, P2, P3, P4, R> {
        R apply(P1 one, P2 two, P3 three, P4 four);
    }

    public static final Map<StatType<?>, QuadFunction<ServerPlayerEntity, Stat<?>, Integer, Integer, Boolean>> BEFORE_STAT_INCREMENT_HANDLERS = Map.of(
            Stats.CRAFTED, Crafting::handle);

    public static final Map<StatType<?>, QuadFunction<ServerPlayerEntity, Stat<?>, Integer, Integer, Boolean>> AFTER_STAT_INCREMENT_HANDLERS = Map.of(
            Stats.CUSTOM, Custom::handle, Stats.USED, Building::handle, Stats.MINED, Mining::handle);

    public static void onDiscover(ServerPlayerEntity player) {
        rewardPlayer(player, MarTweaks.CONFIG.engagementRewards.discoveryMultiplier());
        engage(player, true);
    }

    public static void onHarvest(ServerWorld world, BlockPos pos, Function<Integer, Integer> multiply) {
        if (MarTweaks.CONFIG.engagementRewards.engagements.rewardHarvestingCrops()) {
            int baseXp = UniformIntProvider.create(0, 4).get(world.getRandom());
            int xp = multiply.apply(baseXp);

            if (xp > 0) {
                if (world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
                    ExperienceOrbEntity.spawn(world, Vec3d.ofCenter(pos), xp);
                }
            }
        }
    }

    public static void onStatIncrement(ServerPlayerEntity player, Stat<?> stat, boolean before) {
        StatType<?> type = stat.getType();
        Map<StatType<?>, QuadFunction<ServerPlayerEntity, Stat<?>, Integer, Integer, Boolean>> handlers = before ? BEFORE_STAT_INCREMENT_HANDLERS : AFTER_STAT_INCREMENT_HANDLERS;

        if (handlers.containsKey(type)) {
            int engagementRate = getEngagementRate(player);
            int statCount = getStatCount(player, stat);
            boolean engagementSatisfied = handlers.get(type).apply(player, stat, statCount, engagementRate);

            if (engagementSatisfied) {
                rewardPlayer(player);
            }
        }
    }

    static void rewardPlayer(ServerPlayerEntity player) {
        rewardPlayer(player, 1);
    }

    static void rewardPlayer(ServerPlayerEntity player, float multiplier) {
        int min = MarTweaks.CONFIG.engagementRewards.minXPReward();
        int max = MarTweaks.CONFIG.engagementRewards.maxXPReward();
        int xpReward = MathHelper.floor(multiplier * MathHelper.nextInt(player.getRandom(), min, max));
        player.addExperience(xpReward);
        ServerPlayNetworking.send(player, new EngagedPayload());
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

    static boolean canEngage(ServerPlayerEntity player, Item item, EngagementCache cacheType) {
        boolean strict = player.getWorld().getGameRules().get(ModGamerules.STRICT_ENGAGEMENT).get();
        if (strict) {
            List<Item> cache = EngagementCache.getCache(player, cacheType);
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
            if (MarTweaks.CONFIG.engagementRewards.engagements.rewardBuilding()) {
                if (stat.getValue() instanceof BlockItem blockItem) {
                    if (canEngage(player, blockItem, EngagementCache.BUILDING)) {
                        return engage(player, every(engagementRate, statCount),
                                cache(EngagementCache.BUILDING, blockItem));
                    }
                }
            }
            return false;
        }
    }

    static class Crafting {
        static boolean handle(ServerPlayerEntity player, Stat<?> stat, int statCount, int engagementRate) {
            if (MarTweaks.CONFIG.engagementRewards.engagements.rewardCrafting()) {
                return engage(player, statCount <= 0);
            }
            return false;
        }
    }

    static class Custom {
        static boolean handle(ServerPlayerEntity player, Stat<?> stat, int statCount, int engagementRate) {
            List<Identifier> stats = parseStatIds(MarTweaks.CONFIG.engagementRewards.engagements.customEngagements());

            List<Identifier> discoveryStats = parseStatIds(
                    MarTweaks.CONFIG.engagementRewards.engagements.discovery.customDiscoveryTypes());

            if (stat.getValue() instanceof Identifier id) {
                if (discoveryStats.contains(id)) {
                    onDiscover(player);
                }
                return stats.contains(id);
            }

            return false;
        }

        static List<Identifier> parseStatIds(List<String> stats) {
            return stats.stream().map(Identifier::tryParse).filter(Objects::nonNull).toList();
        }
    }

    static class Mining {
        static boolean handle(ServerPlayerEntity player, Stat<?> stat, int statCount, int engagementRate) {
            if (MarTweaks.CONFIG.engagementRewards.engagements.rewardMining()) {
                if (stat.getValue() instanceof Block block) {
                    Item item = block.asItem();
                    if (canEngage(player, item, EngagementCache.MINING)) {
                        return engage(player, every(engagementRate, statCount), cache(EngagementCache.MINING, item));
                    }
                }
            }

            return false;
        }
    }
}
