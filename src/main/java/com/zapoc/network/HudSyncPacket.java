package com.zapoc.network;

import com.zapoc.client.ClientHudData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HudSyncPacket {
    public final int day;
    public final int daysLeft;
    public final boolean hordeNight;
    public final boolean hardcore;
    public final boolean hasBed;
    public final String hordeNightType;
    public final int currentWave;
    public final int maxWaves;
    public final boolean forcedHorde;
    public final int activeRoamingZombies;

    public HudSyncPacket(
            int day,
            int daysLeft,
            boolean hordeNight,
            boolean hardcore,
            boolean hasBed,
            String hordeNightType,
            int currentWave,
            int maxWaves,
            boolean forcedHorde,
            int activeRoamingZombies
    ) {
        this.day = day;
        this.daysLeft = daysLeft;
        this.hordeNight = hordeNight;
        this.hardcore = hardcore;
        this.hasBed = hasBed;
        this.hordeNightType = hordeNightType;
        this.currentWave = currentWave;
        this.maxWaves = maxWaves;
        this.forcedHorde = forcedHorde;
        this.activeRoamingZombies = activeRoamingZombies;
    }

    public static void encode(HudSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.day);
        buf.writeInt(msg.daysLeft);
        buf.writeBoolean(msg.hordeNight);
        buf.writeBoolean(msg.hardcore);
        buf.writeBoolean(msg.hasBed);
        buf.writeUtf(msg.hordeNightType);
        buf.writeInt(msg.currentWave);
        buf.writeInt(msg.maxWaves);
        buf.writeBoolean(msg.forcedHorde);
        buf.writeInt(msg.activeRoamingZombies);
    }

    public static HudSyncPacket decode(FriendlyByteBuf buf) {
        return new HudSyncPacket(
                buf.readInt(),
                buf.readInt(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readUtf(),
                buf.readInt(),
                buf.readInt(),
                buf.readBoolean(),
                buf.readInt()
        );
    }

    public static void handle(HudSyncPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> handleClient(msg));
        ctx.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(HudSyncPacket msg) {
        ClientHudData.day = msg.day;
        ClientHudData.daysLeft = msg.daysLeft;
        ClientHudData.hordeNight = msg.hordeNight;
        ClientHudData.hardcore = msg.hardcore;
        ClientHudData.hasBed = msg.hasBed;
        ClientHudData.hordeNightType = msg.hordeNightType;
        ClientHudData.currentWave = msg.currentWave;
        ClientHudData.maxWaves = msg.maxWaves;
        ClientHudData.forcedHorde = msg.forcedHorde;
        ClientHudData.activeRoamingZombies = msg.activeRoamingZombies;
    }
}
