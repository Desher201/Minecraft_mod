package com.zapoc.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HudOverlay {

    private static final int WHITE = 0xE6E6E6;
    private static final int MUTED = 0xA8A8A8;
    private static final int GREEN = 0x55FF55;
    private static final int YELLOW = 0xFFD35A;
    private static final int ORANGE = 0xFF9A3D;
    private static final int RED = 0xFF5555;
    private static final int DARK_RED = 0xB52828;
    private static final int PURPLE = 0xC45AFF;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {

        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL)
            return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.options.hideGui)
            return;

        PoseStack pose = event.getMatrixStack();
        Font font = mc.font;

        List<HudLine> lines = buildLines();
        int x = 8;
        int y = 8;
        int width = getWidth(font, lines) + 14;
        int height = lines.size() * 11 + 10;

        GuiComponent.fill(pose, x - 4, y - 4, x + width, y + height, 0x99000000);
        GuiComponent.fill(pose, x - 4, y - 4, x + width, y - 2, getAccentColor() | 0xFF000000);

        int drawY = y;

        for (HudLine line : lines) {
            font.draw(pose, line.text, x, drawY, line.color);
            drawY += 11;
        }
    }

    private static List<HudLine> buildLines() {

        List<HudLine> lines = new ArrayList<>();

        if (ClientHudData.hordeNight) {
            addHordeLines(lines);
        } else {
            addDayLines(lines);
        }

        if (ClientHudData.hardcore) {
            lines.add(new HudLine("HARDCORE MODE", RED));
            lines.add(new HudLine("No base protection", ORANGE));
        }

        return lines;
    }

    private static void addDayLines(List<HudLine> lines) {

        boolean warning = ClientHudData.daysLeft <= 1;
        int dayColor = warning ? YELLOW : WHITE;
        int hordeColor = warning ? ORANGE : MUTED;

        lines.add(new HudLine("ZAPOC APOCALYPSE", MUTED));
        lines.add(new HudLine("Day: " + ClientHudData.day, dayColor));
        lines.add(new HudLine("Horde in: " + ClientHudData.daysLeft + " day(s)", hordeColor));
        lines.add(new HudLine("Base: " + getBaseText(false), ClientHudData.hasBed ? GREEN : RED));
        lines.add(new HudLine("Mode: " + (ClientHudData.hardcore ? "Hardcore" : "Survival"), ClientHudData.hardcore ? RED : GREEN));

        if (ClientHudData.activeRoamingZombies > 0) {
            lines.add(new HudLine("Roaming: " + ClientHudData.activeRoamingZombies, YELLOW));
        }
    }

    private static void addHordeLines(List<HudLine> lines) {

        boolean finalHorde = isFinalHorde();
        String type = getTypeText();

        if (finalHorde) {
            lines.add(new HudLine("FINAL HORDE", PURPLE));
            lines.add(new HudLine("SURVIVE THE NIGHT", RED));
        } else {
            lines.add(new HudLine("HORDE ACTIVE", RED));
            lines.add(new HudLine("Type: " + type, ORANGE));
        }

        if (ClientHudData.maxWaves > 0) {
            lines.add(new HudLine("Wave: " + ClientHudData.currentWave + " / " + ClientHudData.maxWaves, WHITE));
        }

        lines.add(new HudLine("Base: " + getBaseText(true), ClientHudData.hasBed ? YELLOW : RED));

        if (ClientHudData.forcedHorde) {
            lines.add(new HudLine("Forced: true", MUTED));
        }
    }

    private static String getBaseText(boolean horde) {

        if (!ClientHudData.hasBed)
            return "No Bed";

        return horde ? "Protect the bed" : "Stable";
    }

    private static String getTypeText() {
        return ClientHudData.hordeNightType.toUpperCase(Locale.ROOT);
    }

    private static boolean isFinalHorde() {
        return "Final Horde".equalsIgnoreCase(ClientHudData.hordeNightType);
    }

    private static int getAccentColor() {

        if (ClientHudData.hordeNight && isFinalHorde())
            return PURPLE;

        if (ClientHudData.hordeNight)
            return DARK_RED;

        if (ClientHudData.hardcore)
            return RED;

        if (ClientHudData.daysLeft <= 1)
            return ORANGE;

        return GREEN;
    }

    private static int getWidth(Font font, List<HudLine> lines) {

        int width = 0;

        for (HudLine line : lines) {
            width = Math.max(width, font.width(line.text));
        }

        return Math.max(width, 150);
    }

    private static class HudLine {
        private final String text;
        private final int color;

        private HudLine(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }
}
