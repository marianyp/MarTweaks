package dev.mariany.martweaks.advancement;

import dev.mariany.martweaks.MarTweaks;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModAdvancements {
    public static final Identifier DISCOVERED_ALL_BIOMES = MarTweaks.id("discovered_all_biomes");
    public static final Identifier DISCOVERED_ALL_STRUCTURES = MarTweaks.id("discovered_all_structures");

    public static final List<Identifier> DISCOVERY_ADVANCEMENTS = List.of(DISCOVERED_ALL_BIOMES,
            DISCOVERED_ALL_STRUCTURES);

    public static Advancement discoveredAllBiomes(RegistryWrapper.WrapperLookup lookup) {
        Map<String, AdvancementCriterion<?>> requirements = generateBiomesCriteria(lookup);
        return new Advancement(Optional.empty(), Optional.empty(), AdvancementRewards.NONE, requirements,
                allOf(requirements), false);
    }

    public static Advancement discoveredAllStructures(RegistryWrapper.WrapperLookup lookup) {
        Map<String, AdvancementCriterion<?>> requirements = generateStructuresCriteria(lookup);
        return new Advancement(Optional.empty(), Optional.empty(), AdvancementRewards.NONE, requirements,
                allOf(requirements), false);
    }

    private static AdvancementRequirements allOf(Map<String, AdvancementCriterion<?>> requirements) {
        return AdvancementRequirements.allOf(requirements.keySet());
    }

    private static Map<String, AdvancementCriterion<?>> generateBiomesCriteria(
            RegistryWrapper.WrapperLookup wrapperLookup) {
        Map<String, AdvancementCriterion<?>> requirements = new HashMap<>();

        wrapperLookup.getWrapperOrThrow(RegistryKeys.BIOME).streamEntries().forEach(biomeReference -> {
            String id = biomeReference.registryKey().getValue().toString().replace(':', '_');
            AdvancementCriterion<TickCriterion.Conditions> criterion = TickCriterion.Conditions.createLocation(
                    LocationPredicate.Builder.createBiome(biomeReference));
            requirements.put(id, criterion);
        });

        return requirements;
    }

    private static Map<String, AdvancementCriterion<?>> generateStructuresCriteria(
            RegistryWrapper.WrapperLookup wrapperLookup) {
        Map<String, AdvancementCriterion<?>> requirements = new HashMap<>();

        wrapperLookup.getWrapperOrThrow(RegistryKeys.STRUCTURE).streamEntries().forEach(biomeReference -> {
            String id = biomeReference.registryKey().getValue().toString().replace(':', '_');
            AdvancementCriterion<TickCriterion.Conditions> criterion = TickCriterion.Conditions.createLocation(
                    LocationPredicate.Builder.createStructure(biomeReference));
            requirements.put(id, criterion);
        });

        return requirements;
    }
}
