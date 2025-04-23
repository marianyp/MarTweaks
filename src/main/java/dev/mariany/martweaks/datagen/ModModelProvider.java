package dev.mariany.martweaks.datagen;

import dev.mariany.martweaks.block.ModBlocks;
import dev.mariany.martweaks.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class ModModelProvider extends FabricModelProvider {
    private static final BlockStateVariantMap<ModelVariantOperator> EAST_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS = BlockStateVariantMap.operations(
                    Properties.HORIZONTAL_FACING).register(Direction.EAST, BlockStateModelGenerator.NO_OP)
            .register(Direction.SOUTH, BlockStateModelGenerator.ROTATE_Y_90)
            .register(Direction.WEST, BlockStateModelGenerator.ROTATE_Y_180)
            .register(Direction.NORTH, BlockStateModelGenerator.ROTATE_Y_270);

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

        WeightedVariant topVariant = BlockStateModelGenerator.createWeightedVariant(topIdentifier);
        WeightedVariant bottomVariant = BlockStateModelGenerator.createWeightedVariant(bottomIdentifier);

        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(ModBlocks.SUNFLOWER)
                .with(BlockStateVariantMap.models(Properties.DOUBLE_BLOCK_HALF)
                        .register(DoubleBlockHalf.UPPER, topVariant).register(DoubleBlockHalf.LOWER, bottomVariant))
                .coordinate(EAST_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }
}
