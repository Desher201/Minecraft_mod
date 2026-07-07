package com.zapoc.zombie;

import com.zapoc.config.ZapocConfig;
import com.zapoc.horde.HordeManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ZombieEnvironmentalPowerEvents {

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {

        LivingEntity entity = event.getEntityLiving();

        if (entity.level.isClientSide())
            return;

        if (!isZapocZombie(entity))
            return;

        if (!isSunFireImmune(entity.level))
            return;

        clearFire(entity);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {

        LivingEntity entity = event.getEntityLiving();

        if (entity.level.isClientSide())
            return;

        if (isZapocZombie(entity) && event.getSource().isFire() && isSunFireImmune(entity.level)) {
            event.setCanceled(true);
            clearFire(entity);
            return;
        }

        if (!(entity instanceof Player player))
            return;

        if (ZapocConfig.ZOMBIE_SUN_FIRE_CAN_IGNITE_TARGETS.get())
            return;

        if (isZapocZombie(event.getSource().getEntity())) {
            clearFire(player);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {

        LivingEntity entity = event.getEntityLiving();

        if (entity.level.isClientSide())
            return;

        if (!(entity instanceof Player player))
            return;

        if (ZapocConfig.ZOMBIE_SUN_FIRE_CAN_IGNITE_TARGETS.get())
            return;

        if (isZapocZombie(event.getSource().getEntity())) {
            clearFire(player);
        }
    }

    private static boolean isSunFireImmune(Level level) {

        if (HordeManager.isHordeActive())
            return true;

        int day = HordeManager.calculateDay(level.getDayTime());

        return day >= ZapocConfig.ZOMBIE_SUN_FIRE_IMMUNE_MIN_DAY.get();
    }

    private static boolean isZapocZombie(Object entity) {

        if (!(entity instanceof LivingEntity livingEntity))
            return false;

        return ZombieTypeManager.isManaged(livingEntity);
    }

    private static void clearFire(LivingEntity entity) {
        entity.clearFire();
        entity.setRemainingFireTicks(0);
    }
}
