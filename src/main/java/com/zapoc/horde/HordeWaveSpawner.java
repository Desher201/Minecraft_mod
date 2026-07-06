package com.zapoc.horde;

import com.zapoc.bed.BedManager;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.Random;

public class HordeWaveSpawner {

    private static final int WAVE_INTERVAL_TICKS = 20 * 40;
    private static final int FIRST_WAVE_DELAY_TICKS = 20 * 5;

    private static final int SPAWN_BATCH_INTERVAL_TICKS = 8;
    private static final int SPAWNS_PER_BATCH = 3;

    private static final int MIN_SPAWN_DISTANCE = 28;
    private static final int MAX_SPAWN_DISTANCE = 46;

    private static final int MAX_WAVES = 10;
    private static final int MAX_ZOMBIES_PER_WAVE = 120;
    private static final int MAX_ACTIVE_HORDE_ZOMBIES = 180;

    private static final double ACTIVE_ZOMBIE_CHECK_RADIUS = 112.0D;

    private static final Random RANDOM = new Random();

    private static int waveTimer = FIRST_WAVE_DELAY_TICKS;
    private static int currentWave = 0;
    private static boolean started = false;

    private static int pendingSpawnCount = 0;
    private static int pendingSpawnDay = 1;
    private static int pendingSpawnWave = 0;
    private static int pendingSpawnIndex = 0;
    private static int spawnBatchTimer = 0;

    public static void start() {

        waveTimer = FIRST_WAVE_DELAY_TICKS;
        currentWave = 0;
        started = true;

        pendingSpawnCount = 0;
        pendingSpawnDay = 1;
        pendingSpawnWave = 0;
        pendingSpawnIndex = 0;
        spawnBatchTimer = 0;
    }

    public static void stop() {

        waveTimer = FIRST_WAVE_DELAY_TICKS;
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

        if (countActiveHordeZombies(level, bedPos) >= MAX_ACTIVE_HORDE_ZOMBIES) {
            waveTimer = 20 * 10;
            return;
        }

        prepareWave();

        currentWave++;
        waveTimer = WAVE_INTERVAL_TICKS;
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

        spawnBatchTimer = SPAWN_BATCH_INTERVAL_TICKS;

        int activeZombies = countActiveHordeZombies(level, bedPos);

        if (activeZombies >= MAX_ACTIVE_HORDE_ZOMBIES) {
            pendingSpawnCount = 0;
            System.out.println("[ZApoc] Wave " + pendingSpawnWave + " stopped because active zombie limit was reached.");
            return;
        }

        int amount = Math.min(SPAWNS_PER_BATCH, pendingSpawnCount);

        for (int i = 0; i < amount; i++) {

            int spawnIndex = pendingSpawnIndex;
            pendingSpawnIndex++;

            BlockPos spawnPos = findSpawnPos(level, bedPos, spawnIndex);

            if (spawnPos == null)
                continue;

            ZombieType type = getZombieTypeForDay(pendingSpawnDay, spawnIndex);
            spawnZombie(level, spawnPos, type, pendingSpawnDay);

            pendingSpawnCount--;
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

        for (int attempt = 0; attempt < 20; attempt++) {

            int directionIndex = (index + attempt) % 4;
            int distance = MIN_SPAWN_DISTANCE + RANDOM.nextInt(MAX_SPAWN_DISTANCE - MIN_SPAWN_DISTANCE + 1);
            int sideOffset = RANDOM.nextInt(21) - 10;

            int x = bedPos.getX();
            int z = bedPos.getZ();

            if (directionIndex == 0) {
                z -= distance;
                x += sideOffset;
            } else if (directionIndex == 1) {
                z += distance;
                x += sideOffset;
            } else if (directionIndex == 2) {
                x -= distance;
                z += sideOffset;
            } else {
                x += distance;
                z += sideOffset;
            }

            BlockPos surface = level.getHeightmapPos(
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    new BlockPos(x, bedPos.getY(), z)
            );

            if (isSafeSpawnPos(level, surface)) {
                return surface;
            }
        }

        return null;
    }

    private static boolean isSafeSpawnPos(ServerLevel level, BlockPos pos) {

        BlockState feet = level.getBlockState(pos);
        BlockState head = level.getBlockState(pos.above());
        BlockState below = level.getBlockState(pos.below());

        if (!feet.getCollisionShape(level, pos).isEmpty())
            return false;

        if (!head.getCollisionShape(level, pos.above()).isEmpty())
            return false;

        return !below.getCollisionShape(level, pos.below()).isEmpty();
    }

    private static int countActiveHordeZombies(ServerLevel level, BlockPos bedPos) {

        AABB area = new AABB(bedPos).inflate(ACTIVE_ZOMBIE_CHECK_RADIUS);

        return level.getEntitiesOfClass(
                Zombie.class,
                area,
                zombie -> zombie.isAlive()
        ).size();
    }

    private static int getMaxWaves() {

        int day = HordeManager.getCurrentDay();

        int waves = 4 + day / 15;

        if (waves > MAX_WAVES)
            return MAX_WAVES;

        return waves;
    }

    private static int getZombiesForWave(int day, int wave) {

        int count = 22 + day / 3 + wave * 8;

        if (count > MAX_ZOMBIES_PER_WAVE)
            return MAX_ZOMBIES_PER_WAVE;

        return count;
    }

    private static ZombieType getZombieTypeForDay(int day, int spawnIndex) {

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

        if (day < 20) {

            if (roll < 12)
                return ZombieType.BREAKER;

            if (roll < 25)
                return ZombieType.RUNNER;

            return ZombieType.NORMAL;
        }

        if (day < 40) {

            if (roll < 18)
                return ZombieType.BREAKER;

            if (roll < 32)
                return ZombieType.RUNNER;

            if (roll < 42)
                return ZombieType.CRAWLER;

            if (roll < 52)
                return ZombieType.TANK;

            return ZombieType.NORMAL;
        }

        if (day < 60) {

            if (roll < 24)
                return ZombieType.BREAKER;

            if (roll < 40)
                return ZombieType.RUNNER;

            if (roll < 55)
                return ZombieType.CRAWLER;

            if (roll < 70)
                return ZombieType.TANK;

            if (roll < 82)
                return ZombieType.HUNTER;

            return ZombieType.NORMAL;
        }

        if (day < 80) {

            if (roll < 30)
                return ZombieType.BREAKER;

            if (roll < 46)
                return ZombieType.RUNNER;

            if (roll < 62)
                return ZombieType.CRAWLER;

            if (roll < 78)
                return ZombieType.TANK;

            if (roll < 90)
                return ZombieType.HUNTER;

            return ZombieType.NORMAL;
        }

        if (roll < 38)
            return ZombieType.BREAKER;

        if (roll < 52)
            return ZombieType.RUNNER;

        if (roll < 68)
            return ZombieType.CRAWLER;

        if (roll < 82)
            return ZombieType.TANK;

        if (roll < 94)
            return ZombieType.HUNTER;

        return ZombieType.NORMAL;
    }
}
