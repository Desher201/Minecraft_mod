package com.zapoc;

import com.zapoc.hardcore.HardcoreEvents;
import com.zapoc.ai.ZombieAIEvents;
import com.zapoc.bed.BedEvents;
import com.zapoc.client.HudOverlay;
import com.zapoc.network.NetworkHandler;
import com.zapoc.server.ServerTickHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("zapocmod")
public class ZapocMod {

    public ZapocMod() {

        System.out.println("===== ZAPOC MOD START =====");

        // Регистрация сети
        NetworkHandler.register();

        // Серверные события
        MinecraftForge.EVENT_BUS.register(ZombieAIEvents.class);
        MinecraftForge.EVENT_BUS.register(ServerTickHandler.class);
        MinecraftForge.EVENT_BUS.register(BedEvents.class);
        MinecraftForge.EVENT_BUS.register(HardcoreEvents.class);

        // Клиентские события
        if (FMLEnvironment.dist == Dist.CLIENT) {

            System.out.println("===== CLIENT =====");

            MinecraftForge.EVENT_BUS.register(new HudOverlay());

            System.out.println("===== HUD REGISTERED =====");
        }
    }
}