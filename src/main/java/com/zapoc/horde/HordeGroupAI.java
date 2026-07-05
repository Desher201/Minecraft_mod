package com.zapoc.horde;

public class HordeGroupAI {

    public static void tick(HordeGroup group) {

        if (group == null)
            return;

        HordeLeaderAI.tick(group);
        HordeFollowerAI.tick(group);
    }
}