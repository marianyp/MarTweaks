package dev.mariany.martweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.martweaks.MarTweaks;
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

        if (!MarTweaks.CONFIG.convenientMaps.enabled()) {
            return iterable;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player != null) {
            ClientWorld world = (ClientWorld) player.getWorld();
            List<MapDecoration> decorations = StreamSupport.stream(iterable.spliterator(), false).toList();
            List<MapDecoration> updated = new ArrayList<>();

            int scale = 1 << instance.scale;
            float playerRelativeX = (float) (player.getX() - (double) instance.centerX) / (float) scale;
            float playerRelativeZ = (float) (player.getZ() - (double) instance.centerZ) / (float) scale;

            // Threshold distance to consider an icon "close enough" to the player
            double closestDistance = Double.MAX_VALUE;
            MapDecoration closestDecoration = null;

            // Find the closest player decoration
            for (MapDecoration decoration : decorations) {
                if (decoration.type().matches(MapDecorationTypes.PLAYER) || decoration.type()
                        .matches(MapDecorationTypes.PLAYER_OFF_LIMITS) || decoration.type()
                        .matches(MapDecorationTypes.PLAYER_OFF_MAP)) {

                    // Calculate Euclidean distance
                    double dx = playerRelativeX - decoration.x();
                    double dz = playerRelativeZ - decoration.z();
                    double distance = Math.sqrt(dx * dx + dz * dz);

                    // Update if this decoration is closer to the player's position
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestDecoration = decoration;
                    }
                }
            }

            for (MapDecoration decoration : decorations) {
                if (decoration.equals(closestDecoration) && !decoration.type().matches(MapDecorationTypes.PLAYER)) {
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
