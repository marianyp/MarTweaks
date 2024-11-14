package dev.mariany.martweaks.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import dev.mariany.martweaks.advancement.ModAdvancements;
import dev.mariany.martweaks.mixin.accessor.AdvancementManagerAccesor;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ServerAdvancementLoader.class)
public class ServerAdvancementLoaderMixin {
    @Shadow
    @Final
    private RegistryWrapper.WrapperLookup registryLookup;
    @Shadow
    private AdvancementManager manager;
    @Shadow
    private Map<Identifier, AdvancementEntry> advancements;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At(value = "TAIL"))
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler,
                         CallbackInfo ci) {
        AdvancementEntry biomesEntry = new AdvancementEntry(ModAdvancements.DISCOVERED_ALL_BIOMES,
                ModAdvancements.discoveredAllBiomes(registryLookup));

        AdvancementEntry structuresEntry = new AdvancementEntry(ModAdvancements.DISCOVERED_ALL_STRUCTURES,
                ModAdvancements.discoveredAllStructures(registryLookup));

        List<AdvancementEntry> entries = List.of(biomesEntry, structuresEntry);

        Map<Identifier, AdvancementEntry> advancementsCopy = new HashMap<>(advancements);

        for (AdvancementEntry entry : entries) {
            advancementsCopy.put(entry.id(), entry);
            ((AdvancementManagerAccesor) manager).martweaks$tryAdd(entry);
        }

        this.advancements = ImmutableMap.copyOf(advancementsCopy);
    }
}
