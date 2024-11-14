package dev.mariany.martweaks.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Inject(method = "onStacksDropped", at = @At(value = "HEAD"))
    public void injectOnStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool,
                                      boolean dropExperience, CallbackInfo ci) {
        if (state.getBlock() instanceof CropBlock cropBlock) {
            if (cropBlock.isMature(state)) {
                boolean hoe = tool.isIn(ItemTags.HOES);
                int baseXp = UniformIntProvider.create(0, 4).get(world.getRandom());
                int xp = hoe ? EnchantmentHelper.getBlockExperience(world, tool, baseXp) : baseXp;

                if (xp > 0) {
                    if (world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
                        ExperienceOrbEntity.spawn(world, Vec3d.ofCenter(pos), xp);
                    }
                }
            }
        }
    }
}
