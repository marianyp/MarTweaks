package dev.mariany.martweaks.entity;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.entity.decoration.ConnectedLeashKnotEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class ModEntities {
    public static final EntityType<ConnectedLeashKnotEntity> CONNECTED_LEASH_KNOT = register("connected_leash_knot",
            EntityType.Builder.<ConnectedLeashKnotEntity>create(ConnectedLeashKnotEntity::new, SpawnGroup.MISC)
                    .dimensions(0.375F, 0.5F).eyeHeight(0.0625F).maxTrackingRange(10)
                    .trackingTickInterval(Integer.MAX_VALUE));

    private static <T extends Entity> EntityType<T> register(RegistryKey<EntityType<?>> key,
                                                             EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }

    private static RegistryKey<EntityType<?>> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ENTITY_TYPE, MarTweaks.id(id));
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return register(keyOf(id), type);
    }

    public static void bootstrap() {
        MarTweaks.LOGGER.info("Registering entities for " + MarTweaks.MOD_ID);
    }
}
