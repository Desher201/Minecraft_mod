package com.zapoc.bed;

import com.zapoc.horde.HordeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BedDropCleanupEvents {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {

        if (event.getWorld().isClientSide())
            return;

        if (!(event.getWorld() instanceof ServerLevel level))
            return;

        if (!(event.getEntity() instanceof ItemEntity itemEntity))
            return;

        ItemStack stack = itemEntity.getItem();

        if (!stack.is(ItemTags.BEDS))
            return;

        if (HordeManager.isHordeActive()) {
            removeItem(event, itemEntity);
            return;
        }

        BlockPos itemPos = itemEntity.blockPosition();

        if (BedNoDropHelper.shouldRemoveBedDrop(level, itemPos)) {
            removeItem(event, itemEntity);
        }
    }

    private static void removeItem(EntityJoinWorldEvent event, ItemEntity itemEntity) {

        event.setCanceled(true);
        itemEntity.discard();
    }
}