package com.zapoc.horde;

import com.zapoc.bed.BedManager;
import com.zapoc.config.ZapocConfig;
import com.zapoc.spawn.ZapocSpawnPositionHelper;
import com.zapoc.zombie.ZombieAIController;
import com.zapoc.zombie.ZombiePowerSystem;
import com.zapoc.zombie.ZombieType;
import com.zapoc.zombie.ZombieTypeApplier;
import com.zapoc.zombie.ZombieTypeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.phys.AABB;

import java.util.Optional;
import java.util.Random;

public class HordeWaveSpawner {

    private static final Random RANDOM = new Random();

    private static int waveTimer = getFirstWaveDelayTicks();
    private static int currentWave = 0;
    private static boolean started = false;

    private static int pendingSpawnCount = 0;
    private static int pendingSpawnDay = 1;
    private static int pendingSpawnWave = 0;
    private static int pendingSpawnIndex = 0;
    private static int spawnBatchTimer = 0;

    public static void start() {

        waveTimer = getFirstWaveDelayTicks();
        currentWave = 0;
        started = true;

        pendingSpawnCount = 0;
        pendingSpawnDay = 1;
        pendingSpawnWave = 0;
        pendingSpawnIndex = 0;
        spawnBatchTimer = 0;
    }

    public static void stop() {

        waveTimer = getFirstWaveDelayTicks();
        currentWave = 0;
        started = false;

        pendingSpawnCount = 0;
        pendingSpawnDay = 1;
        pendingSpawnWave = 0;
        pendingSpawnIndex = 0;
        spawnBatchTimer = 0;
    }

    public static void tick(MinecraftServer server) {

        if (server == null)
            return;

        if (!started)
            return;

        if (!HordeManager.isHordeActive())
            return;

        if (!BedManager.hasBed())
            return;

        ServerLevel level = server.getLevel(BedManager.getDimension());

        if (level == null)
            return;

        if (pendingSpawnCount > 0) {
            tickPendingSpawns(level);
            return;
        }

        waveTimer--;

        if (waveTimer > 0)
            return;

        int maxWaves = getMaxWaves();

        if (currentWave >= maxWaves)
            return;

        BlockPos bedPos = BedManager.getBedPos();

        if (bedPos == null)
            return;

        if (countActiveHordeZombies(level, bedPos) >= ZapocConfig.MAX_ACTIVE_HORDE_ZOMBIES.get()) {
            waveTimer = 20 * 10;
            return;
        }

        prepareWave();

        currentWave++;
        waveTimer = getWaveIntervalTicks();
    }

    private static void prepareWave() {

        int day = HordeManager.getCurrentDay();
        int count = getZombiesForWave(day, currentWave);

        pendingSpawnCount = count;
        pendingSpawnDay = day;
        pendingSpawnWave = currentWave + 1;
        pendingSpawnIndex = 0;
        spawnBatchTimer = 0;

        System.out.println("[ZApoc] Preparing horde wave " + pendingSpawnWave + " with " + count + " zombies.");
    }

    private static void tickPendingSpawns(ServerLevel level) {

        BlockPos bedPos = BedManager.getBedPos();

        if (bedPos == null) {
            pendingSpawnCount = 0;
            return;
        }

        spawnBatchTimer--;

        if (spawnBatchTimer > 0)
            return;

        spawnBatchTimer = ZapocConfig.SPAWN_BATCH_INTERVAL_TICKS.get();

        int activeZombies = countActiveHordeZombies(level, bedPos);

        if (activeZombies >= ZapocConfig.MAX_ACTIVE_HORDE_ZOMBIES.get()) {
            pendingSpawnCount = 0;
            System.out.println("[ZApoc] Wave " + pendingSpawnWave + " stopped because active zombie limit was reached.");
            return;
        }

        int amount = Math.min(ZapocConfig.SPAWNS_PER_BATCH.get(), pendingSpawnCount);

        for (int i = 0; i < amount; i++) {

            int spawnIndex = pendingSpawnIndex;
            pendingSpawnIndex++;

            BlockPos spawnPos = findSpawnPos(level, bedPos, spawnIndex);

            pendingSpawnCount--;

            if (spawnPos == null) {
                System.out.println("[ZApoc] Skipped zombie spawn: no safe surface position found.");
                continue;
            }

            ZombieType type = getZombieTypeForDay(pendingSpawnDay, spawnIndex);
            spawnZombie(level, spawnPos, type, pendingSpawnDay);
        }

        if (pendingSpawnCount <= 0) {
            System.out.println("[ZApoc] Horde wave " + pendingSpawnWave + " finished spawning.");
        }
    }

    private static void spawnZombie(ServerLevel level, BlockPos pos, ZombieType type, int day) {

        Zombie zombie = EntityType.ZOMBIE.create(level);

        if (zombie == null)
            return;

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

        level.addFreshEntity(zombie);

        HordeGroupManager.addZombie(zombie);
    }

    private static BlockPos findSpawnPos(ServerLevel level, BlockPos bedPos, int index) {

        int minDistance = ZapocConfig.MIN_SPAWN_DISTANCE.get();
        int maxDistance = Math.max(minDistance, ZapocConfig.MAX_SPAWN_DISTANCE.get());
        Optional<BlockPos> pos = ZapocSpawnPositionHelper.findSurfaceSpawnPosition(
                level,
                bedPos,
                minDistance,
                maxDistance,
                RANDOM
        );

        return pos.orElse(null);
    }

    private static int countActiveHordeZombies(ServerLevel level, BlockPos bedPos) {

        AABB area = new AABB(bedPos).inflate(ZapocConfig.ACTIVE_ZOMBIE_CHECK_RADIUS.get());

        return level.getEntitiesOfClass(
                Zombie.class,
                area,
                zombie -> zombie.isAlive()
        ).size();
    }

    private static int getMaxWaves() {

        int day = HordeManager.getCurrentDay();

        int waves = 4 + day / 15;

        int maxWaves = ZapocConfig.MAX_WAVES.get();

        if (waves > maxWaves)
            return maxWaves;

        return waves;
    }

    private static int getZombiesForWave(int day, int wave) {

        int count = 22 + day / 3 + wave * 8;
        HordeNightType type = HordeNightEventManager.getCurrentType();

        if (type == HordeNightType.FINAL_HORDE) {
            count = (int) (count * 1.5D);
        } else if (type == HordeNightType.RUNNER_RUSH) {
            count = (int) (count * 1.15D);
        } else if (type == HordeNightType.TANK_PUSH) {
            count = (int) (count * 0.85D);
        }

        int maxZombies = ZapocConfig.MAX_ZOMBIES_PER_WAVE.get();

        if (count > maxZombies)
            return maxZombies;

        return count;
    }

    private static ZombieType getZombieTypeForDay(int day, int spawnIndex) {

        HordeNightType nightType = HordeNightEventManager.getCurrentType();

        if (nightType != HordeNightType.MIXED)
            return getZombieTypeForNightType(nightType, day);

        if (day >= 80 && spawnIndex % 6 == 0)
            return ZombieType.CRAWLER;

        if (day >= 80 && spawnIndex % 3 == 0)
            return ZombieType.BREAKER;

        if (day >= 60 && spawnIndex % 8 == 0)
            return ZombieType.CRAWLER;

        if (day >= 60 && spawnIndex % 4 == 0)
            return ZombieType.BREAKER;

        if (day >= 40 && spawnIndex % 10 == 0)
            return ZombieType.CRAWLER;

        if (day >= 40 && spawnIndex % 5 == 0)
            return ZombieType.BREAKER;

        if (day >= 20 && spawnIndex % 14 == 0)
            return ZombieType.CRAWLER;

        if (day >= 20 && spawnIndex % 7 == 0)
            return ZombieType.BREAKER;

        if (day < 20 && spawnIndex % 10 == 0)
            return ZombieType.BREAKER;

        int roll = RANDOM.nextInt(100);
        int breakerChance = getBreakerChance(day);
        int crawlerChance = getCrawlerChance(day);
        int runnerChance = ZapocConfig.RUNNER_CHANCE.get();
        int tankChance = ZapocConfig.TANK_CHANCE.get();
        int hunterChance = ZapocConfig.HUNTER_CHANCE.get();

        if (day < 20) {

            if (roll < 12)
                return ZombieType.BREAKER;

            if (roll < 25)
                return ZombieType.RUNNER;

            return ZombieType.NORMAL;
        }

        if (day < 40) {

            if (roll < breakerChance)
                return ZombieType.BREAKER;

            if (roll < breakerChance + runnerChance)
                return ZombieType.RUNNER;

            if (roll < breakerChance + runnerChance + crawlerChance)
                return ZombieType.CRAWLER;

            if (roll < breakerChance + runnerChance + crawlerChance + tankChance)
                return ZombieType.TANK;

            return ZombieType.NORMAL;
        }

        if (day < 60) {

            if (roll < breakerChance)
                return ZombieType.BREAKER;

            if (roll < breakerChance + runnerChance)
                return ZombieType.RUNNER;

            if (roll < breakerChance + runnerChance + crawlerChance)
                return ZombieType.CRAWLER;

            if (roll < breakerChance + runnerChance + crawlerChance + tankChance)
                return ZombieType.TANK;

            if (roll < breakerChance + runnerChance + crawlerChance + tankChance + hunterChance)
                return ZombieType.HUNTER;

            return ZombieType.NORMAL;
        }

        if (day < 80) {

            if (roll < breakerChance)
                return ZombieType.BREAKER;

            if (roll < breakerChance + runnerChance)
                return ZombieType.RUNNER;

            if (roll < breakerChance + runnerChance + crawlerChance)
                return ZombieType.CRAWLER;

            if (roll < breakerChance + runnerChance + crawlerChance + tankChance)
                return ZombieType.TANK;

            if (roll < breakerChance + runnerChance + crawlerChance + tankChance + hunterChance)
                return ZombieType.HUNTER;

            return ZombieType.NORMAL;
        }

        if (roll < breakerChance)
            return ZombieType.BREAKER;

        if (roll < breakerChance + runnerChance)
            return ZombieType.RUNNER;

        if (roll < breakerChance + runnerChance + crawlerChance)
            return ZombieType.CRAWLER;

        if (roll < breakerChance + runnerChance + crawlerChance + tankChance)
            return ZombieType.TANK;

        if (roll < breakerChance + runnerChance + crawlerChance + tankChance + hunterChance)
            return ZombieType.HUNTER;

        return ZombieType.NORMAL;
    }

    private static ZombieType getZombieTypeForNightType(HordeNightType nightType, int day) {

        int roll = RANDOM.nextInt(100);

        switch (nightType) {
            case BREAKER_SIEGE:
                if (roll < 48)
                    return ZombieType.BREAKER;
                if (roll < 65)
                    return ZombieType.TANK;
                if (roll < 80)
                    return ZombieType.HUNTER;
                if (roll < 90)
                    return ZombieType.RUNNER;
                return ZombieType.NORMAL;

            case RUNNER_RUSH:
                if (roll < 55)
                    return ZombieType.RUNNER;
                if (roll < 68)
                    return ZombieType.HUNTER;
                if (roll < 78)
                    return ZombieType.BREAKER;
                if (day >= 40 && roll < 88)
                    return ZombieType.CRAWLER;
                return ZombieType.NORMAL;

            case CRAWLER_SWARM:
                if (roll < 52)
                    return ZombieType.CRAWLER;
                if (roll < 68)
                    return ZombieType.RUNNER;
                if (roll < 80)
                    return ZombieType.BREAKER;
                if (roll < 90)
                    return ZombieType.HUNTER;
                return ZombieType.NORMAL;

            case TANK_PUSH:
                if (roll < 42)
                    return ZombieType.TANK;
                if (roll < 62)
                    return ZombieType.BREAKER;
                if (roll < 78)
                    return ZombieType.HUNTER;
                if (roll < 88)
                    return ZombieType.CRAWLER;
                return ZombieType.NORMAL;

            case HUNTER_NIGHT:
                if (roll < 48)
                    return ZombieType.HUNTER;
                if (roll < 65)
                    return ZombieType.RUNNER;
                if (roll < 78)
                    return ZombieType.CRAWLER;
                if (roll < 88)
                    return ZombieType.BREAKER;
                return ZombieType.NORMAL;

            case FINAL_HORDE:
                if (roll < 24)
                    return ZombieType.BREAKER;
                if (roll < 46)
                    return ZombieType.CRAWLER;
                if (roll < 64)
                    return ZombieType.TANK;
                if (roll < 82)
                    return ZombieType.HUNTER;
                if (roll < 94)
                    return ZombieType.RUNNER;
                return ZombieType.NORMAL;

            case MIXED:
            default:
                return ZombieType.NORMAL;
        }
    }

    private static int getWaveIntervalTicks() {
        return ZapocConfig.WAVE_INTERVAL_SECONDS.get() * 20;
    }

    private static int getFirstWaveDelayTicks() {
        return ZapocConfig.FIRST_WAVE_DELAY_SECONDS.get() * 20;
    }

    private static int getBreakerChance(int day) {

        if (day >= 80)
            return ZapocConfig.BREAKER_CHANCE_DAY_80.get();

        if (day >= 60)
            return ZapocConfig.BREAKER_CHANCE_DAY_60.get();

        if (day >= 40)
            return ZapocConfig.BREAKER_CHANCE_DAY_40.get();

        return ZapocConfig.BREAKER_CHANCE_DAY_20.get();
    }

    private static int getCrawlerChance(int day) {

        if (day >= 80)
            return ZapocConfig.CRAWLER_CHANCE_DAY_80.get();

        if (day >= 60)
            return ZapocConfig.CRAWLER_CHANCE_DAY_60.get();

        if (day >= 40)
            return ZapocConfig.CRAWLER_CHANCE_DAY_40.get();

        return ZapocConfig.CRAWLER_CHANCE_DAY_20.get();
    }
}
