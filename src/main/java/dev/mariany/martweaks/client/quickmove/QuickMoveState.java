package dev.mariany.martweaks.client.quickmove;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class QuickMoveState {
    private static final boolean DEFAULT_USE_KNOWN_ITEMS = true;
    private static final boolean DEFAULT_SHOULD_INCLUDE_HOTBAR = false;

    private static final int DEFAULT_RESET_IN_TICKS = 10;

    @Nullable
    private BlockPos pos = null;
    private boolean useKnownItems = DEFAULT_USE_KNOWN_ITEMS;
    private boolean shouldIncludeHotbar = DEFAULT_SHOULD_INCLUDE_HOTBAR;

    private int resetInTicks = DEFAULT_RESET_IN_TICKS;

    @Nullable
    public BlockPos getPos() {
        return this.pos;
    }

    public boolean useKnownItems() {
        return this.useKnownItems;
    }

    public boolean shouldIncludeHotbar() {
        return this.shouldIncludeHotbar;
    }

    public void update() {
        if (this.pos != null || this.useKnownItems != DEFAULT_USE_KNOWN_ITEMS || this.shouldIncludeHotbar != DEFAULT_SHOULD_INCLUDE_HOTBAR) {
            --this.resetInTicks;
        }

        if (this.resetInTicks < 0) {
            reset();
        }
    }

    public QuickMoveState next(BlockPos quickMovePos) {
        if (!quickMovePos.equals(this.pos)) {
            reset();
        }

        if (this.pos == null) {
            this.pos = quickMovePos;
        } else if (this.useKnownItems == DEFAULT_USE_KNOWN_ITEMS) {
            this.useKnownItems = !useKnownItems;
        } else if (this.shouldIncludeHotbar == DEFAULT_SHOULD_INCLUDE_HOTBAR) {
            this.shouldIncludeHotbar = !shouldIncludeHotbar;
        }

        resetCooldown();

        return this;
    }

    private void reset() {
        pos = null;
        useKnownItems = DEFAULT_USE_KNOWN_ITEMS;
        shouldIncludeHotbar = DEFAULT_SHOULD_INCLUDE_HOTBAR;
        resetInTicks = DEFAULT_RESET_IN_TICKS;
    }

    private void resetCooldown() {
        resetInTicks = DEFAULT_RESET_IN_TICKS;
    }
}
