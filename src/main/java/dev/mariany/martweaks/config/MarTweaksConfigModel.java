package dev.mariany.martweaks.config;

import blue.endless.jankson.Comment;
import dev.mariany.martweaks.MarTweaks;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Nest;
import io.wispforest.owo.config.annotation.Sync;

import java.util.List;

@SuppressWarnings("unused")
@Config(name = MarTweaks.MOD_ID + "-common", wrapperName = "MarTweaksConfig")
@Sync(value = Option.SyncMode.OVERRIDE_CLIENT)
public final class MarTweaksConfigModel {
    private static final String CUSTOM_ENGAGEMENTS_COMMENT = "List of custom stat types that, when incremented, players are rewarded for.";
    private static final String LOOTR_LOOTED_STAT = "lootr:looted_stat";

    @Nest
    public ArrowRecovery arrowRecovery = new ArrowRecovery();

    @Nest
    @Sync(value = Option.SyncMode.NONE)
    public ConvenientMaps convenientMaps = new ConvenientMaps();

    @Nest
    public DirectionalSunflowers directionalSunflowers = new DirectionalSunflowers();

    @Nest
    public EngagementRewards engagementRewards = new EngagementRewards();

    @Nest
    public InFighting inFighting = new InFighting();

    @Nest
    public LavaSwimming lavaSwimming = new LavaSwimming();

    @Nest
    public Leads leads = new Leads();

    @Nest
    public OceanMonumentRewards oceanMonumentRewards = new OceanMonumentRewards();

    @Nest
    public RecoveryCompass recoveryCompass = new RecoveryCompass();

    @Nest
    public UnlimitedBedRange unlimitedBedRange = new UnlimitedBedRange();

    public static final class Client {
        @Comment("When enabled, prevents player icons from being hidden when they are too far from the covered map space.")
        public boolean convenientMaps = true;
    }

    public static final class ArrowRecovery {
        @Comment("When enabled, will allow the player to remove arrows from their body by shift clicking.")
        public boolean enabled = true;

        @Comment("Determines if experience orbs should spawn when repairing a broken arrow.")
        public boolean rewardRepairingBrokenArrow = true;
    }

    public static final class ConvenientMaps {
        @Comment("When enabled, will always show the direction the player is facing on maps and prevents player icons from being hidden when they are too far from the covered map space.")
        public boolean enabled = true;
    }

    public static final class DirectionalSunflowers {
        @Comment("Determines if sunflowers can be placed in any direction other than east.")
        public boolean enabled = true;
    }

    public static final class EngagementRewards {
        @Comment("Minimum experience points to give for an engagement reward.")
        public int minXPReward = 3;

        @Comment("Maximum experience points to give for an engagement reward.")
        public int maxXPReward = 6;

        @Comment("Multiplier used when determining XP reward for discovery engagements.")
        public float discoveryMultiplier = 4F;

        @Nest
        public Engagements engagements = new EngagementRewards.Engagements();

        public static final class Engagements {
            @Comment("Determines if players can occasionally receive XP for placing blocks.")
            public boolean rewardBuilding = true;

            @Comment("Determines if players can occasionally receive XP for mining blocks.")
            public boolean rewardMining = true;

            @Comment("Determines if experience orbs should spawn when a crop block is harvested.")
            public boolean rewardHarvestingCrops = true;

            @Comment("Determines if players should receive XP when they craft an item for the first time.")
            public boolean rewardCrafting = true;

            @Comment(CUSTOM_ENGAGEMENTS_COMMENT)
            public List<String> customEngagements = List.of();

            @Nest
            public Discovery discovery = new Discovery();

            public static final class Discovery {
                @Comment("Determines if players should receive XP when they enter a biome for the first time.")
                public boolean rewardDiscoveringBiomes = true;

                @Comment("Determines if players should receive XP when they enter a structure for the first time.")
                public boolean rewardDiscoveringStructures = true;

                @Comment("Determines if players should receive XP when they loot chests.")
                public boolean rewardDiscoveringLoot = true;

                @Comment(CUSTOM_ENGAGEMENTS_COMMENT + " (Default: [\"" + LOOTR_LOOTED_STAT + "\"])")
                public List<String> customDiscoveryTypes = List.of(LOOTR_LOOTED_STAT);
            }
        }
    }

    public static final class InFighting {
        @Comment("ENABLED = Vanilla, DISABLED = No in fighting, DIFFERING_MOB_TYPES = Mobs of the same type won't fight")
        public Behavior behavior = Behavior.DISABLED;

        public enum Behavior {
            ENABLED, DISABLED, DIFFERING_MOB_TYPES;
        }
    }

    public static final class LavaSwimming {
        @Comment("Allows players to swim in lava while they have fire resistance.")
        public boolean enabled = true;
    }

    public static final class Leads {
        @Comment("Determines if leaded mobs should teleport to the player to avoid a lead from breaking.")
        public boolean noLeadBreaking = true;

        @Comment("When enabled, allows for leads to be used on fences for decoration.")
        public boolean leadFences = true;
    }

    public static final class OceanMonumentRewards {
        @Comment("Determines if players should receive loot after killing all Elder Guardians in an Ocean Monument.")
        public boolean enabled = true;
    }

    public static final class RecoveryCompass {
        @Comment("When enabled, will allow using a Recovery Compass on a Lodestone to be teleported to your last death location.")
        public boolean enabled = true;
        @Comment("When enabled, will destroy any blocks that may cause a player to suffocate and/or place a support block to prevent falling.")
        public boolean protectLastDeathLocation = true;
    }

    public static final class UnlimitedBedRange {
        @Comment("When enabled, prevents the 'You may not rest now; the bed is too far away' message.")
        public boolean enabled = true;
    }
}
