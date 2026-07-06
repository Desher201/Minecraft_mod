package com.zapoc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.zapoc.bed.BedManager;
import com.zapoc.horde.HordeGroup;
import com.zapoc.horde.HordeGroupManager;
import com.zapoc.horde.HordeManager;
import com.zapoc.horde.HordeNightEventManager;
import com.zapoc.horde.HordeWaveSpawner;
import com.zapoc.spawn.ZapocSpawnPositionHelper;
import com.zapoc.zombie.ZombieAIController;
import com.zapoc.zombie.ZombiePowerSystem;
import com.zapoc.zombie.ZombieType;
import com.zapoc.zombie.ZombieTypeApplier;
import com.zapoc.zombie.ZombieTypeManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.Random;

public class ZapocCommand {

    private static final Random RANDOM = new Random();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("zapoc")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("info")
                                .executes(ctx -> info(ctx.getSource())))
                        .then(Commands.literal("horde")
                                .then(Commands.literal("start")
                                        .executes(ctx -> hordeStart(ctx.getSource())))
                                .then(Commands.literal("stop")
                                        .executes(ctx -> hordeStop(ctx.getSource())))
                                .then(Commands.literal("info")
                                        .executes(ctx -> hordeInfo(ctx.getSource())))
                                .then(Commands.literal("clear")
                                        .executes(ctx -> hordeClear(ctx.getSource()))))
                        .then(Commands.literal("day")
                                .then(Commands.literal("get")
                                        .executes(ctx -> dayGet(ctx.getSource())))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("day", IntegerArgumentType.integer(1))
                                                .executes(ctx -> daySet(
                                                        ctx.getSource(),
                                                        IntegerArgumentType.getInteger(ctx, "day")
                                                )))))
                        .then(Commands.literal("bed")
                                .then(Commands.literal("info")
                                        .executes(ctx -> bedInfo(ctx.getSource()))))
                        .then(Commands.literal("spawn")
                                .then(spawnType("normal", ZombieType.NORMAL))
                                .then(spawnType("runner", ZombieType.RUNNER))
                                .then(spawnType("tank", ZombieType.TANK))
                                .then(spawnType("hunter", ZombieType.HUNTER))
                                .then(spawnType("breaker", ZombieType.BREAKER))
                                .then(spawnType("crawler", ZombieType.CRAWLER)))
        );
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> spawnType(String name, ZombieType type) {

        return Commands.literal(name)
                .executes(ctx -> spawn(ctx.getSource(), type, 1))
                .then(Commands.argument("count", IntegerArgumentType.integer(1, 100))
                        .executes(ctx -> spawn(
                                ctx.getSource(),
                                type,
                                IntegerArgumentType.getInteger(ctx, "count")
                        )));
    }

    private static int info(CommandSourceStack source) {

        send(source, "Zapoc info:");
        send(source, "Day: " + HordeManager.getCurrentDay());
        send(source, "Days until next horde: " + HordeManager.getDaysUntilNextHorde());
        send(source, "Horde active: " + HordeManager.isHordeActive());
        send(source, "Bed exists: " + BedManager.hasBed());
        send(source, "Hardcore active: " + BedManager.isHardcore());

        return 1;
    }

    private static int hordeStart(CommandSourceStack source) {

        HordeManager.forceStartHorde();
        send(source, "Forced horde started.");

        return 1;
    }

    private static int hordeStop(CommandSourceStack source) {

        HordeManager.forceStopHorde();
        send(source, "Forced horde stopped.");

        return 1;
    }

    private static int hordeInfo(CommandSourceStack source) {

        send(source, "Horde info:");
        send(source, "Active: " + HordeManager.isHordeActive());
        send(source, "Forced: " + HordeManager.isForcedHorde());
        send(source, "Event type: " + HordeNightEventManager.getDisplayName());
        send(source, "Horde number: " + HordeManager.getHordeNumber());
        send(source, "Group count: " + HordeGroupManager.getGroups().size());
        send(source, "Tracked active zombies: " + countTrackedHordeZombies());

        return 1;
    }

    private static int hordeClear(CommandSourceStack source) {

        int removed = removeTrackedHordeZombies();
        HordeWaveSpawner.stop();
        HordeGroupManager.clear();

        send(source, "Cleared horde groups. Removed tracked zombies: " + removed);

        return 1;
    }

    private static int dayGet(CommandSourceStack source) {

        int day = HordeManager.calculateDay(source.getLevel().getDayTime());
        HordeManager.setCurrentDay(day);
        send(source, "Current day: " + day);

        return 1;
    }

    private static int daySet(CommandSourceStack source, int day) {

        ServerLevel level = source.getLevel();
        long time = (long) (day - 1) * 24000L;

        level.setDayTime(time);
        HordeManager.setCurrentDay(HordeManager.calculateDay(level.getDayTime()));

        send(source, "Set day to " + day + " and time to " + time + ".");

        return 1;
    }

    private static int bedInfo(CommandSourceStack source) {

        send(source, "Bed info:");
        send(source, "Exists: " + BedManager.hasBed());

        BlockPos bedPos = BedManager.getBedPos();
        ResourceKey<Level> dimension = BedManager.getDimension();

        send(source, "Position: " + (bedPos == null ? "none" : bedPos.toShortString()));
        send(source, "Dimension: " + (dimension == null ? "none" : dimension.location().toString()));
        send(source, "Hardcore active: " + BedManager.isHardcore());

        return 1;
    }

    private static int spawn(CommandSourceStack source, ZombieType type, int count) {

        try {
            ServerPlayer player = source.getPlayerOrException();
            ServerLevel level = player.getLevel();
            int day = HordeManager.getCurrentDay();
            int spawned = 0;

            for (int i = 0; i < count; i++) {
                Optional<BlockPos> optionalSpawnPos = ZapocSpawnPositionHelper.findSurfaceSpawnPosition(
                        level,
                        player.blockPosition(),
                        3,
                        10 + count / 10,
                        RANDOM
                );

                if (optionalSpawnPos.isEmpty()) {
                    System.out.println("[ZApoc] Skipped debug zombie spawn: no safe surface position found.");
                    continue;
                }

                BlockPos surface = optionalSpawnPos.get();
                Zombie zombie = EntityType.ZOMBIE.create(level);

                if (zombie == null)
                    continue;

                zombie.moveTo(
                        surface.getX() + 0.5D,
                        surface.getY(),
                        surface.getZ() + 0.5D,
                        player.getYRot(),
                        0.0F
                );

                zombie.finalizeSpawn(
                        level,
                        level.getCurrentDifficultyAt(surface),
                        MobSpawnType.COMMAND,
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
                spawned++;
            }

            send(source, "Spawned " + spawned + " " + type.name().toLowerCase() + " zombie(s).");

            return spawned;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static int countTrackedHordeZombies() {

        int count = 0;

        for (HordeGroup group : HordeGroupManager.getGroups()) {
            for (net.minecraft.world.entity.Mob mob : group.getZombies()) {
                if (mob != null && mob.isAlive() && !mob.isRemoved()) {
                    count++;
                }
            }
        }

        return count;
    }

    private static int removeTrackedHordeZombies() {

        int removed = 0;

        for (HordeGroup group : HordeGroupManager.getGroups()) {
            for (net.minecraft.world.entity.Mob mob : new java.util.ArrayList<>(group.getZombies())) {
                if (mob != null && !mob.isRemoved()) {
                    mob.discard();
                    removed++;
                }
            }
        }

        return removed;
    }

    private static void send(CommandSourceStack source, String message) {

        source.sendSuccess(new TextComponent(message), false);
    }
}
