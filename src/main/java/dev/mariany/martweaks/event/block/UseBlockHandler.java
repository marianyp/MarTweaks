package dev.mariany.martweaks.event.block;

import dev.mariany.martweaks.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class UseBlockHandler {
    private static final Map<RegistryKey<World>, Block> PLATFORMS = Map.of(World.OVERWORLD, Blocks.DIRT, World.NETHER,
            Blocks.NETHERRACK, World.END, Blocks.END_STONE);
    private static final Map<Pair<Item, Block>, BiFunction<PlayerEntity, ItemStack, ActionResult>> HANDLERS = Map.of(
            new Pair<>(Items.RECOVERY_COMPASS, Blocks.LODESTONE), UseBlockHandler::recover);

    public static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult result) {
        ItemStack stack = player.getStackInHand(hand);
        Pair<Item, Block> key = getHandlerKey(player, stack, result);

        if (HANDLERS.containsKey(key)) {
            BiFunction<PlayerEntity, ItemStack, ActionResult> handler = HANDLERS.get(key);
            return handler.apply(player, stack);
        }

        return ActionResult.PASS;
    }

    private static Pair<Item, Block> getHandlerKey(PlayerEntity player, ItemStack stack, BlockHitResult result) {
        BlockPos pos = result.getBlockPos();
        BlockState blockState = player.getWorld().getBlockState(pos);

        return new Pair<>(stack.getItem(), blockState.getBlock());
    }

    private static ActionResult recover(PlayerEntity player, ItemStack stack) {
        if (player.getWorld() instanceof ServerWorld world) {
            MinecraftServer server = world.getServer();
            Optional<GlobalPos> optionalLastDeathPos = player.getLastDeathPos();

            if (optionalLastDeathPos.isPresent()) {
                GlobalPos lastDeathPos = optionalLastDeathPos.get();
                BlockPos lastDeathBlockPos = lastDeathPos.pos();

                RegistryKey<World> dimensionKey = lastDeathPos.dimension();
                ServerWorld lastDeathWorld = server.getWorld(dimensionKey);

                if (lastDeathWorld != null) {
                    int minY = lastDeathWorld.getDimension().minY();

                    if (lastDeathBlockPos.getY() <= minY) {
                        lastDeathBlockPos = new BlockPos(lastDeathBlockPos.getX(), minY + 1, lastDeathBlockPos.getZ());
                    }

                    player.teleportTo(
                            new TeleportTarget(lastDeathWorld, lastDeathBlockPos.toBottomCenterPos(), Vec3d.ZERO,
                                    player.getYaw(), player.getPitch(), TeleportTarget.NO_OP));

                    playTeleportSound(player);

                    EquipmentSlot slot = ItemStack.areEqual(stack,
                            player.getStackInHand(Hand.MAIN_HAND)) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;

                    stack.damage(1, player, slot);

                    createPlatform(lastDeathWorld, lastDeathBlockPos, player);

                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

    private static void createPlatform(ServerWorld world, BlockPos pos, PlayerEntity player) {
        Box playerDimensions = player.getDimensions(EntityPose.STANDING).getBoxAt(Vec3d.ZERO);

        BlockPos floorPos = pos.down();
        BlockState floorState = world.getBlockState(floorPos);

        for (VoxelShape voxelShape : world.getBlockCollisions(player,
                playerDimensions.offset(pos.toBottomCenterPos()))) {
            if (!voxelShape.isEmpty()) {
                BlockPos iterationPos = BlockPos.stream(voxelShape.getBoundingBox()).iterator().next();
                world.breakBlock(iterationPos, true);
            }
        }

        if (!Block.isFaceFullSquare(floorState.getCollisionShape(world, floorPos), Direction.UP)) {
            if (floorState.getHardness(world, floorPos) >= 0) {
                Block platformBlock = PLATFORMS.get(world.getRegistryKey());

                world.breakBlock(floorPos, true);
                world.setBlockState(floorPos, platformBlock.getDefaultState());
            }
        }
    }

    private static void playTeleportSound(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(
                    new PlaySoundFromEntityS2CPacket(RegistryEntry.of(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE),
                            SoundCategory.AMBIENT, serverPlayer, 0.5F, 2F, serverPlayer.getRandom().nextLong()));
        }
    }
}
