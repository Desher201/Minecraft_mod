package com.zapoc.hardcore;

import com.zapoc.bed.BedManager;
import com.zapoc.client.DeathScreen;
import com.zapoc.message.ApocalypseMessageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class HardcoreEvents {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {

        if (!(event.getEntityLiving() instanceof ServerPlayer player))
            return;

        if (!BedManager.isHardcore())
            return;

        System.out.println("[ZApoc] Hardcore death: " + player.getName().getString());
        ApocalypseMessageManager.sendHardcoreDeathMessage(player);

        // Пока просто переводим в режим наблюдателя
        player.setGameMode(net.minecraft.world.level.GameType.SPECTATOR);

        // Потом сюда добавим отправку пакета,
        // чтобы открыть экран YOU DIED.
    }
}
