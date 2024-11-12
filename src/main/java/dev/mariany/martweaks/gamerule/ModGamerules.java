package dev.mariany.martweaks.gamerule;

import dev.mariany.martweaks.MarTweaks;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class ModGamerules {
    public static final GameRules.Key<GameRules.IntRule> ELDER_SEARCH_RADIUS = GameRuleRegistry.register(
            "elderSearchRadius", GameRules.Category.MISC, GameRuleFactory.createIntRule(58, 0));

    public static void registerModGamerules() {
        MarTweaks.LOGGER.info("Registering Mod Gamerules for " + MarTweaks.MOD_ID);
    }
}
