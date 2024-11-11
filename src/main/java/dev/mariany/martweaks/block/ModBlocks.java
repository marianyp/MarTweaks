package dev.mariany.martweaks.block;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.block.custom.SunflowerBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlocks {
    public static final Block SUNFLOWER = registerBlock("sunflower",
            new SunflowerBlock(AbstractBlock.Settings.copy(Blocks.SUNFLOWER)));

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, MarTweaks.id(name), block);
    }

    public static void registerModBlocks() {
        MarTweaks.LOGGER.info("Registering Mod Blocks for " + MarTweaks.MOD_ID);
    }
}
