package com.zapoc.bed;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public class BedSavedData extends SavedData {

    public static final String DATA_NAME = "zapoc_bed";

    private BlockPos bedPos;
    private ResourceKey<Level> dimension;
    private boolean hardcore = true;

    public BedSavedData() {
    }

    public static BedSavedData load(CompoundTag tag) {

        BedSavedData data = new BedSavedData();

        if (tag.contains("x")) {

            data.bedPos = new BlockPos(
                    tag.getInt("x"),
                    tag.getInt("y"),
                    tag.getInt("z")
            );

            data.dimension = ResourceKey.create(
                    net.minecraft.core.Registry.DIMENSION_REGISTRY,
                    new ResourceLocation(tag.getString("dimension"))
            );
        }

        data.hardcore = tag.getBoolean("hardcore");

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {

        if (bedPos != null) {

            tag.putInt("x", bedPos.getX());
            tag.putInt("y", bedPos.getY());
            tag.putInt("z", bedPos.getZ());

            tag.putString(
                    "dimension",
                    dimension.location().toString()
            );
        }

        tag.putBoolean("hardcore", hardcore);

        return tag;
    }

    public static BedSavedData get(ServerLevel level) {

        return level.getDataStorage().computeIfAbsent(
                BedSavedData::load,
                BedSavedData::new,
                DATA_NAME
        );
    }

    public void setBed(BlockPos pos, ResourceKey<Level> dimension) {

        this.bedPos = pos;
        this.dimension = dimension;

        this.hardcore = false;

        setDirty();
    }

    public void removeBed() {

        bedPos = null;
        hardcore = true;

        setDirty();
    }

    public BlockPos getBedPos() {
        return bedPos;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public boolean isHardcore() {
        return hardcore;
    }
}
