package dev.mariany.martweaks.mixin;

import dev.mariany.martweaks.engagement.EngagementManager;
import dev.mariany.martweaks.util.GourdCache;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "onBreak", at = @At(value = "HEAD"))
    public void injectOnBroken(World world, BlockPos pos, BlockState state, PlayerEntity player,
                               CallbackInfoReturnable<BlockState> cir) {
        if (world instanceof ServerWorld serverWorld) {
            if (GourdCache.isGourd(serverWorld, pos, state)) {
                ItemStack tool = player.getMainHandStack();
                boolean correctTool = tool.getItem().isCorrectForDrops(tool, state);
                EngagementManager.onHarvest(serverWorld, pos, base -> correctTool ? Math.max(1,
                        EnchantmentHelper.getBlockExperience(serverWorld, tool, base + 4)) : base);
            }
        }
    }
}
