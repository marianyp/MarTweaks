package dev.mariany.martweaks.event.item;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class ModifyItemComponentsHandler {
    public static void modify(DefaultItemComponentEvents.ModifyContext modifyContext) {
        modifyContext.modify(Items.RECOVERY_COMPASS, maxDamage(6));
    }

    private static Consumer<ComponentMap.Builder> maxDamage(int maxDamage) {
        return builder -> {
            builder.add(DataComponentTypes.MAX_DAMAGE, maxDamage);
            builder.add(DataComponentTypes.MAX_STACK_SIZE, 1);
            builder.add(DataComponentTypes.DAMAGE, 0);
        };
    }
}
