package com.zapoc.ai;

import com.zapoc.horde.HordeGroupManager;
import com.zapoc.horde.HordeManager;
import com.zapoc.zombie.ZombieRoleAI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

public class ZombieAIEvents {

    private static final Set<ResourceLocation> TARGET_TYPES = new HashSet<>();

    static {

        TARGET_TYPES.add(new ResourceLocation("minecraft", "zombie"));
        TARGET_TYPES.add(new ResourceLocation("minecraft", "husk"));
        TARGET_TYPES.add(new ResourceLocation("minecraft", "drowned"));
        TARGET_TYPES.add(new ResourceLocation("minecraft", "zombie_villager"));

        String[] zombieExtreme = {
                "assasin_black_ops",
                "bomber",
                "boomer",
                "chainsaw",
                "clicker",
                "crawler",
                "demolisher",
                "devastated",
                "faceless",
                "fetus",
                "goon",
                "infected",
                "infected_hazmat",
                "infected_juggernaut",
                "infected_military",
                "infected_police",
                "inflated",
                "military",
                "night_hunter",
                "parasite",
                "patient_zero",
                "rat_king",
                "revived",
                "runner"
        };

        for (String id : zombieExtreme) {
            TARGET_TYPES.add(new ResourceLocation("zombie_extreme", id));
        }
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinWorldEvent event) {

        if (event.getWorld().isClientSide())
            return;

        if (!(event.getEntity() instanceof Mob mob))
            return;

        if (!isTargetMob(mob))
            return;

        mob.goalSelector.addGoal(2, new GoToBedGoal(mob));

        if (HordeManager.isHordeActive()) {
            HordeGroupManager.addZombie(mob);
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {

        if (event.getEntityLiving().level.isClientSide())
            return;

        if (!(event.getEntityLiving() instanceof Mob mob))
            return;

        if (!isTargetMob(mob))
            return;

        protectFromSun(mob);

        ZombieRoleAI.tick(mob);

        if (!HordeManager.isHordeActive())
            return;

        HordeGroupManager.addZombie(mob);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {

        if (event.getEntityLiving().level.isClientSide())
            return;

        if (!(event.getEntityLiving() instanceof Mob mob))
            return;

        if (!isTargetMob(mob))
            return;

        if (!event.getSource().isFire())
            return;

        if (!isInSun(mob))
            return;

        event.setCanceled(true);
        mob.clearFire();
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {

        if (event.getEntityLiving().level.isClientSide())
            return;

        if (!(event.getEntityLiving() instanceof Mob mob))
            return;

        if (!isTargetMob(mob))
            return;

        HordeGroupManager.removeZombie(mob);
    }

    private static void protectFromSun(Mob mob) {

        if (!isInSun(mob))
            return;

        mob.clearFire();
    }

    private static boolean isInSun(Mob mob) {

        Level level = mob.level;

        if (!level.isDay())
            return false;

        if (level.isRaining())
            return false;

        if (!level.canSeeSky(mob.blockPosition()))
            return false;

        return true;
    }

    private static boolean isTargetMob(Mob mob) {

        ResourceLocation id = ForgeRegistries.ENTITIES.getKey(mob.getType());

        if (id == null)
            return false;

        return TARGET_TYPES.contains(id);
    }
}