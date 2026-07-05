package com.zapoc.horde;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class BlockLevelSystem {

    private static final Map<ResourceLocation, Integer> BLOCK_LEVELS = new HashMap<>();

    static {

        register("minecraft:dirt", 1);
        register("minecraft:grass_block", 1);
        register("minecraft:coarse_dirt", 1);
        register("minecraft:sand", 1);
        register("minecraft:gravel", 1);
        register("minecraft:clay", 1);
        register("minecraft:glass", 1);

        register("minecraft:oak_planks", 2);
        register("minecraft:spruce_planks", 2);
        register("minecraft:birch_planks", 2);
        register("minecraft:jungle_planks", 2);
        register("minecraft:acacia_planks", 2);
        register("minecraft:dark_oak_planks", 2);

        register("minecraft:oak_log", 2);
        register("minecraft:spruce_log", 2);
        register("minecraft:birch_log", 2);
        register("minecraft:jungle_log", 2);
        register("minecraft:acacia_log", 2);
        register("minecraft:dark_oak_log", 2);

        register("minecraft:stone", 3);
        register("minecraft:cobblestone", 3);
        register("minecraft:stone_bricks", 3);
        register("minecraft:bricks", 3);
        register("minecraft:sandstone", 3);
        register("minecraft:andesite", 3);
        register("minecraft:diorite", 3);
        register("minecraft:granite", 3);

        register("minecraft:deepslate", 4);
        register("minecraft:cobbled_deepslate", 4);
        register("minecraft:polished_deepslate", 4);
        register("minecraft:iron_block", 4);
        register("minecraft:iron_door", 4);

        register("minecraft:obsidian", 5);
        register("minecraft:crying_obsidian", 5);

        register("minecraft:bedrock", -1);

        // Future mod blocks example:
        // register("some_mod:steel_block", 4);
        // register("some_mod:reinforced_concrete", 5);
    }

    public static int getLevel(ServerLevel level, BlockPos pos) {

        BlockState state = level.getBlockState(pos);

        if (state.isAir())
            return 0;

        if (state.getBlock() instanceof BedBlock)
            return 0;

        float hardness = state.getDestroySpeed(level, pos);

        if (hardness < 0)
            return -1;

        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());

        if (id != null) {

            Integer customLevel = BLOCK_LEVELS.get(id);

            if (customLevel != null) {
                return customLevel;
            }
        }

        return getLevelByHardness(hardness);
    }

    public static int getRequiredProgress(ServerLevel level, BlockPos pos) {

        int blockLevel = getLevel(level, pos);

        if (blockLevel <= 0)
            return -1;

        return switch (blockLevel) {

            case 1 -> 20;
            case 2 -> 40;
            case 3 -> 70;
            case 4 -> 110;
            case 5 -> 180;

            default -> 260;
        };
    }

    public static boolean canBeBroken(ServerLevel level, BlockPos pos) {

        return getLevel(level, pos) > 0;
    }

    private static int getLevelByHardness(float hardness) {

        if (hardness <= 0.8F)
            return 1;

        if (hardness <= 2.0F)
            return 2;

        if (hardness <= 4.0F)
            return 3;

        if (hardness <= 8.0F)
            return 4;

        if (hardness <= 20.0F)
            return 5;

        return 6;
    }

    private static void register(String id, int level) {

        BLOCK_LEVELS.put(
                new ResourceLocation(id),
                level
        );
    }
}
