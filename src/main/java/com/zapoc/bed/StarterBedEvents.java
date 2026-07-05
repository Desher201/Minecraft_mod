package com.zapoc.bed;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class StarterBedEvents {

    private static final String STARTER_BED_GIVEN_TAG = "ZapocStarterBedGiven";

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {

        if (!(event.getPlayer() instanceof ServerPlayer player))
            return;

        if (BedManager.isHardcore())
            return;

        if (BedManager.hasBed())
            return;

        if (player.getPersistentData().getBoolean(STARTER_BED_GIVEN_TAG))
            return;

        if (hasAnyBed(player)) {
            player.getPersistentData().putBoolean(STARTER_BED_GIVEN_TAG, true);
            return;
        }

        ItemStack bed = new ItemStack(Items.RED_BED);

        boolean added = player.getInventory().add(bed);

        if (!added) {
            player.drop(bed, false);
        }

        player.getPersistentData().putBoolean(STARTER_BED_GIVEN_TAG, true);
    }

    private static boolean hasAnyBed(ServerPlayer player) {

        for (ItemStack stack : player.getInventory().items) {

            if (stack.is(ItemTags.BEDS)) {
                return true;
            }
        }

        return false;
    }
}