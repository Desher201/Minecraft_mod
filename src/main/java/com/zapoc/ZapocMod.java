package com.zapoc;

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

        NetworkHandler.register();

        MinecraftForge.EVENT_BUS.register(ZombieAIEvents.class);
        MinecraftForge.EVENT_BUS.register(ServerTickHandler.class);

        // Регистрируем систему кровати
        MinecraftForge.EVENT_BUS.register(new BedEvents());

        if (FMLEnvironment.dist == Dist.CLIENT) {

            System.out.println("===== CLIENT =====");

            HudOverlay overlay = new HudOverlay();

            MinecraftForge.EVENT_BUS.register(overlay);

            System.out.println("===== HUD REGISTERED =====");
        }
    }
}
