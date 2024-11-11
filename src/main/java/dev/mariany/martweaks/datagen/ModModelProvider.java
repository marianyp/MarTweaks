package dev.mariany.martweaks.datagen;

import dev.mariany.martweaks.block.ModBlocks;
import dev.mariany.martweaks.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        registerSunflower(blockStateModelGenerator);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.BROKEN_ARROW, Models.HANDHELD);
    }

    private void registerSunflower(BlockStateModelGenerator blockStateModelGenerator) {
        Identifier topIdentifier = ModelIds.getBlockSubModelId(Blocks.SUNFLOWER, "_top");
        Identifier bottomIdentifier = ModelIds.getBlockSubModelId(Blocks.SUNFLOWER, "_bottom");

        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.SUNFLOWER)
                .coordinate(BlockStateModelGenerator.createEastDefaultHorizontalRotationStates()).coordinate(
                        BlockStateVariantMap.create(Properties.DOUBLE_BLOCK_HALF).register(DoubleBlockHalf.LOWER,
                                        BlockStateVariant.create().put(VariantSettings.MODEL, bottomIdentifier))
                                .register(DoubleBlockHalf.UPPER,
                                        BlockStateVariant.create().put(VariantSettings.MODEL, topIdentifier))));
    }
}
