package dev.mariany.martweaks.entity.decoration;

import dev.mariany.martweaks.entity.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConnectedLeashKnotEntity extends LeashKnotEntity implements Leashable {
    @Nullable
    private Leashable.LeashData leashData;

    public ConnectedLeashKnotEntity(EntityType<? extends ConnectedLeashKnotEntity> entityType, World world) {
        super(entityType, world);
    }

    public ConnectedLeashKnotEntity(World world, BlockPos pos) {
        super(ModEntities.CONNECTED_LEASH_KNOT, world);
        this.setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        this.writeLeashData(view, this.leashData);
    }

    @Override
    public void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.readLeashData(view);
    }

    @Nullable
    @Override
    public Leashable.LeashData getLeashData() {
        return this.leashData;
    }

    @Override
    public void setLeashData(@Nullable Leashable.LeashData leashData) {
        this.leashData = leashData;
    }

    @Override
    public void beforeLeashTick(Entity leashHolder) {
    }

    @Override
    public boolean applyElasticity(Entity leashHolder, Leashable.LeashData data) {
        return false;
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.getWorld().isClient && reason.shouldDestroy() && this.isLeashed()) {
            this.detachLeash();
        }

        super.remove(reason);
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
        ServerWorld world = (ServerWorld) this.getWorld();
        int holderId = 0;

        if (leashData != null && leashData.unresolvedLeashData != null) {
            Optional<UUID> entityUUID = leashData.unresolvedLeashData.left();
            Optional<BlockPos> anchoredPos = leashData.unresolvedLeashData.right();

            if (entityUUID.isPresent()) {
                Entity leashHolder = world.getEntity(entityUUID.get());
                if (leashHolder != null) {
                    holderId = leashHolder.getId();
                }
            } else if (anchoredPos.isPresent()) {
                holderId = ConnectedLeashKnotEntity.getOrCreate(world, anchoredPos.get()).getId();
            }
        }

        return new EntitySpawnS2CPacket(this, entityTrackerEntry, holderId);
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);

        int holderId = packet.getEntityData();

        if (holderId > 0) {
            this.setUnresolvedLeashHolderId(holderId);
        }
    }

    @Override
    public boolean canStayAttached() {
        return super.canStayAttached() && (this.mightBeLeashed() || !getLeashedTo(this).isEmpty());
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Leashable.tickLeash(serverWorld, this);
        }
    }

    public static List<? extends Entity> getLeashedEntities(ServerWorld world) {
        return world.getEntitiesByType(TypeFilter.instanceOf(Entity.class),
                entity -> !entity.isRemoved() && entity instanceof Leashable leashable && leashable.isLeashed());
    }

    public static List<? extends Entity> getLeashedTo(Entity holder) {
        if (holder.getWorld() instanceof ServerWorld serverWorld) {
            return getLeashedEntities(serverWorld).stream().filter(entity -> {
                if (entity instanceof Leashable leashable) {
                    Entity entitiesHolder = leashable.getLeashHolder();
                    if (entitiesHolder != null) {
                        return entitiesHolder.equals(holder);
                    }
                }

                return false;
            }).toList();
        }

        return List.of();
    }

    public static ConnectedLeashKnotEntity convertTo(@NotNull LeashKnotEntity original) {
        if (original instanceof ConnectedLeashKnotEntity connectedLeashKnot) {
            return connectedLeashKnot;
        }

        World world = original.getWorld();

        ConnectedLeashKnotEntity connectedLeashKnot = new ConnectedLeashKnotEntity(world,
                BlockPos.ofFloored(original.getPos()));

        connectedLeashKnot.copyPositionAndRotation(original);

        if (world instanceof ServerWorld serverWorld) {
            List<? extends Entity> leashedEntities = getLeashedEntities(serverWorld);

            for (Entity entity : leashedEntities) {
                Leashable leashedEntity = (Leashable) entity;
                Entity holder = leashedEntity.getLeashHolder();
                if (holder != null && holder.equals(original)) {
                    leashedEntity.attachLeash(connectedLeashKnot, true);
                }
            }
        }

        world.spawnEntity(connectedLeashKnot);
        original.discard();
        return connectedLeashKnot;
    }

    public static ConnectedLeashKnotEntity getOrCreate(World world, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        List<LeashKnotEntity> leashKnotEntities = world.getNonSpectatingEntities(LeashKnotEntity.class,
                new Box(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1));

        for (LeashKnotEntity leashKnot : leashKnotEntities) {
            if (leashKnot.getAttachedBlockPos().equals(pos)) {
                return convertTo(leashKnot);
            }
        }

        ConnectedLeashKnotEntity connectedLeashKnot = new ConnectedLeashKnotEntity(world, pos);
        world.spawnEntity(connectedLeashKnot);
        return connectedLeashKnot;
    }

    public static boolean place(Entity owner, BlockPos pos) {
        World world = owner.getWorld();
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isIn(BlockTags.FENCES)) {
            ConnectedLeashKnotEntity connectedLeashKnot = getOrCreate(world, pos);
            connectedLeashKnot.attachLeash(owner, true);
            return true;
        }
        return false;
    }
}
