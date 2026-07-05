package com.zapoc.server;

import com.zapoc.bed.BedChunkLoader;
import com.zapoc.bed.BedManager;
import com.zapoc.bed.BedPersistenceManager;
import com.zapoc.horde.HordeManager;
import com.zapoc.network.HudSyncPacket;
import com.zapoc.network.NetworkHandler;
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

    // Чтобы не спамить усилением каждый тик
    private static int lastZombieDay = -1;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
            return;

        // ==========================
        // Загрузка BedSavedData
        // ==========================
        if (!loaded) {
            BedPersistenceManager.loadBed(server.overworld());
            loaded = true;
        }

        // ==========================
        // Тик-логика
        // ==========================
        tickCounter++;

        if (tickCounter < 5)
            return;

        tickCounter = 0;

        // ==========================
        // Проверка существования кровати
        // ==========================
        if (BedManager.hasBed()) {

            BlockPos pos = BedManager.getBedPos();
            ServerLevel level = server.getLevel(BedManager.getDimension());

            if (level == null) {

                BedManager.removeBed();

            } else if (level.isLoaded(pos)) {

                if (!(level.getBlockState(pos).getBlock() instanceof BedBlock)) {

                    BedChunkLoader.unloadChunks(level);
                    BedManager.removeBed();
                    BedPersistenceManager.saveBed(level);

                }
            }

        } else {

            BedManager.setHardcore(true);

        }

        // ==========================
        // День
        // ==========================
        int day = (int) (server.overworld().getDayTime() / 24000L);

        HordeManager.setCurrentDay(day);

        int daysLeft = 10 - ((day - 1) % 10);

        long time = server.overworld().getDayTime() % 24000L;

        // ==========================
        // Орда
        // ==========================
        if (daysLeft == 1 && time >= 13000) {

            HordeManager.startHorde();

        } else {

            HordeManager.stopHorde();

        }

        boolean hordeNight = HordeManager.isHordeActive();

        // ==========================
        // Усиление зомби
        // ==========================
        if (day != lastZombieDay) {

            ZombiePowerSystem.applyToWorld(server.overworld(), day);
            lastZombieDay = day;

        }

        // ==========================
        // HUD
        // ==========================
        NetworkHandler.CHANNEL.send(
                PacketDistributor.ALL.noArg(),
                new HudSyncPacket(
                        day,
                        daysLeft,
                        hordeNight,
                        BedManager.isHardcore()
                )
        );
    }

    public static void resetLoadedFlag() {

        loaded = false;
        lastZombieDay = -1;

    }
}