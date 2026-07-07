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
    public static final ForgeConfigSpec.BooleanValue HOLD_NIGHT_DURING_HORDE;
    public static final ForgeConfigSpec.IntValue HORDE_HELD_NIGHT_TIME;
    public static final ForgeConfigSpec.DoubleValue FINAL_WAVE_ZOMBIE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue FINAL_HORDE_FINAL_WAVE_ZOMBIE_MULTIPLIER;
    public static final ForgeConfigSpec.IntValue BED_CHUNK_LOAD_RADIUS;

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
    public static final ForgeConfigSpec.IntValue ZOMBIE_SUN_FIRE_IMMUNE_MIN_DAY;
    public static final ForgeConfigSpec.BooleanValue ZOMBIE_SUN_FIRE_CAN_IGNITE_TARGETS;
    public static final ForgeConfigSpec.BooleanValue ZOMBIE_SPAWN_ALLOW_WATER;
    public static final ForgeConfigSpec.BooleanValue HORDE_MESSAGES_ENABLED;
    public static final ForgeConfigSpec.BooleanValue HORDE_TITLE_MESSAGES_ENABLED;
    public static final ForgeConfigSpec.BooleanValue HORDE_CHAT_MESSAGES_ENABLED;
    public static final ForgeConfigSpec.BooleanValue HARDCORE_DEATH_MESSAGES_ENABLED;
    public static final ForgeConfigSpec.BooleanValue BLOCK_DROWNED_SPAWNS;

    public static final ForgeConfigSpec.BooleanValue ROAMING_GROUPS_ENABLED;
    public static final ForgeConfigSpec.BooleanValue ROAMING_GROUPS_SPAWN_DURING_HORDE;
    public static final ForgeConfigSpec.IntValue ROAMING_GROUP_SPAWN_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue ROAMING_GROUP_MAX_ACTIVE_ZOMBIES;
    public static final ForgeConfigSpec.IntValue ROAMING_GROUP_MIN_SPAWN_DISTANCE;
    public static final ForgeConfigSpec.IntValue ROAMING_GROUP_MAX_SPAWN_DISTANCE;
    public static final ForgeConfigSpec.IntValue ROAMING_GROUP_DESPAWN_DISTANCE;
    public static final ForgeConfigSpec.IntValue ROAMING_GROUP_MEMBER_SCATTER_RADIUS;
    public static final ForgeConfigSpec.IntValue ROAMING_GROUP_AGGRO_RANGE;
    public static final ForgeConfigSpec.IntValue ROAMING_GROUP_ASSIST_RANGE;
    public static final ForgeConfigSpec.IntValue ROAMING_GROUP_KEEP_TOGETHER_RANGE;
    public static final ForgeConfigSpec.IntValue ROAMING_GROUP_RALLY_RADIUS;
    public static final ForgeConfigSpec.DoubleValue ROAMING_GROUP_RALLY_SPEED;
    public static final ForgeConfigSpec.IntValue ROAMING_GROUP_TARGET_FORGET_RANGE;
    public static final ForgeConfigSpec.IntValue ROAMING_SMALL_PACK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue ROAMING_SMALL_PACK_MAX_SIZE;
    public static final ForgeConfigSpec.IntValue ROAMING_RUNNER_PACK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue ROAMING_RUNNER_PACK_MAX_SIZE;
    public static final ForgeConfigSpec.IntValue ROAMING_CRAWLER_PACK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue ROAMING_CRAWLER_PACK_MAX_SIZE;
    public static final ForgeConfigSpec.IntValue ROAMING_HEAVY_PATROL_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue ROAMING_HEAVY_PATROL_MAX_SIZE;
    public static final ForgeConfigSpec.IntValue ROAMING_HORDE_REMNANT_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue ROAMING_HORDE_REMNANT_MAX_SIZE;
    public static final ForgeConfigSpec.IntValue ROAMING_RUNNER_PACK_MIN_DAY;
    public static final ForgeConfigSpec.IntValue ROAMING_CRAWLER_PACK_MIN_DAY;
    public static final ForgeConfigSpec.IntValue ROAMING_HEAVY_PACK_MIN_DAY;
    public static final ForgeConfigSpec.IntValue ROAMING_REMNANT_PACK_MIN_DAY;
    public static final ForgeConfigSpec.IntValue ROAMING_NORMAL_WEIGHT;
    public static final ForgeConfigSpec.IntValue ROAMING_RUNNER_WEIGHT;
    public static final ForgeConfigSpec.IntValue ROAMING_HUNTER_WEIGHT;
    public static final ForgeConfigSpec.IntValue ROAMING_TANK_WEIGHT;
    public static final ForgeConfigSpec.IntValue ROAMING_BREAKER_WEIGHT;
    public static final ForgeConfigSpec.IntValue ROAMING_CRAWLER_WEIGHT;

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
        HOLD_NIGHT_DURING_HORDE = builder.define("holdNightDuringHorde", true);
        HORDE_HELD_NIGHT_TIME = builder.defineInRange("hordeHeldNightTime", 18000, 13000, 23000);
        FINAL_WAVE_ZOMBIE_MULTIPLIER = builder.defineInRange("finalWaveZombieMultiplier", 2.0D, 1.0D, 20.0D);
        FINAL_HORDE_FINAL_WAVE_ZOMBIE_MULTIPLIER = builder.defineInRange("finalHordeFinalWaveZombieMultiplier", 3.0D, 1.0D, 30.0D);
        BED_CHUNK_LOAD_RADIUS = builder.defineInRange("bedChunkLoadRadius", 2, 0, 8);
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
        ZOMBIE_SUN_FIRE_IMMUNE_MIN_DAY = builder.defineInRange("zombieSunFireImmuneMinDay", 5, 1, 10000);
        ZOMBIE_SUN_FIRE_CAN_IGNITE_TARGETS = builder.define("zombieSunFireCanIgniteTargets", false);
        ZOMBIE_SPAWN_ALLOW_WATER = builder.define("zombieSpawnAllowWater", false);
        builder.pop();

        builder.push("messages");
        HORDE_MESSAGES_ENABLED = builder.define("hordeMessagesEnabled", true);
        HORDE_TITLE_MESSAGES_ENABLED = builder.define("hordeTitleMessagesEnabled", true);
        HORDE_CHAT_MESSAGES_ENABLED = builder.define("hordeChatMessagesEnabled", true);
        HARDCORE_DEATH_MESSAGES_ENABLED = builder.define("hardcoreDeathMessagesEnabled", true);
        BLOCK_DROWNED_SPAWNS = builder.define("blockDrownedSpawns", true);
        builder.pop();

        builder.push("roaming_groups");
        ROAMING_GROUPS_ENABLED = builder.define("roamingGroupsEnabled", true);
        ROAMING_GROUPS_SPAWN_DURING_HORDE = builder.define("roamingGroupsSpawnDuringHorde", false);
        ROAMING_GROUP_SPAWN_INTERVAL_TICKS = builder.defineInRange("roamingGroupSpawnIntervalTicks", 1200, 20, 24000);
        ROAMING_GROUP_MAX_ACTIVE_ZOMBIES = builder.defineInRange("roamingGroupMaxActiveZombies", 35, 0, 500);
        ROAMING_GROUP_MIN_SPAWN_DISTANCE = builder.defineInRange("roamingGroupMinSpawnDistance", 32, 1, 512);
        ROAMING_GROUP_MAX_SPAWN_DISTANCE = builder.defineInRange("roamingGroupMaxSpawnDistance", 80, 1, 1024);
        ROAMING_GROUP_DESPAWN_DISTANCE = builder.defineInRange("roamingGroupDespawnDistance", 160, 16, 2048);
        ROAMING_GROUP_MEMBER_SCATTER_RADIUS = builder.defineInRange("roamingGroupMemberScatterRadius", 5, 0, 64);
        ROAMING_GROUP_AGGRO_RANGE = builder.defineInRange("roamingGroupAggroRange", 32, 1, 256);
        ROAMING_GROUP_ASSIST_RANGE = builder.defineInRange("roamingGroupAssistRange", 48, 1, 256);
        ROAMING_GROUP_KEEP_TOGETHER_RANGE = builder.defineInRange("roamingGroupKeepTogetherRange", 18, 1, 256);
        ROAMING_GROUP_RALLY_RADIUS = builder.defineInRange("roamingGroupRallyRadius", 6, 0, 64);
        ROAMING_GROUP_RALLY_SPEED = builder.defineInRange("roamingGroupRallySpeed", 1.0D, 0.05D, 3.0D);
        ROAMING_GROUP_TARGET_FORGET_RANGE = builder.defineInRange("roamingGroupTargetForgetRange", 80, 1, 512);
        ROAMING_SMALL_PACK_MIN_SIZE = builder.defineInRange("roamingSmallPackMinSize", 5, 1, 100);
        ROAMING_SMALL_PACK_MAX_SIZE = builder.defineInRange("roamingSmallPackMaxSize", 9, 1, 100);
        ROAMING_RUNNER_PACK_MIN_SIZE = builder.defineInRange("roamingRunnerPackMinSize", 4, 1, 100);
        ROAMING_RUNNER_PACK_MAX_SIZE = builder.defineInRange("roamingRunnerPackMaxSize", 8, 1, 100);
        ROAMING_CRAWLER_PACK_MIN_SIZE = builder.defineInRange("roamingCrawlerPackMinSize", 5, 1, 100);
        ROAMING_CRAWLER_PACK_MAX_SIZE = builder.defineInRange("roamingCrawlerPackMaxSize", 10, 1, 100);
        ROAMING_HEAVY_PATROL_MIN_SIZE = builder.defineInRange("roamingHeavyPatrolMinSize", 5, 1, 100);
        ROAMING_HEAVY_PATROL_MAX_SIZE = builder.defineInRange("roamingHeavyPatrolMaxSize", 8, 1, 100);
        ROAMING_HORDE_REMNANT_MIN_SIZE = builder.defineInRange("roamingHordeRemnantMinSize", 8, 1, 100);
        ROAMING_HORDE_REMNANT_MAX_SIZE = builder.defineInRange("roamingHordeRemnantMaxSize", 14, 1, 100);
        ROAMING_RUNNER_PACK_MIN_DAY = builder.defineInRange("roamingRunnerPackMinDay", 20, 1, 10000);
        ROAMING_CRAWLER_PACK_MIN_DAY = builder.defineInRange("roamingCrawlerPackMinDay", 40, 1, 10000);
        ROAMING_HEAVY_PACK_MIN_DAY = builder.defineInRange("roamingHeavyPackMinDay", 60, 1, 10000);
        ROAMING_REMNANT_PACK_MIN_DAY = builder.defineInRange("roamingRemnantPackMinDay", 80, 1, 10000);
        ROAMING_NORMAL_WEIGHT = builder.defineInRange("roamingNormalWeight", 60, 0, 1000);
        ROAMING_RUNNER_WEIGHT = builder.defineInRange("roamingRunnerWeight", 15, 0, 1000);
        ROAMING_HUNTER_WEIGHT = builder.defineInRange("roamingHunterWeight", 10, 0, 1000);
        ROAMING_TANK_WEIGHT = builder.defineInRange("roamingTankWeight", 5, 0, 1000);
        ROAMING_BREAKER_WEIGHT = builder.defineInRange("roamingBreakerWeight", 5, 0, 1000);
        ROAMING_CRAWLER_WEIGHT = builder.defineInRange("roamingCrawlerWeight", 5, 0, 1000);
        builder.pop();

        SERVER_SPEC = builder.build();
    }

    private ZapocConfig() {
    }
}
