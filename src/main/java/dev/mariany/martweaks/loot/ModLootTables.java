package dev.mariany.martweaks.loot;

import dev.mariany.martweaks.MarTweaks;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class ModLootTables {
    public static final RegistryKey<LootTable> ELDER_GUARDIAN_REWARD = of("gameplay/elder_guardian_reward");

    private static RegistryKey<LootTable> of(String id) {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, MarTweaks.id(id));
    }
}
