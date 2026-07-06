package com.zapoc.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ZapocConfig {

    public static final ForgeConfigSpec SERVER_SPEC;

    public static final ForgeConfigSpec.IntValue HORDE_INTERVAL_DAYS;
    public static final ForgeConfigSpec.IntValue WAVE_INTERVAL_SECONDS;
    public static final ForgeConfigSpec.IntValue FIRST_WAVE_DELAY_SECONDS;
    public static final ForgeConfigSpec.IntValue SPAWN_BATCH_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue SPAWNS_PER_BATCH;
    public static final ForgeConfigSpec.IntValue MAX_WAVES;
    public static final ForgeConfigSpec.IntValue MAX_ZOMBIES_PER_WAVE;
    public static final ForgeConfigSpec.IntValue MAX_ACTIVE_HORDE_ZOMBIES;
    public static final ForgeConfigSpec.DoubleValue ACTIVE_ZOMBIE_CHECK_RADIUS;
    public static final ForgeConfigSpec.IntValue MIN_SPAWN_DISTANCE;
    public static final ForgeConfigSpec.IntValue MAX_SPAWN_DISTANCE;

    public static final ForgeConfigSpec.IntValue BREAKER_CHANCE_DAY_20;
    public static final ForgeConfigSpec.IntValue BREAKER_CHANCE_DAY_40;
    public static final ForgeConfigSpec.IntValue BREAKER_CHANCE_DAY_60;
    public static final ForgeConfigSpec.IntValue BREAKER_CHANCE_DAY_80;
    public static final ForgeConfigSpec.IntValue CRAWLER_CHANCE_DAY_20;
    public static final ForgeConfigSpec.IntValue CRAWLER_CHANCE_DAY_40;
    public static final ForgeConfigSpec.IntValue CRAWLER_CHANCE_DAY_60;
    public static final ForgeConfigSpec.IntValue CRAWLER_CHANCE_DAY_80;
    public static final ForgeConfigSpec.IntValue RUNNER_CHANCE;
    public static final ForgeConfigSpec.IntValue TANK_CHANCE;
    public static final ForgeConfigSpec.IntValue HUNTER_CHANCE;

    public static final ForgeConfigSpec.DoubleValue NORMAL_BASE_SPEED;
    public static final ForgeConfigSpec.DoubleValue NORMAL_MAX_SPEED;
    public static final ForgeConfigSpec.DoubleValue RUNNER_BASE_SPEED;
    public static final ForgeConfigSpec.DoubleValue RUNNER_MAX_SPEED;
    public static final ForgeConfigSpec.DoubleValue TANK_BASE_SPEED;
    public static final ForgeConfigSpec.DoubleValue TANK_MAX_SPEED;
    public static final ForgeConfigSpec.DoubleValue HUNTER_BASE_SPEED;
    public static final ForgeConfigSpec.DoubleValue HUNTER_MAX_SPEED;
    public static final ForgeConfigSpec.DoubleValue BREAKER_BASE_SPEED;
    public static final ForgeConfigSpec.DoubleValue BREAKER_MAX_SPEED;
    public static final ForgeConfigSpec.DoubleValue CRAWLER_BASE_SPEED;
    public static final ForgeConfigSpec.DoubleValue CRAWLER_MAX_SPEED;

    public static final ForgeConfigSpec.DoubleValue HEALTH_POWER_BASE;
    public static final ForgeConfigSpec.DoubleValue MAX_HEALTH_POWER;
    public static final ForgeConfigSpec.DoubleValue DAMAGE_PER_DAY;
    public static final ForgeConfigSpec.DoubleValue MAX_DAMAGE_POWER;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("horde");
        HORDE_INTERVAL_DAYS = builder.defineInRange("hordeIntervalDays", 10, 1, 365);
        WAVE_INTERVAL_SECONDS = builder.defineInRange("waveIntervalSeconds", 40, 1, 3600);
        FIRST_WAVE_DELAY_SECONDS = builder.defineInRange("firstWaveDelaySeconds", 5, 0, 3600);
        SPAWN_BATCH_INTERVAL_TICKS = builder.defineInRange("spawnBatchIntervalTicks", 8, 1, 1200);
        SPAWNS_PER_BATCH = builder.defineInRange("spawnsPerBatch", 3, 1, 100);
        MAX_WAVES = builder.defineInRange("maxWaves", 10, 1, 100);
        MAX_ZOMBIES_PER_WAVE = builder.defineInRange("maxZombiesPerWave", 120, 1, 1000);
        MAX_ACTIVE_HORDE_ZOMBIES = builder.defineInRange("maxActiveHordeZombies", 180, 1, 2000);
        ACTIVE_ZOMBIE_CHECK_RADIUS = builder.defineInRange("activeZombieCheckRadius", 112.0D, 8.0D, 512.0D);
        MIN_SPAWN_DISTANCE = builder.defineInRange("minSpawnDistance", 28, 1, 512);
        MAX_SPAWN_DISTANCE = builder.defineInRange("maxSpawnDistance", 46, 1, 1024);
        builder.pop();

        builder.push("zombie_chances");
        BREAKER_CHANCE_DAY_20 = builder.defineInRange("breakerChanceDay20", 18, 0, 100);
        BREAKER_CHANCE_DAY_40 = builder.defineInRange("breakerChanceDay40", 24, 0, 100);
        BREAKER_CHANCE_DAY_60 = builder.defineInRange("breakerChanceDay60", 30, 0, 100);
        BREAKER_CHANCE_DAY_80 = builder.defineInRange("breakerChanceDay80", 38, 0, 100);
        CRAWLER_CHANCE_DAY_20 = builder.defineInRange("crawlerChanceDay20", 10, 0, 100);
        CRAWLER_CHANCE_DAY_40 = builder.defineInRange("crawlerChanceDay40", 15, 0, 100);
        CRAWLER_CHANCE_DAY_60 = builder.defineInRange("crawlerChanceDay60", 16, 0, 100);
        CRAWLER_CHANCE_DAY_80 = builder.defineInRange("crawlerChanceDay80", 16, 0, 100);
        RUNNER_CHANCE = builder.defineInRange("runnerChance", 14, 0, 100);
        TANK_CHANCE = builder.defineInRange("tankChance", 10, 0, 100);
        HUNTER_CHANCE = builder.defineInRange("hunterChance", 12, 0, 100);
        builder.pop();

        builder.push("zombie_speed");
        NORMAL_BASE_SPEED = builder.defineInRange("normalBaseSpeed", 0.23D, 0.01D, 2.0D);
        NORMAL_MAX_SPEED = builder.defineInRange("normalMaxSpeed", 0.29D, 0.01D, 2.0D);
        RUNNER_BASE_SPEED = builder.defineInRange("runnerBaseSpeed", 0.32D, 0.01D, 2.0D);
        RUNNER_MAX_SPEED = builder.defineInRange("runnerMaxSpeed", 0.38D, 0.01D, 2.0D);
        TANK_BASE_SPEED = builder.defineInRange("tankBaseSpeed", 0.17D, 0.01D, 2.0D);
        TANK_MAX_SPEED = builder.defineInRange("tankMaxSpeed", 0.22D, 0.01D, 2.0D);
        HUNTER_BASE_SPEED = builder.defineInRange("hunterBaseSpeed", 0.27D, 0.01D, 2.0D);
        HUNTER_MAX_SPEED = builder.defineInRange("hunterMaxSpeed", 0.33D, 0.01D, 2.0D);
        BREAKER_BASE_SPEED = builder.defineInRange("breakerBaseSpeed", 0.22D, 0.01D, 2.0D);
        BREAKER_MAX_SPEED = builder.defineInRange("breakerMaxSpeed", 0.28D, 0.01D, 2.0D);
        CRAWLER_BASE_SPEED = builder.defineInRange("crawlerBaseSpeed", 0.22D, 0.01D, 2.0D);
        CRAWLER_MAX_SPEED = builder.defineInRange("crawlerMaxSpeed", 0.27D, 0.01D, 2.0D);
        builder.pop();

        builder.push("zombie_power");
        HEALTH_POWER_BASE = builder.defineInRange("healthPowerBase", 1.015D, 1.0D, 2.0D);
        MAX_HEALTH_POWER = builder.defineInRange("maxHealthPower", 6.0D, 1.0D, 100.0D);
        DAMAGE_PER_DAY = builder.defineInRange("damagePerDay", 0.025D, 0.0D, 10.0D);
        MAX_DAMAGE_POWER = builder.defineInRange("maxDamagePower", 5.0D, 1.0D, 100.0D);
        builder.pop();

        SERVER_SPEC = builder.build();
    }

    private ZapocConfig() {
    }
}
