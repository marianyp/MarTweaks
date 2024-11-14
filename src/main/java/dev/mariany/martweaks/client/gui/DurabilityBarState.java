package dev.mariany.martweaks.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class DurabilityBarState {
    private static final DurabilityBarState INSTANCE = new DurabilityBarState();

    private static final int WARN_DURABILITY = 16;
    private static final int BLACK = Colors.BLACK;
    private static final int RED = Colors.RED;
    private static final int YELLOW = Colors.YELLOW;
    private static final int BAR_WIDTH = 13;

    private final FilledState warningState = new FilledState(300);
    private final FilledState dangerState = new FilledState(175);

    public static DurabilityBarState getInstance() {
        return INSTANCE;
    }

    public void update() {
        warningState.update();
        dangerState.update();
    }

    private boolean shouldWarn(ItemStack stack) {
        int maxDamage = stack.getMaxDamage();
        int damage = stack.getDamage();
        int warnThreshold = MathHelper.floor((float) maxDamage / 2);
        if (warnThreshold <= 1) {
            return false;
        }
        if (warnThreshold > WARN_DURABILITY) {
            warnThreshold = WARN_DURABILITY;
        }
        return maxDamage - damage <= warnThreshold;
    }

    private boolean shouldWarnDanger(ItemStack stack) {
        int maxDamage = stack.getMaxDamage();
        int damage = stack.getDamage();
        return damage >= maxDamage - 1;
    }

    @Nullable
    public Integer getBarColor(ItemStack stack) {
        if (shouldWarnDanger(stack)) {
            return this.dangerState.isFilled() ? RED : BLACK;
        }
        if (shouldWarn(stack)) {
            return this.warningState.isFilled() ? YELLOW : BLACK;
        }
        return null;
    }

    public int getBarWidth(ItemStack stack) {
        if (shouldWarnDanger(stack)) {
            return BAR_WIDTH;
        }
        return Math.max(1, stack.getItemBarStep());
    }

    public void draw(DrawContext context, ItemStack stack, int x, int y) {
        if (!stack.isEmpty() && stack.isItemBarVisible() && stack.isDamaged()) {
            Integer color = this.getBarColor(stack);
            if (color != null) {
                int startX = x + 2;
                int barY = y + BAR_WIDTH;
                int fillWidth = getBarWidth(stack);

                context.fill(RenderLayer.getGuiOverlay(), startX, barY, startX + BAR_WIDTH, barY + 2, BLACK);
                context.fill(RenderLayer.getGuiOverlay(), startX, barY, startX + fillWidth, barY + 1, color | BLACK);
            }
        }
    }

    static class FilledState {
        final int intervalMs;
        long lastToggleTime;
        boolean filled;

        public FilledState(int intervalMs) {
            this.intervalMs = intervalMs;
            this.lastToggleTime = Util.getMeasuringTimeMs();
            this.filled = false;
        }

        public void update() {
            long currentTime = Util.getMeasuringTimeMs();
            long diff = currentTime - this.lastToggleTime;
            if (diff >= this.intervalMs) {
                this.lastToggleTime = currentTime;
                this.filled = !this.filled;
            }
        }

        public boolean isFilled() {
            return this.filled;
        }
    }
}
