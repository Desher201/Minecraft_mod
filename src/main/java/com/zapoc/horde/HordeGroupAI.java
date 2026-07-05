package com.zapoc.horde;

public class HordeGroupAI {

    public static void tick(HordeGroup group) {

        if (group == null)
            return;

        // Лидер принимает решение
        HordeLeaderAI.tick(group);

        // Остальные выполняют его
        HordeFollowerAI.tick(group);

    }

}