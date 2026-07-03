package com.zapoc.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

public class DeathScreen extends Screen {

    public DeathScreen() {
        super(new TextComponent(""));
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {

        fill(pose, 0, 0, this.width, this.height, 0xFF000000);

        drawCenteredString(
                pose,
                this.font,
                "ВЫ ПОГИБЛИ",
                this.width / 2,
                this.height / 2,
                0xFF0000
        );

        drawCenteredString(
                pose,
                this.font,
                "Cube Apocalypse",
                this.width / 2,
                this.height / 2 + 20,
                0x880000
        );
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}