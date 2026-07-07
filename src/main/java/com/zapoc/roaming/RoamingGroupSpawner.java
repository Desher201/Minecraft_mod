package com.zapoc.roaming;

import com.zapoc.config.ZapocConfig;
import com.zapoc.horde.HordeManager;
import com.zapoc.spawn.ZapocSpawnPositionHelper;
import com.zapoc.zombie.ZombieAIController;
import com.zapoc.zombie.ZombiePowerSystem;
import com.zapoc.zombie.ZombieType;
import com.zapoc.zombie.ZombieTypeApplier;
import com.zapoc.zombie.ZombieTypeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RoamingGroupSpawner {

    private static final Random RANDOM = new Random();
    private static int spawnTimer = 0;

    public static void tick(MinecraftServer server) {

        if (server == null)
            return;

        RoamingGroupManager.tick(server);

        if (!ZapocConfig.ROAMING_GROUPS_ENABLED.get())
            return;

        if (HordeManager.isHordeActive() && !ZapocConfig.ROAMING_GROUPS_SPAWN_DURING_HORDE.get())
            return;

        spawnTimer += 5;

        if (spawnTimer < ZapocConfig.ROAMING_GROUP_SPAWN_INTERVAL_TICKS.get())
            return;

        spawnTimer = 0;

        if (RoamingGroupManager.getActiveCount() >= ZapocConfig.ROAMING_GROUP_MAX_ACTIVE_ZOMBIES.get())
            return;

        ServerPlayer player = choosePlayer(server);

        if (player == null)
            return;

        int day = HordeManager.getCurrentDay();
        RoamingGroupType type = chooseGroupType(day);

        spawnGroupNearPlayer(server.overworld(), player, type, day);
    }

    public static int spawnGroupNearPlayer(ServerLevel level, ServerPlayer player, RoamingGroupType type, int day) {

        if (level == null || player == null || type == null)
            return 0;

        int size = getGroupSize(type);
        int maxAllowed = Math.max(0, ZapocConfig.ROAMING_GROUP_MAX_ACTIVE_ZOMBIES.get() - RoamingGroupManager.getActiveCount());

        if (size > maxAllowed)
            size = maxAllowed;

        Optional<BlockPos> optionalCenter = ZapocSpawnPositionHelper.findSurfaceSpawnPosition(
                level,
                player.blockPosition(),
                ZapocConfig.ROAMING_GROUP_MIN_SPAWN_DISTANCE.get(),
                ZapocConfig.ROAMING_GROUP_MAX_SPAWN_DISTANCE.get(),
                RANDOM
        );

        if (optionalCenter.isEmpty()) {
            System.out.println("[ZApoc] Skipped roaming group spawn: no safe group center found.");
            return 0;
        }

        BlockPos groupCenter = optionalCenter.get();
        int groupId = RoamingGroupManager.createGroup(type, groupCenter);
        int spawned = 0;

        for (int i = 0; i < size; i++) {
            Optional<BlockPos> optionalPos = ZapocSpawnPositionHelper.findSurfaceSpawnPositionNear(
                    level,
                    groupCenter,
                    ZapocConfig.ROAMING_GROUP_MEMBER_SCATTER_RADIUS.get(),
                    RANDOM
            );

            if (optionalPos.isEmpty()) {
                if (ZapocSpawnPositionHelper.isValidSpawnPosition(level, groupCenter)) {
                    optionalPos = Optional.of(groupCenter);
                } else {
                    System.out.println("[ZApoc] Skipped roaming zombie spawn: no safe pack member position found.");
                    continue;
                }
            }

            ZombieType zombieType = chooseZombieType(type, i);

            if (spawnZombie(level, optionalPos.get(), zombieType, day, groupId, spawned == 0)) {
                spawned++;
            }
        }

        return spawned;
    }

    public static void reset() {
        spawnTimer = 0;
    }

    private static boolean spawnZombie(ServerLevel level, BlockPos pos, ZombieType type, int day, int groupId, boolean leader) {

        Zombie zombie = EntityType.ZOMBIE.create(level);

        if (zombie == null)
            return false;

        zombie.moveTo(
                pos.getX() + 0.5D,
                pos.getY(),
                pos.getZ() + 0.5D,
                RANDOM.nextFloat() * 360.0F,
                0.0F
        );

        zombie.finalizeSpawn(
                level,
                level.getCurrentDifficultyAt(pos),
                MobSpawnType.EVENT,
                null,
                null
        );

        zombie.setBaby(false);
        zombie.setCustomName(null);
        zombie.setCustomNameVisible(false);

        ZombieTypeManager.setType(zombie, type);
        ZombieTypeApplier.apply(zombie, type, day);
        ZombiePowerSystem.applyToZombie(zombie, day);
        ZombieAIController.applyAI(zombie, type);

        RoamingGroupManager.track(zombie, groupId, leader);

        level.addFreshEntity(zombie);
        return true;
    }

    private static ServerPlayer choosePlayer(MinecraftServer server) {

        List<ServerPlayer> players = new ArrayList<>();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {

            if (!player.isAlive())
                continue;

            if (player.isCreative() || player.isSpectator())
                continue;

            if (!player.level.dimension().equals(ServerLevel.OVERWORLD))
                continue;

            players.add(player);
        }

        if (players.isEmpty())
            return null;

        return players.get(RANDOM.nextInt(players.size()));
    }

    private static RoamingGroupType chooseGroupType(int day) {

        List<RoamingGroupType> available = new ArrayList<>();
        available.add(RoamingGroupType.SMALL_PACK);

        if (day >= ZapocConfig.ROAMING_RUNNER_PACK_MIN_DAY.get())
            available.add(RoamingGroupType.RUNNER_PACK);

        if (day >= ZapocConfig.ROAMING_CRAWLER_PACK_MIN_DAY.get())
            available.add(RoamingGroupType.CRAWLER_PACK);

        if (day >= ZapocConfig.ROAMING_HEAVY_PACK_MIN_DAY.get())
            available.add(RoamingGroupType.HEAVY_PATROL);

        if (day >= ZapocConfig.ROAMING_REMNANT_PACK_MIN_DAY.get())
            available.add(RoamingGroupType.HORDE_REMNANT);

        return available.get(RANDOM.nextInt(available.size()));
    }

    private static int getGroupSize(RoamingGroupType type) {

        int min;
        int max;

        switch (type) {
            case RUNNER_PACK:
                min = ZapocConfig.ROAMING_RUNNER_PACK_MIN_SIZE.get();
                max = ZapocConfig.ROAMING_RUNNER_PACK_MAX_SIZE.get();
                break;

            case CRAWLER_PACK:
                min = ZapocConfig.ROAMING_CRAWLER_PACK_MIN_SIZE.get();
                max = ZapocConfig.ROAMING_CRAWLER_PACK_MAX_SIZE.get();
                break;

            case HEAVY_PATROL:
                min = ZapocConfig.ROAMING_HEAVY_PATROL_MIN_SIZE.get();
                max = ZapocConfig.ROAMING_HEAVY_PATROL_MAX_SIZE.get();
                break;

            case HORDE_REMNANT:
                min = ZapocConfig.ROAMING_HORDE_REMNANT_MIN_SIZE.get();
                max = ZapocConfig.ROAMING_HORDE_REMNANT_MAX_SIZE.get();
                break;

            case SMALL_PACK:
            default:
                min = ZapocConfig.ROAMING_SMALL_PACK_MIN_SIZE.get();
                max = ZapocConfig.ROAMING_SMALL_PACK_MAX_SIZE.get();
                break;
        }

        max = Math.max(min, max);

        return min + RANDOM.nextInt(max - min + 1);
    }

    private static ZombieType chooseZombieType(RoamingGroupType groupType, int index) {

        switch (groupType) {
            case RUNNER_PACK:
                if (RANDOM.nextInt(100) < 70)
                    return ZombieType.RUNNER;
                return chooseWeightedType();

            case CRAWLER_PACK:
                if (RANDOM.nextInt(100) < 70)
                    return ZombieType.CRAWLER;
                return chooseWeightedType();

            case HEAVY_PATROL:
                if (index == 0)
                    return ZombieType.TANK;

                if (RANDOM.nextInt(100) < 35)
                    return ZombieType.TANK;
                if (RANDOM.nextInt(100) < 50)
                    return ZombieType.HUNTER;
                return ZombieType.NORMAL;

            case HORDE_REMNANT:
                if (RANDOM.nextInt(100) < 20)
                    return ZombieType.BREAKER;
                if (RANDOM.nextInt(100) < 40)
                    return ZombieType.CRAWLER;
                if (RANDOM.nextInt(100) < 55)
                    return ZombieType.TANK;
                if (RANDOM.nextInt(100) < 75)
                    return ZombieType.HUNTER;
                return ZombieType.RUNNER;

            case SMALL_PACK:
            default:
                return chooseWeightedType();
        }
    }

    private static ZombieType chooseWeightedType() {

        int normal = ZapocConfig.ROAMING_NORMAL_WEIGHT.get();
        int runner = ZapocConfig.ROAMING_RUNNER_WEIGHT.get();
        int hunter = ZapocConfig.ROAMING_HUNTER_WEIGHT.get();
        int tank = ZapocConfig.ROAMING_TANK_WEIGHT.get();
        int breaker = ZapocConfig.ROAMING_BREAKER_WEIGHT.get();
        int crawler = ZapocConfig.ROAMING_CRAWLER_WEIGHT.get();
        int total = normal + runner + hunter + tank + breaker + crawler;

        if (total <= 0)
            return ZombieType.NORMAL;

        int roll = RANDOM.nextInt(total);

        if (roll < normal)
            return ZombieType.NORMAL;

        roll -= normal;

        if (roll < runner)
            return ZombieType.RUNNER;

        roll -= runner;

        if (roll < hunter)
            return ZombieType.HUNTER;

        roll -= hunter;

        if (roll < tank)
            return ZombieType.TANK;

        roll -= tank;

        if (roll < breaker)
            return ZombieType.BREAKER;

        return ZombieType.CRAWLER;
    }
}
