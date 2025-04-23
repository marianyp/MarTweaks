package dev.mariany.martweaks.entity.boss.guardian;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.attachment.ModAttachmentTypes;
import dev.mariany.martweaks.gamerule.ModGamerules;
import dev.mariany.martweaks.loot.ModLootTables;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ElderGuardianFight {
    public static void onElderDeath(ElderGuardianEntity elder) {
        if (MarTweaks.CONFIG.oceanMonumentRewards.enabled()) {
            if (elder.getWorld() instanceof ServerWorld world) {
                List<ElderGuardianEntity> neighbouringElders = getNeighbouringElders(elder);

                if (neighbouringElders.isEmpty()) {
                    LootTable lootTable = world.getServer().getReloadableRegistries()
                            .getLootTable(ModLootTables.ELDER_GUARDIAN_REWARD);
                    List<Entity> participants = getParticipants(elder);

                    for (Entity participant : participants) {
                        LootWorldContext.Builder builder = new LootWorldContext.Builder(world).add(
                                        LootContextParameters.ORIGIN, participant.getPos())
                                .add(LootContextParameters.THIS_ENTITY, participant);

                        LootWorldContext lootWorldContext = builder.build(LootContextTypes.GIFT);
                        lootTable.generateLoot(lootWorldContext, elder.getLootTableSeed(),
                                stack -> dropLoot(stack, participant));
                        createRewardEffect(participant);
                    }

                    world.playSound(null, elder.getBlockPos(), SoundEvents.GOAT_HORN_SOUNDS.get(2).value(),
                            SoundCategory.AMBIENT, 1F, 0.5F);
                }
            }
        }
    }

    public static void onElderDamaged(ElderGuardianEntity elder, DamageSource source) {
        if (MarTweaks.CONFIG.oceanMonumentRewards.enabled()) {
            Entity attacker = source.getAttacker();
            if (attacker instanceof PlayerEntity) {
                List<ElderGuardianEntity> neighbouringElders = getNeighbouringElders(elder);
                List<UUID> participants = addParticipant(elder, attacker);

                updateNeighbouringElders(neighbouringElders, participants);
            }
        }
    }

    private static List<ElderGuardianEntity> getNeighbouringElders(ElderGuardianEntity elder) {
        if (elder.getWorld() instanceof ServerWorld world) {
            BlockPos pos = elder.getBlockPos();
            int radius = world.getGameRules().getInt(ModGamerules.ELDER_SEARCH_RADIUS);
            Box area = Box.from(new BlockBox(pos).expand(radius));
            return world.getOtherEntities(elder, area).stream().filter(ElderGuardianEntity.class::isInstance)
                    .map(ElderGuardianEntity.class::cast).toList();
        }
        return List.of();
    }

    private static List<UUID> addParticipant(ElderGuardianEntity elder, Entity attacker) {
        UUID attackerUUID = attacker.getUuid();
        List<UUID> participants = elder.getAttachedOrCreate(ModAttachmentTypes.PARTICIPANTS);
        participants.add(attackerUUID);

        Set<UUID> participantsSet = new HashSet<>(participants);
        participants.clear();
        participants.addAll(participantsSet);

        elder.setAttached(ModAttachmentTypes.PARTICIPANTS, participants);
        return participants;
    }

    private static void updateNeighbouringElders(List<ElderGuardianEntity> neighbouringElders,
                                                 List<UUID> participants) {
        for (ElderGuardianEntity elder : neighbouringElders) {
            elder.setAttached(ModAttachmentTypes.PARTICIPANTS, participants);
        }
    }

    private static List<Entity> getParticipants(ElderGuardianEntity elder) {
        if (elder.getWorld() instanceof ServerWorld world) {
            return elder.getAttachedOrCreate(ModAttachmentTypes.PARTICIPANTS).stream().map(world::getEntity).toList();
        }

        return List.of();
    }

    private static void createRewardEffect(Entity participant) {
        if (participant.getWorld() instanceof ServerWorld world) {
            double x = participant.getX();
            double y = participant.getY();
            double z = participant.getZ();

            world.spawnParticles(ParticleTypes.FIREWORK, x, y, z, 30, 0, 0, 0, 0.2D);
            world.playSound(null, participant.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS,
                    1.4F, 2);
        }
    }

    private static void dropLoot(ItemStack stack, Entity participant) {
        if (participant.getWorld() instanceof ServerWorld serverWorld) {
            ItemEntity itemEntity = participant.dropStack(serverWorld, stack);
            if (itemEntity != null) {
                itemEntity.setOwner(participant.getUuid());
                itemEntity.setAttached(ModAttachmentTypes.ELDER_REWARD, true);
            }
        }
    }
}
