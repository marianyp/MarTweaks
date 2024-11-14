package dev.mariany.martweaks.item;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.item.custom.BrokenArrowItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {
    public static final Item BROKEN_ARROW = registerItem("broken_arrow", new BrokenArrowItem(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, MarTweaks.id(name), item);
    }

    public static void registerModItems() {
        MarTweaks.LOGGER.info("Registering Mod Items for " + MarTweaks.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.addBefore(Items.ARROW, BROKEN_ARROW);
        });
    }
}
