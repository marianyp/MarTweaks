package dev.mariany.martweaks.mixin;

import net.minecraft.item.map.MapState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MapState.class)
public class MapStateMixin {
//    @ModifyVariable(method = "addDecoration", at = @At(value = "STORE", ordinal = 0), index = 1, argsOnly = true)
//    private RegistryEntry<MapDecorationType> preventOffMapType(RegistryEntry<MapDecorationType> type) {
//        if (type.matches(MapDecorationTypes.PLAYER_OFF_MAP)) {
//            return MapDecorationTypes.PLAYER;
//        }
//        return type;
//    }
//
//    @ModifyVariable(method = "addDecoration", at = @At(value = "STORE", ordinal = 1), index = 1, argsOnly = true)
//    private RegistryEntry<MapDecorationType> preventOffLimitsType(RegistryEntry<MapDecorationType> type) {
//        if (type.matches(MapDecorationTypes.PLAYER_OFF_LIMITS)) {
//            return MapDecorationTypes.PLAYER;
//        }
//        return type;
//    }
//
//    @ModifyVariable(method = "addDecoration", at = @At(value = "STORE", ordinal = 0), index = 6, argsOnly = true)
//    private double adjustRotationForPlayerType(double rotation) {
//        return rotation;
//    }
}
