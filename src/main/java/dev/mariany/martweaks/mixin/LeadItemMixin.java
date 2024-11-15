package dev.mariany.martweaks.mixin;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.entity.decoration.ConnectedLeashKnotEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.LeadItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeadItem.class)
public class LeadItemMixin {
    @Inject(method = "useOnBlock", at = @At(value = "HEAD"), cancellable = true)
    public void injectUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (MarTweaks.CONFIG.leads.leadFences()) {
            PlayerEntity player = context.getPlayer();
            if (player != null) {
                World world = context.getWorld();
                BlockPos pos = context.getBlockPos();
                ItemStack stack = context.getStack();
                BlockState blockState = world.getBlockState(pos);
                if (blockState.isIn(BlockTags.FENCES)) {
                    if (ConnectedLeashKnotEntity.place(player, pos)) {
                        stack.decrement(1);
                        cir.setReturnValue(ActionResult.success(world.isClient));
                    }
                }
            }
        }
    }
}
