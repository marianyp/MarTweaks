package dev.mariany.martweaks.item;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.item.custom.BrokenArrowItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;

public class ModItems {
    public static final Item BROKEN_ARROW = register("broken_arrow", BrokenArrowItem::new);

    private static RegistryKey<Item> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ITEM, MarTweaks.id(id));
    }

    private static Item register(String name, Function<Item.Settings, Item> factory) {
        return register(name, factory, new Item.Settings());
    }

    private static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
        RegistryKey<Item> key = keyOf(name);
        Item item = factory.apply(settings.registryKey(key));
        return Registry.register(Registries.ITEM, key, item);
    }


    public static void bootstrap() {
        MarTweaks.LOGGER.info("Registering items for " + MarTweaks.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.addBefore(Items.ARROW, BROKEN_ARROW);
        });
    }
}
