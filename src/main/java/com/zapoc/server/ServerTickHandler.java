package com.zapoc.server;

import com.zapoc.ai.ZombieAIEvents;
import com.zapoc.network.HudSyncPacket;
import com.zapoc.network.NetworkHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ServerTickHandler {

    private static int counter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        counter++;
        if (counter < 20) return;
        counter = 0;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        ServerLevel level = server.overworld();
        Scoreboard scoreboard = level.getScoreboard();

        int day = ZombieAIEvents.getGlobalScore(scoreboard, "#day");
        int daysLeft = ZombieAIEvents.getGlobalScore(scoreboard, "#daysleft");
        boolean hordeNight = ZombieAIEvents.getGlobalScore(scoreboard, "#horde_night") >= 1;
        boolean hardcore = ZombieAIEvents.getGlobalScore(scoreboard, "#global_hardcore") >= 1;

        HudSyncPacket packet = new HudSyncPacket(day, daysLeft, hordeNight, hardcore);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }
}
