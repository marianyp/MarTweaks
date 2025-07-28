package dev.mariany.martweaks.mixin;

import dev.mariany.martweaks.client.MarTweaksClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    @Inject(method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "TAIL"))
    private void injectDrawItemBarColorChange(TextRenderer textRenderer, ItemStack stack, int x, int y,
                                              String countOverride, CallbackInfo ci) {
        DrawContext context = (DrawContext) (Object) this;
        Matrix3x2fStack matrices = context.getMatrixStack();

        matrices.pushMatrix();
        MarTweaksClient.DURABILITY_BAR_STATE.draw(context, stack, x, y);
        matrices.popMatrix();
    }
}
