package com.zapoc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.zapoc.zombie.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;

public class ZapocCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(

                Commands.literal("zapoc")

                        .then(
                                Commands.literal("spawn")

                                        .then(
                                                Commands.literal("normal")
                                                        .executes(ctx -> spawn(ctx.getSource(), ZombieType.NORMAL))
                                        )

                                        .then(
                                                Commands.literal("runner")
                                                        .executes(ctx -> spawn(ctx.getSource(), ZombieType.RUNNER))
                                        )

                                        .then(
                                                Commands.literal("tank")
                                                        .executes(ctx -> spawn(ctx.getSource(), ZombieType.TANK))
                                        )

                                        .then(
                                                Commands.literal("hunter")
                                                        .executes(ctx -> spawn(ctx.getSource(), ZombieType.HUNTER))
                                        )

                                        .then(
                                                Commands.literal("breaker")
                                                        .executes(ctx -> spawn(ctx.getSource(), ZombieType.BREAKER))
                                        )

                        )

        );

    }

    private static int spawn(CommandSourceStack source, ZombieType type) {

        try {

            ServerPlayer player = source.getPlayerOrException();
            ServerLevel level = player.getLevel();

            Zombie zombie = EntityType.ZOMBIE.create(level);

            if (zombie == null)
                return 0;

            zombie.moveTo(
                    player.getX(),
                    player.getY(),
                    player.getZ() + 3,
                    player.getYRot(),
                    0
            );

            int day = (int) (level.getDayTime() / 24000L) + 1;

            ZombieTypeManager.setType(zombie, type);

            ZombieTypeApplier.apply(zombie, type);

            ZombiePowerSystem.applyToZombie(zombie, day);

            ZombieAIController.applyAI(zombie, type);

            zombie.setCustomName(new TextComponent("§c" + type.name()));
            zombie.setCustomNameVisible(true);

            level.addFreshEntity(zombie);

            source.sendSuccess(
                    new TextComponent("Spawned " + type.name()),
                    false
            );

            return 1;

        } catch (Exception e) {

            e.printStackTrace();

            return 0;
        }

    }

}
