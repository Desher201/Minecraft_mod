package com.zapoc.roaming;

import com.zapoc.config.ZapocConfig;
import com.zapoc.zombie.ZombiePositioningHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RoamingGroupManager {

    private static final String GROUP_ID_TAG = "ZapocRoamingGroupId";
    private static final String MEMBER_TAG = "ZapocRoamingMember";
    private static final String LEADER_TAG = "ZapocRoamingLeader";

    private static final Map<UUID, Mob> ROAMING_ZOMBIES = new HashMap<>();
    private static final Map<Integer, RoamingGroup> GROUPS = new HashMap<>();
    private static final Map<UUID, Integer> MOB_TO_GROUP = new HashMap<>();

    private static int nextGroupId = 1;
    private static int aiTimer = 0;

    public static int createGroup(RoamingGroupType type, BlockPos center) {

        int groupId = nextGroupId++;
        GROUPS.put(groupId, new RoamingGroup(groupId, type, center));

        return groupId;
    }

    public static void track(Mob mob, int groupId, boolean leader) {

        if (mob == null)
            return;

        RoamingGroup group = GROUPS.get(groupId);

        if (group == null)
            return;

        UUID uuid = mob.getUUID();

        ROAMING_ZOMBIES.put(uuid, mob);
        MOB_TO_GROUP.put(uuid, groupId);
        group.memberIds.add(uuid);

        mob.getPersistentData().putInt(GROUP_ID_TAG, groupId);
        mob.getPersistentData().putBoolean(MEMBER_TAG, true);

        if (leader || group.leaderId == null) {
            setLeader(group, mob);
        }
    }

    public static boolean isRoamingMob(Mob mob) {

        if (mob == null)
            return false;

        return MOB_TO_GROUP.containsKey(mob.getUUID())
                || mob.getPersistentData().getBoolean(MEMBER_TAG);
    }

    public static int getGroupCount() {
        cleanup(null);
        return GROUPS.size();
    }

    public static int getActiveCount() {
        cleanup(null);
        return ROAMING_ZOMBIES.size();
    }

    public static int getSharedTargetCount() {

        int count = 0;

        for (RoamingGroup group : GROUPS.values()) {
            if (group.sharedTargetId != null) {
                count++;
            }
        }

        return count;
    }

    public static void tick(MinecraftServer server) {

        cleanup(server);

        aiTimer += 5;

        if (aiTimer < 15)
            return;

        aiTimer = 0;

        for (RoamingGroup group : GROUPS.values()) {
            tickGroup(server, group);
        }
    }

    public static void alertGroup(Mob mob, Player player) {

        if (mob == null || player == null)
            return;

        Integer groupId = MOB_TO_GROUP.get(mob.getUUID());

        if (groupId == null)
            return;

        RoamingGroup group = GROUPS.get(groupId);

        if (group == null)
            return;

        if (!(player instanceof ServerPlayer serverPlayer))
            return;

        if (!isValidTarget(serverPlayer))
            return;

        setSharedTarget(group, serverPlayer);
        applySharedTarget(group, serverPlayer);
    }

    public static void cleanup(MinecraftServer server) {

        Iterator<Map.Entry<UUID, Mob>> iterator = ROAMING_ZOMBIES.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, Mob> entry = iterator.next();
            UUID uuid = entry.getKey();
            Mob mob = entry.getValue();

            if (mob == null || mob.isRemoved() || !mob.isAlive()) {
                removeMember(uuid);
                iterator.remove();
                continue;
            }

            if (server != null && isFarFromAllPlayers(server, mob)) {
                mob.discard();
                removeMember(uuid);
                iterator.remove();
            }
        }

        GROUPS.values().removeIf(group -> group.memberIds.isEmpty());
    }

    public static int clear() {

        int removed = 0;

        for (Mob mob : ROAMING_ZOMBIES.values()) {
            if (mob != null && !mob.isRemoved()) {
                mob.discard();
                removed++;
            }
        }

        ROAMING_ZOMBIES.clear();
        MOB_TO_GROUP.clear();
        GROUPS.clear();

        return removed;
    }

    private static void tickGroup(MinecraftServer server, RoamingGroup group) {

        cleanupGroup(group);
        ensureLeader(group);

        if (group.leaderId == null)
            return;

        ServerPlayer sharedTarget = findSharedTarget(server, group);

        if (sharedTarget != null) {
            setSharedTarget(group, sharedTarget);
            applySharedTarget(group, sharedTarget);
            return;
        }

        group.sharedTargetId = null;
        rallyGroup(group);
    }

    private static ServerPlayer findSharedTarget(MinecraftServer server, RoamingGroup group) {

        ServerPlayer current = getPlayer(server, group.sharedTargetId);

        if (current != null && isTargetStillValid(group, current)) {
            return current;
        }

        for (UUID uuid : group.memberIds) {
            Mob mob = ROAMING_ZOMBIES.get(uuid);

            if (mob == null || mob.isRemoved() || !mob.isAlive())
                continue;

            if (mob.getTarget() instanceof ServerPlayer player && isValidTarget(player)) {
                return player;
            }

            ServerPlayer detected = detectPlayer(mob);

            if (detected != null) {
                return detected;
            }
        }

        return null;
    }

    private static ServerPlayer detectPlayer(Mob mob) {

        double range = ZapocConfig.ROAMING_GROUP_AGGRO_RANGE.get();
        AABB area = mob.getBoundingBox().inflate(range);

        for (ServerPlayer player : mob.level.getEntitiesOfClass(ServerPlayer.class, area, RoamingGroupManager::isValidTarget)) {
            if (mob.hasLineOfSight(player)) {
                return player;
            }
        }

        return null;
    }

    private static void applySharedTarget(RoamingGroup group, ServerPlayer target) {

        double range = ZapocConfig.ROAMING_GROUP_ASSIST_RANGE.get();
        double rangeSqr = range * range;

        for (UUID uuid : group.memberIds) {
            Mob mob = ROAMING_ZOMBIES.get(uuid);

            if (mob == null || mob.isRemoved() || !mob.isAlive())
                continue;

            if (!mob.level.dimension().equals(target.level.dimension()))
                continue;

            if (mob.distanceToSqr(target) > rangeSqr)
                continue;

            mob.setTarget(target);
        }
    }

    private static void rallyGroup(RoamingGroup group) {

        Mob leader = ROAMING_ZOMBIES.get(group.leaderId);

        if (leader == null || leader.isRemoved() || !leader.isAlive())
            return;

        double range = ZapocConfig.ROAMING_GROUP_KEEP_TOGETHER_RANGE.get();
        double rangeSqr = range * range;
        double radius = ZapocConfig.ROAMING_GROUP_RALLY_RADIUS.get();
        double speed = ZapocConfig.ROAMING_GROUP_RALLY_SPEED.get();

        for (UUID uuid : group.memberIds) {

            if (uuid.equals(group.leaderId))
                continue;

            Mob mob = ROAMING_ZOMBIES.get(uuid);

            if (mob == null || mob.isRemoved() || !mob.isAlive())
                continue;

            if (!mob.level.dimension().equals(leader.level.dimension()))
                continue;

            if (mob.distanceToSqr(leader) <= rangeSqr)
                continue;

            Vec3 pos = ZombiePositioningHelper.getSpreadPositionAroundTarget(mob, leader, radius);
            mob.getNavigation().moveTo(pos.x, pos.y, pos.z, speed);
        }
    }

    private static boolean isTargetStillValid(RoamingGroup group, ServerPlayer player) {

        if (!isValidTarget(player))
            return false;

        double range = ZapocConfig.ROAMING_GROUP_TARGET_FORGET_RANGE.get();
        double rangeSqr = range * range;

        for (UUID uuid : group.memberIds) {
            Mob mob = ROAMING_ZOMBIES.get(uuid);

            if (mob == null || mob.isRemoved() || !mob.isAlive())
                continue;

            if (!mob.level.dimension().equals(player.level.dimension()))
                continue;

            if (mob.distanceToSqr(player) <= rangeSqr)
                return true;
        }

        return false;
    }

    private static ServerPlayer getPlayer(MinecraftServer server, UUID uuid) {

        if (server == null || uuid == null)
            return null;

        return server.getPlayerList().getPlayer(uuid);
    }

    private static void setSharedTarget(RoamingGroup group, ServerPlayer player) {
        group.sharedTargetId = player.getUUID();
    }

    private static void cleanupGroup(RoamingGroup group) {

        Iterator<UUID> iterator = group.memberIds.iterator();

        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            Mob mob = ROAMING_ZOMBIES.get(uuid);

            if (mob == null || mob.isRemoved() || !mob.isAlive()) {
                MOB_TO_GROUP.remove(uuid);
                ROAMING_ZOMBIES.remove(uuid);
                iterator.remove();
            }
        }
    }

    private static void ensureLeader(RoamingGroup group) {

        Mob leader = ROAMING_ZOMBIES.get(group.leaderId);

        if (leader != null && leader.isAlive() && !leader.isRemoved())
            return;

        group.leaderId = null;

        for (UUID uuid : group.memberIds) {
            Mob mob = ROAMING_ZOMBIES.get(uuid);

            if (mob != null && mob.isAlive() && !mob.isRemoved()) {
                setLeader(group, mob);
                return;
            }
        }
    }

    private static void setLeader(RoamingGroup group, Mob mob) {

        if (group.leaderId != null) {
            Mob oldLeader = ROAMING_ZOMBIES.get(group.leaderId);

            if (oldLeader != null) {
                oldLeader.getPersistentData().remove(LEADER_TAG);
            }
        }

        group.leaderId = mob.getUUID();
        mob.getPersistentData().putBoolean(LEADER_TAG, true);
    }

    private static void removeMember(UUID uuid) {

        Integer groupId = MOB_TO_GROUP.remove(uuid);

        if (groupId == null)
            return;

        RoamingGroup group = GROUPS.get(groupId);

        if (group == null)
            return;

        group.memberIds.remove(uuid);

        if (uuid.equals(group.leaderId)) {
            group.leaderId = null;
        }
    }

    private static boolean isValidTarget(Player player) {
        return player != null && player.isAlive() && !player.isCreative() && !player.isSpectator();
    }

    private static boolean isFarFromAllPlayers(MinecraftServer server, Mob mob) {

        double distance = ZapocConfig.ROAMING_GROUP_DESPAWN_DISTANCE.get();
        double maxDistanceSqr = distance * distance;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {

            if (player.isSpectator())
                continue;

            if (!player.level.dimension().equals(mob.level.dimension()))
                continue;

            if (player.distanceToSqr(mob) <= maxDistanceSqr)
                return false;
        }

        return true;
    }

    private static class RoamingGroup {
        private final int groupId;
        private final RoamingGroupType type;
        private final BlockPos center;
        private final Set<UUID> memberIds = new HashSet<>();
        private UUID leaderId;
        private UUID sharedTargetId;

        private RoamingGroup(int groupId, RoamingGroupType type, BlockPos center) {
            this.groupId = groupId;
            this.type = type;
            this.center = center;
        }
    }
}
