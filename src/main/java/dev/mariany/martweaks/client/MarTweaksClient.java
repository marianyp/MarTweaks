package dev.mariany.martweaks.client;

import dev.mariany.martweaks.block.ModBlocks;
import dev.mariany.martweaks.config.MarTweaksClientConfig;
import dev.mariany.martweaks.entity.ModEntities;
import dev.mariany.martweaks.event.client.ClientTickHandler;
import dev.mariany.martweaks.packet.clientbound.ClientBoundPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LeashKnotEntityRenderer;

@Environment(EnvType.CLIENT)
public class MarTweaksClient implements ClientModInitializer {
    public static final MarTweaksClientConfig CONFIG = MarTweaksClientConfig.createAndLoad();

    @Override
    public void onInitializeClient() {
        ClientBoundPackets.init();
        registerBlockRenderLayers();
        EntityRendererRegistry.INSTANCE.register(ModEntities.CONNECTED_LEASH_KNOT, LeashKnotEntityRenderer::new);
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickHandler::onClientTick);
    }

    private void registerBlockRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SUNFLOWER, RenderLayer.getCutout());
    }
}
