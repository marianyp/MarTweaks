package dev.mariany.martweaks.client;

import dev.mariany.martweaks.block.ModBlocks;
import dev.mariany.martweaks.entity.ModEntities;
import dev.mariany.martweaks.event.client.ClientTickHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LeashKnotEntityRenderer;

public class MarTweaksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerBlockRenderLayers();
        EntityRendererRegistry.INSTANCE.register(ModEntities.CONNECTED_LEASH_KNOT, LeashKnotEntityRenderer::new);
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickHandler::onClientTick);
    }

    private void registerBlockRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SUNFLOWER, RenderLayer.getCutout());
    }
}
