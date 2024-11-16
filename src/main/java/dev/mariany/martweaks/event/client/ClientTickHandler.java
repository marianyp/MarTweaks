package dev.mariany.martweaks.event.client;

import dev.mariany.martweaks.client.MarTweaksClient;
import net.minecraft.client.MinecraftClient;

public class ClientTickHandler {
    public static void onEnd(MinecraftClient client) {
        if (!client.isPaused()) {
            MarTweaksClient.DURABILITY_BAR_STATE.update();
        }

        MarTweaksClient.QUICK_MOVE_STATE.update();
    }
}
