package com.zapoc.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HudOverlay {

    private boolean printed = false;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {

        if (!printed) {
            System.out.println("===== ZAPOC HUD REGISTERED =====");
            printed = true;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.options.hideGui)
            return;

        PoseStack pose = event.getMatrixStack();
        Font font = mc.font;

        GuiComponent.fill(
                pose,
                5,
                5,
                170,
                70,
                0x88000000
        );

        font.draw(pose, "Cube Apocalypse Alpha", 10, 10, 0xFFFFFF);

        font.draw(
                pose,
                "Day: " + ClientHudData.day,
                10,
                25,
                0xFFFFFF
        );

        font.draw(
                pose,
                "Next Horde: " + ClientHudData.daysLeft,
                10,
                40,
                0xFFFF55
        );

        font.draw(
                pose,
                ClientHudData.hardcore ? "HARDCORE" : "SAFE",
                10,
                55,
                ClientHudData.hardcore ? 0xFF5555 : 0x55FF55
        );
    }
}