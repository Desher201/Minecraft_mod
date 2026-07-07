package com.zapoc.message;

import com.zapoc.config.ZapocConfig;
import com.zapoc.horde.HordeNightEventManager;
import com.zapoc.horde.HordeNightType;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ApocalypseMessageManager {

    private static int lastDayBeforeWarningDay = -1;
    private static boolean bedDestroyedMessageSent = false;
    private static final Set<UUID> HARDCORE_WARNING_SENT = new HashSet<>();

    public static void resetBaseState() {
        bedDestroyedMessageSent = false;
        HARDCORE_WARNING_SENT.clear();
    }

    public static void sendDayBeforeHordeWarning(ServerLevel level, int day, int daysUntilHorde) {

        if (!messagesEnabled())
            return;

        if (daysUntilHorde != 1)
            return;

        if (lastDayBeforeWarningDay == day)
            return;

        lastDayBeforeWarningDay = day;

        sendChat(level, "The infected are gathering...");
        sendChat(level, "The air feels wrong...");
        sendChat(level, "A horde is coming soon.");
    }

    public static void sendHordeStartMessages(MinecraftServer server) {

        if (!messagesEnabled() || server == null)
            return;

        HordeNightType type = HordeNightEventManager.getCurrentType();

        if (type == HordeNightType.FINAL_HORDE) {
            sendTitle(server, "FINAL HORDE", "Survive the night", 10, 70, 20);
            sendChat(server, "The final horde has begun. Everything is coming.");
            return;
        }

        sendTitle(server, "HORDE IS COMING", "Protect the bed", 10, 60, 20);
        sendChat(server, "The horde has begun. Protect the bed.");
        sendChat(server, getHordeTypeMessage(type));
    }

    public static void sendHordeWaveMessage(ServerLevel level, int wave, int maxWaves) {

        if (!messagesEnabled())
            return;

        String prefix = HordeNightEventManager.isFinalHorde() ? "Final Horde - " : "";
        String message = prefix + "Wave " + wave + " is approaching...";

        if (maxWaves > 0) {
            message = message + " (" + wave + "/" + maxWaves + ")";
        }

        sendChat(level, message);
        sendActionBar(level, message);
    }

    public static void sendFinalWaveMessage(ServerLevel level) {

        if (!messagesEnabled())
            return;

        String subtitle = HordeNightEventManager.isFinalHorde()
                ? "The apocalypse is ending tonight"
                : "Survive the last attack";

        sendTitle(level, "FINAL WAVE", subtitle, 10, 70, 20);
        sendChat(level, "Final wave is approaching.");
    }

    public static void sendBedDestroyedMessage(ServerLevel level) {

        if (!messagesEnabled())
            return;

        if (bedDestroyedMessageSent)
            return;

        bedDestroyedMessageSent = true;

        sendTitle(level, "THE BASE HAS FALLEN", "Hardcore mode enabled", 10, 80, 30);
        sendChat(level, "The bed has been destroyed. The world has entered hardcore mode.");
    }

    public static void sendHardcoreWarning(MinecraftServer server) {

        if (!messagesEnabled() || server == null)
            return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {

            if (player.isSpectator())
                continue;

            if (HARDCORE_WARNING_SENT.contains(player.getUUID()))
                continue;

            HARDCORE_WARNING_SENT.add(player.getUUID());
            sendChat(player, "Hardcore mode is active. There is no base left.");
        }
    }

    public static void sendHardcoreDeathMessage(ServerPlayer player) {

        if (!messagesEnabled())
            return;

        if (!ZapocConfig.HARDCORE_DEATH_MESSAGES_ENABLED.get())
            return;

        sendTitle(player, "YOU FAILED TO SURVIVE", "The apocalypse claimed another survivor", 10, 80, 30);

        MinecraftServer server = player.getServer();

        if (server != null) {
            sendChat(server, player.getName().getString() + " failed to survive the apocalypse.");
        }
    }

    public static void sendTestTitle(ServerPlayer player) {
        sendTitle(player, "ZAPOC TEST", "Title messages are working", 10, 60, 20);
    }

    private static String getHordeTypeMessage(HordeNightType type) {

        switch (type) {
            case BREAKER_SIEGE:
                return "Breakers are coming. Reinforce the walls.";
            case RUNNER_RUSH:
                return "Runners are rushing the base.";
            case CRAWLER_SWARM:
                return "Crawlers are climbing.";
            case TANK_PUSH:
                return "Tanks are pushing forward.";
            case HUNTER_NIGHT:
                return "Hunters are stalking the defenders.";
            case MIXED:
            default:
                return "The infected are attacking.";
        }
    }

    private static boolean messagesEnabled() {
        return ZapocConfig.HORDE_MESSAGES_ENABLED.get();
    }

    private static void sendTitle(MinecraftServer server, String title, String subtitle, int fadeIn, int stay, int fadeOut) {

        if (!ZapocConfig.HORDE_TITLE_MESSAGES_ENABLED.get())
            return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    private static void sendTitle(ServerLevel level, String title, String subtitle, int fadeIn, int stay, int fadeOut) {

        if (!ZapocConfig.HORDE_TITLE_MESSAGES_ENABLED.get())
            return;

        for (ServerPlayer player : level.players()) {
            sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    private static void sendTitle(ServerPlayer player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {

        if (!ZapocConfig.HORDE_TITLE_MESSAGES_ENABLED.get())
            return;

        if (!isMessagePlayer(player))
            return;

        player.connection.send(new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut));
        player.connection.send(new ClientboundSetTitleTextPacket(new TextComponent(title)));
        player.connection.send(new ClientboundSetSubtitleTextPacket(new TextComponent(subtitle)));
    }

    private static void sendActionBar(ServerLevel level, String message) {

        if (!ZapocConfig.HORDE_CHAT_MESSAGES_ENABLED.get())
            return;

        for (ServerPlayer player : level.players()) {

            if (!isMessagePlayer(player))
                continue;

            player.displayClientMessage(new TextComponent(message), true);
        }
    }

    private static void sendChat(MinecraftServer server, String message) {

        if (!ZapocConfig.HORDE_CHAT_MESSAGES_ENABLED.get())
            return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendChat(player, message);
        }
    }

    private static void sendChat(ServerLevel level, String message) {

        if (!ZapocConfig.HORDE_CHAT_MESSAGES_ENABLED.get())
            return;

        for (ServerPlayer player : level.players()) {
            sendChat(player, message);
        }
    }

    private static void sendChat(ServerPlayer player, String message) {

        if (!isMessagePlayer(player))
            return;

        player.sendMessage(new TextComponent(message), Util.NIL_UUID);
    }

    private static boolean isMessagePlayer(ServerPlayer player) {
        return player != null && !player.isSpectator();
    }
}
