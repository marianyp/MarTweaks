package dev.mariany.martweaks.gamerule;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.util.ModConstants;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class ModGamerules {
    public static final GameRules.Key<GameRules.IntRule> ELDER_SEARCH_RADIUS = GameRuleRegistry.register(
            "elderSearchRadius", GameRules.Category.MISC, GameRuleFactory.createIntRule(58, 0));

    public static final GameRules.Key<GameRules.BooleanRule> STRICT_ENGAGEMENT = GameRuleRegistry.register(
            "strictEngagement", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.IntRule> ENGAGEMENT_RATE = GameRuleRegistry.register("engagementRate",
            GameRules.Category.MISC, GameRuleFactory.createIntRule(ModConstants.DEFAULT_ENGAGEMENT_RATE, 0));

    public static final GameRules.Key<GameRules.IntRule> AUTO_CLOSE_IN_TICKS = GameRuleRegistry.register(
            "autoCloseInTicks", GameRules.Category.MISC, GameRuleFactory.createIntRule(30, 1));
    public static final GameRules.Key<GameRules.BooleanRule> AUTO_CLOSE_DOORS = GameRuleRegistry.register(
            "autoCloseDoors", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> AUTO_CLOSE_TRAPDOORS = GameRuleRegistry.register(
            "autoCloseTrapdoors", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> AUTO_CLOSE_FENCE_GATES = GameRuleRegistry.register(
            "autoCloseFenceGates", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static void bootstrap() {
        MarTweaks.LOGGER.info("Registering gamerules for " + MarTweaks.MOD_ID);
    }
}
