package com.zapoc.server;
import com.zapoc.bed.BedChunkLoader;
import com.zapoc.bed.BedManager;
import com.zapoc.network.HudSyncPacket;
import com.zapoc.network.NetworkHandler;
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

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
            return;

        // Проверяем раз в 5 тиков
        tickCounter++;
        if (tickCounter < 5)
            return;

        tickCounter = 0;

        // ==========================
        // Проверка общей кровати
        // ==========================

        if (BedManager.hasBed()) {

            BlockPos pos = BedManager.getBedPos();
            ServerLevel level = server.getLevel(BedManager.getDimension());

            if (level == null) {

                BedManager.removeBed();

            } else if (level.isLoaded(pos)) {

                if (!(level.getBlockState(pos).getBlock() instanceof BedBlock)) {
                    BedChunkLoader.unloadChunks((ServerLevel) level);
                    BedManager.removeBed();
                    BedManager.removeBed();

                }

            }

        } else {

            BedManager.setHardcore(true);

        }

        // ==========================
        // HUD DATA
        // ==========================

        int day = (int) (server.overworld().getDayTime() / 24000L) + 1;

        int daysLeft = 10 - ((day - 1) % 10);

        boolean hordeNight = false;

        long time = server.overworld().getDayTime() % 24000L;

        if (daysLeft == 1 && time >= 13000) {
            hordeNight = true;
        }

        // ==========================
        // Отправляем HUD
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
}