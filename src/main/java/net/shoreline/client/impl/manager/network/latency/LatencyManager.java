package net.shoreline.client.impl.manager.network.latency;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.util.Globals;
import net.shoreline.client.util.world.FakePlayerEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linus
 * @since 1.0
 */
public class LatencyManager implements Globals {
    //
    private final Map<PlayerEntity, PlayerLatencyTracker> trackers = new HashMap<>();

    /**
     *
     */
    public LatencyManager() {
        // Shoreline.EVENT_HANDLER.subscribe(this);
    }

//    @EventListener
//    public void onPacketInbound(PacketEvent.Inbound event) {
//        if (mc.player != null && mc.world != null) {
//            if (event.getPacket() instanceof EntityPositionS2CPacket packet) {
//                final Entity entity = mc.world.getEntityById(packet.getId());
//                if (entity instanceof PlayerEntity player) {
//                    final PlayerLatencyTracker latency = trackers.get(player);
//                    if (latency == null) {
//                        return;
//                    }
//                    final Vec3d pos = new Vec3d(packet.getX(), packet.getY(),
//                            packet.getZ());
//                    latency.onPositionUpdate(pos);
//                }
//            } else if (event.getPacket() instanceof EntitySpawnS2CPacket packet) {
//                final Entity entity = mc.world.getEntityById(packet.getId());
//                if (packet.getEntityType() == EntityType.PLAYER) {
//                    final PlayerEntity player = (PlayerEntity) entity;
//                    final Vec3d spawn = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
//                    if (player == null) {
//                        return;
//                    }
//                    trackers.put(player, new PlayerLatencyTracker(player, spawn));
//                }
//            } else if (event.getPacket() instanceof EntitiesDestroyS2CPacket packet) {
//                trackers.entrySet().removeIf(t ->
//                        packet.getEntityIds().contains(t.getKey().getId()));
//            }
//        }
//    }

    /**
     * @param floor
     * @param player
     * @param time
     * @return
     */
    public FakePlayerEntity getTrackedPlayer(final Vec3d floor,
                                             final PlayerEntity player,
                                             final long time) {
        final PlayerLatencyTracker p = trackers.get(player);
        if (p != null) {
            return p.getTrackedPlayer(floor, time);
        }
        return null;
    }

    /**
     * @param floor
     * @param player
     * @param time
     * @return
     */
    public Vec3d getTrackedData(final Vec3d floor,
                                final PlayerEntity player,
                                final long time) {
        final PlayerLatencyTracker p = trackers.get(player);
        if (p != null) {
            return p.getTrackedData(floor, time);
        }
        return null;
    }
}
