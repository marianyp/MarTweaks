package dev.mariany.martweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.item.map.MapState;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Mixin(targets = "net.minecraft.client.render.MapRenderer$MapTexture")
public class MapRendererMixin {
    @WrapOperation(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/map/MapState;getDecorations()Ljava/lang/Iterable;"))
    Iterable<MapDecoration> draw(MapState instance, Operation<Iterable<MapDecoration>> original) {
        Iterable<MapDecoration> iterable = original.call(instance);
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player != null) {
            ClientWorld world = (ClientWorld) player.getWorld();
            List<MapDecoration> decorations = StreamSupport.stream(iterable.spliterator(), false).toList();
            List<MapDecoration> updated = new ArrayList<>();

            for (MapDecoration decoration : decorations) {
                if (decoration.type().matches(MapDecorationTypes.PLAYER_OFF_LIMITS) || decoration.type()
                        .matches(MapDecorationTypes.PLAYER_OFF_MAP)) {
                    updated.add(new MapDecoration(MapDecorationTypes.PLAYER, decoration.x(), decoration.z(),
                            getRotation(player, world), decoration.name()));
                } else {
                    updated.add(decoration);
                }
            }

            return updated;
        }

        return iterable;
    }

    @Unique
    private static byte getRotation(ClientPlayerEntity player, ClientWorld world) {
        float yaw = player.getYaw();
        byte rotation;

        yaw += (float) (yaw < 0 ? -8 : 8);
        rotation = (byte) ((int) (yaw * 16 / 360));

        if (world.getRegistryKey() == World.NETHER) {
            int timeFactor = (int) (world.getLevelProperties().getTimeOfDay() / 10L);
            rotation = (byte) (timeFactor * timeFactor * 34187121 + timeFactor * 121 >> 15 & 15);
        }
        return rotation;
    }
}
