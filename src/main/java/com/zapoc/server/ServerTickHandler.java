package com.zapoc.server;

import com.zapoc.bed.BedChunkLoader;
import com.zapoc.bed.BedManager;
import com.zapoc.bed.BedPersistenceManager;
import com.zapoc.config.ZapocConfig;
import com.zapoc.horde.HordeGroupManager;
import com.zapoc.horde.HordeManager;
import com.zapoc.horde.HordeNightEventManager;
import com.zapoc.horde.HordeWaveSpawner;
import com.zapoc.message.ApocalypseMessageManager;
import com.zapoc.network.HudSyncPacket;
import com.zapoc.network.NetworkHandler;
import com.zapoc.roaming.RoamingGroupManager;
import com.zapoc.roaming.RoamingGroupSpawner;
import com.zapoc.zombie.ZombiePowerSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BedBlock;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber
public class ServerTickHandler {

    private static int tickCounter = 0;
    private static boolean loaded = false;
    private static int lastZombieDay = -1;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server == null)
            return;

        if (!loaded) {
            BedPersistenceManager.loadBed(server.overworld());
            loaded = true;
        }

        if (HordeManager.isHordeActive()) {
            HordeWaveSpawner.tick(server);
        }

        tickCounter++;

        if (tickCounter < 5)
            return;

        tickCounter = 0;

        if (BedManager.hasBed()) {

            BlockPos pos = BedManager.getBedPos();
            ServerLevel level = server.getLevel(BedManager.getDimension());

            if (level == null) {

                ApocalypseMessageManager.sendBedDestroyedMessage(server.overworld());
                BedManager.removeBed();

            } else if (level.isLoaded(pos)) {

                if (!(level.getBlockState(pos).getBlock() instanceof BedBlock)) {

                    BedChunkLoader.unloadChunks(level);
                    ApocalypseMessageManager.sendBedDestroyedMessage(level);
                    BedManager.removeBed();
                    BedPersistenceManager.saveBed(level);
                }
            }

        } else {

            BedManager.setHardcore(true);
        }

        int day = HordeManager.calculateDay(server.overworld().getDayTime());

        HordeManager.setCurrentDay(day);

        int daysLeft = HordeManager.getDaysUntilNextHorde();
        long time = server.overworld().getDayTime() % 24000L;

        ApocalypseMessageManager.sendDayBeforeHordeWarning(server.overworld(), day, daysLeft);

        if (HordeManager.isForcedHorde()) {
            if (!HordeManager.isHordeActive()) {
                HordeManager.startHorde();
            }
            holdNightIfNeeded(server, time);
        } else {
            if (HordeManager.isHordeActive()) {
                if (!BedManager.hasBed() || HordeWaveSpawner.isFinished()) {
                    HordeManager.stopHorde();
                } else {
                    holdNightIfNeeded(server, time);
                }
            } else if (daysLeft == 1 && time >= 13000 && !HordeManager.isScheduledHordeSuppressedForDay(day)) {
                HordeManager.startHorde();
            } else {
                HordeManager.stopHorde();
            }
        }

        boolean hordeNight = HordeManager.isHordeActive();

        if (hordeNight) {

            HordeGroupManager.cleanupDeadZombies();
            HordeGroupManager.tickGroups();
        }

        if (day != lastZombieDay) {

            ZombiePowerSystem.applyToWorld(server.overworld(), day);
            lastZombieDay = day;
        }

        RoamingGroupSpawner.tick(server);

        if (BedManager.isHardcore()) {
            ApocalypseMessageManager.sendHardcoreWarning(server);
        }

        NetworkHandler.CHANNEL.send(
                PacketDistributor.ALL.noArg(),
                new HudSyncPacket(
                        day,
                        daysLeft,
                        hordeNight,
                        BedManager.isHardcore(),
                        BedManager.hasBed(),
                        HordeNightEventManager.getDisplayName(),
                        HordeWaveSpawner.getCurrentWave(),
                        HordeWaveSpawner.getMaxWavesForCurrentDay(),
                        HordeManager.isForcedHorde(),
                        RoamingGroupManager.getActiveCount()
                )
        );
    }

    private static void holdNightIfNeeded(MinecraftServer server, long localTime) {

        if (!ZapocConfig.HOLD_NIGHT_DURING_HORDE.get())
            return;

        if (localTime >= 13000L && localTime < 23000L)
            return;

        ServerLevel level = server.overworld();
        long dayStart = level.getDayTime() - localTime;
        long heldTime = dayStart + ZapocConfig.HORDE_HELD_NIGHT_TIME.get();
        level.setDayTime(heldTime);
    }

    public static void resetLoadedFlag() {

        loaded = false;
        lastZombieDay = -1;

        HordeWaveSpawner.stop();
        HordeGroupManager.clear();
        RoamingGroupSpawner.reset();
        RoamingGroupManager.clear();
        ApocalypseMessageManager.resetBaseState();
    }
}
