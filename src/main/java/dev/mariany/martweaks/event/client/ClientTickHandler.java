package dev.mariany.martweaks.event.client;

import dev.mariany.martweaks.client.gui.DurabilityBarState;
import net.minecraft.client.MinecraftClient;

public class ClientTickHandler {
    public static void onClientTick(MinecraftClient client) {
        if (client.isPaused()) {
            return;
        }
        DurabilityBarState.getInstance().update();
    }
}
