package com.zapoc.zombie;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import com.zapoc.horde.HordeManager;

@Mod.EventBusSubscriber
public class ZombieSpawnEvents {

    private static final int UNDERGROUND_DEPTH = 8;
    private static final double CAVE_PLAYER_RADIUS = 48.0;

    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {

        if (!(event.getWorld() instanceof ServerLevel level))
            return;

        if (!(event.getEntity() instanceof Monster monster))
            return;

        if (event.getSpawnReason() != MobSpawnType.NATURAL)
            return;

        if (!isZombieLike(monster))
            return;

        BlockPos spawnPos = monster.blockPosition();

        if (!isDeepUnderground(level, spawnPos))
            return;

        if (!hasUndergroundPlayerNearby(level, spawnPos)) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onZombieSpawn(LivingSpawnEvent.SpecialSpawn event) {

        if (!(event.getEntity() instanceof Zombie zombie))
            return;

        if (!(event.getWorld() instanceof ServerLevel level))
            return;

        int day = HordeManager.calculateDay(level.getDayTime());

        ZombieType type = ZombieTypeSelector.getType(day);

        ZombieTypeManager.setType(zombie, type);

        ZombieTypeApplier.apply(zombie, type);

        ZombiePowerSystem.applyToZombie(zombie, day);

        zombie.setCustomName(
                new TextComponent("§c" + type.name())
        );

        zombie.setCustomNameVisible(true);

        ZombieTypeApplier.apply(zombie, type, day);
    }

    private static boolean isZombieLike(Monster monster) {

        ResourceLocation id = ForgeRegistries.ENTITIES.getKey(monster.getType());

        if (id == null)
            return false;

        String namespace = id.getNamespace();
        String path = id.getPath();

        if (namespace.equals("minecraft")) {

            return path.equals("zombie")
                    || path.equals("husk")
                    || path.equals("drowned")
                    || path.equals("zombie_villager");
        }

        return namespace.equals("zombie_extreme");
    }

    private static boolean isDeepUnderground(ServerLevel level, BlockPos pos) {

        int surfaceY = level.getHeight(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                pos.getX(),
                pos.getZ()
        );

        return surfaceY - pos.getY() >= UNDERGROUND_DEPTH;
    }

    private static boolean hasUndergroundPlayerNearby(ServerLevel level, BlockPos spawnPos) {

        double maxDistanceSqr = CAVE_PLAYER_RADIUS * CAVE_PLAYER_RADIUS;

        for (ServerPlayer player : level.players()) {

            if (player.distanceToSqr(
                    spawnPos.getX() + 0.5,
                    spawnPos.getY() + 0.5,
                    spawnPos.getZ() + 0.5
            ) > maxDistanceSqr) {
                continue;
            }

            if (isDeepUnderground(level, player.blockPosition())) {
                return true;
            }
        }

        return false;
    }
}
