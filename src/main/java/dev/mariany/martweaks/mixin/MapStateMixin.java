package dev.mariany.martweaks.mixin;

import dev.mariany.martweaks.MarTweaks;
import net.minecraft.item.map.MapState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MapState.class)
public class MapStateMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private static boolean modifyUnlimitedTracking(boolean unlimitedTracking) {
        return unlimitedTracking || MarTweaks.CONFIG.convenientMaps.enabled();
    }
}
