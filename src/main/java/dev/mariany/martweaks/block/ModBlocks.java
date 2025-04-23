package dev.mariany.martweaks.block;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.block.custom.SunflowerBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public class ModBlocks {
    public static final Block SUNFLOWER = register("sunflower", SunflowerBlock::new,
            AbstractBlock.Settings.copy(Blocks.SUNFLOWER)
                    .overrideTranslationKey(Util.createTranslationKey("block", Identifier.ofVanilla("sunflower"))));

    private static Block register(String name, Function<AbstractBlock.Settings, Block> factory,
                                  AbstractBlock.Settings settings) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, MarTweaks.id(name));
        Block block = factory.apply(settings.registryKey(key));
        return Registry.register(Registries.BLOCK, key, block);
    }

    public static void bootstrap() {
        MarTweaks.LOGGER.info("Registering blocks for " + MarTweaks.MOD_ID);
    }
}
