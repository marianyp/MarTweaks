package dev.mariany.martweaks.engagement;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.attachment.ModAttachmentTypes;
import dev.mariany.martweaks.gamerule.ModGamerules;
import dev.mariany.martweaks.util.ModUtils;
import dev.mariany.martweaks.util.Pair;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class EngagementManager {
    private static final Map<StatType<?>, EngagementHandler> BEFORE_STAT_INCREMENT_HANDLERS = Map.of(
            Stats.CRAFTED, Crafting::handle
    );

    private static final Map<StatType<?>, EngagementHandler> AFTER_STAT_INCREMENT_HANDLERS = Map.of(
            Stats.CUSTOM, Custom::handle,
            Stats.USED, Building::handle,
            Stats.MINED, Mining::handle
    );

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
                    spawnReward(world, pos, xp);
                }
            }
        }
    }

    private static void spawnReward(ServerPlayerEntity serverPlayer, int amount) {
        spawnReward(serverPlayer.getEntityWorld(), serverPlayer.getEntityPos(), amount, 20);
    }

    private static void spawnReward(ServerWorld world, BlockPos pos, int amount) {
        spawnReward(world, pos.toCenterPos(), amount, -1);
    }

    private static void spawnReward(ServerWorld world, Vec3d pos, int amount, int remainingTicks) {
        while (amount > 0) {
            int i = ExperienceOrbEntity.roundToOrbSize(amount);

            amount -= i;

            if (!ExperienceOrbEntity.wasMergedIntoExistingOrb(world, pos, i)) {
                ExperienceOrbEntity experienceOrbEntity = new ExperienceOrbEntity(world, pos, Vec3d.ZERO, i);

                if (remainingTicks > 0) {
                    experienceOrbEntity.age = 6000 - remainingTicks;
                }

                ((EngagementRewardMarkable) experienceOrbEntity).marTweaks$mark();

                world.spawnEntity(experienceOrbEntity);
            }
        }
    }

    public static void onStatIncrement(ServerPlayerEntity player, Stat<?> stat, boolean before) {
        if (stat.getValue() instanceof ItemConvertible itemConvertible && itemConvertible.asItem().equals(Items.AIR)) {
            return;
        }

        StatType<?> type = stat.getType();

        Map<StatType<?>, EngagementHandler> handlers = before ?
                BEFORE_STAT_INCREMENT_HANDLERS :
                AFTER_STAT_INCREMENT_HANDLERS;

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

        spawnReward(player, xpReward);
    }

    static int getStatCount(ServerPlayerEntity player, Stat<?> stat) {
        return player.getStatHandler().getStat(stat);
    }

    static int getRemainingEngagement(ServerPlayerEntity player) {
        return player.getAttachedOrCreate(ModAttachmentTypes.REMAINING_ENGAGEMENT);
    }

    static void updateRemainingEngagement(ServerPlayerEntity player) {
        if (player.isSpectator() || player.isCreative()) {
            return;
        }

        int remainingEngagement = getRemainingEngagement(player) - 1;

        if (remainingEngagement < 0) {
            ServerWorld world = player.getEntityWorld();
            GameRules gameRules = world.getGameRules();
            int max = gameRules.get(ModGamerules.ENGAGEMENT_RATE).get() + player.experienceLevel;
            int min = Math.max(0, max <= 0 ? 0 : (max / 2) - 1);
            remainingEngagement = MathHelper.nextInt(player.getRandom(), min, max);
        }

        player.setAttached(ModAttachmentTypes.REMAINING_ENGAGEMENT, remainingEngagement);
    }

    static boolean canEngage(ServerPlayerEntity player, Item item, EngagementCache cacheType) {
        boolean strict = player.getEntityWorld().getGameRules().get(ModGamerules.STRICT_ENGAGEMENT).get();

        if (player.isSpectator() || player.isCreative()) {
            return false;
        }

        if (getRemainingEngagement(player) > 0) {
            return false;
        }

        if (strict) {
            List<Item> cache = EngagementCache.getCache(player, cacheType);
            return !cache.contains(item);
        }

        return true;
    }

    static boolean engage(ServerPlayerEntity player, @Nullable Pair<EngagementCache, Item> cache) {
        return engage(player, true, cache);
    }

    static boolean engage(ServerPlayerEntity player, boolean criteria) {
        return engage(player, criteria, null);
    }

    static boolean engage(ServerPlayerEntity player, boolean criteria, @Nullable Pair<EngagementCache, Item> cache) {
        if (criteria) {
            if (cache != null) {
                EngagementCache.addToCache(player, cache.getLeft(), cache.getRight());
            }

            updateRemainingEngagement(player);
        }
        return criteria;
    }

    static Pair<EngagementCache, Item> cache(EngagementCache cache, Item item) {
        return new Pair<>(cache, item);
    }

    static class Building {
        static boolean handle(ServerPlayerEntity player, Stat<?> stat, int statCount) {
            if (!MarTweaks.CONFIG.engagementRewards.engagements.rewardBuilding()) {
                return false;
            }

            if (stat.getValue() instanceof BlockItem blockItem) {
                if (!blockItem.getDefaultStack().contains(DataComponentTypes.FOOD)) {
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
            if (MarTweaks.CONFIG.engagementRewards.engagements.rewardMining()) {
                if (stat.getValue() instanceof Block block) {
                    if (MarTweaks.CONFIG.engagementRewards.engagements.rewardHarvestingCrops()) {
                        if (ModUtils.isCropLike(block)) {
                            return false;
                        }
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

    interface EngagementHandler {
        boolean apply(ServerPlayerEntity player, Stat<?> stat, int statCount);
    }
}
