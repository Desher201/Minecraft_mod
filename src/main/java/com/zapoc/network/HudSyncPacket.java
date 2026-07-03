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

    public HudSyncPacket(int day, int daysLeft, boolean hordeNight, boolean hardcore) {
        this.day = day;
        this.daysLeft = daysLeft;
        this.hordeNight = hordeNight;
        this.hardcore = hardcore;
    }

    public static void encode(HudSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.day);
        buf.writeInt(msg.daysLeft);
        buf.writeBoolean(msg.hordeNight);
        buf.writeBoolean(msg.hardcore);
    }

    public static HudSyncPacket decode(FriendlyByteBuf buf) {
        return new HudSyncPacket(buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readBoolean());
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
    }
}
