package dev.mariany.martweaks.engagement;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.attachment.ModAttachmentTypes;
import dev.mariany.martweaks.gamerule.ModGamerules;
import dev.mariany.martweaks.packet.clientbound.EngagedPayload;
import dev.mariany.martweaks.util.ModUtils;
import dev.mariany.martweaks.util.Pair;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
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
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class EngagementManager {
    public static final Map<StatType<?>, TriFunction<ServerPlayerEntity, Stat<?>, Integer, Boolean>> BEFORE_STAT_INCREMENT_HANDLERS = Map.of(
            Stats.CRAFTED, Crafting::handle);

    public static final Map<StatType<?>, TriFunction<ServerPlayerEntity, Stat<?>, Integer, Boolean>> AFTER_STAT_INCREMENT_HANDLERS = Map.of(
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
        if (stat.getValue() instanceof ItemConvertible itemConvertible && itemConvertible.asItem().equals(Items.AIR)) {
            return;
        }

        StatType<?> type = stat.getType();
        Map<StatType<?>, TriFunction<ServerPlayerEntity, Stat<?>, Integer, Boolean>> handlers = before ? BEFORE_STAT_INCREMENT_HANDLERS : AFTER_STAT_INCREMENT_HANDLERS;

        if (handlers.containsKey(type)) {
            int statCount = getStatCount(player, stat);
            boolean engagementSatisfied = handlers.get(type).apply(player, stat, statCount);

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

    static int getRemainingEngagement(ServerPlayerEntity player) {
        return player.getAttachedOrCreate(ModAttachmentTypes.REMAINING_ENGAGEMENT);
    }

    static void updateRemainingEngagement(ServerPlayerEntity player) {
        int remainingEngagement = getRemainingEngagement(player) - 1;

        if (remainingEngagement < 0) {
            int max = player.getServerWorld().getGameRules().get(ModGamerules.ENGAGEMENT_RATE)
                    .get() + player.experienceLevel;
            int min = Math.max(0, max <= 0 ? 0 : (max / 2) - 1);
            remainingEngagement = MathHelper.nextInt(player.getRandom(), min, max);
        }

        player.setAttached(ModAttachmentTypes.REMAINING_ENGAGEMENT, remainingEngagement);
    }

    static boolean canEngage(ServerPlayerEntity player, Item item, EngagementCache cacheType) {
        boolean strict = player.getServerWorld().getGameRules().get(ModGamerules.STRICT_ENGAGEMENT).get();

        if (getRemainingEngagement(player) > 0) {
            return false;
        }

        if (strict) {
            List<Item> cache = EngagementCache.getCache(player, cacheType);
            return !cache.contains(item);
        }

        return true;
    }

    static boolean engage(ServerPlayerEntity player, Optional<Pair<EngagementCache, Item>> optionalCache) {
        return engage(player, true, optionalCache);
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
            updateRemainingEngagement(player);
        }
        return criteria;
    }

    static Optional<Pair<EngagementCache, Item>> cache(EngagementCache cache, Item item) {
        return Optional.of(new Pair<>(cache, item));
    }

    static class Building {
        static boolean handle(ServerPlayerEntity player, Stat<?> stat, int statCount) {
            if (MarTweaks.CONFIG.engagementRewards.engagements.rewardBuilding()) {
                if (stat.getValue() instanceof BlockItem blockItem && !blockItem.getDefaultStack()
                        .contains(DataComponentTypes.FOOD)) {
                    if (canEngage(player, blockItem, EngagementCache.BUILDING)) {
                        return engage(player, cache(EngagementCache.BUILDING, blockItem));
                    }
                    updateRemainingEngagement(player);
                }
            }
            return false;
        }
    }

    static class Crafting {
        static boolean handle(ServerPlayerEntity player, Stat<?> stat, int statCount) {
            if (MarTweaks.CONFIG.engagementRewards.engagements.rewardCrafting()) {
                return engage(player, statCount <= 0);
            }
            return false;
        }
    }

    static class Custom {
        static boolean handle(ServerPlayerEntity player, Stat<?> stat, int statCount) {
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
        static boolean handle(ServerPlayerEntity player, Stat<?> stat, int statCount) {
            boolean rewardMining = MarTweaks.CONFIG.engagementRewards.engagements.rewardMining();
            if (rewardMining) {
                if (stat.getValue() instanceof Block block) {
                    boolean rewardHarvestingCrops = MarTweaks.CONFIG.engagementRewards.engagements.rewardHarvestingCrops();
                    if (rewardHarvestingCrops && ModUtils.isCropLike(block)) {
                        return false; // Not handling as XP orbs will spawn
                    }

                    Item item = block.asItem();
                    if (canEngage(player, item, EngagementCache.MINING)) {
                        return engage(player, cache(EngagementCache.MINING, item));
                    }

                    updateRemainingEngagement(player);
                }
            }

            return false;
        }
    }
}
